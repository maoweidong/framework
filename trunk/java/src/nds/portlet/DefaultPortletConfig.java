package nds.portlet;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import nds.log.Logger;
import nds.log.LoggerManager;
import nds.portlet.util.PortletUtils;
import nds.util.Tools;
import nds.util.Validator;

import com.liferay.portal.util.PortalUtil;
import com.liferay.util.ParamUtil;

/**
* Include these parameters set on page
                 ListenTo[] - portlet������Щportlet�����������û�����ͨ����������ָ��(��õ�ǰLayout������Ŀǰ���ڵ�portlet�ķ���
                                �μ�html/portal/layout_portlets.jsp)
                Priority(layout)  - ���������ȼ��� ��ListenTo��أ���ͬ��ListenTo portlet ���в�ͬ�����ȼ�
            StopChain(layout) - ���ڱ��Ҵ�����ɵ���Ϣ���Ƿ���ֹ����portlet����Ӧ�ʹ���ֻ����һ����Ӧ����)�������������objectlist ��Ҫ����ʾ��
                                ʱ�򣬽�����������list : wide and narrow��������ֻҪ��һ��portlet����Ϳ�����
                EventType(layout) - ��Ӧ��Ϣ���ͣ�������Щ��Ϣ����( ListenTo ��������Ϣ�ж��֣���һ�����Ǳ�portlet��Ҫ��Ӧ��)
                EventToChain(layout)	- �Ƿ�ҳ������ݷ���Chain���Ա���ͬҳ����������ChainPortlet ������Ӧ
                DefaultPage(layout)	   -�����������Ϣû���κ�portlet��Ӧ�������ĸ�page����Ӧ���������磬��ǰlayout ��û��
                           objectlist portlet�����˵��������ʱ��ϵͳ������һ���µĴ��ڣ����ڱ�����,��defaulttarget����
                           ����ʾ�����б�
                           ��ѡpage �Ļ�òμ���html\common\init.jsp
                           Layout layout = (Layout)request.getAttribute(WebKeys.LAYOUT);
                          Layout[] layouts = (Layout[])request.getAttribute(WebKeys.LAYOUTS);

                DefaultTarget(layout) - defaultpage ������ʱ��Ŀ��λ��: _self, _blank
 *
 */
public class DefaultPortletConfig {
	private static Logger logger= LoggerManager.getInstance().getLogger(DefaultPortletConfig.class.getName());
    private final static String PREFIX="config.";
    private int priority;
    private String[] listenTo;
    private boolean stopChain;
    private String[] eventTypes;
    private boolean eventToChain;
    private String defaultPage;
    private String defaultTarget;


    public DefaultPortletConfig() {
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
    /**
    * Portlet Ids
    */
    public String[] getListenTo() {
        return listenTo;
    }
    /**
    * Portlet NameSpaces that this portlet will listens to
    */
    public String[] getListenToNameSpaces(){
    	String[] s= new String[listenTo.length];
    	for(int i=0;i<s.length;i++)
    		s[i]=PortalUtil.getPortletNamespace(listenTo[i]);

    	return s;
    }
    public void setListenTo(String[] listenTo) {
        this.listenTo = listenTo;
    }
    public boolean isStopChain() {
        return stopChain;
    }
    public void setStopChain(boolean stopChain) {
        this.stopChain = stopChain;
    }
    public String[] getEventTypes() {
        return eventTypes;
    }
    public void setEventTypes(String[] eventType) {
        this.eventTypes = eventType;
    }
    public boolean isEventToChain() {
        return eventToChain;
    }
    public void setEventToChain(boolean eventToChain) {
        this.eventToChain = eventToChain;
    }
    public String getDefaultPage() {
        return defaultPage;
    }
    public void setDefaultPage(String defaultPage) {
        this.defaultPage = defaultPage;
    }
    public String getDefaultTarget() {
        return defaultTarget;
    }
    public void setDefaultTarget(String defaultTarget) {
        this.defaultTarget = defaultTarget;
    }
    public static DefaultPortletConfig loadFromRequest(PortletConfig config,ActionRequest r){
        DefaultPortletConfig c=new DefaultPortletConfig();
        c.setDefaultPage(getValue(r, "defaultPage", PortletUtils.getDefaultEventHandleLayout(config, r )));
        c.setDefaultTarget(getValue(r,"defaultTarget", "_self"));
        c.setEventToChain(Tools.getBoolean(getValue(r,   "isEventToChain", "true"),true));
		logger.debug("eventTypes:"+ getValues(r, "eventTypes", new String[]{}));
        c.setEventTypes(getValues(r, "eventTypes", new String[]{}));

        c.setListenTo(getValues(r, "listenTo",new String[]{}));

        c.setPriority(Tools.getInt(getValue(r,"priority", "0"), 0 ));

        c.setStopChain(Tools.getBoolean(getValue(r, "isStopChain", "true"),true));
        return c;

    }

    public static DefaultPortletConfig loadFromPreferences(PortletConfig config, PortletRequest req){
        DefaultPortletConfig c=new DefaultPortletConfig();
        PortletPreferences r= req.getPreferences();
        c.setDefaultPage(getValue(r, "defaultPage", PortletUtils.getDefaultEventHandleLayout(config, req )));
        c.setDefaultTarget(getValue(r,"defaultTarget", "_self"));
        c.setEventToChain(Tools.getBoolean(getValue(r,   "isEventToChain", "true"),true));

        c.setEventTypes(getValues(r, "eventTypes", new String[]{}));

        c.setListenTo(getValues(r, "listenTo",new String[]{}));

        c.setPriority(Tools.getInt(getValue(r,"priority", "0"), 0 ));

        c.setStopChain(Tools.getBoolean(getValue(r, "isStopChain", "true"),true));
        return c;
    }
    /**
     * Save to preferences
     * @param prefs PortletPreferences
     */
    public void store(PortletPreferences prefs) throws javax.portlet.ReadOnlyException {
        prefs.setValue(PREFIX+ "defaultPage", getDefaultPage());
        prefs.setValue(PREFIX+ "defaultTarget", getDefaultTarget());
        prefs.setValues(PREFIX+ "eventTypes", getEventTypes());
        prefs.setValues(PREFIX+ "listenTo", getListenTo());
        prefs.setValue(PREFIX+ "priority", ""+getPriority());
        prefs.setValue(PREFIX+ "isEventToChain", ""+isEventToChain());
        prefs.setValue(PREFIX+ "isStopChain", ""+isStopChain());
    }
    private static String getValue(PortletRequest req, String name, String defaultValue){

        return ParamUtil.getString(req, name , defaultValue);
    }
    /**
    * String is parsed by comma to array
    */
    private static String[] getValues(PortletRequest req, String name, String[] defaultValue){
        String r= ParamUtil.getString(req, name );
        if( Validator.isNotNull(r)){
        	StringTokenizer st= new StringTokenizer(r,",");
        	ArrayList al=new ArrayList();
        	while(st.hasMoreTokens()){
        		String t= st.nextToken();
        		if( Validator.isNotNull(t)) al.add(t);
        	}
        	String[] s=new String[al.size()];
        	for(int i=0;i<s.length;i++) s[i]=(String) al.get(i);
        	return s;
        }
        return defaultValue;
    }


    private static String getValue(PortletPreferences prefs, String name, String defaultValue){
        return prefs.getValue( PREFIX + name , defaultValue);
    }
    private static String[] getValues(PortletPreferences prefs, String name, String[] defaultValue){
        return prefs.getValues(PREFIX + name , defaultValue);
    }
    public String toDebugString(){
        StringBuffer sb=new StringBuffer();
        sb.append("defaultPage:"+ getDefaultPage()+Tools.LINE_SEPARATOR);
        sb.append("defaultTarget:"+ getDefaultTarget()+Tools.LINE_SEPARATOR);
        sb.append("eventTypes:"+ Tools.toString(getEventTypes())+Tools.LINE_SEPARATOR);
        sb.append("listentos:"+ Tools.toString(getListenTo())+Tools.LINE_SEPARATOR);
        sb.append("priority:"+ getPriority()+Tools.LINE_SEPARATOR);
        return sb.toString();
    }

}
