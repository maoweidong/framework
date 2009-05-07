package nds.control.ejb.command;

import java.rmi.RemoteException;
import java.util.ArrayList;

import nds.control.ejb.Command;
import nds.control.event.DefaultWebEvent;
import nds.control.event.NDSEventException;
import nds.control.util.ValueHolder;
import nds.query.*;
import nds.util.*;




/*import com.liferay.portal.ejb.UserManagerUtil;
import com.liferay.portal.ejb.UserManager;
import com.liferay.portal.ejb.UserManagerFactory;

*/
import nds.security.User;

/**
 * Reclaim asset
 *
 */

public class ReclaimAsset extends Command {
	/**
	 *  ���ô洢���� OA_ASSET_USE_RECLAIM, �ṩ��������ǰ�����ˣ�����id
	 */
  public ValueHolder execute(DefaultWebEvent event) throws RemoteException, NDSException {
  	User usr=helper.getOperator(event);
  	
  	int columnId =Tools.getInt( event.getParameterValue("columnid"), -1);
  	int objectId= Tools.getInt( event.getParameterValue("objectid"), -1);
	
  	ArrayList al=new ArrayList();
  	al.add(new Integer(objectId));
  	al.add(usr.getId());
  	helper.executeStoredProcedure("OA_ASSET_USE_RECLAIM", al, false  );
	ValueHolder holder= new ValueHolder();
	holder.put("message", "�ɹ��ջأ�");
	holder.put("code","0");
	return holder;
  }
}