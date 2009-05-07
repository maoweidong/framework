package nds.control.ejb.command;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.*;

import nds.control.ejb.Command;
import nds.control.ejb.command.tree.TreeNodeHolder;
import nds.control.ejb.command.tree.TreeNodeManager;
import nds.control.event.DefaultWebEvent;
import nds.control.event.NDSEventException;
import nds.control.util.NavNode;
import nds.control.util.ValueHolder;
import nds.query.QueryEngine;
import nds.security.Directory;
import nds.security.User;
import nds.util.NDSException;
import nds.util.NDSRuntimeException;
import nds.util.Tools;
import nds.model.*;
import nds.model.dao.*;
import org.hibernate.*;
import nds.log.*;


import com.liferay.util.SimpleCachePool;
//import com.liferay.portlet.calendar.ejb.CalEventLocalUtil;

/**
 * ���ڵײ��portal ������cache, ʹ�õ����µ�event ���뵽���ݿ��ʱ��
 * �����ϲ�������ʾ��event��cache ��λ���� com.liferay.portlet.calendar.ejb.CalEventLocalUtil
 * cache ���û���(userid)��λ��
 * 
 * ��������ϵͳ�������̽������ύ�����ݿ�󣬽����õ�ǰad_client �ڵ������û���cache��Ч��
 * �Ӷ���ʹportal ����װ�����ݡ�
 * 
 * ���巽ʽΪ������Ѱ�� SimpleCachePool �к��� ad_client.domain ��Object, ���֮��
 * 
 * �μ�:
 * com.liferay.portlet.calendar.ejb.CalEventLocalUtil
 * com.liferay.util.SimpleCachePool 
 */
public class U_NOTICE_OLD_ListSubmit extends Command {

	public ValueHolder execute(DefaultWebEvent event) throws NDSException, RemoteException {
    	String clientDomain= helper.getOperator(event).getClientDomain();

    	String[] itemidStr = event.getParameterValues("itemid");
        if (itemidStr==null) itemidStr= new String[0];
        String oid="";
        if(itemidStr.length >0 ) oid= itemidStr[0];
        for(int i=1;i< itemidStr.length;i++){
        	oid += ","+itemidStr[i];
        }
        
    	event.setParameter("command","ListSubmit");
    	ValueHolder vh=helper.handleEvent(event);
    	try{
    		U_NOTICE_OLD_Submit.clearCacheOfRecievers( clientDomain,oid,  logger);
    		vh.put("message","֪ͨ���ύ�ɹ�!") ;
    		return vh;
    	}catch(Exception e){
    		logger.error("Could not submit notice:", e); 
    		throw new NDSEventException(e.getMessage());    		
    	}finally{
    	}
    }
    

}
