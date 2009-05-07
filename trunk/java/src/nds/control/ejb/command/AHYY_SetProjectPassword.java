package nds.control.ejb.command;

import java.rmi.RemoteException;
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
 * ������Ŀ����
 * ϵͳ����Ա�����ˣ�ÿ���˶���Ҫ��������Ŀ���������д���롣���������������ʹ��CA�����м��ܣ����ܺ�����ĺ�����һ���ϴ��������������ԶԳ��㷨���ܺ󱣴浽������������פ���ڴ档���˵����ĺϲ�����ΪAES�㷨�ļ������ӣ��Կͻ��ϴ��ļ��ܱ��۽��ж��μ��ܡ�
CA ��������������棬ʹ��CFCA_HashMessage ����
�޸Ĺ���Ա�������ý��档�û��������룬����ύʱ��ʹ����Կ��������м��ܣ�����ΪCFCA_HashMessage�������Ľ���ΪASE�㷨���ӣ������������ݿ��С�����ʹ��CFCA_EnvelopeMessage������������жԳ��㷨���ܣ������ı��������ݿ��С�
��̨c_project_ctrl �������޸�

 *
 */

public class AHYY_SetProjectPassword extends Command {
	/**
	 * @param event "password1" �����뽫���öԳ��㷨���ܱ����ں�̨���Է�����Ա����,
	 *  "objectid" "columnid","keycode" - ����Ա��CA��ʶ
	 * "pwdhash" - ��CA��ϣ�������룬�����뽫פ���ڴ���Ϊ��������
	 * 
	 */
  public ValueHolder execute(DefaultWebEvent event) throws RemoteException, NDSException {
  	User usr=helper.getOperator(event);
  	
  	event.setParameter("directory", "C_PROJECT_CTRL_LIST");
  	helper.checkDirectoryWritePermission(event, usr);
  	QueryEngine engine=QueryEngine.getInstance();
  	
	int projectCtrlId=Tools.getInt( event.getParameterValue("objectid",true), -1);
	int columnId=Tools.getInt( event.getParameterValue("columnid",true), -1);
	TableManager manager= TableManager.getInstance();
	int pos= manager.getColumn(columnId).getDescription(event.getLocale()).indexOf('1')>0? 1:2;
	int projectId= Tools.getInt( engine.doQueryOne("select c_project_id from c_project_ctrl where id="+ projectCtrlId),-1);
	
	java.lang.String password1= (String)event.getParameterValue("password1",true);
	java.lang.String password2= (String)event.getParameterValue("password2",true);
	String message=null;

	try{
		ProjectPasswordManager ppm= ProjectPasswordManager.getInsatnce();
		if(ppm.isPasswordSet(projectId, pos)) throw new NDSException("����������");
		
	  	if(!nds.ahyy.Utils.isKeycodeValid(usr.id.intValue(),(String) event.getParameterValue("keycode",true))){
	  		throw new NDSException("@usbkey-error@");
	  	}
	  	String pwdhash=(String) event.getParameterValue("keycode",true);
	  	if(nds.util.Validator.isNull(pwdhash)){
	  		throw new NDSException("���ܴ�������usbkey");
	  	}
		
		if(pos==1) {
			message=ppm.setPassword1(projectId, password1,password2,pwdhash);
		}
		else message=ppm.setPassword2(projectId, password1,password2,pwdhash);
		
	}catch(Throwable t){
  		if(t instanceof NDSException) throw (NDSException)t;
  		logger.error("exception",t);
  		throw new NDSException(t.getMessage(), t);
  	}
	
	//ProjectPasswordManager.ProjectPassword  pp= ppm.getProjectPassword(projectId);
	
	
	/**
	 */

	ValueHolder holder= new ValueHolder();
	holder.put("message", message);
	holder.put("code","0");
	return holder;
  }
}