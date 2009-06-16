package nds.web.welcome;

import javax.servlet.ServletContext;

import nds.control.web.UserWebImpl;
import nds.control.web.WebUtils;
import nds.util.Configurations;
import nds.util.Director;
import nds.util.ServletContextActor;
import nds.util.WebKeys;
import nds.query.*;
import nds.util.*;

public class BardManager extends DefaultManager{
	/**
	 * Welcome page
	 * �û�����'tempcust', ��Ҫ���û����봴���û� /prj/bard/newuser.jsp
	 * �û���users.usertype==2 (������)����δ�������뵥����Ҫ��������д���뵥
	 * @return null if no welcome page needed
	 */
	public String getWelcomePageURL(UserWebImpl user){
		if(user==null|| user.isGuest()) return null;
		try{
			if( user.getUserName().equalsIgnoreCase("tempcust") ) return "/prj/bard/newuser.jsp";
			
			QueryEngine engine =QueryEngine.getInstance();
			int cnt=Tools.getInt(engine.doQueryOne(
					"select users_checkcust("+ user.getUserId()+") from dual"),0);
			if(cnt>0){
				return "/prj/bard/newcust.jsp";
			}else{
				return null;
			}
		}catch(Throwable t){
			logger.error("Fail to load welcome url for user:"+ user.getUserId(), t);
			return null;
		}
	
	}
	
}
