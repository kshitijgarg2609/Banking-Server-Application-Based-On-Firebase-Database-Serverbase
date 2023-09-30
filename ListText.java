import java.io.*;
import java.util.*;
class ListText
{
LinkedList<String> fileToList(String dir)
{
LinkedList<String> link = new LinkedList<String>();
File chk;
String str;
BufferedReader br;
try
{
chk = new File(dir);
if(!(chk.exists()&&chk.isFile()))
{
return null;
}
br=new BufferedReader(new FileReader(dir));
while((str=br.readLine())!=null)
{
link.add(str);
}
br.close();
}
catch(IOException e)
{
System.out.println("ERROR !!!");
}
return link;
}
int listToFile(String dir,LinkedList<String> link)
{
File chk;
PrintWriter pw;
try
{
newFile(dir);
chk = new File(dir);
if(!(chk.exists()&&chk.isFile()))
{
return -1;
}
if(link==null)
{
return 0;
}
pw=new PrintWriter(new BufferedWriter(new FileWriter(dir)));
for(String str : link)
{
pw.println(str);
}
pw.close();
}
catch(IOException e)
{
System.out.println("ERROR !!!");
}
return 1;
}
int logData(String dir,String a)
{
File chk;
PrintWriter pw;
try
{
//newFile(dir);
/*
chk = new File(dir);
if(chk.isFile())
{
return -1;
}
*/
if(a==null)
{
return 0;
}
pw=new PrintWriter(new BufferedWriter(new FileWriter(dir,true)));
pw.println(a);
pw.close();
}
catch(IOException e)
{
System.out.println("ERROR !!!");
}
return 1;
}
void newFile(String dir)
{
PrintWriter pw;
int indx;
indx=dir.lastIndexOf('\\');
if(indx==-1)
{
return;
}
if(!(new File(dir.substring(0,indx+1))).exists())
{
return;
}
try
{
pw=new PrintWriter(new BufferedWriter(new FileWriter(dir)));
pw.close();
}
catch (IOException e)
{
System.out.println("ERROR !!!");
}
}
}