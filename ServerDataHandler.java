import java.io.*;
import java.util.*;
class ServerDataHandler
{
LinkedList<DataList> li = new LinkedList<DataList>();
DataList arr[];
ServerBase sb;
Thread loop=new Thread()
{
public void run()
{
while(true)
{
sb.delay(250);
cmd();
}
}
}
;
ServerDataHandler()
{
String fdir;
String cdir=System.getProperty("user.dir")+"\\ServerData\\";
int i;
try
{
//System.out.println(cdir);
File ff = new File(cdir);
for(String dta : ff.list())
{
try
{
fdir=cdir+dta;
if(dta.length()!=9||dta.equals("xyz")||(new File(fdir)).isFile())
{
continue;
}
i=Integer.parseInt(dta);
}
catch(Exception e)
{
continue;
}
li.add((new DataList(fdir)));
}
}
catch(Exception e)
{
System.out.println("Error");
}
//testing
arr=new DataList[li.size()];
i=0;
System.out.println("Printing Info ...");
for(DataList dl : li)
{
System.out.println("_____________________________________________________________________");
dl.displayInfo();
arr[i]=dl;
i++;
}

sb=new ServerBase(20);
start();
}
void cmd()
{
int i;
String rec;
//System.out.println("PROCESSING ...");
for(i=0;i<sb.climit;i++)
{
//sb.delay(10);
if(sb.dop[i].recieve())
{
rec=sb.recieveData(i);
System.out.println(i+":"+rec);
//sb.delay(50);
if(rec.indexOf("auth:")!=-1)
{
System.out.println("#auth() called !!!");
auth(rec,i);
}

else if(rec.indexOf("pay:")!=-1)
{
System.out.println("#pay() called !!!");
pay(rec,i);
}

}
/*
for(DataList dta : arr)
{
i=dta.cli;
if(i!=-1)
{
if(!sb.dop[i].set)
{
disconnectClient(i);
dta.cli=-1;
}
}
}
*/
}
}
void auth(String a,int i)
{
int j;
String uid,pass;
j=a.indexOf('@');
if(j==-1)
{
System.out.println("#auth (unauthorized case) called !!!");
disconnectClient(i);
sb.delay(200);
return;
}
uid=a.substring(5,j);
pass=a.substring(j+1);
for(DataList dta : arr)
{
if(dta.uid.equals(uid))
{
//if(dta.cli==-1)
if(dta.matchPassword(pass))
{
dta.cli=i;
System.out.println("#auth (msg connected) !!!");
sendData(i,String.format("connected, your current balance is Rs. %d",dta.amount));
return;
}
else
{
//return "password incorrect";
System.out.println("#auth (msg password incorrect) !!!");
sendData(i,"password incorrect");
return;
}
/*
else
{
System.out.println("#auth (already connected (kicked)) !!!");
disconnectClient(i);
sb.delay(200);
return;
}
*/
}
}
System.out.println("#auth (user does not exist) !!!");
sendData(i,"user does not exist");
}
void pay(String a,int i)
{
int j;
String destid;
int amt=0;
DataList src = null;
DataList dest = null;
j=a.indexOf('@');
if(j==-1)
{
disconnectClient(i);
sb.delay(200);
return;
}
try
{
destid=a.substring(4,j);
amt=Integer.parseInt(a.substring(j+1));
System.out.println(String.format("Check %s :- %d ",destid,amt));
}
catch(Exception e)
{
disconnectClient(i);
sb.delay(200);
return;
}
for(DataList dta : arr)
{
if(dta.cli==i)
{
src=dta;
}
else if(dta.uid.equals(destid))
{
dest=dta;
System.out.println("paying dest");
}
if(src!=null&&dest!=null)
{
break;
}
}
if(src==null)
{
disconnectClient(i);
sb.delay(200);
return;
}
if(dest!=null&&src.uid.equals(dest.uid))
{
sendData(i,"Invalid Sender's ID");
return;
}
if(amt<=src.amount)
{
src.subtract(amt);
dest.add(amt);
sendData(i,String.format("You have paid Rs. %d to %s (%s) and you current balance is Rs. %d",amt,destid,dest.name,src.amount));
if(dest.cli!=-1)
{
sendData(dest.cli,String.format("%s (%s) has paid you Rs. %d amd your current balance is Rs. %d",src.uid,src.name,amt,dest.amount));
}
}
else
{
sendData(i,"Insuffiecient Balance");
return;
}
}
boolean disconnectClient(int a)
{
return sb.disconnectClient(a);
}
boolean sendData(int a,String data)
{
return sb.sendData(a,data);
}
void start()
{
sb.startHandlingClients();
loop.start();
}
}