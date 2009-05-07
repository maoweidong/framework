package nds.net;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Properties;

import nds.query.QueryEngine;
import nds.util.CommandExecuter;
import nds.util.Tools;

/**
 * Export POS data to .gz file, and mv them to POS specified folder
 * ������������:
 * ��һ�Ų�����־�� posexpdata ( id, shopId, shopGroupId, sqlText),��¼���ܲ��Ĺ����ŵ��������־.
 * (ע������ŵ����³�ʼ����õĴ�������,��ͨ������ĳ��������.)
 * ExportPOSData ��ִ��ʱ, ���ÿһ���ŵ������������
 *  for each pos
 *    dump following data to a file named $net.PosDB.Download.RootDir/$(customer.no)/yyyymmddHHMM.sql
 *    ( note all sql files have the same name[execution start time])
 *    select distinct posexpdata.id, sqltext from posexpdata, shopgroupR
 *           where posexpdata.shopid=$1 or posexpdata.shopid=-1
 *                 or (shopgroupR.shopId= $1 and posexpdata.shopgroupId=shopgroupR.shopgroupId )
 *           order by posexpdata.id asc
 *  next
 *  gzip all file has the same name
 *
 * �ŵ�Ķ���:
 *   PosExpData���ֶ�˵���� shopID ���Ϊ-1,��ʾ���е��ŵ궼���մ����ݣ������Ӧ��ָ�����ŵ�
 *   CustomerSort ����CustomerSort=1 ���ֶζ�Ӧ��ID(Ŀǰ�� 1, 2), ��Ӧ��Customer��
 *     create view vshop as select * from customer where (customersortid=1 or customersortid=2)
 *
 * 2003-07-25 ���ӽ�������Ҫ�����Ļ������ݣ�����һ��������Ŀ¼:
 *    ��Ҫ�����Ļ������ݣ�������������
 *    ���� table.getDispatchType() ��= table.DISPATCH_ALL��ͳһ���õ� $download_root/full/Ŀ¼��
 *     �����ļ���Ϊtablename��������ϵͳ����zippath�ϲ���һ���ļ���ѹ����
 *    ���� table.getDispatchType() ��= table.DISPATCH_SPEC
 *      ��ÿһ��$download_root/full/$shopĿ¼�½�������tableName���������������ϲ���ѹ��
 *    ���õ�nds.net.ExportFullPOSData

 *  @changelog
�����ǵ�oracleϵͳ����һ���·����ݵĴ洢���У������вֿ��������ⵥ�������������Ϣ���ύʱ���ɵ����У����ŵ�Ļ�Ʒ��ת�������ŵ��ϴ��ܲ�ʱ���ɵ����У����Լ�������Ϣ���Ʒ���ͻ��ȡ�������һ��java�ػ����̣�ÿ����Сʱ���·����е�����ȡ����������ŵ�������ļ�����ͨ��ͬ�����̴��ݵ�mit3��ӦĿ¼��

java�ػ�����ȡ�����·����ݺ󣬻Ὣ������ա�����ͳ������

���ػ����̿�ʼȡ�·�����ʱ�����������1000�����ݡ�����������̵�ʱ���Լ��Ҫ4���ӡ���ϵͳ�߷�����ʱ�䣬���������ʱ��ʱ���������ʱϵͳ������ģ����ܲ������µ��·����ݡ�Ҳ����˵���ڴ��100�����ݵ�ʱ������һЩ�·����������ˡ���������̲���֪����Щ���ݡ��ȴ��������java���̵�������շ���������100�������ݶ����������������ݵĶ�ʧ��

�������������bug������2004-04-24������ɡ��Ľ��ķ����ǣ��ڴ��ǰ����¼�·����е����һ�����ݵ�λ�á��ڴ����ɺ󣬽������¼λ��ǰ��������¼λ�ñ��������ݡ�

 *
 */
public class ExportPOSData extends ThreadProcess {
    private final static SimpleDateFormat expFileNameFormatter
            =new SimpleDateFormat("yyyyMMddHHmm");

    private String downloadRootDir;
    private String expFileName;
    private String tmpDir;
    private QueryEngine engine;
    private ExportFullPOSData fpd= new ExportFullPOSData();
    private String cmdRootPath;

    public ExportPOSData() {

    }
    public void init(Properties props) {
        // getting property needed
        if(props ==null)props=new Properties();
        downloadRootDir = props.getProperty("PosDB.Download.RootDir", "f:/act/posdb/download");
        tmpDir =props.getProperty("PosDB.TmpDir", "f:/act/posdb/tmp");
        cmdRootPath= props.getProperty("cmd.root", "/");
        fpd.init(props);
    }
    /**
    * Create one shop exp data
    * @param maxId only data whose id is less or equal than maxId will be loaded
    */
    private void executeOne(int shopId, String shopNo, Connection conn, long maxId) {
        ResultSet rs=null;
        PreparedStatement stmt=null;
        PrintWriter out=null;
        try{
        String path= downloadRootDir + "/" + shopNo ;
        String file=path +"/" + expFileName;

        String exportSQL="select  posexpdata.id, sqltext from posexpdata " +
            "where posexpdata.id<= "+ maxId + " and ( posexpdata.shopid=? or posexpdata.shopid=-1 or posexpdata.shopgroupid=-1 "+
                 " or (posexpdata.shopgroupid in ( select shopgroupId from shopgroupR " +
                      " where shopId=? ))) " +
            "order by posexpdata.id asc";

        stmt=conn.prepareStatement(exportSQL);
        stmt.setInt(1, shopId);
        stmt.setInt(2, shopId);
        rs= stmt.executeQuery();

        String data;
        int count=0;

        while( rs.next() ){
            if( count ==0){
                CreateFolder(path);
                out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            }
            data= rs.getString(2);
            out.println(data);
            count ++;
        }
        //if(count >0)logger.info("Total " + count+ " records exported for Shop:" +
        //            shopNo+ " to " +file );
        }catch(Exception e){
            logger.error("Export POS data for " + shopNo+ "failed", e);
        }finally{
            try{ if(stmt !=null) stmt.close();}catch(Exception e2){e2.printStackTrace() ;}
            if(out !=null)out.close();
        }
    }
    public void execute() {
        fpd.execute();
//        if(true) return;


        Connection conn=null;
        Statement stmt=null;
        try{

            conn= QueryEngine.getInstance().getConnection();
            ResultSet rs=null;
            // yfzhu 2004-04-24 get max id from posexpdata
            long maxId=-1;
            stmt= conn.createStatement();
            rs=stmt.executeQuery("select max(id) from posexpdata");
            if ( rs.next()){
                maxId= rs.getLong(1);
            }
            rs.close();
            stmt.close();

            expFileName= expFileNameFormatter.format(new java.util.Date())+".sql";
            stmt= conn.createStatement();
            rs=stmt.executeQuery("select id,no from vshop");
            int id; String no;
            while( rs.next() ){
                id= rs.getInt(1);
                no= rs.getString(2);
                this.executeOne(id, no, conn,maxId);
            }
            // gzip all files
            // start a command like :
            // "find /tmp/download -name 200211301231.sql | gzip"
            String outputFileName=tmpDir +"/" + expFileName +".out";
            CommandExecuter exec= new CommandExecuter(outputFileName);
            String cmd=  "sh "+ cmdRootPath+ "/zipsql " +downloadRootDir +" "+expFileName;
            exec.run(cmd);
            logger.debug("Result in executing " +cmd+":" + Tools.getFileContent(outputFileName ));
            // delete all in posexpdata
            stmt.close();
            stmt= conn.createStatement();
            int d=stmt.executeUpdate("delete from posexpdata where id<="+ maxId);
            logger.debug("Total " + d+ " records deleted in posexpdata");

        }catch(Exception e){
            logger.error("Error exporting data.", e);
        }finally{
            try{if( stmt !=null) stmt.close();}catch(Exception e2){}
            try{if( conn!=null)conn.close();}catch(Exception  ee){}
        }

    }
    private void CreateFolder(String path){
        try{
            File file= new File(path);
            if (!file.exists() ) file.mkdirs() ;
        }catch(Exception e){
            ;
        }
    }


}



