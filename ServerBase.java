import java.io.*;
import java.util.*;
import com.google.firebase.*;
import com.google.firebase.database.*;
import com.google.auth.oauth2.*;
class ServerBase
{
//authentication
String dir;
static FirebaseDatabase firedb;
static DatabaseReference ref;
//process
static boolean con=false;
static int climit=0;
int lindx=-1;
Thread req = new Thread()
{
public void run()
{
while(true)
{
handle();
}
}
}
;
DataSnapshot dss;
static boolean rec=false;
//timeouts
int requesttimeout=5;
int verifytimeout=5;
int ontimeout=5;
//structure
Map<String,Object> dta;
DataOperation dop[];
//integrate
ServerClientID scid[];// = new ServerClientID();
RequestHandle reqh = new RequestHandle();
ServerBase(int a)
{
try
{
authenticateJson();
firedb=FirebaseDatabase.getInstance();
ref=firedb.getReference();
makeStructure();
climit=a;
dop=new DataOperation[climit];
scid=new ServerClientID[climit];
for(int i=0;i<climit;i++)
{
dop[i]=new DataOperation();
scid[i]=new ServerClientID();
}
addRequestEvent();
//resetClient();
con=true;
}
catch(Exception e)
{
con=false;
}
//ref.removeEventListener(_ValueEventListener_);
}

void authenticateJson()throws Exception
{
dir=System.getProperty("user.dir")+"\\json\\";
File f = new File(dir);
int indx;
String fbio="";
for(String x : f.list())
{
if(x.indexOf(".json")!=-1)
{
indx=x.indexOf("-firebase-");
fbio=x.substring(0,indx);
dir+=x;
break;
}
}
fbio="https://"+fbio+"-default-rtdb.firebaseio.com";
FileInputStream serviceAccount = new FileInputStream(dir);
FirebaseOptions options = new FirebaseOptions.Builder()
.setCredentials(GoogleCredentials.fromStream(serviceAccount))
.setDatabaseUrl(fbio)
.build();
FirebaseApp.initializeApp(options);
}
void makeStructure()
{
ref.setValue(null,null);
ref.child("client").child("key").setValue("value",null);
ref.child("request").child("key").setValue("value",null);
}
void startHandlingClients()
{
req.start();
}
void handle()
{
if(rec==false)
{
return;
}
String keyds,valueds;
String ids;
//System.out.println("new call");
delay(1000);
refresh();
for(DataSnapshot strd : update(null).getChildren())
{
//reqh
//mp
keyds="";
valueds="";
try
{
keyds=strd.getKey();
valueds=strd.getValue(String.class);
}
catch(Exception e)
{
deleteRequest(keyds);
continue;
}
if(keyds.equals("key"))
{
continue;
}
//System.out.println("Request Available");
//System.out.println(keyds+":"+valueds);
if(!verifyKey(keyds))
{
deleteRequest(keyds);
continue;
}
if(valueds.equals("request")&&existing(keyds))
{
deleteRequest(keyds);
continue;
}
if(valueds.equals("request"))
{
ids=reqh.generateServerID();
if(connectClient(ids))
{
respond(keyds,ids);
scid[lindx].cli=keyds;
}
else
{
respond(keyds,"rejected");
}
}
else if(valueds.equals("rejected")||verifyKey(valueds))
{
if(!reqh.mp.containsKey(keyds))
{
reqh.mp.put(keyds,new RequestData());
}
}
else
{
deleteRequest(keyds);
}
//System.out.println(+"::"+);
}
//System.out.println("check key");
///////////change
try
{
//Map<String,RequestData> tmp=reqh.mp;
if(reqh.mp.entrySet()!=null&&reqh.mp.entrySet().size()>=0)
{
for(Map.Entry<String,RequestData> hkey : reqh.mp.entrySet())
{
if(hkey==null)
{
continue;
}
keyds=hkey.getKey();
//System.out.println(keyds+": timeleft :- "+hkey.getValue().elapsing());
if(hkey.getValue().elapsing()>=requesttimeout)
{
reqh.mp.remove(keyds);
deleteRequest(keyds);
}
}
//reqh.mp=tmp;
}
}
catch(Exception e)
{
System.out.println("SERVER MAP ERROR !!!");
}
for(int i=0;i<climit;i++)
{
if(scid[i].all&&scid[i].ver)
{
//System.out.println("check : "+dop[i].getNotification());
if((dop[i].getNotification()).equals("C"))
{
dop[i].notifyOnline();
scid[i].resetElapsing();
//System.out.println("First case");
}
else if((scid[i].elapsing()>=ontimeout)&&(dop[i].getNotification()).equals("S"))
{
disconnectClient(i);
//System.out.println("Second case");
}
else if(!(dop[i].getNotification()).equals("S")&&!(dop[i].getNotification()).equals("C"))
{
disconnectClient(i);
//System.out.println("Third case");
}
else
{
//System.out.println("No Case");
}
}
else if(scid[i].all)
{
//System.out.println("verifying allocated :- "+i+"\trem :- "+scid[i].elapsing());
if(scid[i].elapsing()>=verifytimeout)
{
//System.out.println("timeup :- "+i);
disconnectClient(i);
}
else if((scid[i].cli).equals(dop[i].getNotification()))
{
//System.out.println("verifying allocated :- "+i);
scid[i].ver=true;
dop[i].notifyOnline();
scid[i].resetElapsing();
}
}
}
}
boolean connectClient(String a)
{
for(int i=0;i<climit;i++)
{
if(!dop[i].set)
{
scid[i].ser=a;
scid[i].all=true;
scid[i].startElapsing();
dop[i].allocate(a);
lindx=i;
return true;
}
}
return false;
}
boolean connectClient(int a,String b)
{
if((a>=0&&a<climit)&&!dop[a].set)
{
scid[a].ser=b;
scid[a].all=true;
scid[a].ver=true;
dop[a].allocate(b);
return true;
}
return false;
}
boolean disconnectClient(int a)
{
if((a>=0&&a<climit)&&dop[a].set)
{
scid[a]=new ServerClientID();
dop[a].deallocate();
return true;
}
return false;
}
String recieveData(int a)
{
if((a>=0&&a<climit)&&dop[a].set)
{
return dop[a].fetchData();
}
return "";
}
boolean sendData(int a,String data)
{
if((a>=0&&a<climit)&&dop[a].set)
{
dop[a].sendData(data);
return true;
}
return false;
}
//request handling
void addRequestEvent()
{
ref.child("request").addValueEventListener(new ValueEventListener()
{
public void onDataChange(DataSnapshot ds)
{
if(ds==null)
{
return;
}
if(!rec)
{
rec=true;
}
update(ds);
}
public void onCancelled(DatabaseError de)
{
}
}
);
}
synchronized DataSnapshot update(DataSnapshot dds)
{
if(dds==null)
{
return dss;
}
else
{
dss=dds;
return null;
}
}
void refresh()
{
respond("key","value");
}
void respond(String a,String b)
{
ref.child("request").child(a).setValue(b,null);
}
void deleteAllRequests()
{
ref.child("request").setValue(null,null);
ref.child("request").child("key").setValue("value",null);
}
void deleteRequest(String a)
{
if(a==null||a=="")
{
return;
}
ref.child("request").child(a).removeValue(null);
}
//searching
boolean existing(String a)
{
for(ServerClientID idsc : scid)
{
if(idsc.all)
{
if(a.equals(idsc.ser)||a.equals(idsc.cli))
{
return true;
}
}
}
return false;
}
boolean verifyKey(String a)
{
long numstr;
try
{
//System.out.println("Verifying :- "+a);
if(a.length()==10)
{
numstr=Long.parseLong(a);
//System.out.println("Verified");
return true;
}
//System.out.println("Can't Verify");
return false;
}
catch(Exception e)
{
//System.out.println("Can't Verify (exception)");
return false;
}
}
//synchronized DataSnapshot(String a)
void close()
{
System.exit(0);
}
//testing
//DataOperation
class DataOperation
{
FireClientDataSet dataset;
boolean set;
String id;
Map<String,Object> structure;
RData rd;
TData td;
OnlineFlagData ofd;
DataOperation()
{
dataset=new FireClientDataSet();
set=false;
id="";
structure=null;
rd=new RData();
td=new TData();
ofd=new OnlineFlagData();
}
void allocate(String a)
{
if(set)
{
return;
}
id=a;
structure=new HashMap<String,Object>();
structure.put("rx",dataset.zero);
structure.put("rxd",dataset.blank);
ref.child("client").child(id).child("rdata").setValue(structure,null);
ref.child("client").child(id).child("rdata").addValueEventListener(rd);

structure=new HashMap<String,Object>();
structure.put("tx",dataset.zero);
structure.put("txd",dataset.blank);
ref.child("client").child(id).child("tdata").setValue(structure,null);
ref.child("client").child(id).child("tdata").addValueEventListener(td);

structure=new HashMap<String,Object>();
structure.put("clientid",dataset.blank);
ref.child("client").child(id).child("id").setValue(structure,null);
ref.child("client").child(id).child("id").addValueEventListener(ofd);

structure=null;
set=true;
}
void deallocate()
{
if(!set)
{
return;
}

ref.child("client").child(id).child("rdata").removeEventListener(rd);
ref.child("client").child(id).child("tdata").removeEventListener(td);
ref.child("client").child(id).child("id").removeEventListener(ofd);
ref.child("client").child(id).removeValue(null);

id="";
set=false;
}
boolean recieve()
{
return (set&&dataset.rx.intValue()==1);
}
String fetchData()
{
String arrdata="";
if(set&&dataset.rx.intValue()==1)
{
arrdata=dataset.rxd;
ref.child("client").child(id).child("rdata").child("rx").setValue(FireClientDataSet.zero,null);
}
return arrdata;
}
boolean sendData(String a)
{
if(set&&dataset.tx.intValue()==0)
{
ref.child("client").child(id).child("tdata").child("txd").setValue(a,null);
ref.child("client").child(id).child("tdata").child("tx").setValue(FireClientDataSet.one,null);
return true;
}
return false;
}
String getNotification()
{
return dataset.clientid;
}
void notifyOnline()
{
ref.child("client").child(id).child("id").child("clientid").setValue("S",null);
}
class RData implements ValueEventListener
{
public void onDataChange(DataSnapshot ds)
{
if(ds==null)
{
return;
}
try
{
dataset.rx=ds.child("rx").getValue(Integer.class);
dataset.rxd=ds.child("rxd").getValue(String.class);
}
catch(Exception e)
{
}
}
public void onCancelled(DatabaseError de)
{
}
}
class TData implements ValueEventListener
{
public void onDataChange(DataSnapshot ds)
{
if(ds==null)
{
return;
}
try
{
dataset.tx=ds.child("tx").getValue(Integer.class);
dataset.txd=ds.child("txd").getValue(String.class);
}
catch(Exception e)
{
}
}
public void onCancelled(DatabaseError de)
{
}
}
class OnlineFlagData implements ValueEventListener
{
public void onDataChange(DataSnapshot ds)
{
if(ds==null)
{
return;
}
try
{
dataset.clientid=ds.child("clientid").getValue(String.class);
}
catch(Exception e)
{
}
}
public void onCancelled(DatabaseError de)
{
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