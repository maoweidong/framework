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
 * ʵ���������ӿڵĿۿ����룬�� ������4������֧��ϵͳ�̻��ӿ�V1.0.doc��7.7.2��	������������Ϣ�ļ�
 * 
 * ������������Ϣ�ļ�Ϊ�ı���ʽ��һ����¼ռһ�У���һ����¼Ϊ�ܼƼ�¼��������ۿ���Ϣ��¼�ȡ�
 * 
 * ����ʵ��
 * ׼�����ݣ�
 * 	���������ύ����δ�ۿ�ġ��Ѹ�� ���ݰ�����ϲ����� B_PAY_SUM ���ݣ����ݸ�ʽ��
 * 		���ݱ�ţ�����ҽԺ���ܽ��
 * 	B_PAY_SUM ��Ϣ��д������B_PAY ��
 * 
 *  ���ڿۿ�ɹ��ļ�¼�����е�B_PAY Ҳ���������Ͽۿ�ɹ�����������Ϊδ֧������Ӧ�̡�Ȼ���и�����������Ӧ�̵ĸ���
 *  ���ڿۿ�ɹ��ļ�¼�����е�B_PAYҲ���������Ͽۿ�ɹ����´��ٸ�
 *  B_PAY_SUM ���ڼ�¼����ǰ����ʾ
 *  
 */
public class AHYY_Payment extends SvrProcess
{
	/**
	 * ?	���۷���Ϣ��¼�ʵ�����
	 */
	private final static String BILL_TYPE="00";
	/**
	 * ?	�̻���־�루8λ�����̻�������Ϣ��
	 */
	private String ahyyCode;
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
		// 	load query into cache file directly
		//int userId= this.getAD_User_ID();
		//User user= SecurityUtils.getUser(userId); 
		
		Connection conn= null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		QueryEngine engine= QueryEngine.getInstance();
		conn= engine.getConnection();

	    Configurations conf= (Configurations)WebUtils.getServletContextManager().getActor( nds.util.WebKeys.CONFIGURATIONS);
	    String folder= conf.getProperty("ahyy.payment.folder.upload", "e:/act/ahyy/upload");
	    File fd= new File(folder);
	    if(!fd.exists()) fd.mkdirs();
	    ahyyCode= conf.getProperty("ahyy.payment.code");
	    if(ahyyCode==null || ahyyCode.length()!=8) throw new NDSException("Wrong code for bank payment interface");
	    
    	SimpleDateFormat df=new SimpleDateFormat("yyMMdd");
	    String fileWithoutFolder="MERFEE"+ ahyyCode+ df.format(new java.util.Date());
	    String file=folder+"/"+fileWithoutFolder;
		
		try{
			/**
			 * ׼�������ܵ�
			 */
			int cnt=0;
			
			nds.query.SPResult sr=engine.executeStoredProcedure("b_pay_sum_generate", new ArrayList(),true, conn);
			cnt=sr.getCode(); 
			if (cnt>0){
			
		        FileOutputStream fos=new FileOutputStream(new File(file));
		        OutputStreamWriter w= new OutputStreamWriter(fos, "UTF-8");
				String lineSep="\n";
				//�ܼƼ�¼��
		/*
		?	���۷���Ϣ�ܼƼ�¼��ʽ
		���	������	����	����
		1	��¼��ʶ	3	���FEE��
		2	�̻�ƽ̨��������	11	�󿿣��Ҳ��ո񣻱������ļ������̻���־����ͬ
		3	�ļ���������	4	�������ļ�����������ͬ
		4	�ܼƱ���	10	�ҿ�����0���磬����Ϊ34����¼����Ϊ��0000000034����
		5	Ӧ�ɽ������	16	�ҿ�����0������С���㣻�磬���Ϊ3489.45Ԫ����Ϊ��0000000000348945����
		6	�س���	1	0x00��0x0A
		 */		
				// ��δ�ۿ�,��ۿ�ʧ�ܵĿۿ���ܵ������ܵ�ÿ��ֻ��������һ��
				String s=(String) engine.doQueryOne("select 'FEE"+ formatSpace(this.ahyyCode,11)+"' || to_char(sysdate,'YYMMDD') || "+
						"trim(to_char(count(*),'0000000009')) || "+ 
						"trim(to_char( sum(amt)*100 ,'0000000000000009')) from b_pay_sum where state in ('N','P')",conn);
				w.write(s);
				w.write(lineSep);
	/*
	1	�ʵ�����	2	01���ֻ����ѣ�02���̻��ѣ�03��ˮ�ѣ�04����ѣ�05��ú����06���籣��07��С��ͨ���ѣ�08�����ÿ�����ȡ�
	2	�ʵ�����	20	�󿿣��Ҳ��ո� (������п����ţ����迨��Ϊ12λ��
	3	���˽ɷѽ��	12	�ҿ�����0������С���㣻�磬���Ϊ124.20Ԫ����Ϊ��000000012420����
	4	���������򳤶�	3	000����ʾû�и���������
		ʵ�ʳ��ȣ���ʾ�и����������ҿ�����0���磬021Ϊ21��λ����������
	5	����������	200	
	6	�س���	1	0x00��0x0A
	 */			 
				// RE0807260001 12λ����8��0 ���ʵ�����
				pstmt=conn.prepareStatement("SELECT '"+BILL_TYPE+
						"'|| billno || trim(to_char( amt*100 ,'000000000009'))||'000'  from b_pay_sum where state in ('N','P')");
				rs= pstmt.executeQuery();
				while(rs.next()){
					w.write( rs.getString(1));
					w.write(lineSep);
					cnt++;
				}
				w.close();
				
				// �����ܵ�Ϊ�ۿ���
				conn.createStatement().executeUpdate("update b_pay_sum set state='R' where state in ('N','P')");
	
				CommandExecuter cmd= new CommandExecuter(folder+"/log/"+fileWithoutFolder+".log");
				String exec=conf.getProperty("ahyy.payment.upload", "e:/act/bin/upload.cmd");
				int err=cmd.run(exec +" "+ file);
			}

			this.addLog("���� "+ (cnt)+ " ��");
			return "���";
		}finally{
			if(rs!=null)try{rs.close();}catch(Throwable t){}
			if(pstmt!=null)try{pstmt.close();}catch(Throwable t){}
			if(conn!=null)try{conn.close();}catch(Throwable t){}
		}			
		
		
	}
	private final static String ZERO="0000000000000000000000000000000000000000";
	private final static String SPACE="                                       ";
	/***
	 * �Ҳ��ո��ܳ��ȴﵽlen Ҫ��
	 * @param s
	 * @param len
	 * @return
	 * @throws NDSException
	 */
	private String formatSpace(String s, int len)throws NDSException{
		if(s==null) return SPACE.substring(0, len); 
		int l= s.length();
		if(l>len) throw new NDSException(s+" is too long (max="+ len+")");
		if( l==len ) return s;
		return s+SPACE.substring(0, len-l);
	}
	/**
	 * ��� s �ĳ���< len����ͨ���趨0������
	 * @param s
	 * @param len ���ô�Լ40
	 * @return
	 */
	private String format(String s, int len) throws NDSException{
		if(s==null) return ZERO.substring(0, len); 
		int l= s.length();
		if(l>len) throw new NDSException(s+" is too long (max="+ len+")");
		if( l==len ) return s;
		return ZERO.substring(0, len-l)+s;
	}
}
