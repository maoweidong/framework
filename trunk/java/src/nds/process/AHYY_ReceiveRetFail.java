/*
 * Agile Control Technologies Ltd,. CO.
 * http://www.agileControl.com
 */
package nds.process;

import nds.control.util.SecurityUtils;
import nds.control.web.WebUtils;
import nds.query.QueryEngine;
import nds.query.QueryUtils;
import nds.security.User;
import nds.util.*;
import nds.schema.*;

import java.text.*;
import java.sql.*;
import java.util.*;
import java.io.*;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
/**
 * ͨ�� �̶��˻� �������˻���֧�����������Ѿ���ɿۿ��ҽԺ�ĸ������ʵ���������˻���֧��
 * ��:
 * select * from b_pay where state_kou='Y' and state_fu in ('N','P') 
 *  
 *  ����֧���ɹ����У����޸�state_fu='P', ��Ϊ �տ�Զ����� ���տ 
 *  
 */
public class AHYY_ReceiveRetFail extends SvrProcess
{
	
	/**
	 *  Parameters:
	 *    no 
	 */
	protected void prepare()
	{
		
		/*ProcessInfoParameter[] para = getParameters();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
		}*/
	}	//	prepare	
	


	/**
	 *  Perrform process.
	 *  @return Message that would be set to process infor summary (no use currently)
	 *  @throws Exception if not successful
	 */
	protected String doIt() throws Exception{

		Connection conn= null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		QueryEngine engine= QueryEngine.getInstance();
		conn= engine.getConnection();

	    Configurations conf= (Configurations)WebUtils.getServletContextManager().getActor( nds.util.WebKeys.CONFIGURATIONS);
	    String folder= conf.getProperty("ahyy.payment.folder.download", "e:/act/ahyy/download");
	    File fd= new File(folder);
	    if(!fd.exists()) fd.mkdirs();
	    //��yz--yymmdd.txt���ϴ�������yymmdd--ylcg.xls;ʧ���ļ�yymmdd--ylsb.xls
    	SimpleDateFormat df=new SimpleDateFormat("yyMMdd");
	    String ftpFile=df.format(new java.util.Date())+"--ylsb.xls";
	    String file=folder+"/"+ftpFile;
		StringBuffer sb=new StringBuffer();
		String lineSep =Tools.LINE_SEPARATOR;
		try{
			//�����ļ�
			CommandExecuter cmd= new CommandExecuter(folder+"/log/"+ftpFile+".log");
			String exec=conf.getProperty("ahyy.payment.download", "e:/act/bin/download.cmd");
			int err=cmd.run(exec +" "+ ftpFile);
			if(err!=0){
				throw new NDSException("Error(code="+ err+") when doing "+exec +" "+ ftpFile);
			}
			if(!(new File(file)).exists()){
				throw new NDSException("File not download when doing "+exec +" "+ ftpFile);
			}
			//BANKACCOUNT ||'|' || BANKOWNER || '|' || BANKNAME
			String[][] data= AHYY_ReceiveRetSuccess.convertExcelToArray(file,log);
			pstmt=conn.prepareStatement("update b_pay set state_fu=?, msg_fu =? where docno=? "+
					"and TOT_AMT_ACTUAL=? and state_kou='Y' and state_fu='R' and status=2 and "+
					"exists (select 1 from c_bpartner c where c.id=b_pay.C_BPARTNER2_ID and "+
					"c.BANKACCOUNT=? and c.BANKOWNER=? and c.BANKNAME=?)");
			for(int i=0;i<data.length;i++){
				/*
	 * �տ��˺�	�ܽ��	�տ���	�����ַ	������	��;	��ע	��ʽ	�տ��к�	������
4904720010200060789	79.2	���		�й����г����з���	08-21,898340272980028,0.80		��ͨ	104362004010	�ɹ�
				 * 
				 */
				String billNo= data[i][6];// ��ע �����ǵĵ���
				if(Validator.isNull(billNo)) continue;
				double amt= Double.valueOf(data[i][1]).doubleValue();
				String BANKACCOUNT=data[i][0];
				String BANKOWNER=data[i][2];
				String BANKNAME=data[i][4];
					
				//java.math.BigDecimal bd=new java.math.BigDecimal(amt);
				pstmt.setString(1,"P");
				pstmt.setString(2,"����˻�����");
				pstmt.setString(3, billNo.trim());
				
				pstmt.setDouble(4, amt);
				pstmt.setString(5,BANKACCOUNT.trim() );
				pstmt.setString(6,BANKOWNER.trim());
				pstmt.setString(7,BANKNAME.trim());
				
				int ret= pstmt.executeUpdate();
				if(ret!=1){
					sb.append("line "+(i+1)+" updated "+ ret+" lines").append(lineSep);
				}
			}
			try{
				log.debug("Move "+ file +" to "+ folder+"/log/"+ftpFile);
			(new File(file)).renameTo(new File(folder+"/log/"+ftpFile));
			}catch(Throwable t){
				log.error("Fail to move "+ file +" to "+ folder+"/log/"+ftpFile, t);
			}
			
			Vector vet=new Vector();
			String log=sb.toString();
			this.log.debug(log);
			this.addLog(log);
			return "���";
		}finally{
			if(rs!=null)try{rs.close();}catch(Throwable t){}
			if(pstmt!=null)try{pstmt.close();}catch(Throwable t){}
			if(conn!=null)try{conn.close();}catch(Throwable t){}
		}			
		
		
	}
	
}
