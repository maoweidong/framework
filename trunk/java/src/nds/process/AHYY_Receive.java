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
/**
 * ͨ�� �̶��˻� �������˻���֧�����������Ѿ���ɿۿ��ҽԺ�ĸ������ʵ���������˻���֧��
 * ��:
 * select * from b_pay where state_kou='Y' and state_fu in ('N','P') 
 *  
 *  ����֧���ɹ����У����޸�state_fu='P', ��Ϊ �տ�Զ����� ���տ 
 *  
 */
public class AHYY_Receive extends SvrProcess
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

		/**
		 * ͨ�� �̶��˻� �������˻���֧�����������Ѿ���ɿۿ��ҽԺ�ĸ������ʵ���������˻���֧��
		 * ��:
		 * select * from b_pay where state_kou='Y' and state_fu in ('N','P') and status=2
		 *  
		 *  ����֧���ɹ����У����޸�state_fu='P', ��Ϊ �տ�Զ����� ���տ 
		 *  ��yz--yymmdd.txt���ϴ�����yymmdd--ahyl.xls������
		 * 
2|0.02
117010152500003115|��|117010152500002797|��ҵ����������������|��ҵ����||0.01|ת��||
117010152500003115|��|117010152500002797|��ҵ����������������|��ҵ����||0.01|ת��||

��һ�У�����|���
�ڶ��п�ʼ�������˺�|�Ƿ���ҵ����|�տ��˺�|�տλ����|�տ�����|����ص�|���|��;|��ע|����С���־����ռ��ɣ�|�տ��к�|

 */ 
	
		Connection conn= null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		QueryEngine engine= QueryEngine.getInstance();
		conn= engine.getConnection();

	    Configurations conf= (Configurations)WebUtils.getServletContextManager().getActor( nds.util.WebKeys.CONFIGURATIONS);
	    String folder= conf.getProperty("ahyy.payment.folder.upload", "e:/act/ahyy/upload");
	    File fd= new File(folder);
	    if(!fd.exists()) fd.mkdirs();
	    
    	SimpleDateFormat df=new SimpleDateFormat("yyMMdd");
	    String fileWithoutFolder="yz--"+ df.format(new java.util.Date())+".txt";
	    String file=folder+"/"+fileWithoutFolder;
		
		try{
			
	        FileOutputStream fos=new FileOutputStream(new File(file));
	        OutputStreamWriter w= new OutputStreamWriter(fos, "UTF-8");
			String lineSep="\r\n";

			String s=(String) engine.doQueryOne("select trim(to_char(count(*))) || '|' || "+ 
					"trim(to_char( sum(TOT_AMT_ACTUAL) )) from b_pay where state_kou='Y' and state_fu in ('N','P') and status=2",conn);
			w.write(s);
			w.write(lineSep);
			
			String accountNo=conf.getProperty("ahyy.payment.account.no");
			String isXinYe="Y".equals(conf.getProperty("ahyy.payment.account.xinye"))?"��":"��";//trim(to_char( amt*100 ,'000000000009'))
			String sql="SELECT '"+accountNo+"|"+ isXinYe+
			"|'|| BANKACCOUNT ||'|' || BANKOWNER || '|' || BANKNAME || '||' || (trim(to_char(TOT_AMT_ACTUAL,'99999999999990.99')))  ||'|ת��|'|| docno||'|||' from b_pay b, c_bpartner c where b.state_kou='Y' and b.state_fu in ('N','P') and b.status=2 and c.id=b.c_bpartner2_id";
			pstmt=conn.prepareStatement(sql);
			rs= pstmt.executeQuery();
			int lineCount=0;
			while(rs.next()){
				w.write( rs.getString(1));
				w.write(lineSep);
				lineCount++;
			}
			w.flush();
			w.close();
			fos.close();
			
			// ���õ���Ϊ�ۿ���
			conn.createStatement().executeUpdate("update b_pay set state_fu='R', msg_fu=null where state_kou='Y' and state_fu in ('N','P') and status=2");

			CommandExecuter cmd= new CommandExecuter(folder+"/log/"+fileWithoutFolder+".log");
			String exec=conf.getProperty("ahyy.payment.upload", "cmd /c e:\\act\\bin\\upload.cmd");
			int err=cmd.run(exec +" "+ file);
			
			this.addLog("���� "+ (lineCount)+ " ��, �ϴ��������:"+ err);
			return "���";
		}finally{
			if(rs!=null)try{rs.close();}catch(Throwable t){}
			if(pstmt!=null)try{pstmt.close();}catch(Throwable t){}
			if(conn!=null)try{conn.close();}catch(Throwable t){}
		}			
		
		
	}
}
