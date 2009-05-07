/*
 * Agile Control Technologies Ltd,. CO.
 * http://www.agileControl.com
 */
package nds.web.button;

import java.io.*;
import java.util.Hashtable;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nds.log.Logger;
import nds.log.LoggerManager;
import nds.query.QueryEngine;
import nds.security.Directory;
import nds.security.LoginFailedException;
import nds.util.*;
import nds.control.event.NDSEventException;
import nds.schema.*;
import nds.control.util.SecurityUtils;
import nds.control.web.*;
/**
 * For M_V_N_INOUT table , ������Ԥ����Ԥ���ͽ��
 * �ֹ�����ⵥ������Ҫ��һ��ʱ�������ִ�У��������֮ǰ����Ҫ���ᣬ����ʹ�ñ���ť�����Ӧ����
 * 
 * ��ť������ m_inout �ϵ��ֶ� adj_storage ��ֵ�����ж����жϣ�'Y' ��ʶ�Ѿ�Ԥ����Ԥ���˿�棬��ť����ʾΪ���
 * 'N' (default) ��ʶδԤ����Ԥ����棬��ť�����ݳ�������;�������Ϊ��⣬��ʾԤ����������ʾΪԤ��
 * 
 * �����ݴ���Ԥ��Ԥ��״̬������ֹ����ϸ���е���
 */
public class ButtonMIOPrepareStorage extends ButtonCommandUI_Impl{
	/**
	 * Which real implementation will handle the command click event. Default sets to column.ValueInterpeter
	 * If extends this class, this method must be overriden.
	 * 
	 * �����չ�˴��࣬�������Column.ValueInterpeter �������µĴ����࣬���˷���Ӧ��ָ�����������ִ���࣬����Ҫ���ء�
	 * ��Ȼ����������� getHandleURL ��������������Ͳ��ᱻ�����ˡ�
	 * 
	 * @param request
	 * @param column
	 * @param objectId
	 * @return
	 */
	protected String getDelegator(HttpServletRequest request, Column column, int objectId){
		return "M_INOUT_PREPARE_STORAGE";
	}	
	/**
	 * 
	 * @return true when user can do submit, and the mio is unsubmitted
	 */
	protected boolean isValid(HttpServletRequest request, Column column, int objectId ){
		UserWebImpl userWeb= null;
		boolean b=false;
		try{
			int status= Tools.getInt(QueryEngine.getInstance().doQueryOne(
					"select status from m_inout a where id="+objectId), -1);
			if( status==1){
				userWeb=((UserWebImpl)WebUtils.getSessionContextManager(request.getSession(true)).getActor(nds.util.WebKeys.USER));	 
				b=SecurityUtils.hasObjectPermission(userWeb.getUserId(), userWeb.getUserName(),
						column.getTable().getName(),objectId, Directory.SUBMIT, userWeb.getSession() );
			}else{
				b=false;
			}
		}catch(Throwable t){
			logger.error("Could not check user permission on ButtonReclaim: column="+ column+", objectId="+objectId+
					", user="+ ( userWeb!=null? userWeb.getUserId():-1), t);
		}
		return b;
	}	
	/**
	 * Button caption
	 * ��ť������ m_inout �ϵ��ֶ� adj_storage ��ֵ�����ж����жϣ�'Y' ��ʶ�Ѿ�Ԥ����Ԥ���˿�棬��ť����ʾΪ���
 * 'N' (default) ��ʶδԤ����Ԥ����棬��ť�����ݳ�������;�������Ϊ��⣬��ʾԤ����������ʾΪԤ��
	 * @return
	 */
	protected String getCaption(HttpServletRequest request, Column column, int objectId ){
		boolean adjusted=false;
		String doctype;
		try{
			List al=QueryEngine.getInstance().doQueryList("select adj_storage, doctype from m_inout where id="+ objectId);
			adjusted= "Y".equals( ((List)al.get(0)).get(0));
			doctype=  (String)((List)al.get(0)).get(1);
		}catch(Exception e){
			logger.error("Fail to get columns from m_inout (id="+objectId+"):"+ e.getLocalizedMessage());
			return "unknown";
		}
		boolean in= "MMR".equals( doctype);
		String caption= adjusted? (in? "clear-added-storage":"clear-preserved-storage"): (in?"pre-add-storage":"pre-reserve-storage");
		Locale locale = (Locale)request.getSession(true).getAttribute(org.apache.struts.Globals.LOCALE_KEY);
		return MessagesHolder.getInstance().getMessage(locale, caption);
	}	
}
