class ServerClientID
{
String ser,cli;
boolean all,ver,act;
long t=-1;
ServerClientID()
{
ser="";
cli="";
all=false;
ver=false;
act=false;
}
void startElapsing()
{
if(act)
{
return;
}
t=System.currentTimeMillis();
act=true;
}
void resetElapsing()
{
if(!act)
{
return;
}
t=System.currentTimeMillis();
}
void stopElapsing()
{
if(!act)
{
return;
}
t=-1;
act=false;
}
int elapsing()
{
if(!act)
{
return -1;
}
return (int)(System.currentTimeMillis()-t)/1000;
}
}