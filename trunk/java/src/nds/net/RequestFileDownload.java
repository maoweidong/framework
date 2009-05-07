package nds.net;
import java.io.File;

import com.echomine.jabber.JabberChatMessage;


/**
 * ����һ����Ϣ��������ndsMgr��ndsJava������Ϣ����������ĳ���ļ��ĺ����ļ���
  ndsJava�ĺ�����������toRelay.lst �ļ��н����ļ���������ļ���ȷ��ΪndsMgr�Ѿ����أ���
  ������������滹����ҪndsJava���յ��ļ�������NotifyFileArrived�����ndsMgr

��Ϣ����
LastDownloadClientName:String
LastDownloadFile:String

 * Client ask for download db, incoming message should have following params set<p>
 * "CommandType"= "RequestFileDownload"<br>
 * "ClientName"="NDSMGR"<br>
 * "LastDownloadClientName" (int) <br>
 * "LastDownloadFile"
 * While the outgoing message should have these params: <p>
 * "CommandType"= "NotifyFileArrived"
  ClientName:�ͻ�������
  FileName:String
  FileCheckSum: String
  DownloadURL: String
  TotalFileLength: int
 */
public class RequestFileDownload extends AbstractSessionListener{
    // downloadRootDir is for local file system, while downloadRootURL is the ftp addr of the downloadRootDir
    private String  downloadRootDir, downloadRootURL ,sessionMsgPrefix;
    private SyncManager manager;

        /* some times this server(SyncManager) may be down, while ServerManager will send
           request message at specified interval. So many message will be flushed in after
           this server is brought up.
           A signal is added here to reject following message which is very close to last message
           handled.
        */
    private long lastHandleTime=0;
    private final static long MIN_HANDLE_INTERVAL= 1000* 5; // five seconds
    public RequestFileDownload() {
    }

    public void setController(SessionController controller){
        super.setController(controller);
        // ServerManager's download url should be POS' upload url
        downloadRootDir =controller.getAttribute("PosDB.Upload.RootDir", "f:/act/posdb/upload");
        downloadRootURL=controller.getAttribute("PosDB.Upload.RootURL", "ftp://anonymous:aaa@localhost/posdb/upload");
        sessionMsgPrefix= controller.getCommander().getSessionMsgPrefix();
        manager=(SyncManager)controller.getCommander();
    }
    public void onMessage(SessionMsg msg){
        /* some times this server(SyncManager) may be down, while ServerManager will send
           request message at specified interval. So many message will be flushed in after
           this server is brought up.
           A signal is added here to reject following message which is very close to last message
           handled.
        */
        if( System.currentTimeMillis() - lastHandleTime< MIN_HANDLE_INTERVAL){
            logger.debug("Message discarded forcefully since it's too close to last handled messge");
            return;
        }
        lastHandleTime = System.currentTimeMillis() ;
        String lasDownloadClientName= msg.getParam("LastDownloadClientName");
        String lastDownloadFile= msg.getParam("LastDownloadFile");
        // yfzhu added 2003-11-09 to insert table poslog
        notifyClientStatus(lasDownloadClientName, "LastDownloadFile", lastDownloadFile);

//        logger.debug("RequestFileDownload request LastDownloadClientName " + lasDownloadClientName+", LastDownloadFile:" + lastDownloadFile );
        manager.getRelayManager().removeFile(lasDownloadClientName,lastDownloadFile, 1  );

        // get to relay file, format is clientName/file
        String nextRelayFile= manager.getRelayManager().getFirstRelayFile() ;
        if(nextRelayFile==null){
            // no more file to relay, return
            return;
        }
        int seperator= nextRelayFile.indexOf("/");
        String clientName= nextRelayFile.substring(0, seperator);
        String fileName= nextRelayFile.substring(seperator+1);

//        logger.debug("relay client:"+ clientName +", file:"+ fileName);


        // get download file url
        String downloadURL, checkSum ;

        SessionMsg out = new SessionMsg(sessionMsgPrefix);
        JabberChatMessage jcm= msg.getOrigionalJabberMessage();
        out.setParentMsgID(msg.getID()  );
        out.setThreadID(msg.getThreadID()) ;
        out.addParam("CommandType", "NotifyFileArrived");
        // list only file with specified extension
        File file= new File(downloadRootDir+ "/" + clientName+"/"+ fileName);
        checkSum= MD5SumUtil.getCheckSum(file, file.getParentFile().getAbsolutePath() + "/checksum/"+ fileName +".sum");
        downloadURL = downloadRootURL +  "/" + clientName+ "/" + fileName;
        out.addParam("DownloadURL",downloadURL );
        out.addParam("ClientName",clientName );
        out.addParam("FileName", fileName);
        out.addParam("FileCheckSum", checkSum);// file MD5 checksum, added at 2003-06-11 by yfzhu);
        out.addParam("FileLength", "" + file.length());
        controller.sendMsg(out, jcm.getFrom() ,jcm.getThreadID());
        // increment retry times, so after too many retry time, skip that file
        manager.getRelayManager().incrementFileRetryTimes(clientName, fileName);
    }


}