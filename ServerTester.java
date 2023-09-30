import java.util.*;
class ServerTester
{
Scanner sc = new Scanner(System.in);
ServerHandler sh = new ServerHandler(100);
void test()
{
String str;
System.out.println("Send Message");
System.out.println(":->view");
System.out.println(":->indx:msg");
while(true)
{
//System.out.println("Enter some value");
str=sc.nextLine();
if(str.equalsIgnoreCase("view"))
{
System.out.println("_______________________________________________________");
for(int i=0;i<sh.sb.climit;i++)
{
str=String.format("%d : S%s -> C%s",i,sh.sb.scid[i].ser,sh.sb.scid[i].cli);
System.out.println(str);
}
System.out.println("_______________________________________________________");
}
else
{
int ii=str.indexOf(':');
try
{
if(ii!=-1)
{
int index = Integer.parseInt(str.substring(0,ii));
if(sh.sendData(index,str.substring(ii+1)))
{
System.out.println("MESSAGE SENT SUCCESSFULLY");
}
else
{
System.out.println("MESSAGE COULDN'T BE SENT");
}
}
else
{
System.out.println("Invalid Command !!!");
}
}
catch(Exception e)
{
System.out.println("Invalid Command !!!");
}
}
}
}
void delay(int a)
{
try
{
Thread.currentThread().sleep(a);
}
catch (InterruptedException e)
{
System.out.println(e);
}
}
}