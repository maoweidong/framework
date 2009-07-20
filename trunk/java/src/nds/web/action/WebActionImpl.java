package nds.web.action;


import nds.log.Logger;
import nds.log.LoggerManager;
import nds.query.*;
import nds.schema.*;
import nds.util.*;
import nds.control.web.*;
import org.json.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class WebActionImpl implements WebAction{
	protected Logger logger=LoggerManager.getInstance().getLogger(this.getClass().getName());
	
	protected int id;
	protected String name;
	protected String description;
	protected String iconURL;
	/**
	 * AD_TABLE.ID
	 */
	protected int tableId;
	/**
	 * AD_TABLECATEGORY.ID�����TreeNode
	 */
	protected int tableCategoryId;
	/**
	 * ad_subsystem.id �����TreeNode
	 */
	protected int subSystemId;
	/**
	 * ��ʾ�����ֶΣ�֧��sql �� beanshell (java �Ľ����νű�����) �����﷨
	 * sql	����:Select count(*) from xxx where yyy
			��select �ĵ�һ���ֶη��ص����ݴ������ʾ��ʾ��С�ڵ������ʾ����ʾ��Sql �п������û���������
		beanshell	������Xxx
			Return true | false;
			Script ���Ի�õĻ�������Ϊ request, response, session��
		������sql ���� beanshell������ͨ��{@link #isSQLFilter} ��ȷ��	
	 */
	protected String filter;
	/**
	 * ��ʾ�����ֶε�������sql ���� beanshell����ͨ����������ж�
	 */
	protected boolean isSQLFilter;
	/**
	 * ����url, javascript, stored procedure, ad_process, beanshell, os command ���ڵĶ����ڽű�����
	 */
	protected String script;
	/**
	 * ���url, ������� _xxx��ͷ������Ϊ�ǵ�ǰҳ���һ��div��id
	 */
	protected String urlTarget;
	/**
	 * 
	 * �Ƿ����� [YESNO]�� ���Button��MenuItem
	 */
	protected boolean shouldConfirm;
	protected String comments;
	/**
	 * �ڽ������ŷŵ�λ��, ԽСԽ��ǰ
	 */
	protected int order;
	protected SaveObjectEnum saveObjType;
	protected ActionTypeEnum actionType;
	protected DisplayTypeEnum displayType;
	
	public WebActionImpl(){}
	public WebActionImpl(int id){
		this.id =id;
	}
	 
	/**
	 * Used for {@link #canDisplay(Map)}
	 * @param param the parameter name to look for
	 * @param env
	 * @param defaultValue 
	 * @param mustExist, will throw error if not found in env
	 * @return object in env
	 */
	protected Object getValueFromMap(String param,Map env, Object defaultValue, boolean mustExist){
		Object v= env.get(param);
		if(v==null){
			if(mustExist) throw new IllegalArgumentException(param + " not found in env :"+ Tools.toString(env));
			else v= defaultValue;
		}
		
		return v;
	}
	/**
	 * Check whether this action can display in specified session or not.
	 * @param env contains web environment, mainly "httpservletrequest", "connection" 
	 * @return true if can be displayed
	 */
	public boolean canDisplay(Map env) throws Exception{
		boolean b=false;
		if(nds.util.Validator.isNull(filter)) return true;
		String f=filter;
		HttpServletRequest request=(HttpServletRequest) getValueFromMap("httpservletrequest", env, null,true);
		UserWebImpl userWeb= (UserWebImpl)getValueFromMap("userweb", env, null,true);
		Connection conn= (Connection)getValueFromMap("connection", env, null,true);
		
		f=QueryUtils.replaceVariables(f,userWeb.getSession());

		if(isSQLFilter){
			// replace environment variables
			
			int cnt= Tools.getInt(QueryEngine.getInstance().doQueryOne(f,conn), -1);
			b=(cnt>0); 
		}else{
			Object ret=BshScriptUtils.evalScript(f,new StringBuffer(),false, env);
			//when null, return false
			if(ret!=null){
				if(ret instanceof Boolean) b= ((Boolean)ret).booleanValue();
				else if(ret instanceof java.lang.Number) b=((Number)ret).intValue()>0;
				else b= Tools.getBoolean(ret, false);
			}
		}
		return b;
	}

	/**
	 * This can be url, ad_process.name, beashell script, os command, and so on
	 * the content comes from ad_action.content, ad_action.scripts in order
	 * @return
	 */
	public String getScript(){
		return script;
	}
	
	/**
	 * Execute scripts defined by script including BeanShell, stored procedure, and OS command
	 * @param params contains environment parameters, such as operator, connection.
	 * @return at least contains "code" (String of Integer), message (String), some script may
	 * 	return more information
	 */
	public Map execute(Map params) throws Exception{
		HashMap map=new HashMap();
		switch(actionType){
		case StoredProcedure:
			
			Connection conn= (Connection)getValueFromMap("connection",params,null,true);
			Integer userId= (Integer)getValueFromMap("userid",params,null,true);
			JSONObject query= (JSONObject)getValueFromMap("query",params,null,false);
			String qs=null;
			if(query!=null){
				// recontruct query to xml format
				qs= org.json.XML.toString(query);
				logger.debug("query:"+qs);
			}else
				qs="";
			
			ArrayList p=new ArrayList();
			p.add(userId);
			p.add(qs);
			SPResult ret=QueryEngine.getInstance().executeStoredProcedure(this.getScript(), p, true, conn);
			map.put("code",ret.getCode());
			map.put("message",ret.getMessage());
			break;
		case BeanShell:
			
			Object br=BshScriptUtils.evalScript(this.getScript(),new StringBuffer(),false, params);
			if(br instanceof Map){
				map.putAll((Map)br);
			}else if(br instanceof Number){
				map.put("code", ((Number)br).intValue());
			}else{
				map.put("code",0);
				map.put("message", br.toString());
			}
			break;
		case OSShell:
			
			Configurations conf= (Configurations)WebUtils.getServletContextManager().getActor( nds.util.WebKeys.CONFIGURATIONS);
			
			String logFile = conf.getProperty("dir.tmp","/tmp") + File.separator+ "ExecWebAction_"+ this.getId()+"_"+System.currentTimeMillis()+".log"; 
			CommandExecuter cmdE= new CommandExecuter(logFile);
			
			int err=cmdE.run(this.getScript());
			
			UserWebImpl usr= (UserWebImpl)getValueFromMap("userweb",params,null,true);
			logger.info("User "+ usr.getUserName() + "@" + usr.getClientDomain()+" runs command :"+ this.getScript()+" with return code:"+err);
    		SysLogger.getInstance().info("sys", "exec", usr.getUserName(), "", getScript()+"("+ err+")", usr.getAdClientId());
			String message= Tools.readFile(logFile);
			//delete log file
			FileUtils.delete(logFile);
			
			map.put("code",err);
			map.put("message",message);
			break;
		default:
			throw new NDSException("Not supported action type in execute:"+ actionType.getType());
		}
		return map;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public void setActionType(ActionTypeEnum actionType) {
		this.actionType = actionType;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setDisplayType(DisplayTypeEnum displayType) {
		this.displayType = displayType;
	}
	
	public void setFilter(String filter) {
		if(filter ==null) return;
		this.filter = filter.trim();
		this.isSQLFilter= this.filter.toUpperCase().startsWith("SELECT"); 
	}
	
	public void setIconURL(String iconURL) {
		this.iconURL = iconURL;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setOrder(int od) {
		this.order = od;
	}
	
	public void setTableCategoryId(int tableCategoryId) {
		this.tableCategoryId = tableCategoryId;
	}
	
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	public void setSaveObjType(SaveObjectEnum saveObjType) {
		this.saveObjType = saveObjType;
	}
	public int getId(){
		return id;
	}
	
	public ActionTypeEnum getActionType() {
		return actionType;
	}
	public String getComments() {
		return comments;
	}
	public String getDescription() {
		return description;
	}
	public DisplayTypeEnum getDisplayType() {
		return displayType;
	}
	public String getFilter() {
		return filter;
	}
	public String getIconURL() {
		return iconURL;
	}
	public String getName() {
		return name;
	}
	public int getOrder() {
		return order;
	}
	public SaveObjectEnum getSaveObjType() {
		return saveObjType;
	}
	public int getTableCategoryId() {
		return tableCategoryId;
	}
	public int getTableId() {
		return tableId;
	}
	public String getUrlTarget() {
		return urlTarget;
	}
	
	public void setUrlTarget(String urlTarget) {
		this.urlTarget = urlTarget;
	}
	
	public void setScript(String script) {
		if(script!=null)this.script = script.trim();
		else script="";
	}
	public int getSubSystemId() {
		return subSystemId;
	}
	public void setSubSystemId(int subSystemId) {
		this.subSystemId = subSystemId;
	}
	
}
