package nds.control.ejb.command;

import java.rmi.RemoteException;
import java.util.ArrayList;

import nds.control.ejb.Command;
import nds.control.event.DefaultWebEvent;
import nds.control.event.NDSEventException;
import nds.control.util.SecurityUtils;
import nds.control.util.ValueHolder;
import nds.control.web.UserWebImpl;
import nds.control.web.WebUtils;
import nds.query.*;
import nds.util.*;




/*import com.liferay.portal.ejb.UserManagerUtil;
import com.liferay.portal.ejb.UserManager;
import com.liferay.portal.ejb.UserManagerFactory;

*/
import nds.schema.TableManager;
import nds.security.Directory;
import nds.security.User;

/**
 * ��MPIO��Ӧ��δ���������Ѿ������˷�Ʊ��ʱ�򣬴�������ɶ�Ӧ�ĺ�巢Ʊ
 * ����δ���ɷ�Ʊ��MPIO��ֱ�ӵ������ݿ��M_INOUT_CLOSE_PLAN 
 */

public class CloseMPIO extends Command {
	/**
	 * @param event
	 *    columnid
	 *    objectid
	 *    invoicetype - the invoice type that user created, this must be reverse type of original invoice created
	 *                  that will be checked in db procedure 
	 */
  public ValueHolder execute(DefaultWebEvent event) throws RemoteException, NDSException {
  	User usr=helper.getOperator(event);
  	
  	int columnId =Tools.getInt( event.getParameterValue("columnid",true), -1);
  	int objectId= Tools.getInt( event.getParameterValue("objectid",true), -1);

  	if(!SecurityUtils.hasObjectPermission(usr.getId().intValue(), usr.getName(), TableManager.getInstance().getColumn(columnId).getTable().getName(), objectId, nds.security.Directory.WRITE, event.getQuerySession())) 
  		throw new NDSException("@no-permission@");
  	
  	String invoiceType=(String) event.getParameterValue("invoicetype");
  	if (Validator.isNull(invoiceType)) throw new NDSException("invoicetype not set");
  	
  	ArrayList al=new ArrayList();
  	al.add(new Integer(objectId));
  	al.add(invoiceType);
  	
  	SPResult result= helper.executeStoredProcedure("M_INOUT_CLOSE_PLAN", al, true  );
  	
  	ValueHolder holder= new ValueHolder();
  	if(result.isSuccessful() ){
  		holder.put("message",result.getMessage() ) ;
  		holder.put("next-screen", "/html/nds/info.jsp");
        
    }else{
        throw new NDSEventException(result.getDebugMessage());
    }  	
	return holder;
  }
}