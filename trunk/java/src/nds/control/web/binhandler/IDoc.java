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
import java.net.URI;


import nds.control.web.*;

/**
 * IDoc is used for client application to handle specific tasks locally.
 * 
 * ��portal�Ͻ�����Ӧ�ı�����button���͵��ֶΣ�nds.web.button.ButtonLocalProcess��
 * ���ֶ���ʾ����Ϊ����ǰ�û��Ƿ�Ϊ���ݵĿ��޸�Ȩ���û����������i_doc���д���/���¼�¼��
 * Ȼ���ض��򵽷��������ļ������� /servlets/binserv/IDoc�������Ӷ�Ӧnds. control.web.binhandler.IDoc��
 * �����жϵ�ǰ�û��Ƿ����i_doc��Ӧ�ļ�¼��Դ����޸�Ȩ�ޣ�����ǣ�������.nea�ļ����ṩ���ͻ���
 * 
 * @author yfzhu@agilecontrol.com
 * @since 4.0 
 */

public class IDoc implements BinaryHandler{
    /**
     * @param request, contains:
     * 	"docno" - doc no for idoc
     */
	public void process(HttpServletRequest request,HttpServletResponse  response)  throws Exception{
		
		nds.export.IDoc  doc=new nds.export.IDoc();
		URI u= new URI(request.getRequestURI());
		String wsdl=new URI(u.getScheme(),
		        u.getUserInfo(), u.getHost(), u.getPort(),
		        "/services/DocService", null,null).toString();
			//((Configurations)WebUtils.getServletContextManager().getActor( nds.util.WebKeys.CONFIGURATIONS)).getProperty("web.url","http://localhost");
		//String wsdl= webRoot +"/services/DocService"; 
		doc.setWsdl(wsdl);
		
		doc.setDocNo(request.getParameter("docno"));
		
		doc.setIpAddress(request.getRemoteAddr());
		
		doc.setSessionId(request.getSession().getId());
		
		UserWebImpl user=(UserWebImpl)WebUtils.getSessionContextManager(request.getSession()).getActor(nds.util.WebKeys.USER);
		doc.setUser(user.getUserName()+"@"+ user.getClientDomain());

		byte[] bytes = doc.getBytes() ;
		
		response.setContentType(doc.MIME_TYPE);
		response.setHeader("Content-Disposition","inline;filename=\""+doc.getDocNo()+"."+ doc.FILE_EXTENSION+"\"");		
		response.setContentLength(bytes.length);
		ServletOutputStream ouputStream = response.getOutputStream();
		ouputStream.write(bytes, 0, bytes.length);
		ouputStream.flush();
		ouputStream.close();

					
	}
	
}
