/*
 * Agile Control Technologies Ltd,. CO.
 * http://www.agileControl.com
 */
package nds.control.web.binhandler;

import javax.servlet.*;
import javax.servlet.http.*;
import nds.schema.*;
import nds.util.*;
import java.util.*;
import java.io.*;
import nds.control.web.*;

/**
 * Return attachment
 * @author yfzhu@agilecontrol.com
 */

public class Attach implements BinaryHandler{
	public void init(ServletContext context){}
    /**
     * Things needed in this page:
     *  table* - (int) table id
     *  column* - (int) column id
     *  objectid* - (int) record id
     *  version - (int ) -1 if not found
     *  thum (String) if not found get thum img file
     */
	public void process(HttpServletRequest request,HttpServletResponse  response)  throws Exception{
		UserWebImpl userWeb =null;
		userWeb= ((UserWebImpl)WebUtils.getSessionContextManager(request.getSession(true)).getActor(nds.util.WebKeys.USER));	
		
		TableManager tableManager=TableManager.getInstance();
		int tableId= ParamUtils.getIntAttributeOrParameter(request, "table", -1);
		int columnId =ParamUtils.getIntAttributeOrParameter(request, "column", -1);
		int objectId= ParamUtils.getIntAttributeOrParameter(request, "objectid", -1);
		int version= ParamUtils.getIntAttributeOrParameter(request, "version", -1);
		String thum=ParamUtils.getAttributeOrParameter(request, "thum");
		InputStream is=null;
		Table table;
		if( tableId == -1) {
	        	throw new IllegalArgumentException("object type not set");
		}else{
	    	table= tableManager.getTable(tableId);
		}
		/**------check permission---**/
		if(!userWeb.hasObjectPermission(table.getName(),objectId,  nds.security.Directory.READ)){
		   	throw new NDSException("no permission");
		}
		/**------check permission end---**/

		Column col=tableManager.getColumn(columnId);
		AttachmentManager attm=(AttachmentManager)WebUtils.getServletContextManager().getActor(nds.util.WebKeys.ATTACHMENT_MANAGER);
		Attachment att= attm.getAttachmentInfo(userWeb.getClientDomain()+"/" + table.getRealTableName()+"/"+col.getName(),  objectId+"" , version);
		if(att!=null){
			String fileName= table.getName()+ "_"+ tableManager.getColumn(columnId).getName()+"_"+ objectId+"_"+att.getVersion()+"."+ att.getExtension();
	        String ct= Tools.getContentType(att.getExtension(), "application/octetstream");
	        response.setContentType(ct+"; charset=GBK");
	        //if(ct.indexOf("text/")>-1|| ct.indexOf("image")>-1)
	        /*
	         * ��ʵ����RFC2231�Ķ��壬�����Ա����Content-DispositionӦ����ô���壺

Content-Disposition: attachment; filename*="utf8''%E4%B8%AD%E6%96%87%20%E6%96%87%E4%BB%B6%E5%90%8D.txt"

����

    * filename����ĵȺ�֮ǰҪ�� *
    * filename��ֵ�õ����ŷֳ����Σ��ֱ����ַ���(utf8)������(��)��urlencode�����ļ�����
    * ��ü���˫���ţ������ļ����пո����Ĳ�����Firefox����ʾ������
    * ע��urlencode�Ľ����php��urlencode���������̫��ͬ��php��urlencode��ѿո��滻��+����������Ҫ�滻��%20
	         * 
	         */
        	response.setHeader("Content-Disposition","inline;"+ 
        			WebUtils.getContentDispositionFileName(att.getOrigFileName(), request));
        	//add get thum image file
        	if(thum!=null&&thum.equals("Y")){
        		is=attm.getAttachmentData(att,thum);
        	}else{
        		is=attm.getAttachmentData(att);
        	}
        	response.setContentLength((int)att.getSize());
        	 
			ServletOutputStream os = response.getOutputStream();
	            byte[] b = new byte[8192];
	            int bInt;
	            while((bInt = is.read(b,0,b.length)) != -1)
	            {
	                os.write(b,0,bInt);
	            }
	            is.close();
	            os.flush();
	            os.close();

			return;
		}else{
			PrintWriter writer = response.getWriter();
			writer.print(NO_ATTACH);
			writer.close();
		}
					
	}
	private final static String NO_ATTACH="<html><head><title>Attachment</title></head><body><p> File not found!</body></html>";
}
