
package nds.saasifc.fw086;

import javax.jws.WebService;

import nds.log.Logger;
import nds.log.LoggerManager;
import nds.util.Tools;

import org.json.JSONObject;

import com.microsoft.sispark.saasinterface.RequestInfo;
import com.microsoft.sispark.saasinterface.ResponseInfo;

@WebService(serviceName = "ISV2SaaSInterface", targetNamespace = "http://SaaSInterface.Sispark.Microsoft.com/", endpointInterface = "nds.saasifc.fw086.ISV2SaaSInterfaceSoap")
public class ISV2SaaSInterfaceImpl
    implements ISV2SaaSInterfaceSoap
{

	private Logger logger= LoggerManager.getInstance().getLogger(SaaS2ISVInterfaceImpl.class.getName());

    public ResponseInfo iSVSaaSInterface(RequestInfo req) {
        
        String code=req.getHead().getCode();
        logger.debug("Call me:"+code);
        ResponseInfo ri=null;
        try{
	        if("ISV10101".equals(code)){
	        	//��ҵά����������ͣ�ã�
	        	ri=isv10101Request( req);
	        }else if("ISV10102".equals(code)){
	        	//�½�Ա��
	        	ri=isv10102Request(req);
	        }else if("ISV10201".equals(code)){
	        	//���ع�˾�Ľ�ɫ�б�
	        	ri=isv10201Request(req);
	        }else if("ISV10202".equals(code)){
	        	//����ָ���û��Ľ�ɫ�б�
	        	ri=isv10202Request(req);
	        }else if("ISV10203".equals(code)){
	        	//ͬ����ɫ�б�
	        	ri=isv10203Request(req);
	        }else{
	        	logger.error("Fail to parse req code="+ code+", infor:"+ req.getHead());
	        
	        }
        }catch(Throwable t){
        	logger.error("Fail to do service:"+ req, t);
        	try{
        		ri=  FW086Manager.getInstance().createResponse(req.getHead(),1, "ϵͳ�����쳣:"+t.getMessage());
        	}catch(Throwable t2){
        		logger.error("Fail to createResponseError:", t2);
        		return null;
        	}
        }
        return ri;
    }
	private ResponseInfo isv10201Request(RequestInfo req)  throws Exception{
		ResponseInfo ri=null;
		FW086Manager mgr=FW086Manager.getInstance();
		String xml=mgr.decodeBodyXML( req.getBody());
		JSONObject jo=org.json.XML.toJSONObject(xml);
		String isvId=jo.getString("ISVID");
		String appId= jo.getString("AppID");
		String corpId=jo.getString("CorpID");
		
		/*if(!appId.equals(mgr.getAppId()) || !isvId.equals(mgr.getISVId()) ){
			ri=  mgr.createResponse(req.getHead(), 1,"APPID��ISVID����ȷ");
		}else{
			ri= mgr.executeCommand("FW086_ListRoles", jo, req);
		}*/
		ri= mgr.executeCommand("FW086_ListRoles", jo, req);
		return ri;
	}
	private ResponseInfo isv10202Request(RequestInfo req)  throws Exception{
		ResponseInfo ri=null;
		FW086Manager mgr=FW086Manager.getInstance();
		String xml=mgr.decodeBodyXML( req.getBody());
		JSONObject jo=org.json.XML.toJSONObject(xml);
		String isvId=jo.getString("ISVID");
		String appId= jo.getString("AppID");
		String corpId=jo.getString("CorpID");
		String userId=jo.getString("UserID");
		/*if(!appId.equals(mgr.getAppId()) || !isvId.equals(mgr.getISVId()) ){
			ri=  mgr.createResponse(req.getHead(), 1,"APPID��ISVID����ȷ");
		}else{
			ri= mgr.executeCommand("FW086_ListRolesOfUser", jo, req);
		}*/
		ri= mgr.executeCommand("FW086_ListRolesOfUser", jo, req);
		return ri;
	}
	private ResponseInfo isv10203Request(RequestInfo req)  throws Exception{
		ResponseInfo ri=null;
		FW086Manager mgr=FW086Manager.getInstance();
		String xml=mgr.decodeBodyXML( req.getBody());
		JSONObject jo=org.json.XML.toJSONObject(xml);
		String isvId=jo.getString("ISVID");
		String appId= jo.getString("AppID");
		String corpId=jo.getString("CorpID");
		String userId=jo.getString("UserID");
		String roleIds=jo.getString("RoleIDs");
		/*if(!appId.equals(mgr.getAppId()) || !isvId.equals(mgr.getISVId()) ){
			ri=  mgr.createResponse(req.getHead(), 1,"APPID��ISVID����ȷ");
		}else{*/
			// make sure user exists
			try{
				ri= mgr.executeCommand("FW086_UpdateRolesOfUser", jo, req);
			}catch(nds.security.UserNotFoundException e){
				JSONObject rt= mgr.getUsersInfo(corpId,userId);
				ri= mgr.executeCommand("FW086_UsersCreate", rt, req);
				ri= mgr.executeCommand("FW086_UpdateRolesOfUser", jo, req);
			}
		//}
		return ri;
	}	
	/**
	 * Ա��ά��
	 * @param req
	 * @return
	 * @throws Exception
	 */
	private ResponseInfo isv10102Request(RequestInfo req)  throws Exception{
		ResponseInfo ri=null;
		FW086Manager mgr=FW086Manager.getInstance();
		String xml=mgr.decodeBodyXML( req.getBody());
		JSONObject jo=org.json.XML.toJSONObject(xml);
		int opType=Tools.getInt(jo.getString("OPType"),-1);
		String opNote=jo.getString("OPNote");
		String appId= jo.getString("AppID");
		String corpId=jo.getString("CorpID");
		String userIds= jo.getString("UserIDs");
		/*if(!appId.equals(mgr.getAppId())){
			ri=  mgr.createResponse(req.getHead(), 1,"APPID����ȷ:"+ appId);
		}else{*/
			JSONObject rt;
			switch(opType){
			case 1:// ��ͨ
				rt= mgr.getUsersInfo(corpId,userIds);
				ri= mgr.executeCommand("FW086_UsersCreate", rt, req);
				break;
			case 2:// ��ͣ
				ri= mgr.executeCommand("FW086_UsersPause", jo, req);
				break;
			case 3:// �ָ�
				ri= mgr.executeCommand("FW086_UsersResume", jo, req);
				break;
			case 4: // ͣ��
				ri= mgr.executeCommand("FW086_UsersPause", jo, req);
				break;
			default:
				ri=  mgr.createResponse(req.getHead(),1, "��֧�ֵ�optype:"+opType);	
				
			}
		//}
		return ri;
	}
	private ResponseInfo isv10101Request(RequestInfo req)  throws Exception{
		ResponseInfo ri=null;
		FW086Manager mgr=FW086Manager.getInstance();
		String xml=mgr.decodeBodyXML( req.getBody());
		JSONObject jo=org.json.XML.toJSONObject(xml);
		int opType=Tools.getInt(jo.getString("OPType"),-1);
		String opNote=jo.getString("OPNote");
		String appId= jo.getString("AppID");
		String corpId=jo.optString("CorpID");
		
		/*if(!appId.equals(mgr.getAppId())){
			ri=  mgr.createResponse(req.getHead(), 1,"APPID����ȷ:"+ appId);
		}else{*/
			JSONObject rt;
			switch(opType){
			case 1:// ��ͨ
				// read corp id from message body
				
				//rt= mgr.getCorpInfo(jo.getString("CorpID"));
				rt= jo.getJSONObject("CorpInfo");
				ri= mgr.executeCommand("FW086_AdClientCreate", rt, req);
				break;
			case 2:// ��ͣ
				ri= mgr.createResponse(req.getHead(), 1,"Ŀǰ��֧�ֶԹ�˾����ͣ����");
				break;
			case 3:// �ָ�
				ri= mgr.createResponse(req.getHead(),1, "Ŀǰ��֧�ֶԹ�˾�Ļָ�����");
				break;
			case 4:// ����
				//rt= mgr.getCorpInfo(corpId);
				rt= jo.getJSONObject("CorpInfo");
				ri= mgr.executeCommand("FW086_AdClientUpdate", rt, req);
				break;
			case 5: // �˶�
				rt=new JSONObject();// mgr.getCorpInfo(corpId);
				rt.put("CorpID",corpId );
				ri= mgr.executeCommand("FW086_AdClientRemove", rt, req);
				break;
			default:
				ri=  mgr.createResponse(req.getHead(),1, "��֧�ֵ�optype:"+opType);	
				
			}
		//}
		return ri;
	}
	
}
