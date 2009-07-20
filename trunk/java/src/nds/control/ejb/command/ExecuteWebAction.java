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
 * 执行WebAction对应的后台动作，包括sp,beanshell,osshell
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
		tag - this is used by client to remember locale status, such as for row information,
				  it will be sent back unchanged.
		pageurl - String url for loading page, should be absolute url path
	 * @return "data" will be jsonObject with following format:
	 * { 	
	 * 		target: from request
	 		message* - string converted JSON object from xml returned from database
	 		code *-?
	 		0 不刷新
			1 刷新当前列表
			2 刷新整个页面
			3 以p_message内容作为新的URL，按URL目标页定义替换当前页面的DIV或者构造HREF
			4 以p_message内容作为新的JAVASCRIPT, 解析并执行
			99 关闭当前页面

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
  		WebContext wc=(WebContext) jo.get("org.directwebremoting.WebContext");

    	UserWebImpl userWeb= ((UserWebImpl)WebUtils.getSessionContextManager(wc.getSession()).getActor(nds.util.WebKeys.USER));
    	conn=QueryEngine.getInstance().getConnection();
    	
    	
    	HashMap webActionEnv=new HashMap();
    	webActionEnv.put("connection",conn);
    	webActionEnv.put("httpservletrequest",wc.getHttpServletRequest());
    	webActionEnv.put("userweb",userWeb);
    	webActionEnv.put("userid",usr.id);
    	webActionEnv.put("webaction",action);
    	webActionEnv.put("objectid",objectId);

    	
    	// convert internal query object, which can be read by AjaxUtils.parseQuery, to sql like:
    	// select id from m_product where name like ‘adf%’ and isactive=’Y’
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