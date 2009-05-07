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
 * �жϵ�ǰ�ǳ�������ʱ����audit_state-1������audit_state+1
 */

public class RequestOne extends Command{

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
        int init_state=Tools.getInt( table.getColumn("audit_state").getDefaultValue(),3);
        int audit_state= Tools.getInt( engine.doQueryOne("select audit_state from "+ tableName+
        		" where id="+pid), 3);
        if (audit_state== init_state) audit_state--;
        else audit_state++;
    	ValueHolder v=null;
    	try{
	    	// update
	    	Vector sqls= new Vector();
	    	
	    	sqls.addElement("update "+tableName+" set audit_state="+ audit_state+ ",modifierid="+ userId+ ", modifieddate=sysdate where id="+pid);
	    	engine.doUpdate(sqls);
        	v=new ValueHolder();
            v.put("message","״̬����Ϊ�����." ) ;
            v.put("next-screen", "/html/nds/info.jsp");
            return v;
        }catch(Exception e){
            if(e instanceof NDSEventException )throw (NDSEventException)e;
            else throw new NDSEventException(e.getMessage() );
        }

    }
}