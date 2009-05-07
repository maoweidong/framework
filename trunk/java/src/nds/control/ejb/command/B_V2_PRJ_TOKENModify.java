package nds.control.ejb.command;
import org.json.*;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;

import nds.control.ejb.Command;
import nds.control.event.DefaultWebEvent;
import nds.control.event.NDSEventException;
import nds.control.util.ValueHolder;
import nds.query.*;
import nds.schema.*;
import nds.util.*;

import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.*;
/*import com.liferay.portal.ejb.UserManagerUtil;
import com.liferay.portal.ejb.UserManager;
import com.liferay.portal.ejb.UserManagerFactory;

*/
import nds.security.User;
import nds.ahyy.*;


/**
 * 
 * ���ܱ���
 * �������������÷�����������ʱ����Ҫ���ü۸�Ϊ0�����ҽ��Ƿ��������Ϊ��
 * �۸��������Ϊ�������ϴα��ۣ�����ϴα��۲�Ϊ��
 *  ���в��� "keycode" �Ǽ���ʱ��ı���Ҫ���뵱ǰ�û���keycode һ��

 **/

public class B_V2_PRJ_TOKENModify extends Command {
	
	private double getDouble(Object str, double defaultValue) {
    	if(str==null) return defaultValue;
        try {
            return Double.parseDouble(str.toString());
        } catch(Exception e) {
        }
        return defaultValue;
    }
	/**
	 * @param event
	 * 
	 */
  public ValueHolder execute(DefaultWebEvent event) throws RemoteException, NDSException {
	  logger.debug(event.toDetailString());
  	User usr=helper.getOperator(event);
  	int objectId =Tools.getInt(event.getParameterValue("id",true),-1);//B_PRJ_TOKEN
  	
  	event.setParameter("directory", "B_V2_PRJ_TOKEN_LIST");
  	if(!nds.control.util.SecurityUtils.hasObjectPermission(usr.id.intValue(), usr.name, 
  			"B_V2_PRJ_TOKEN", objectId, nds.security.Directory.WRITE, event.getQuerySession())){
  		throw new NDSException("@no-permission@");
  	}
  	JSONArray row= (JSONArray)event.getParameterValue("JSONROW");
  	if(row==null)throw new NDSException("�����б�������л����޸�ģʽ���м۸��");
  	
  	QueryEngine engine=QueryEngine.getInstance();
/*  	Object po= event.getParameterValue("price"); // ���ܵļ۸�
  	String price=null;
  	if(po !=null ) price= po.toString();
  	double currentPrice=0;
  	try{
  		if(price!=null) currentPrice=Double.parseDouble(price);
  		else throw new NDSException("��������۸�");
  	}catch(NumberFormatException nfe){
  		throw new NDSException("�۸����Ϊ������:"+ price);
  	}
  	if(currentPrice<0) throw new NDSException("�۸���С��0");
  	else if(currentPrice > 100000000) throw new NDSException("�۸��ܴ���1��");
*/
  	
  	TableManager manager= TableManager.getInstance();
		
	PreparedStatement stmt2=null;
	Connection conn= engine.getConnection();
	ProjectPasswordManager.ProjectPassword pp=null;
//	double lastPrice =getDouble( engine.doQueryOne("select PRICELAST from B_PRJ_TOKEN where id="+ objectId, conn),Double.MAX_VALUE);

//	if(currentPrice >lastPrice) throw new NDSException("���۲��ø����ϴα���("+lastPrice+")");
	
	try{
	  	Object pomd5= row.get(row.length()-1); 

		int projectId= Tools.getInt( engine.doQueryOne("select c_project_id from B_PRJ_TOKEN where id="+ objectId, conn),-1);
		ProjectPasswordManager ppm= ProjectPasswordManager.getInsatnce();
		if(!ppm.isPasswordSet(projectId, 1 ) || !ppm.isPasswordSet(projectId, 2) ) 
			throw new NDSException("��Ŀͳһ��������δ���ã�����������ϵͳ����Ա.");
		
		/**
		 * ��js/portalcontrol.js�Ͻ��������������,  ÿ�����еĵ����ڶ���Ϊ�û���keycode,��ֹ�ô���key
		 */
		String keyCode=( row.optString(row.length()-2, "") );
		String userKey=(String) engine.doQueryOne("select EMAILVERIFY from users where id="+ usr.id);
		if(nds.util.Validator.isNull(userKey) ){
			throw new NDSException("��ǰ�û�("+ usr.name+")δ����CA��ʶ���޷����ܱ���");
		}
		if(nds.util.Validator.isNull(keyCode) ){
			throw new NDSException("δ�ڱ���ʱʹ��USBKEY���޷����ܱ���");
		}
		if(!keyCode.equals(userKey)){
			throw new NDSException("��ǰ���۲��õ�USBKEY�����û�������KEY����ʹ����ȷ��USBKEY");
		}
		pp= ppm.getProjectPassword(projectId);
		pp.generateCipher(); // make sure cipher exists
		String encodedPrice=pp.encrypt( (String)event.getParameterValue("price"));
		/**
		 * �� js/portalcontrol.js�Ͻ�������������ã��������������ɼ��ܣ�����ǰ�Լ۸����md5������pricehash��
		 * pricehash =md5("M"+ b_prj_token.id + b_prj_token.price), �����۸�һ�µ������ɳ�����pricehashҲ�ǲ�һ�µġ� 
		 * �ڽ��ܺ󽫶Լ۸���бȽϣ������һ�£�������
		 */
		String pricehash=( row.optString(row.length()-1, "") );
		
		String sql="update b_prj_token set pricecoded=?, pricehash=?,price=?, abort_flag=?, gupinfo=?,state_bidprice='Y', modifieddate=sysdate, modifierid=? where id=?";
		logger.debug(sql+"(pricecoded="+ encodedPrice+", id="+ objectId+")");
		
		stmt2= conn.prepareStatement(sql);
		int c=1;
		stmt2.setString(c++,encodedPrice);
		stmt2.setString(c++,pricehash);
		stmt2.setNull(c++,java.sql.Types.FLOAT );
		String abort_flag= (String) event.getParameterValue("abort_flag");
		String giveupInfo= (String) event.getParameterValue("GUPINFO");
		logger.debug("abort_flag="+abort_flag+", giveupInfo="+giveupInfo);
		if(abort_flag ==null) stmt2.setNull(c++, java.sql.Types.VARCHAR);
		else stmt2.setString(c++, abort_flag);

		if(giveupInfo ==null) stmt2.setNull(c++, java.sql.Types.VARCHAR);
		else stmt2.setString(c++, giveupInfo);
		
		stmt2.setInt(c++,usr.id.intValue());
		stmt2.setInt(c++,objectId);
		
		stmt2.executeUpdate();

		// CALL DB PROC
		ArrayList params=new ArrayList();
		params.add(new Integer(objectId));
		engine.executeStoredProcedure("B_V2_PRJ_TOKEN_AM", params, false, conn);
		
	}catch(Throwable t){
  		if(t instanceof NDSException) throw (NDSException)t;
  		logger.error("exception",t);
  		throw new NDSException(t.getMessage(), t);
  	}finally{
        try{stmt2.close();}catch(Exception ea){}
        try{conn.close();}catch(Exception e){}
  	} 
	
	ValueHolder holder= new ValueHolder();
	holder.put("message", "���õļ۸��ѱ����ܱ�����ϵͳ��");
	holder.put("code","0");
	return holder;
  }
}