/*
 * Agile Control Technologies Ltd,. CO.
 * http://www.agileControl.com
 */
package nds.report;

import nds.schema.*;
import nds.util.*;
import nds.control.web.*;
import nds.log.Logger;
import nds.log.LoggerManager;
import nds.model.dao.*;
import nds.model.*;
import org.hibernate.*;
import nds.query.*;

import java.util.*;
import java.io.*;
import java.net.URL;
import java.sql.*;
/**
 * Sync report with table information.
 * 
 * @author yfzhu@agilecontrol.com
 */

public class ReportSyncManager {
	private static Logger logger= LoggerManager.getInstance().getLogger(ReportSyncManager.class.getName());
	private String listTemplateXML, subListTemplateXML,objectTemplateXML;
	private Integer listTemplateId, subListTemplateId, objectTemplateId;
	private long listTemplateTime, subListTemplateTime, objectTemplateTime; // last modified time
	
	private final static String  AD_REPORT_INSERT = 
	"insert into ad_report(id,ad_client_id,ad_org_id,name,description,previewurl,fileurl,ad_table_id,auto_update,reporttype,allow_fg,ismaster,master_template_id,ownerid,modifierid,creationdate,modifieddate,isactive )values("+
	"?, get_adclient_id(?), null,?,?,null,?,?,'Y',?,'Y',?,?,null,null, sysdate, sysdate,'Y')";

	private String clientDomain;
	private int adClientId;
	public ReportSyncManager(){
		
	}
	
	/**
	 * ׼������ģ����Ϣ��ע�⣬��ģ���ȡ��󴴽����Ǹ���
	 * ��ǰ��Ʋ�֧�����������
	 * ͬ�����͵ı���(reportType)�ж����ģ�壬���������ڵ���ģ����º��Զ��������ڶ�Ӧ�ı���
	 *
	 */
	public void init(String clientDomain) throws Exception{
		this.clientDomain= clientDomain;
		
        AttachmentManager attm=(AttachmentManager)WebUtils.getServletContextManager().getActor(WebKeys.ATTACHMENT_MANAGER);
		Configurations conf=(Configurations)WebUtils.getServletContextManager().getActor(WebKeys.CONFIGURATIONS);
        QueryEngine engine= QueryEngine.getInstance();
		TableManager manager= TableManager.getInstance();
        
		adClientId= Tools.getInt(engine.doQueryOne("select id from ad_client where domain='"+ clientDomain+"'"),-1);
		//create 3 master report template if not exists
		AdReportDAO dao= new AdReportDAO();
		AdReport adReport;
		String file;

		Connection conn= engine.getConnection();
		InputStream is ;Attachment att;int objectId;String fileURL;
		try{
		PreparedStatement pstmt= conn.prepareStatement(AD_REPORT_INSERT);	
		Table table= manager.getTable("ad_report");
		Column col= manager.getColumn( "ad_report","fileurl");
    			
		// standard list master template
		List ls=dao.find("from AdReport a where a.AdTableId is null and a.ReportType='L' and a.IsMaster='Y' and a.AdClientId="+adClientId+" order by a.Id desc");
		if( ls.isEmpty()){
			file= conf.getProperty("report.template.list");
			
			this.listTemplateXML=Tools.readFile(file, "UTF-8");
			// insert records to db
			objectId=engine.getSequence("ad_report");
			this.listTemplateId = new Integer(objectId);
			fileURL= "/servlets/binserv/Attach?table="+ table.getId()+
			"&column="+ col.getId()+"&objectid="+ objectId;
	    	insertRecord(objectId,  null, fileURL, "L", null, pstmt);
	    	att= new Attachment(clientDomain+"/" + table.getRealTableName()+"/"+col.getName(),  objectId+"" );
			att.setAuthor("root");
			att.setVersion(0);
			att.setExtension("jrxml");
	        is=new FileInputStream(file);
	    	attm.putAttachmentData(att, is);
	    	is.close();
		}else{
			adReport=(AdReport)ls.get(0);
			this.listTemplateId = adReport.getId();
			this.listTemplateTime= adReport.getModifiedDate().getTime();
			fileURL =ReportTools.getReportFile(listTemplateId.intValue(), clientDomain);
			this.listTemplateXML=Tools.readFile(fileURL , "UTF-8");
		}
		// sub list master template
		ls=dao.find("from AdReport a where a.AdTableId is null and a.ReportType='S' and a.IsMaster='Y' and a.AdClientId="+adClientId+" order by a.Id desc");
		if( ls.isEmpty()){
			file= conf.getProperty("report.template.sublist");
			
			this.subListTemplateXML=Tools.readFile(file, "UTF-8");
			// insert records to db
			objectId=engine.getSequence("ad_report");
			this.subListTemplateId =new Integer( objectId);
			fileURL= "/servlets/binserv/Attach?table="+ table.getId()+
			"&column="+ col.getId()+"&objectid="+ objectId;
	    	insertRecord(objectId,  null, fileURL, "S", null, pstmt);
	    	att= new Attachment(clientDomain+"/" + table.getRealTableName()+"/"+col.getName(),  objectId+"" );
			att.setAuthor("root");
			att.setVersion(0);
			att.setExtension("jrxml");
	        is=new FileInputStream(file);
	    	attm.putAttachmentData(att, is);
	    	is.close();
		}else{
			adReport=(AdReport)ls.get(0);
			this.subListTemplateId = adReport.getId();
			this.subListTemplateTime = adReport.getModifiedDate().getTime();
			fileURL =ReportTools.getReportFile(subListTemplateId.intValue(), clientDomain);
			this.subListTemplateXML=Tools.readFile(fileURL, "UTF-8");
		}
		// object master template
		ls=dao.find("from AdReport a where a.AdTableId is null and a.ReportType='O' and a.IsMaster='Y' and a.AdClientId="+adClientId+" order by a.Id desc");
		if( ls.isEmpty()){
			file= conf.getProperty("report.template.object");
			
			this.objectTemplateXML=Tools.readFile(file, "UTF-8");
			// insert records to db
			objectId=engine.getSequence("ad_report");
			this.objectTemplateId= new Integer(objectId);
			fileURL= "/servlets/binserv/Attach?table="+ table.getId()+
			"&column="+ col.getId()+"&objectid="+ objectId;
	    	insertRecord(objectId,  null, fileURL, "O", null, pstmt);
	    	att= new Attachment(clientDomain+"/" + table.getRealTableName()+"/"+col.getName(),  objectId+"" );
			att.setAuthor("root");
			att.setVersion(0);
			att.setExtension("jrxml");
	        is=new FileInputStream(file);
	    	attm.putAttachmentData(att, is);
	    	is.close();
		}else{
			adReport=(AdReport)ls.get(0);
			this.objectTemplateId= adReport.getId();
			this.objectTemplateTime= adReport.getModifiedDate().getTime();
			fileURL =ReportTools.getReportFile(objectTemplateId.intValue(), clientDomain);
			this.objectTemplateXML=Tools.readFile(fileURL, "UTF-8");
		}
		}finally{
			try{conn.close();}catch(Exception e){}
		}
}
	/**
	 * ͬ�����б������ģ�壬�������������ÿ��ģ�嶼��������ȷ�Ϲ��̣�
	 * ָ����ģ��(�Ե�ǰ��ģ��Ϊ��ģ�壬����һ�£�����һ��)�Ƿ���ڣ�������ڣ�ȡ���µ�һ��(ID���)��Ϊ�ж����ݣ�
	 * ���ģ������Ϊ�Զ����£���ģ�������޸����ں���ģ����޸�������Ƚϣ����������ģ�壬��������
	 * ���ָ����ģ�岻���ڣ���������
	 * �����Ӧ�ı������޸�ʱ�����ڱ���Ĵ���ʱ�䣬������
	 * 
	 * ע����ǰģ�壬ÿ������ֻȡһ����������ڶ����ȡ���µ��Ǹ�(ID���)
	 * TableManager should be initialized before this call
	 * @throws Exception
	 */
	public void checkAll() throws Exception{
		Table table;
		for(Iterator it= TableManager.getInstance().getAllTables().iterator();it.hasNext();){
			table= (Table)it.next();
			genReports(table);
		}
	}
	/**
	 * ����ģ�壬�������������ÿ��ģ�嶼��������ȷ�Ϲ��̣�
	 * ָ����ģ��(�Ե�ǰ��ģ��Ϊ��ģ�壬����һ�£�����һ��)�Ƿ���ڣ�������ڣ�ȡ���µ�һ��(ID���)��Ϊ�ж����ݣ�
	 * ���ģ������Ϊ�Զ����£���ģ�������޸����ں���ģ����޸�������Ƚϣ����������ģ�壬��������.
	 * 
	 * �����Ӧ�ı������޸�ʱ�����ڱ���Ĵ���ʱ�䣬������
	 * ���ָ����ģ�岻���ڣ���������
	 * 
	 * ע����ǰģ�壬ÿ������ֻȡһ����������ڶ����ȡ���µ��Ǹ�(ID���)
	 * 
	 * @param table
	 * @throws Exception
	 */
	public void genReports(Table rptTable) throws Exception{
        AttachmentManager attm=(AttachmentManager)WebUtils.getServletContextManager().getActor(WebKeys.ATTACHMENT_MANAGER);
        QueryEngine engine= QueryEngine.getInstance();
		TableManager manager= TableManager.getInstance();
		Connection conn= engine.getConnection();
		InputStream is ;Attachment att;int objectId;String fileURL;List ls;
		AdReportDAO dao= new AdReportDAO();
		AdReport adReport;
		
		try{
		PreparedStatement pstmt= conn.prepareStatement(AD_REPORT_INSERT);	
		Table table= manager.getTable("ad_report");
		Column col= manager.getColumn( "ad_report","fileurl");
		
		// list 
		// object master template
		ls=dao.find("from AdReport a where a.AdTableId="+ rptTable.getId()+" and a.ReportType='L' and a.AdClientId="+adClientId+" and a.MasterTemplateId=" + this.listTemplateId +" order by a.Id desc");
		adReport = ls.isEmpty()?null: (AdReport)ls.get(0);
		// when report not exists, or (autoupdate and old than master)
		if( adReport==null || ((adReport.getModifiedDate().getTime() < this.listTemplateTime ||
				adReport.getModifiedDate().getTime() < rptTable.getModifiedDate().getTime())&& 
				Tools.getYesNo(adReport.getAutoUpdate(),true)==true) ){
			objectId=(adReport==null? engine.getSequence("ad_report"): adReport.getId().intValue());
			if( adReport==null){
				fileURL= "/servlets/binserv/Attach?table="+ table.getId()+
					"&column="+ col.getId()+"&objectid="+ objectId;
				insertRecord(objectId,  rptTable, fileURL, "L", this.listTemplateId, pstmt);
			}else adReport.setModifiedDate( new java.util.Date() );
	    	att= new Attachment(clientDomain+"/" + table.getRealTableName()+"/"+col.getName(),  objectId+"" );
			att.setAuthor("root");
			att.setVersion(0);
			att.setExtension("jrxml");
			is= ReportFactory.createListReportStream(this.listTemplateXML, rptTable, clientDomain, rptTable.getName().toLowerCase()+"_l" , Column.PRINT_LIST);
	    	attm.putAttachmentData(att, is);
		}
    	//sublist
		ls=dao.find("from AdReport a where a.AdTableId="+ rptTable.getId()+" and a.ReportType='S' and a.MasterTemplateId=" + this.subListTemplateId +"  and a.AdClientId="+adClientId+" order by a.Id desc");
		adReport = ls.isEmpty()?null: (AdReport)ls.get(0);
		// when report not exists, or (autoupdate and old than master)
		if( adReport==null || ((adReport.getModifiedDate().getTime() < this.subListTemplateTime  ||
				adReport.getModifiedDate().getTime() < rptTable.getModifiedDate().getTime()) &&
				Tools.getYesNo(adReport.getAutoUpdate(),true)==true) ){
			objectId=(adReport==null? engine.getSequence("ad_report"): adReport.getId().intValue());
			if( adReport==null){
				fileURL= "/servlets/binserv/Attach?table="+ table.getId()+
				"&column="+ col.getId()+"&objectid="+ objectId;
				insertRecord(objectId,  rptTable, fileURL, "S", this.subListTemplateId ,pstmt);
			}else adReport.setModifiedDate( new java.util.Date() );
	    	att= new Attachment(clientDomain+"/" + table.getRealTableName()+"/"+col.getName(),  objectId+"" );
			att.setAuthor("root");
			att.setVersion(0);
			att.setExtension("jrxml");
			is= ReportFactory.createListReportStream(this.subListTemplateXML, rptTable, clientDomain, rptTable.getName().toLowerCase()+"_s", Column.PRINT_SUBLIST );
	    	attm.putAttachmentData(att, is);
		}
    	//object
		ls=dao.find("from AdReport a where a.AdTableId="+ rptTable.getId()+" and a.ReportType='O' and a.MasterTemplateId=" + this.objectTemplateId +"  and a.AdClientId="+adClientId+" order by a.Id desc");
		adReport = ls.isEmpty()?null: (AdReport)ls.get(0);
		// when report not exists, or (autoupdate and old than master)
		if( adReport==null || ((adReport.getModifiedDate().getTime() < this.objectTemplateTime   ||
				adReport.getModifiedDate().getTime() < rptTable.getModifiedDate().getTime())&&
				Tools.getYesNo(adReport.getAutoUpdate(),true)==true) ){
			objectId=(adReport==null? engine.getSequence("ad_report"): adReport.getId().intValue());
			if( adReport==null){
	    	fileURL= "/servlets/binserv/Attach?table="+ table.getId()+
			"&column="+ col.getId()+"&objectid="+ objectId;
	    	insertRecord(objectId,  rptTable, fileURL, "O", this.objectTemplateId, pstmt);
			}else adReport.setModifiedDate( new java.util.Date() );
	    	att= new Attachment(clientDomain+"/" + table.getRealTableName()+"/"+col.getName(),  objectId+"" );
			att.setAuthor("root");
			att.setVersion(0);
			att.setExtension("jrxml");
			is= ReportFactory.createObjectReportStream(this.objectTemplateXML, rptTable, clientDomain, rptTable.getName().toLowerCase()+"_o" , 1);
	    	attm.putAttachmentData(att, is);
		}
		}finally{
			try{conn.close();}catch(Exception e){}
		}
	}
	private void insertRecord(int id,  Table table, String fileURL, String reportType, Integer masterTemplateId, PreparedStatement pstmt) throws Exception{
		pstmt.setInt(1, id);
		pstmt.setString(2, clientDomain);
		String rptName=table==null? "��ģ��":table.getDescription(TableManager.getInstance().getDefaultLocale());
		if(reportType.equals("O")){
			rptName +=" - �������ӡģ��";
		}else if(reportType.equals("L")){
			rptName +=" - ��׼�б��ӡģ��";
		}else if(reportType.equals("S")){
			rptName +=" - Ƕ���б��ӡģ��";
		}else{
			throw new NDSException("Unsupported reportType:"+ reportType);
		}
		pstmt.setString(3, rptName );
		pstmt.setString(4, "");//DESCRIPTION
		pstmt.setString(5, fileURL);
		if( table==null)
			pstmt.setNull(6, java.sql.Types.INTEGER );
		else
			pstmt.setInt(6,table.getId());
		pstmt.setString(7, reportType);
		
		if (table==null && masterTemplateId==null ){
			pstmt.setString(8,"Y" );
		}else{
			pstmt.setString(8,"N");
		}

		if (masterTemplateId==null ){
			pstmt.setNull(9,java.sql.Types.INTEGER );
		}else{
			pstmt.setInt(9,masterTemplateId.intValue());
		}
		pstmt.executeUpdate();
	}
}
