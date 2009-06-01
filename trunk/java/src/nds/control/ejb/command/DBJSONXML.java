package nds.control.ejb.command;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.directwebremoting.WebContext;
import org.json.*;

import nds.control.ejb.Command;
import nds.control.event.DefaultWebEvent;
import nds.control.event.NDSEventException;
import nds.control.util.ValueHolder;
import nds.query.*;
import nds.schema.*;
import nds.util.*;

import java.util.*;

import nds.security.User;

/**
 * �������Javascript ���������ݿ�json �������󣬽�event �ڲ������json ת��Ϊ xml �������ݿ�
 * �ٽ����ݿ��xml ���ת��Ϊ json���󷵻�javascript
 *  
 */
public class DBJSONXML extends Command {
	/**
	 * 
	 * @param event contains 
	 * 	jsonObject -
			param* - String, json ��ʽ����ת��Ϊxml string, ���͸�oracle
	 *      table*  - String, ��Ҫ�����ı��������POSҵ��tableΪM_RETAIL
	 * 		action* - String, ��������
	 *      permission* - "R" ��ʾֻ��, "W"��ʾд�루������Ȩ�ޣ����������ɾ�ģ����ύ��Ӧ���ݵ���Ȩ��
	 ϵͳ��������table,action,permission ����洢������������
	       <table>_$<permission>_<action>(userId,jsonRequest)
	       
	       ���磬m_retail_$w_insertline
				
		uk.ltd.getahead.dwr.WebContext - this is for convenience to request jsp result
		tag - this is used by client to remember locale status, such as for row information,
				  it will be sent back unchanged.
		pageurl - String url for loading page, should be absolute url path
	 * @return "data" will be jsonObject with following format:
	 * { 	
	 * 		tag: from request
	 		jsonResult* -string converted JSON object from xml returned from database
	 		pagecontent -html segment for pageurl
	 * }
	 * 	
	 */
  public ValueHolder execute(DefaultWebEvent event) throws RemoteException, NDSException {
  	TableManager manager=TableManager.getInstance();
  	User usr=helper.getOperator(event);
  	QueryEngine engine=QueryEngine.getInstance();
  	MessagesHolder mh= MessagesHolder.getInstance();
  	try{
	  	JSONObject jo=(JSONObject)event.getParameterValue("jsonObject");
	  	Object tag= jo.opt("tag");
	  	String pageURL= jo.optString("pageurl");
	  	String table= jo.getString("table");
	  	String action= jo.getString("action");
	  	String permission= jo.getString("permission");
	  	String param= jo.getString("param");
	  	JSONObject paramJSON= new JSONObject(param);
	  	
	  	param= org.json.XML.toString(paramJSON);
	  	
	  	// check permission
	 // 	Table t= manager.getTable(table);
	  	Table t= manager.getTable("m_v_product");
		if (!"root".equals(usr.getName())){
		  	int perm= helper.getPermissions(t.getSecurityDirectory(),usr.id.intValue());
		  	int minPerm=1;// read
		  	if("W".equalsIgnoreCase(permission)){
		  		if(t.isActionEnabled(Table.ADD) || t.isActionEnabled(Table.MODIFY)|| t.isActionEnabled(Table.DELETE))
		  			minPerm |=3;
		  		if( t.isActionEnabled(Table.SUBMIT))
		  			minPerm |= 5; 
		  	}
		  	if( (perm & minPerm) !=minPerm){
		  		logger.debug("perm="+ perm+",minPerm="+minPerm);
		  		throw new NDSException("@no-permission@");
		  	}
	  	}
	
	  	// construct procedure name
	  	String func= table + "_$"+ permission+"_"+ action;
	  	logger.debug("Call "+ func+"("+usr.getId()+","+param+")");
	  	ArrayList params= new ArrayList();
	  	params.add(usr.getId());
	  	params.add(param);
	  	
	  	ArrayList res= new ArrayList();
	  	res.add( java.sql.Clob.class);// Clob as return while java converts it to String
	  	
	  	Collection list=QueryEngine.getInstance().executeFunction(func, params, res);
	  	String result= (String)list.iterator().next();// this is xml format, converted to json
	  	logger.debug("result(xml):"+ result);
	  	result =(org.json.XML.toJSONObject(result)).toString();
	  	logger.debug("result(json):"+ result);
	  	
	  	JSONObject data=new JSONObject();
	  	data.put("jsonResult",result);
	  	if(tag!=null)data.put("tag", tag); //  return back unchanged.
	  	
	  	if(Validator.isNotNull(pageURL)){
	  		WebContext wc=(WebContext) jo.get("org.directwebremoting.WebContext");
	  		/**
			 * Please note param "compress=f" is to prohibit  com.liferay.filters.compression.CompressionFilter from compressing file content 
			*/
	  		if(pageURL.indexOf("compress=f")<0) throw new NDSException("Must contain compress=f in pageurl:"+ pageURL);
			String page=wc.forwardToString(pageURL);
			data.put("pagecontent", page);
	  		
	  	}
  		ValueHolder holder= new ValueHolder();
		holder.put("message", mh.getMessage(event.getLocale(), "complete"));
		holder.put("code","0");
		holder.put("data",data);
	  	return holder;
  	}catch(Throwable t){
  		logger.error("exception",t);
  		throw new NDSException(t.getMessage(), t);
  	}
  }
 
}