class ServerHandler
{
ServerBase sb;
Thread loop=new Thread()
{
int i;
public void run()
{
while(true)
{
sb.delay(200);
for(i=0;i<sb.climit;i++)
{
if(sb.dop[i].recieve())
{
System.out.println(i+":"+sb.recieveData(i));
}
}
}
}
}
;
ServerHandler(int a)
{
sb=new ServerBase(a);
start();
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