/*
 * Agile Control Technologies Ltd,. CO.
 * http://www.agileControl.com
 */
package nds.web.button;

import java.io.*;
import java.util.Hashtable;
import java.util.Locale;

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
import nds.util.WebKeys;
import nds.control.event.NDSEventException;
import nds.schema.*;
import nds.control.util.SecurityUtils;
import nds.control.web.*;
/**
 * ʹ�ô˷����İ�ť�����ڱ�Ӧ�þ���isstop�ֶΣ�="Y" ʱ����ť������ ����+"_OPEN" �洢���̣�����N������ť������ ��������_CLOSE������
 * 
 */
public class ButtonSwitchCloseOpen extends ButtonCommandUI_Impl{

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
		return  column.getTable().getRealTableName()+"_"+ (isStop(column,objectId)? "OPEN":"CLOSE");
	}	
	private boolean isStop(Column column, int objectId){
		boolean isStop=false;
		try{
			isStop=nds.util.Tools.getYesNo( (String)QueryEngine.getInstance().doQueryOne("select isstop from "+ column.getTable().getRealTableName()+" where id="+ objectId), false);
		}catch(Throwable t){
			logger.error("Fail to do isstop check on "+ column+", id="+objectId,t);
		}
		return isStop;
	}
	/**
	 * Button caption
	 * @return
	 */
	protected String getCaption(HttpServletRequest request, Column column, int objectId ){
		Locale locale = (Locale)request.getSession(true).getAttribute(org.apache.struts.Globals.LOCALE_KEY);
		if(locale==null)locale= TableManager.getInstance().getDefaultLocale();
		
		return  nds.util.MessagesHolder.getInstance().getMessage(locale, isStop(column,objectId)? "switch-open":"switch-close");
	}	
}
