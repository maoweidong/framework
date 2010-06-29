package nds.control.ejb.command;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.*;
import org.directwebremoting.WebContext;
import org.json.*;

import nds.control.ejb.Command;
import nds.control.event.DefaultWebEvent;
import nds.control.event.NDSEventException;
import nds.control.util.AjaxUtils;
import nds.control.util.SecurityUtils;
import nds.control.util.ValueHolder;
import nds.control.web.UserWebImpl;
import nds.control.web.WebUtils;
import nds.query.*;
import nds.schema.*;
import nds.util.*;

import java.util.*;

import nds.security.Directory;
import nds.security.User;

/**
 * Rest command
 *  
 */
public class GetObject extends Command {
	/**
	 * 
	 * @param  
	 * 	jsonObject -

	�������
	table *	int	��Ӧ���ID
	id*	int	Ҫ�����ļ�¼��ID�����ڲ�֪��ID�����������ͨ�� "ak"����id_find������
	reftables	int[]	��ѡ��ͨ��������ָ����Ҫ��Щ��ǩҳ�����ݣ�ÿ��Ԫ�ض��ǹ������id
	
	���ز�������
	<column_name>		�ֶ������ڱ�ġ��������ֶΡ�
	reftables	{}[]	���趨�Ĳ���reftables ��Ӧ��refobj �����ǵ�����(1:1)��Ҳ�������б�����(1:m)����������ֶμ�Ϊ��������ʾ���ֶΣ��б��������Query����ķ���ֵ���ƣ����� rows, count��
	reftables����Ԫ��(1:1)����
	�� <column-name>		�������ֶκ�ֵ, ���resthome����(�����������޸������ֶ�)
	reftables����Ԫ��(1:m)����
	�� rows *	[][]	��һά���У��ڶ�ά��ÿ�е��ж�Ӧֵ�����ֶ�˳���ձ�ġ��������ֶΡ�������˳��
	 * 	
	 */
  public ValueHolder execute(DefaultWebEvent event) throws RemoteException, NDSException {
  	TableManager manager=TableManager.getInstance();
  	User usr=helper.getOperator(event);
  	QueryEngine engine=QueryEngine.getInstance();
  	MessagesHolder mh= MessagesHolder.getInstance();
  	Connection conn=null;
	JSONObject jo=(JSONObject)event.getParameterValue("jsonObject");
	
	Table table= manager.findTable(jo.opt("table"));
	if(table==null)throw new NDSException("table "+ jo.opt("table") + " is not found");
	int tableId= table.getId();

	int objectId=event.getObjectIdByJSON(jo,table, usr.adClientId, conn);
	// check permission
    boolean b=false;
    try{
    	b=SecurityUtils.hasObjectPermission(usr.id.intValue(), usr.getName(), table.getName(), 
    			objectId,Directory.READ, event.getQuerySession());
    }catch(Exception e){
        throw new NDSEventException(e.getMessage() );
    }
    if (!b) throw new NDSEventException("@no-permission@!" );
    
    JSONObject restResult =null;
	try{
		
		JSONObject objQuery=new JSONObject();
		objQuery.put("table",tableId);
		int[] masks=new int[]{6}; // 
		objQuery.put("column_mask", JSONUtils.toJSONArrayPrimitive(masks));
		
		JSONObject params=new JSONObject();
		params.put("column",  table.getPrimaryKey().getName());
		params.put("condition", "="+ objectId);
		objQuery.put("params", params);
		objQuery.put("count",true);
		
		JSONObject mo=AjaxUtils.doRestQuery(objQuery, event.getQuerySession(),usr.id.intValue(), event.getLocale());
		/*
			 *Inline single object (1:1) handling, for parent id, must using "ak" or "id_find" method
			 *,since web ui does not support such case
			 */
		restResult=new JSONObject();
		if(mo.getInt("count")>0){
			JSONArray data=mo.optJSONArray("rows").getJSONArray(0);
				
			ArrayList al=table.getColumns(masks, false, usr.getSecurityGrade());
			for(int d=0;d<al.size();d++){
				restResult.put( ((Column)al.get(d)).getName() , data.get(d));
			}
		}
		
		
        JSONArray reftables= jo.optJSONArray("reftables");
  		if(reftables!=null){
  			JSONArray reftablesRet=new JSONArray();
  	  		ArrayList refbyTables=table.getRefByTables();
  	  		RefByTable rbt=null;
  			
	  		for(int dojIdx=0;dojIdx<reftables.length();dojIdx++ ){
	  			int refByTableId= reftables.getInt(dojIdx);
	  			boolean rbTFound=false;
	  			for(int rbTIdx=0;rbTIdx<refbyTables.size();rbTIdx++){
	  				rbt=(RefByTable) refbyTables.get(rbTIdx);
	  				if(rbt.getId()== refByTableId){
	  					rbTFound=true;
	  					break;
	  				}
	  			}
	  			if(!rbTFound)throw new NDSException("reftables:"+refByTableId+ " not found in master refby tables("+table);
	  			
  				objQuery=new JSONObject();
  				objQuery.put("table",rbt.getTableId());
  				masks=new int[]{0,2,4}; // 
  				objQuery.put("column_masks", JSONUtils.toJSONArrayPrimitive(masks));
  				
  				params=new JSONObject();
  				//��δʵ�ֹ���rbt.getFilter()�Ĵ���
  				params.put("column",  manager.getColumn(rbt.getRefByColumnId()).getName());
  				params.put("condition", "="+ objectId);
  				
  				objQuery.put("params", params);
  				objQuery.put("count",true);
  				JSONObject j=AjaxUtils.doRestQuery(objQuery, event.getQuerySession(),usr.id.intValue(), event.getLocale());
	  			if(rbt.getAssociationType()==RefByTable.ONE_TO_ONE){
	  				/*
	  				 *Inline single object (1:1) handling, for parent id, must using "ak" or "id_find" method
	  				 *,since web ui does not support such case
	  				 */
	  				JSONObject so=new JSONObject();
	  				if(j.getInt("count")>0){
	  					JSONArray data=j.optJSONArray("rows").getJSONArray(0);
	  					
	  					ArrayList al=manager.getTable(rbt.getTableId()).getColumns(masks, false,usr.getSecurityGrade());
	  					for(int d=0;d<al.size();d++){
	  						so.put( ((Column)al.get(d)).getName() , data.get(d));
	  					}
	  				}
	  				reftablesRet.put(so);
	  				
	  			}else{
	  				/*
	  				 * List (1:m), since no fixed column handling, for parent id, we will
	  				 * compose fixedcolumns for handling 
	  				 */
	  				reftablesRet.put(j);
	  			}
	  		}
	  		restResult.put("reftables", reftablesRet);
  		}
        
  		ValueHolder holder= new ValueHolder();
		holder.put("message", mh.getMessage(event.getLocale(), "complete"));
		holder.put("code","0");
		holder.put("restResult",restResult );
	  	

	  	return holder;
  	}catch(Throwable t){
  		logger.error("exception",t);
  		if(t instanceof NDSException) throw (NDSException)t;
  		else
  			throw new NDSException(t.getMessage(), t);
  	}finally{
  		try{if(conn!=null)conn.close();}catch(Throwable e){}
  	}
  }
 
}