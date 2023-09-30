import java.util.*;
class RequestHandle
{
int phse;
Map<String,RequestData> mp = new HashMap<String,RequestData>();
RequestHandle()
{
}
String generateServerID()
{
int i;
i=((int)(Math.random()*(100000-10000)))+10000;
return String.format("%d%d",i,(int)(System.currentTimeMillis()%100000));
}
void test()
{
String str;
while(true)
{
str=generateServerID();
if(str.length()==10)
{
System.out.println(str);
}
else
{
System.out.println("ERROR\n"+str);
break;
}
}
}
}