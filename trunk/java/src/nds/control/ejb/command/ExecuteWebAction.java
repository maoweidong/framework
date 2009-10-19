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
import nds.control.util.ValueHolder;
import nds.control.web.UserWebImpl;
import nds.control.web.WebUtils;
import nds.query.*;
import nds.schema.*;
import nds.util.*;

import java.util.*;

import nds.security.User;

/**
 * ִ��WebAction��Ӧ�ĺ�̨����������sp,beanshell,osshell
 *  
 */
public class ExecuteWebAction extends Command {
	/**
	 * 
	 * @param  
	 * 	jsonObject -
			webaction* - id of ad_action 
	 *      target*  - String, just return to client
	 *      query*  - json type:
	 *      	selection: array of int for selected record id of current page, this is optional
	 *      	query: query that can be parsed by AjaxUtils.parseQuery, optional
	 *      	table: table id
				id: 	current main table id
		uk.ltd.getahead.dwr.WebContext - this is for convenience to request jsp result
		or javax.servlet.http.HttpServletRequest if no WebContext
		tag - this is used by client to remember locale status, such as for row information,
				  it will be sent back unchanged.
		pageurl - String url for loading page, should be absolute url path
	 * @return "data" will be jsonObject with following format:
	 * { 	
	 * 		target: from request
	 		message* - string converted JSON object from xml returned from database
	 		code *-?
	 		0 ��ˢ��
			1 ˢ�µ�ǰ�б�
			2 ˢ������ҳ��
			3 ��p_message������Ϊ�µ�URL����URLĿ��ҳ�����滻��ǰҳ���DIV���߹���HREF
			4 ��p_message������Ϊ�µ�JAVASCRIPT, ������ִ��
			99 �رյ�ǰҳ��

	 * }
	 * 	
	 */
  public ValueHolder execute(DefaultWebEvent event) throws RemoteException, NDSException {
  	TableManager manager=TableManager.getInstance();
  	User usr=helper.getOperator(event);
  	QueryEngine engine=QueryEngine.getInstance();
  	MessagesHolder mh= MessagesHolder.getInstance();
  	Connection conn=null;
  	try{
	  	JSONObject jo=(JSONObject)event.getParameterValue("jsonObject");
	  	Object target= jo.opt("target");
	  	int objectId= jo.optInt("objectid", -1);
	  	int actionId= jo.getInt("webaction");
	  	WebAction action=manager.getWebAction(actionId);
	  	if(action==null) throw new NDSException("@object-not-found@:WebAction("+actionId+")");
	  	// check permission
	  	
  		WebContext wc=(WebContext) jo.opt("org.directwebremoting.WebContext");
  		javax.servlet.http.HttpSession session=null;
  		javax.servlet.http.HttpServletRequest request=null;
  		if(wc!=null) {
  			session=wc.getSession();
  			request=wc.getHttpServletRequest();
  		}
  		else{
  			request=(javax.servlet.http.HttpServletRequest)jo.opt("javax.servlet.http.HttpServletRequest");
  			if(request==null) throw new NDSException("Could not get HttpServletRequest");
  			session=request.getSession();
  		}
    	UserWebImpl userWeb= ((UserWebImpl)WebUtils.getSessionContextManager(session).getActor(nds.util.WebKeys.USER));
    	conn=QueryEngine.getInstance().getConnection();
    	
    	
    	HashMap webActionEnv=new HashMap();
    	webActionEnv.put("connection",conn);
    	webActionEnv.put("httpservletrequest",request);
    	webActionEnv.put("userweb",userWeb);
    	webActionEnv.put("userid",usr.id);
    	webActionEnv.put("webaction",action);
    	webActionEnv.put("objectid",objectId);

    	
    	// convert internal query object, which can be read by AjaxUtils.parseQuery, to sql like:
    	// select id from m_product where name like ��adf%�� and isactive=��Y��
    	Object queryObj= jo.opt("query");
    	if(queryObj!=null){
        	if(!(queryObj instanceof JSONObject)){
        		queryObj=new JSONObject(queryObj.toString());
        	}
        	JSONObject query=(JSONObject)queryObj;
        	Object iqo= query.opt("query");
        	if(iqo!=null && !JSONObject.NULL.equals(iqo)){
        		if(!(iqo instanceof JSONObject)){
        			iqo=new JSONObject(iqo.toString());
            	}
            	JSONObject iq=(JSONObject)iqo;
            	QueryRequestImpl q=AjaxUtils.parseQuery(iq, userWeb.getSession(), userWeb.getUserId(), userWeb.getLocale());
            	String sqlQuery=q.toPKIDSQL(true);
            	query.put("query", sqlQuery);
            	logger.debug(sqlQuery);
        	}else{
        		query.put("query", "");
        	}
        	
        	webActionEnv.put("query", query);// always json type
    	}else{
    		//for object page
    		if(objectId!=-1){
    			JSONObject oj=new JSONObject();
    			oj.put("table", action.getTableId());
    			oj.put("id", objectId);
        		webActionEnv.put("query", oj);
    		}
    	}
    	
    	
	  	Table table= manager.getTable(action.getTableId());
	  	if(table!=null)
	  		webActionEnv.put("maintable",table.getName());
    	
    	if(!action.canDisplay(webActionEnv)){
    		throw new NDSException("@no-permission@");
    	}
    	
    	Map ret=action.execute(webActionEnv);
    	
	  	
	  	JSONObject data=new JSONObject(ret);
	  	if(target!=null)data.put("target",target);
/*	  	
	  	if(Validator.isNotNull(pageURL)){
	  		WebContext wc=(WebContext) jo.get("org.directwebremoting.WebContext");
	  		if(pageURL.indexOf("compress=f")<0) throw new NDSException("Must contain compress=f in pageurl:"+ pageURL);
			String page=wc.forwardToString(pageURL);
			data.put("pagecontent", page);
	  		
	  	}*/
  		ValueHolder holder= new ValueHolder();
		holder.put("message", mh.getMessage(event.getLocale(), "complete"));
		holder.put("code","0");
		holder.put("data",data );
	  	

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