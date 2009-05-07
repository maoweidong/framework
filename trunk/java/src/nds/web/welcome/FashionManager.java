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

public class FashionManager extends DefaultManager{
	/**
	 * Welcome page
	 * �����ǰ�û��Ǿ����̣����ж��������ھ��У����û����������������
	 * @return null if no welcome page needed
	 */
	public String getWelcomePageURL(UserWebImpl user){
		if(user==null|| user.isGuest()) return null;
		try{
			int cid=Tools.getInt(user.getSession().getAttribute("$USER_CUSTOMER_ID$"), -1);
			if(cid!=-1){
				// is customer, check fair 
				int cnt=nds.fair.FairManager.getInstance().loadFairs(user).size() ;
				if(cnt>0){
					return this.defaultWelcomeURL;
				}else{
					return "/html/nds/fair/index.jsp";
				}
			}
			return this.defaultWelcomeURL;
		}catch(Throwable t){
			logger.error("Fail to load welcome url for user:"+ user.getUserId(), t);
			return null;
		}
	
	}
	
}
