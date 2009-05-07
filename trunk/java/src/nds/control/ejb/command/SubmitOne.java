package nds.control.ejb.command;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;

import nds.control.ejb.Command;
import nds.control.event.DefaultWebEvent;
import nds.control.event.NDSEventException;
import nds.control.util.ValueHolder;
import nds.control.web.UserWebImpl;
import nds.query.QueryEngine;
import nds.query.SPResult;
import nds.schema.Table;
import nds.schema.TableManager;
import nds.util.JNDINames;
import nds.util.NDSException;
import nds.util.Tools;
/**
 * ����һ��¼��status����Ϊ2��Ҫ������event����ʱ����Ӧ����spName= tableName+"Submit"��
 */

public class SubmitOne extends Command{

    /**
     */
    public ValueHolder execute(DefaultWebEvent event) throws NDSException ,RemoteException{
        Integer pid = new Integer(Tools.getInt(event.getParameterValue("id") ,-1));
        int userId=helper.getOperator(event).getId().intValue()  ;
        String spName = (String)event.getParameterValue("spName"); //"TonyTest";
        String tableName = spName.substring(0,spName.indexOf("Submit") ) ;
        Table table=TableManager.getInstance().getTable(tableName);
        tableName=table.getRealTableName();
  
        QueryEngine engine=QueryEngine.getInstance();
    	ValueHolder v=null;
    	try{
	    	// update
	    	Vector sqls= new Vector();
	    	sqls.addElement("update "+tableName+" set status=2,modifierid="+ userId+ ", modifieddate=sysdate where id="+pid);
	    	engine.doUpdate(sqls);
        	v=new ValueHolder();
            v.put("message","״̬����Ϊ���ύ." ) ;
            v.put("next-screen", "/html/nds/info.jsp");
            return v;
        }catch(Exception e){
            if(e instanceof NDSEventException )throw (NDSEventException)e;
            else throw new NDSEventException(e.getMessage() );
        }

    }
}