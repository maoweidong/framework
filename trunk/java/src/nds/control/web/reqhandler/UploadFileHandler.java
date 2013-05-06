/******************************************************************
*$RCSfile: UploadFileHandler.java,v $ $Revision: 1.2 $ $Author: Administrator $
* $Date: 2005/08/28 00:27:03 $
********************************************************************/
package nds.control.web.reqhandler;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import nds.control.event.DefaultWebEvent;
import nds.control.event.NDSEvent;
import nds.control.event.NDSEventException;
import nds.control.web.*;
import nds.log.Logger;
import nds.log.LoggerManager;
import nds.schema.Column;
import nds.schema.Table;
import nds.schema.TableManager;
import nds.util.*;

import org.apache.commons.fileupload.*;
import org.json.JSONObject;



/**
Ŀ�꣺�����û��ĸ����ĵ�
���ܣ��ĵ��б�����˵�����ĵ��ϴ������أ�Ȩ�޹����ĵ��汾��������ҵ��ϵͳ�����Ʒ���ϣ�������Ʒ���ñ�

˼·�������µ�obtainmanner=attach, Ϊurl+button �ķ�ʽ��url Ϊ���ص�ַ��button Ϊ�ϴ���ť��
����ϴ���ť���ϴ�ҳ�棬���ϴ��ļ���ϵͳ���ļ����ڹ̶�Ŀ¼��
upload.root/$clientdomain/tablename/columnname/objectId-dir, 
Ŀ¼�д�Ŷ�Ӧ�ĸ��������汾������Ϊ1.ext, 2.ext, ���������ļ�attachment.properties

���⣬ҲӦ������ָ��ϵͳ���һ����ַ (upload=false ʱ��ȡ fileurl ָ����ֵ��Ϊurl)��

���ֶ��б������������Ϣ��������ϴ����ļ����Զ���¼Ϊ
/attach?table=1032&column=10039&objectid=9290
�����������Ϣ�����û�����ָ��

���ָ������:copytoroot - ��ʾ��Ҫ���ļ�������upload.root/$clientdomain/tablename/columnname
Ŀ¼������������������ļ��ϴ������ַ�ʽ�������ϴ��ļ�������ҪЭ������������磺����
��������
���ϴ��ļ�Ϊjpg �� pngͼƬ�ϴ�ʱ ���Ǹ��ݲ������ɶ�Ӧ����ͼ�ڽ���չʾ

 */
public class UploadFileHandler extends RequestHandlerSupport {
    private static Logger logger=LoggerManager.getInstance().getLogger(UploadFileHandler.class.getName());

    public UploadFileHandler() {}

    public NDSEvent processRequest(HttpServletRequest request) throws NDSEventException {
        try {
        	DefaultWebEvent event=new DefaultWebEvent("CommandEvent");
        	event.setParameter("command", "SaveAttachmentURL");
        	HttpSession session=request.getSession(true);
            WebUtils.getSessionContextManager(session);
            Configurations conf=(Configurations)WebUtils.getServletContextManager().getActor(WebKeys.CONFIGURATIONS);
            AttachmentManager attm=(AttachmentManager)WebUtils.getServletContextManager().getActor(WebKeys.ATTACHMENT_MANAGER);
            UserWebImpl user= ((UserWebImpl)WebUtils.getSessionContextManager(session).getActor(WebKeys.USER));
            String objectpath=null;
            String operatorName= user.getUserName() ; // this will be the sub directory name
            
            DiskFileUpload  fu = new DiskFileUpload();
            // maximum size before a FileUploadException will be thrown
            fu.setSizeMax(1024*1024*1024); // 1GB
            
            List fileItems = fu.parseRequest(request);
            Iterator iter = fileItems.iterator();
            InputStream in=null;
            String fileName="";
            File f=null;
            while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();

                    if (item.isFormField()) {
                        event.setParameter(item.getFieldName(), item.getString());
                        // add to request attributes for later usage
                        // since 2.0
                        request.setAttribute(item.getFieldName(),item.getString() );
                    } else {
                        in=item.getInputStream();
                        fileName= item.getName();
                        //remove file path
                        if(fileName!=null){
                        	int pos= fileName.lastIndexOf("\\");
                        	if(pos>0)
                        		fileName= fileName.substring(pos+1 );
                        	pos= fileName.lastIndexOf("/");
                        	if(pos>0)
                        		fileName= fileName.substring(pos+1 );
                        }
                    }
            }
            if(in !=null &&  Tools.getBoolean(event.getParameterValue("upload"), false)==true){
            // save to file system
        		TableManager manager= TableManager.getInstance();
        		Table table= manager.findTable( event.getParameterValue("table"));
        		Column col= manager.getColumn( Tools.getInt(event.getParameterValue("column"), -1));
            	int objectId= Tools.getInt( event.getParameterValue("objectid"),-1);
            	
            	//add objectId=-1 first add file 
            	if(objectId==-1){
            		//tmpobjectId
            		long Temp=Math.round(Math.random()*89999+10000);
            		objectpath="tmp"+Temp;
            	}else{
            		objectpath=""+objectId;
            	}
            	
                if(objectId!=-1&&!user.hasObjectPermission(table.getName(),objectId,  nds.security.Directory.WRITE)){
                	throw new NDSException("Ȩ�޲��㣡");
                }
            	
            	Attachment att= attm.getAttachmentInfo(user.getClientDomain()+"/" + table.getRealTableName()+"/"+col.getName(),  objectpath, -1);
            	if (att==null){
            		// create it
            		att= new Attachment( user.getClientDomain()+"/" + table.getRealTableName()+"/"+col.getName(), objectpath);
            		att.setAuthor(user.getUserName());
            		att.setVersion(0);
            		att.setExtension(attm.getFileExtension(fileName));
            		att.setOrigFileName(fileName);
            	}else{
            		//add att new file name
            		att.setOrigFileName(fileName);
            	}
            	//�ж��ϴ��ֶ��Ƿ���ͼƬ��������
            	if(col.getJSONProps()!=null){
            		JSONObject jor=col.getJSONProps();
            		if(jor.has("thumg")){
    					JSONObject jo=col.getJSONProps().getJSONObject("thumg");
    					int width=jo.optInt("width",9999);
    					att.setWithimg(width);
            		}
            	}
            		f=attm.putAttachmentData(att, in);
            	//if copytoroot, will copy file to 
            	//upload.root/$clientdomain/tablename/columnname
            	if( Tools.getBoolean(event.getParameterValue("copytoroot"),false)==true){
            		File ftemp= new File(fileName);
            		File f2=new File(attm.getRootPath(), user.getClientDomain()+"/" + table.getRealTableName()+"/"+col.getName()+"/"+ftemp.getName());
            		Tools.copyFile(f, f2, true, true);
            	}
            }
            if(objectpath!=null)event.put("objectpath",objectpath);
            if(user !=null && user.getSession()!=null)
            	event.put("nds.query.querysession",user.getSession());
            return event;
        }
        catch (Exception ex) {
            logger.error("error handling upload file request", ex);
            if(ex instanceof NDSEventException) throw (NDSEventException)ex;
            else throw new NDSEventException("�޷������ϴ�����:"+ ex);
        }
    }
}
