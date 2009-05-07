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
 * Alisoft �Ķ���֪ͨ������������event.params�л�ȡ
 * 
 * ���� http://forum.alisoft.com/viewthread.php?tid=3055&extra=page%3D1  ��ƵĶ���֪ͨҳ��
 * 
 * @author yfzhu
 *
 */
public class Alisoft_Subscribe_Notify extends Command {

	public ValueHolder execute(DefaultWebEvent event) throws RemoteException, NDSException {
		
		logger.debug(event.toDetailString());
		 Configurations conf= (Configurations)WebUtils.getServletContextManager().getActor( nds.util.WebKeys.CONFIGURATIONS);
		 String appId= conf.getProperty("saas.alisoft.appkey");
		 String appsecret= conf.getProperty("saas.alisoft.appsecret");

		 
		  Map  map =(Map) event.getParameterValue("params");
		  String sig=(String)map.get(OrderConstants.PARAMETER_SIGNATURE);//���ڼ���ʱҪȥ����õ�signature�������ԣ����б���
		  
		  String sign=SubscribeSignUtil.Signature(map, appsecret);//����
		  
		  String subevent= (String)map.get(OrderConstants.PARAMETER_EVENT);//�¼����ͣ�subsc-�¶���renewAhead-δ����������renew-����������resource-������Դ��break-�˶�
		  String userId=(String)map.get(OrderConstants.PARAMETER_USERID);

		  String subscId=(String)map.get(OrderConstants.PARAMETER_SUBSCID); //�¶���ID
		  String gmtStart=(String)map.get(OrderConstants.PARAMETER_GMTSTART);
		  String gmtEnd=(String)map.get(OrderConstants.PARAMETER_GMTEND);
		  
		  String ctrlParams=(String)map.get(OrderConstants.PARAMETER_CTRLPARAMS);
		  String totalAmount=(String)map.get(OrderConstants.PARAMETER_TOTALAMOUNT);
		  String amount=(String)map.get(OrderConstants.PARAMETER_AMOUNT); // what i need
		  String rentAmount=(String)map.get(OrderConstants.PARAMETER_RENTAMOUNT);
		  String resourceAmount=(String)map.get(OrderConstants.PARAMETER_RESOURCEAMOUNT);
		  String couponAmount=(String)map.get(OrderConstants.PARAMETER_COUPONAMOUTN);
		  
		  QueryEngine engine=QueryEngine.getInstance();
			PreparedStatement stmt=null;
//		    ResultSet rs=null;
			Connection conn= engine.getConnection();
		  
		  String message=null;
		  ValueHolder holder= new ValueHolder();
		  try{
			  /*
			   * ����֤Ӧ��ID��ǩ���Ƿ�һ��
			   * 
			   */
			  if(sig!=null && !sig.equals(sign)){
				  logger.warning(sig +"!="+ sign);
			  }
			  if(!(appId.equals(map.get(OrderConstants.PARAMETER_APPID))/*&& sig!=null && sig.equals(sign)*/)){
				  logger.error("appId:"+appId+"!="+OrderConstants.PARAMETER_APPID+":"+map.get(OrderConstants.PARAMETER_APPID));
				  message="�������������뷵��<a href='http://www.alisoft.com'>�������</a>����";
			  }else{
				  // �����û��������Ƕ���״̬��ȷ�û��ɹ���ʽ��Ŀǰ����������ԴԤ����
				  logger.debug("subevent="+ subevent);
				  if("resource".equals(subevent)){
					  boolean isNewClient= Tools.getInt(QueryEngine.getInstance().doQueryOne("select count(*) from users where saasvendor='alisoft' and saasuser='"+ userId+"'"), 0)==0;
					  if(isNewClient) subevent="subsc";
				  }
				  if("subsc".equals(subevent)){
					  int amt=(int)Double.parseDouble( totalAmount);
					  //new
					  // ���Դ� ctrlParams ���ȡ email �� domaindesc
					  PairTable pt= PairTable.parse(ctrlParams, null);
					  String email=(String) pt.get("email");
					  String domainDesc=(String) pt.get("domaindesc");
					  if(domainDesc!=null)domainDesc=  URLDecoder.decode(domainDesc, "GBK");
					  
					  logger.debug("new "+domainDesc+", email="+email+",desc="+domainDesc  +", amt="+ amt);
					  
					  if(nds.util.Validator.isNull(email)) throw new NDSException("����Email��Ϣδ����");
					  if(nds.util.Validator.isNull(domainDesc)) domainDesc="����"; 
					  
					  String templateClient=conf.getProperty("newclient.template","demo");
					  
					  ArrayList params=new ArrayList();
					  params.add(email);
					  params.add(userId);//null 
					  params.add("alisoft");// 
					  params.add(new Integer(amt));//
					  params.add(templateClient);
					  params.add(domainDesc);//
					  params.add("test");
					  params.add("");	// domain is null
					  SPResult result=engine.executeStoredProcedure("ad_client_saas_new", params, true, conn);
					  message= result.getMessage();
					  int code= result.getCode();// this is ad_client_id that created
					  String domain =(String )engine.doQueryOne("select domain from ad_client where id="+ code, conn);
					  boolean isMultipleClientEnabled= "true".equals(conf.getProperty("webclient.multiple","false"));					
					  if(isMultipleClientEnabled){
						// clone web folder in /act/webroot/$domain
						String webRoot= conf.getProperty("client.webroot","/act/webroot");
						String srcClientFolder= webRoot+"/"+ templateClient;
						String destClientFolder=webRoot+"/"+ domain;

						logger.debug("copy dir "+ srcClientFolder+" to "+ destClientFolder);
						nds.util.FileUtils.delete(destClientFolder);
						nds.util.FileUtils.copyDirectory(srcClientFolder, destClientFolder);
						

						}
					  //message="������ɣ������Ե�¼��";
				  }else if("break".equals(subevent)){
					  //break
					  int clientId =Tools.getInt(engine.doQueryOne("select ad_client_id from users where saasvendor='alisoft' and saasuser="
							  + QueryUtils.TO_STRING(userId), conn), -1);
					  if(clientId<0){
						  throw new NDSException("δ�ҵ�����Ϊϵͳ����Ա��Ӧ������");
					  }
					  ArrayList params=new ArrayList();
					  params.add(new Integer(clientId));//
						
					  SPResult result=engine.executeStoredProcedure("ad_client_break", params, false, conn);
					  
					  message="�˶����";
				  }else{
					  //pay
					  int clientId =Tools.getInt(engine.doQueryOne("select ad_client_id from users where name='root' and saasvendor='alisoft' and saasuser="
							  + QueryUtils.TO_STRING(userId), conn), -1);
					  if(clientId<0){
						  throw new NDSException("δ�ҵ�����Ϊϵͳ����Ա��Ӧ������");
					  }
					  ArrayList params=new ArrayList();
					  int amt=(int)Double.parseDouble( totalAmount);
					  
					  params.add(new Integer(clientId));//
					  params.add(new Integer(amt));//
					  
						
					  SPResult result=engine.executeStoredProcedure("ad_client_renew", params, false, conn);
					  
					  message="���ѳɹ������ѽ��"+ amt +"Ԫ" ;
				  }
			  }
   		}catch(Throwable t){
   	  		logger.error("exception",t);
   	  		if(t instanceof NDSException) throw (NDSException)t;
   	  		throw new NDSException("���������쳣����ˢ��ҳ�����ԡ�����ȷ���Ѿ��������ϵ���ǵĿͷ���ԱЭ����� "+
   	  			conf.getProperty("company.contactor", ""));
   	  		//holder.put(name, value)
   	  	}finally{
	   	  	try{if(stmt!=null)stmt.close();}catch(Exception ea){}
	        try{conn.close();}catch(Exception e){}   	  		
   	  	}
   		holder.put("message",message);
   		holder.put("code","0");
   		return holder;
   	  }
	
}
