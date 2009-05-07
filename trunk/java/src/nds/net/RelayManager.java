package nds.net;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import nds.log.Logger;
import nds.log.LoggerManager;
import nds.util.Tools;
/**
 *
 *toRelay.lst �ļ��ṹ��
 * �еĵ�һ����ĸΪ�ļ�״̬��
 * D (Deny)�ȴ�����ʾָ�����ļ�����ֹ��ת���������������posNet������RequestDBUpload��
 * ����δ����DBImport����������posNet���ϴ��ļ����жϣ�����ļ���Ӧ����ת��ndsMgr��
 * P (Permit�����ͣ��������ļ��������͡�
 * F (Fail) �Ƕ����ش���������9�ε��ļ�
 * �ڶ�����ĸΪ�ո�
 * ��������ĸΪ 0��9�����֣�Ϊ���ļ��������͵Ĵ������������9�Σ������ڱ�����
 *  ����Ϊ�ո� �� �ŵ��ţ� �ո� �ļ�����
 *
 *
 *
 */
public class RelayManager {
    private Logger logger= LoggerManager.getInstance().getLogger(RelayManager.class.getName());
    /**
     * After specified retry times, the file will be marked fail.
     */
    private final static int MAX_RETRY_TIMES=9;
    public Vector list; //elements: FileRecord
    private String rootPath;
    /**
     * In some instances, one client will send more than once local data, as following:
     *    pos send RequestDBUpload, and ndsjava replies the ftp dir.
     *    pos uploaded, and send DBImport, ndsjava accepted. But then pos disconnected.
     *    So pos thinks it's failed this time, while ndsjava has already send this request
     *    to ndsmgr. so duplication will occur if pos connects again.
     *
     * History of relay files are cached here, but there's still things that we could not
     *    handle successfully:
     *    after pos failed when DBImport not recieved, pos does some business and the upload
     *    file is also changed.
     */
//    private Hashtable history;
    public RelayManager() {
    }
    /**
     * Try read 'toRelay.lst' int rootPath.
     */
    public void init(String rootPath){
        this.rootPath = rootPath;
        try{
            readFromFile();
        }catch(Exception e){
            logger.error("Can not read from file:"+ rootPath+ "/toRelay.lst",e );
        }
    }
    /**
     * When a file increment its retry time to MAX_RETRY_TIMES, its status will be changed to "F" for failure
     */
    public void incrementFileRetryTimes(String clientName, String fileName){
        FileRecord fr;
        for(int i=list.size()-1;i>=0 ;i--){
            fr=(FileRecord)list.elementAt(i);
            if( fr.fileName.equalsIgnoreCase(fileName) && fr.clientName.equals(clientName)){
                if (fr.retryTimes >=MAX_RETRY_TIMES){
                    fr.status = "F";
                    // save to fail.log
                    logFail(fr);
                    list.removeElementAt(i);
                }else fr.retryTimes=fr.retryTimes+1;
                toFile();
                break;
            }
        }

    }
    /**
     * @param fileName including shop no path
     * @param status "P" for permit, "D" for Deny
     */
    public void addFile(String clientName, String fileName, String status){
        if( ! "P".equals(status) && !"D".equals(status)) throw new Error("Internal Error, status should only be 'P' or 'D'");
        list.addElement(new FileRecord(clientName, fileName, status,0));
        toFile();
    }
    /**
     * @param state 0, maintain, 1, success handled, 2, fail handle
     */
    public void removeFile(String clientName, String fileName, int state){
        FileRecord fr;
        for(int i=0;i< list.size();i++){
            fr=(FileRecord)list.elementAt(i);
            if( fr.fileName.equalsIgnoreCase(fileName) && fr.clientName.equals(clientName)){
                if(state ==1)logSuccess(fr);
                else if (state==2) logFail(fr);
                list.removeElementAt(i);
                toFile();
                break;
            }
        }
    }
    private boolean isToRelay(FileRecord fr){
        if(  "P".equals(fr.status)  && fr.retryTimes <= MAX_RETRY_TIMES ) return true;
        else return false;
    }
    /**
     *
     * @return null if no more file, string has format like:
     *  "CSA023/293939434.sql.gz", that is, ClientName + "/"+  fileName
     */
    public String getFirstRelayFile(){
        FileRecord fr;
        for(int i=0;i< list.size();i++){
            fr=(FileRecord)list.elementAt(i);
            if( isToRelay(fr) ){
                return fr.clientName + "/" + fr.fileName ;
            }
        }
        return null;
    }
    /**
     * @return false if file is not in 'toRelay.lst'
     */
    public boolean isToRelay(String clientName,String fileName){
        FileRecord fr;
        for(int i=0;i< list.size();i++){
            fr=(FileRecord)list.elementAt(i);
            if( fr.fileName.equalsIgnoreCase(fileName) && fr.clientName.equalsIgnoreCase(clientName)){
                if( isToRelay(fr) )return true;
                else return false;
            }
        }
        return false;
    }
    private void logFile(String fileName, String msg){
        FileOutputStream fos=null;
        try{
            fos= new FileOutputStream(fileName, true);
            fos.write( ( msg+Tools.LINE_SEPARATOR).getBytes() );
        }catch(Exception e){
            logger.error("Error dump to " + fileName+ ", msg="+ msg, e );
        }finally{
            try{ if (fos !=null){ fos.close();}}catch(Exception e2){}
        }
    }
    private void logSuccess(FileRecord fr){
        logFile( rootPath + "/success.log", fr.clientName+ "/"+ fr.fileName );
    }
    private void logFail(FileRecord fr){
        logFile( rootPath + "/fail.log", fr.clientName+ "/"+ fr.fileName );
    }
    /**
     * Dump to file
     */
    private void toFile(){
        String f=rootPath + "/toRelay.lst";
        try{
            FileOutputStream fos= new FileOutputStream(f);
            String line;
            for(int i=0;i<list.size();i++){
                line=list.elementAt(i).toString() + Tools.LINE_SEPARATOR ;
                fos.write( line.getBytes());
            }
            fos.close();
        }catch(Exception e){
            logger.error("Error dump to " + f, e );
        }
    }
    private void readFromFile() throws IOException{
        list=new Vector();

        String f= rootPath+"/toRelay.lst";
        File file= new File(f);
        if (!file.exists() ) return;

        FileInputStream fis= new FileInputStream(f);
        byte[] data= new byte[(int)(file.length()) ];
        fis.read(data);
        String s= new String(data);
        StringTokenizer st=new StringTokenizer(s, Tools.LINE_SEPARATOR);
        while(st.hasMoreTokens() ){
            String line=st.nextToken();
            try{
                list.addElement(new FileRecord(line));
            }catch(Exception e){
                logger.error("Could not parsing line:"+ line+" "+ e);
            }
        }
    }
    public static void main(String[] args) {
        RelayManager relayManager1 = new RelayManager();
    }
    // represent one line in toRelay.lst
    class FileRecord{
        String clientName;
        String fileName;
        String status;
        int retryTimes;
        public FileRecord(String c, String f, String s, int r){
            clientName=c;
            fileName = f;
            status= s;
            retryTimes=r;
        }
        /**
         * @param line such like "P 0 CSA023 200309080302.sql.gz"
         */
        public FileRecord(String line) throws NoSuchElementException{
            StringTokenizer st= new StringTokenizer(line);
            status= st.nextToken() ;
            try{
                retryTimes=( new Integer(st.nextToken()) ).intValue();
            }catch(Exception e){
                logger.error("Error parsing toRelay line:" + line+ " "+ e);
                retryTimes=0;
            }
            clientName= st.nextToken() ;
            fileName= st.nextToken() ;
        }
        public String toString(){
            return status + " " +retryTimes+ " "+ clientName+ " "+ fileName;
        }
    }
}