package nds.control.ejb.command;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

import org.directwebremoting.WebContext;
import org.json.*;

import nds.control.ejb.Command;
import nds.control.event.DefaultWebEvent;
import nds.control.event.NDSEventException;
import nds.control.util.ValueHolder;
import nds.query.*;
import nds.schema.*;
import nds.util.*;
import com.alisoft.sip.sdk.isv.*;
import nds.saasifc.alisoft.*;

import java.util.*;
import java.net.*;
import nds.security.User;

import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;

import JOscarLib.Request.Request;

import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.*;
/*import com.liferay.portal.ejb.UserManagerUtil;
import com.liferay.portal.ejb.UserManager;
import com.liferay.portal.ejb.UserManagerFactory;

*/
import nds.security.User;
import nds.control.web.*;
import nds.velocity.*;
import javax.servlet.http.*;
/**
 * Alisoft �Ķ���������������session �л�ȡ��������form�л�ȡ
 * 
 *  ���� url ��Ϊ�ض���ҳ��
 *  ����http://forum.alisoft.com/viewthread.php?tid=2407&extra=page%3D3
 * 
 * @author yfzhu
 *
 */
public class Alisoft_Subscribe extends Command {

	public ValueHolder execute(DefaultWebEvent event) throws RemoteException, NDSException {
		
		logger.debug(event.toDetailString());
		 Configurations conf= (Configurations)WebUtils.getServletContextManager().getActor( nds.util.WebKeys.CONFIGURATIONS);
		 String appId= conf.getProperty("saas.alisoft.appkey");
		 String appsecret= conf.getProperty("saas.alisoft.appsecret");
		
		TableManager manager=TableManager.getInstance();
		
	  	JSONObject jo=(JSONObject)event.getParameterValue("jsonObject");
	  	
	  	java.util.Locale locale= event.getLocale();
  	    QueryEngine engine=QueryEngine.getInstance();
        boolean hasError=false;
        MessagesHolder mh= MessagesHolder.getInstance();
        
        String postData;//ƽ̨Ҫ���ԭ���ش��Ĳ���
        String returnUrl;//�ش�url
        String subscType;//��������
        double  amount;//���
        double  rentAmount;
        double  resourceAmount;
        String ctrlParams;//���Ʋ���
        String signature;//ǩ��
   	  	ValueHolder holder= new ValueHolder();

   		try{
   		   JSONObject params=jo.getJSONObject("params");
   			/*
   		   * ��servletcontext�ж�ȡ��Ҫ�Ĳ���
   		   */
   			
   		  subscType=params.getString("subscType");
   		  postData=params.getString("postData");
   		  returnUrl=params.getString("returnUrl");
   		  String gmtStart=params.getString("gmtStart");
   		  
   		  logger.debug("subscType"+ subscType+",gmtStart="+ gmtStart+",postData="+postData+",returnUrl="+returnUrl);

   		  
   		  String gmtEnd=addMon(gmtStart,1);//���㶩������ʱ�䣬��������ʼʱ����϶���ʱ�䣬�˴�д��Ϊһyear
   		  /*
   		   * �������Ͳ�ͬʱ������ƽ̨�Ĳ���Ҳ�ǲ�ͬ�ġ����ԣ����ݶ������ͣ��ֱ���в�������֯
   		   */
   		  Map<String, Object> map=new HashMap<String, Object>();
  		   rentAmount=0;//
  		   
  		   
   		   resourceAmount= params.getInt("amtpay"); // from form
   		   
   		   map.put("rentAmount", rentAmount);
   		   map.put("resourceAmount", resourceAmount);
   		   amount=rentAmount+resourceAmount;
   		   map.put("amount", amount);
   		   ctrlParams="amount="+ amount+"&rent="+rentAmount;
   		   
   		   if(amount <=0) throw new NDSException("֧������ȷ:"+ amount);
   		   
   		   map.put("description", "���ı���֧���ܶ�Ϊ"+amount+"Ԫ");

   		   map.put("postData", postData);
			String email= params.optString("email");
			String domainDesc= params.optString("domaindesc");
			if(nds.util.Validator.isNotNull(email)) ctrlParams+="&email="+ email;
			if(nds.util.Validator.isNotNull(domainDesc)) ctrlParams+="&domaindesc="+encodeURL(domainDesc);

			if(subscType.equals("0")){//�¶�
	   		   map.put("gmtStart",gmtStart);
	   		   map.put("gmtEnd", gmtEnd);
	   		   /**
	   		    * У��email �Ƿ���ϵͳ���Ѿ����ڣ����Ѿ�������Ҫ����
	   		    */
	   		   if(isEmailExists(email)){
	   			   throw new NDSException("Email:"+ email+"��ϵͳ���Ѿ���ʹ�ã����޸�");
	   		   }
   		   }else if(subscType.equals("1")){//δ���������������޸Ķ�����ʼʱ�䣬�����Ʋ���
	   		   map.put("gmtEnd", gmtEnd);
   		  }else if(subscType.equals("2")){//��������
	   		   map.put("gmtStart",gmtStart);
	   		   map.put("gmtEnd", gmtEnd);
   		  }else {//������Դ���������ⲿ��Ϊ��
   		  }
  		   map.put("ctrlParams", ctrlParams);
   		  signature=SubscribeSignUtil.Signature(map, appsecret);//ǩ��
   		  map.put("signature", signature);
   		  /*
   		   * ��֯����
   		   */ 
   		  StringBuffer buffer = new StringBuffer();
   		  boolean notFirst = false;
   		  for (Map.Entry<String, ?> entry : map.entrySet()) {
   		   if (notFirst) {
   		    buffer.append("&");
   		   } else {
   		    notFirst = true;
   		   }
   		   Object value = entry.getValue();
   		   buffer.append(entry.getKey()).append("=").append(
   		     encodeURL(value) );
   		  }
   		  String queryString=buffer.toString();
   		  
   		  /*
   		   * ��ת��ƽ̨����������صĶ�������
   		   */
   		  String url=(returnUrl+"?"+queryString);
   		  logger.debug("url:"+ url);
   		  
   		  holder.put("data", url);
   		  
   		}catch(Throwable t){
   	  		if(t instanceof NDSException) throw (NDSException)t;
   	  		logger.error("exception",t);
   	  		throw new NDSException("���������쳣����ˢ��ҳ������");
   	  		//holder.put(name, value)
   	  	}
   		holder.put("message", nds.util.MessagesHolder.getInstance().translateMessage("@complete@",locale));
   		holder.put("code","0");
   		return holder;
   	  }
	
	private boolean isEmailExists(String email) throws Exception{
		return Tools.getInt( 
				QueryEngine.getInstance().doQueryOne(
				"select count(*) from users where email="+ QueryUtils.TO_STRING(email)), -1)>0;
	}
		
	/*
	  * ����
	  */
	private String encodeURL(Object target) throws Exception {
	  String result = (target != null) ? target.toString() : "";
	  result = URLEncoder.encode(result, "GBK");
	  return result;
	}

	/*
	  * ���ڼ���
	  */
	private static String addMon(String s, int n) {    
	   Calendar cd=null;
	  java.text.SimpleDateFormat TIME_FORMATER = new java.text.SimpleDateFormat("yyyy-MM-dd");//ʱ���ʽ
	  try {    
	              
	   
             cd = Calendar.getInstance();    
            cd.setTime(TIME_FORMATER.parse(s));    
            cd.add(Calendar.MONTH, n);//����һ��    
            cd.add(Calendar.DATE, -1);
	   
        } catch (Exception e) {    
           e.printStackTrace();
        }    
	        
	        return TIME_FORMATER.format(cd.getTime());    
	}    

	
}
