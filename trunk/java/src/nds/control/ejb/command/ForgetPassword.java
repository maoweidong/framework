package nds.control.ejb.command;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import nds.control.ejb.Command;
import nds.control.event.DefaultWebEvent;
import nds.control.event.NDSEventException;
import nds.control.util.ValueHolder;
import nds.query.*;
import nds.util.*;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.*;
/*import com.liferay.portal.ejb.UserManagerUtil;
import com.liferay.portal.ejb.UserManager;
import com.liferay.portal.ejb.UserManagerFactory;

*/
import nds.schema.TableManager;
import nds.security.User;

/**
 * �û����������Ϣ��������ȷ�ش��Լ��趨��������޸�����
 * ��Ҫ�ӽ��淵��������Ϣ��
 * 	email, answer, password1,password2
 */

public class ForgetPassword extends Command {
	/**
     * Whether this command use internal transaction control. For normal command, transaction is controled by
     * caller, yet for some special ones, the command will control transaction seperativly, that is, the command
     * will new transaction and commit that one explicitly
     * @return false if use transaction from caller
     */
    public boolean internalTransaction(){
    	
    	return true;
    }	
	/**
	 * @param event contains "userid" and "password1" and "password2"
	 */
  public ValueHolder execute(DefaultWebEvent event) throws RemoteException, NDSException {
	java.lang.String companyId="liferay.com";
  	
  	
  	/**
  	 * 
 * �û����������Ϣ��������ȷ�ش��Լ��趨��������޸�����
 * ��Ҫ�ӽ��淵��������Ϣ��
 * 	email, answer, password1,password2
  	 */
  	
  	String email= (String)event.getParameterValue("email",true);
  	String answer= (String)event.getParameterValue("answer",true);
  	logger.debug(" Change password when forget by q&a: email="+email+", answer="+answer);
  	
  	int userid= getUserId( email, answer);
	if( userid ==-1){
		throw new NDSException("@operate-fail@");
	}
	java.lang.String password1= (String)event.getParameterValue("password1",true);
	java.lang.String password2= (String)event.getParameterValue("password2",true);
	boolean passwordReset=false;
	/**
	 * д��Liferay ϵͳ
	 */
	try{
		String userd= helper.getUserDomainName(userid);
		//logger.debug("beign update user " + userd + " with password:"+ password1);
		UserLocalServiceUtil.updatePassword(userd, password1,
				password2, passwordReset);
		/*UserManager userManager = UserManagerFactory.getManager();
		
		userManager.updateUser(userd, password1,
			password2, passwordReset);*/
	}catch(Exception e){
		logger.debug("Error when update password:", e);
		String msg;
		if( e instanceof UserPasswordException){
			UserPasswordException ex= (UserPasswordException)e;
		
		if( ex.getType() ==UserPasswordException.PASSWORD_ALREADY_USED){
			msg= "�����Ѿ���ʹ�ù���,�����!";
		}else if( ex.getType()== UserPasswordException.PASSWORD_INVALID){
			msg= "������Ч�������!";
		}else if ( ex.getType()==UserPasswordException.PASSWORDS_DO_NOT_MATCH){
			msg= "�������벻ƥ��,����������!";
		}else msg=ex.getMessage();
		}else
			msg= e.getMessage();
		throw new NDSException("�޷��޸��û����Ż���ȫϵͳ!"+ msg);
	}
	ValueHolder holder= new ValueHolder();
	holder.put("message", "�����޸ĳɹ�, ����<a href='/login.jsp'>��¼�����¼</a>");
	holder.put("code","0");
	return holder;
  }
  /**
   * 
   * @param email
   * @param answer
   * @return -1 if anything error
   */
  private int getUserId(String email, String answer){
	  	
	  	
		PreparedStatement pstmt=null;
		Connection conn=null;
		java.sql.ResultSet rs=null;
		try{
		  	QueryEngine engine=QueryEngine.getInstance();
			conn= engine.getConnection();
			pstmt= conn.prepareStatement("select u.id from users u, u_user_quizz q where q.ownerid= u.id and u.email=? and q.answer=?" );
			pstmt.setString(1,email);
			pstmt.setString(2,answer);
			rs=pstmt.executeQuery();
			if(rs.next()) return rs.getInt(1);
			return -1;
  		}catch(Throwable t){
  			logger.error("Fail to get user with email="+email+", answer="+answer, t);
  			return -1;
  		}finally{
  			try{rs.close();}catch(Exception e){}
  	  		try{pstmt.close();}catch(Exception e){}
  	        try{conn.close();}catch(Exception e){}

  		}
	  
  }
}