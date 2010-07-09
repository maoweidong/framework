package nds.control.ejb.command;


import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nds.control.ejb.Command;
import nds.control.event.DefaultWebEvent;
import nds.control.util.SecurityUtils;
import nds.control.util.ValueHolder;
import nds.control.web.WebUtils;
import nds.query.QueryEngine;
import nds.query.QueryUtils;
import nds.schema.SQLTypes;
import nds.security.User;
import nds.util.Configurations;
import nds.util.JSONUtils;
import nds.util.MessagesHolder;
import nds.util.NDSException;
import nds.util.Tools;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ִ��������������ͨ��REST ���� 
 *  
 */
public class ExecuteBatch extends Command {
	/**
	 * 
	 * @param  
	 * 	jsonObject -
			transctions* - ÿ��Ԫ�ض���һ��Transaction Request�������������ͨ��command 
			������Ҫôȫ�ɹ���Ҫôȫ��ʧ��
	 * @return id,message
	 * 
	 * 	
	 */
  public ValueHolder execute(DefaultWebEvent event) throws RemoteException, NDSException {
  	User usr=helper.getOperator(event);
  	MessagesHolder mh= MessagesHolder.getInstance();
  	try{
	  	JSONObject jo=(JSONObject)event.getParameterValue("jsonObject");
	  	JSONArray ja=jo.getJSONArray("transactions");
	  	HttpServletRequest request=(HttpServletRequest) jo.opt("javax.servlet.http.HttpServletRequest");
	  	for(int i=0;i< ja.length();i++){
	  		JSONObject ji= ja.getJSONObject(i);
	  		DefaultWebEvent jie=nds.control.util.AjaxUtils.createEventByRestTransaction(ji,request,event.getQuerySession(), usr.getId().intValue(), event.getLocale());
	  		
	  		logger.debug(jie.toDetailString());
	  		ValueHolder vh= this.helper.handleEvent(jie);
	  		if(!vh.isOK()){
	  			throw new NDSException(String.valueOf(vh.get("message")));
	  		}
	  	}
  		ValueHolder holder= new ValueHolder();
		holder.put("message", mh.getMessage(event.getLocale(), "complete"));
		holder.put("code","0");
	  	
	  	return holder;
  	}catch(Throwable t){
  		if(t instanceof NDSException) throw (NDSException)t;
  		else
  			throw new NDSException(t.getMessage(), t);
  	}
  }
 
}