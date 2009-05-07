package nds.control.ejb.command;

import java.rmi.RemoteException;
import java.util.ArrayList;

import nds.control.ejb.Command;
import nds.control.ejb.Trigger;
import nds.control.event.DefaultWebEvent;
import nds.control.event.NDSEventException;
import nds.control.util.ValueHolder;
import nds.query.*;
import nds.util.*;
import nds.schema.*;
import nds.security.Directory;
import nds.security.User;

/**
  ��Column �������ValueInterpeter �� DisplayType ='button' ��ʱ�����ڱ�ʶ�������Ӧ��ť�¼��ķ�����
  1)
  ValueInterpeter����"." ���� "nds.web.ButtonCommandUI" �ӿڵ�ʵ��ʱ����ʾ��ʾ��ť�Ŀ�����Ϊ nds.web.ButtonCommandUI_Impl��
  ���ɵ�������nds.control.ejb.command.ButtonCommand����
  ButtonCommand ���ȼ�鴫����¼����Ƿ��о�����¼����������Ϣ��param='delegator')����������ڣ�����Column��
  ValueInterpeter ��Ϊdelegator��delegator����"."������Ĵ�����̽����� delegator ��ָ����
  �洢���̣����delegator����"."������Ĵ�����̽�����delegator ��ָ�����࣬����Ӧ�ü̳�Command��
  2)
  ��ValueInterpeter ΪButtonCommandUI�ӿڵ�ʵ��ʱ����ʾ��ʾ��ť�Ŀ�����ΪValueInterpeter��ָ�����ࡣ���Կ���
  ��չnds.web.ButtonCommandUI_Impl, �����ϣ������ButtonCommand�Ļ��ƣ������йش洢���̣�����override nds.web.ButtonCommandUI_Impl
  �� getDelegator() �������˷���ȱʡ���� Column.ValueInterpeter
 */

public class ButtonCommand extends Command {
	/**
	 * Security consideration: will use record max action permission(r,w,s) for this command.
	 * �������һ����ȫbug��δ�Ծ����¼�����жϣ���ֻ���ж��˱�����Ȩ�ޡ�������������ܹ����е����ִ��Ȩ��ƥ�䣬
	 * ���磬�����˶�д�ύ���ܣ���ǰ���ж϶�д�ύ
	 * 
	 * @param event must contains 
	 *   objectid* - the record to handle.
	 *   columnid* - column id of button object.
	 *   delegator - optional, if not exists, will use column.ValueInterpeter as that one,
	 *   			 delegator can be store procedure name, or class name which implements 
	 * 				 nds.control.ejb.Command interface.
	 *   			 store procedure should have 3 parameter: objectId, r_code, r_message     
	 * 	 operatorid*-for user, set by system.
	 * @return ValueHolder contains code and message, 
	 * 		code: 
	 * 			0 - only show message
	 * 			1 - show message and refresh current page
	 * 			2 - show message and close current page
	 *          <0 - error
	 */
  public ValueHolder execute(DefaultWebEvent event) throws RemoteException, NDSException {
  	ValueHolder v= new ValueHolder();
  	// nerver throw error, always return code
	//try{
	  	User usr=helper.getOperator(event);
	  	int columnId =Tools.getInt( event.getParameterValue("columnid", true), -1);
	  	int objectId= Tools.getInt( event.getParameterValue("objectid",true), -1);
	  	TableManager manager= TableManager.getInstance();
	  	Column column= manager.getColumn(columnId);
	  	Table table =column.getTable();
	  	event.setParameter("directory",  table.getSecurityDirectory() );
	  	
	  	// user permission should has all action permission set on table
	  	int perm= helper.getPermissions(table.getSecurityDirectory(), usr.getId().intValue());
	  	int maxPerm = 0;
	  	if (table.isActionEnabled(Table.SUBMIT)) maxPerm |= Directory.SUBMIT;
	  	if( table.isActionEnabled(Table.MODIFY)|| 
	  			table.isActionEnabled(Table.ADD)||table.isActionEnabled(Table.DELETE)) maxPerm |= Directory.WRITE;
	  	if ( table.isActionEnabled(Table.QUERY)) maxPerm |= Directory.READ;
	  	
	  	if( ((perm & maxPerm) != maxPerm) &&  !"root".equals(usr.getName())) throw new NDSEventException("Permission denied");
	  	
	  	String className=(String)event.getParameterValue("delegator",true);
	  	if(Validator.isNull(className)) className= manager.getColumn(columnId).getValueInterpeter();
	  	if(Validator.isNull(className)) throw new NDSEventException("Delegator of the button command not set(column="+ column+")");
	  	// class or stored procedure
	    if (className.indexOf('.')<0 ){
	    	// stored procedure
	    	// procedure param :  objectId, r_code, r_msg
	      	ArrayList params=new ArrayList();
	      	params.add(new Integer(objectId));
	      	params.add(usr.getId());
	      	SPResult result =helper.executeStoredProcedure(className, params, true);
	        v.put("message",result.getMessage() ) ;
	        v.put("code", new Integer(result.getCode()));
	    }else{
	    	// to avoid recursion, check class name may not be ButtonCommand
	    	if( "nds.control.ejb.command.ButtonCommand".equalsIgnoreCase(className)){
	    		throw new NDSEventException("Column interpreter can not be 'nds.control.ejb.command.ButtonCommand' for 'button' type");
	    	}
	    	DefaultWebEvent e2= (DefaultWebEvent) event.clone();
	    	e2.setParameter("command", className );
	    	v= helper.handleEvent(e2);
	    }
  	
	/* yfzhu modified 20081015 so transaction can be rolled back
	 * catch(Throwable t){
  		logger.error("Fail to execute ", t);
  		v.put("code","0");
  		v.put("message", "@exception@:"+t.getMessage() ) ;
  	}*/
  	return v;
  	
  }
}