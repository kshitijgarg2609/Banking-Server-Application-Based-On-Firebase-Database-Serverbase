class RequestData
{
long t;
RequestData()
{
t=System.currentTimeMillis();
}
int elapsing()
{
return (int)(System.currentTimeMillis()-t)/1000;
}
}