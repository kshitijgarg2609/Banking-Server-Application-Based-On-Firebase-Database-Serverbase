import java.util.*;
class DataList
{
int cli=-1;
String dir="";

String uid="";
String password="";
int amount=-1;
LinkedList<String> info = new LinkedList<String>();
String name="";

static ListText lt = new ListText();
DataList(String d)
{
dir=d;
String tmp;
uid=d.substring(d.lastIndexOf('\\')+1);
for(String dta : lt.fileToList(dir+"\\info.txt"))
{
if(dta.equals("")||dta==null)
{
continue;
}
if(dta.indexOf("name=")!=-1)
{
name=dta.substring(5);
}
info.add(dta);
}
tmp="";
for(String dta : lt.fileToList(dir+"\\password.txt"))
{
tmp+=dta;
}
password=tmp;
tmp="";
for(String dta : lt.fileToList(dir+"\\amount.txt"))
{
tmp+=dta;
}
amount=Integer.parseInt(tmp);
}
void displayInfo()
{
System.out.println("UID : "+uid);
System.out.println("password : "+password);
System.out.println("Amount : "+amount);
System.out.println("Info :-");
for(String dta : info)
{
System.out.println("\t"+dta);
}
}
void updatePassword(String a)
{
LinkedList<String> dta = new LinkedList<String>();
password=a;
dta.add(a);
lt.listToFile((dir+"\\password.txt"),dta);
}
void updateAmount(int a)
{
LinkedList<String> dta = new LinkedList<String>();
amount=a;
dta.add((a+""));
lt.listToFile((dir+"\\amount.txt"),dta);
}
boolean matchPassword(String a)
{
return a.equals(password);
}
boolean subtract(int a)
{
if(a<=amount)
{
//amount-=a;
updateAmount(amount-a);
return true;
}
return false;
}
void add(int a)
{
//amount+=a;
updateAmount(amount+a);
}
}