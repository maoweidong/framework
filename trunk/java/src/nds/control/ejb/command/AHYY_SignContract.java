package nds.control.ejb.command;

import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import nds.control.ejb.Command;
import nds.control.event.DefaultWebEvent;
import nds.control.event.NDSEventException;
import nds.control.util.ValueHolder;
import nds.process.ProcessUtils;
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
 * �Ժ�ͬ��������ǩ��
 * 
 * ����ǩ���ĺ�ͬ��ϢB_CONTRACT ������ID, ���ţ��׷����ҷ�����ͬ��ʼ����ͬ�����������ˣ�����ʱ�䣬�޸��ˣ��޸�ʱ�䡣��Ϊ��ͬժҪ
 * 
 * �����ֶΣ��׷�ǩ���ˣ��ҷ�ǩ���ˣ��׷�ǩ�����ڣ��ҷ�ǩ�����ڣ��׷�ǩ����Ϣ���ҷ�ǩ����Ϣ
 * 
 * �ں�ͬ�����ӡ��׷�ǩ������ť��ǩ����ť�����ǵ�ǰ�û�Ϊ�׷����ҷ��û����Ҽ׷����ҷ�δǩ�����������ʾ�������ǩ�������������棬Ҫ���û�ȷ�ϣ�
 * ����ʵ��Ϊ�˽���ͬժҪ��ȡ���ͻ��ˣ����ȷ�Ϻ��ÿͻ���֤�����ǩ��������ǩ����Ϣ�ϴ����������޸�ǩ����¼
 * 
 * У���û���emailverify �봫���keycode һ��
 */

public class AHYY_SignContract extends Command {
	/**
	 * @param event
	 * 		objectid - b_contract.id
	 * 		aparty	- "Y"|"N" is A party(hospital) or not
	 * 		quit	- "Y"|"N" sign or quit sign
	 * 		signature - when quit="N" for signature
	 * 		keycode	- users.emailverify
	 */
  public ValueHolder execute(DefaultWebEvent event) throws RemoteException, NDSException {
  	User usr=helper.getOperator(event);
  	
  	event.setParameter("directory", "B_CONTRACT_LIST");
  	
  	int objectId=Tools.getInt( event.getParameterValue("objectid",true), -1);
	boolean aParty="Y".equals( event.getParameterValue("aparty",true));
	String sign= (String)event.getParameterValue("signature",true);
	boolean quit="Y".equals( event.getParameterValue("quit",true));// sign or quit
  	
  	if(!nds.control.util.SecurityUtils.hasObjectPermission(usr.id.intValue(),usr.name,"B_CONTRACT", objectId, 3, event.getQuerySession())){
  		throw new NDSException("@no-permission@");
  	}
  	
  	QueryEngine engine=QueryEngine.getInstance();
  	
	TableManager manager= TableManager.getInstance();
	
	Connection conn= engine.getConnection();
	PreparedStatement pstmt=null;
	ValueHolder holder= new ValueHolder();
	int cnt=0;
	try{
	  	if(!nds.ahyy.Utils.isKeycodeValid(usr.id.intValue(),(String) event.getParameterValue("keycode",true))){
	  		throw new NDSException("@usbkey-error@");
	  	}
		//String contractInfo= (String) engine.doQueryOne("select b_contract_abstract("+ objectId+") from b_contract where id="+objectId, conn);
		if(quit){
			
			if(aParty){
				pstmt= conn.prepareStatement("update b_contract set a_signer_id=null, a_sign_date=null, a_sign_info=null,STATE_HOSPITAL='N' where id=? and status=1 and a_sign_info=?" );
			}else{
				pstmt= conn.prepareStatement("update b_contract set b_signer_id=null, b_sign_date=null, b_sign_info=null,STATE_RETAILER='N' where id=? and status=1 and b_sign_info=?" );
			}
			pstmt.setInt(1,objectId);
			pstmt.setString(2,sign);
			cnt=pstmt.executeUpdate();
			if(cnt <1){
				holder.put("message", "@reload-contract-as-update-not-succeed@");
			}else{
				holder.put("message", "@complete@");
			}
		}else{
			if(Validator.isNull("sign")){
				throw new NDSException("Need signature");
			}
			if(aParty){
				pstmt= conn.prepareStatement("update b_contract set a_signer_id=?, a_sign_date=sysdate, a_sign_info=?,STATE_HOSPITAL='Y' where id=? and a_sign_info is null" );
			}else{
				pstmt= conn.prepareStatement("update b_contract set b_signer_id=?, b_sign_date=sysdate, b_sign_info=?,STATE_RETAILER='Y' where id=? and b_sign_info is null" );
			}
			pstmt.setInt(1,usr.id.intValue());
			pstmt.setString(2,sign);
			pstmt.setInt(3,objectId);
			cnt=pstmt.executeUpdate();
			if(cnt==1){
				// mark b_contract unmodifiable if both signed
				engine.executeUpdate("update b_contract set status=2 where id="+objectId + " and STATE_HOSPITAL='Y' and STATE_RETAILER='Y'");
			}
			if(cnt <1){
				holder.put("message", "@reload-contract-as-update-not-succeed@");
				
			}else{
				holder.put("message", "@complete@");
			}
		}
		

	}catch(Throwable t){
  		logger.error("exception",t);
  		if(t instanceof NDSException) throw (NDSException)t;
  		else throw new NDSException(t.getMessage(), t);
  	}finally{
  		try{pstmt.close();}catch(Exception e){}
        try{conn.close();}catch(Exception e){}
  	} 
	
	holder.put("code","0");
	return holder;
  }
}