/*
 * Agile Control Technologies Ltd,. CO.
 * http://www.agileControl.com
 */
package nds.query.web;

import java.io.UnsupportedEncodingException;

import nds.control.web.WebUtils;
import nds.log.Logger;
import nds.log.LoggerManager;
import nds.schema.Column;
import nds.schema.Table;
import nds.util.*;
import nds.util.PairTable;
import nds.util.Tools;
import nds.util.WebKeys;
import org.json.*; 
import java.util.*;
/**
 * Contains information for construting FK object link
 * FK query has two form: dropdown list, and table quick search form, that is decided by
 * table's rowcount. 
 * 
 * If rowcount is less than value of property "query.fk.list", then should show dropdown list.
 *  
 * @author yfzhu@agilecontrol.com
 */
public class FKObjectQueryModel {
	private static Logger logger=LoggerManager.getInstance().getLogger(FKObjectQueryModel.class.getName());
	private static int listThreshold=-1; // dropdown limit
	
	private String url;
	/**
	 * this contains reference columns in wildcard filter, if the acceptor column is filtered by wildcard
	 * format:
	 * {ta:true|false, rc:[<columnId>,<columnId>...], prc:[<columnId>,<columnId>...]}
	 * ta: signs for whether current searchOnColumn is in title area or not
	 * rc: reference columns in wildcard filter, which is in same table as searchOnColumn
	 * prc:reference columns in wildcard filter, which is in parent table of searchOnColumn
	 */
	private String options;  
	private String acceptorId;
	private boolean isDropdown;
	private boolean isSingle_temp;
	private int queryindex;
	/**
	 * @param isInTitleArea when searchOnColumn is WFC(WildcardFilter Column, Column.isFilteredByWildCard=true), this param will decide
	 * 	where to fetch the reference column in WFC filter.
	 * 	a)	���isInTitleArea=true��ʶ��ÿ�������ֶΣ���@�������ض��������ϣ�������������䣺
url=����PATH?...& wfc_<�ֶ�id>=��+ oc.getMainTableColumnInput(<�ֶ�id>)+��&wfc_<�ֶ�id2>=��+oc. getMainTableColumnInput (<�ֶ�id2>)

java ʶ���ֶ�ΪisFilteredByWildCard ʱ������ȡ wfc_<�ֶ�id> ����ֵ��ֱ�ӹ��쵽column ��filter ����У�����column.filter �ڲ�ѯ�������ʾΪ: select * from m_product where M_PRODUCT.C_BPARTNER_ID=123 and xxxx

�����ҪΪ�ֶ����ӹ�����ʾ���ҹ��ܣ�����ʹ��onchange��������ȡ����ֵ��Ȼ����к�������
b)	���isInTitleArea=false��ʶ��ÿ�������ֶΣ�������ڵ�ǰ���ϣ���������column���ڱ����ͱض��������ϡ������ڵ�ǰ����ֶΣ�ͨ��oc. getInlineTableColumnInput��ȡ����ֵ�����ڲ��ڵ�ǰ����ֶΣ�ͨ��oc.getMainTableColumnInput��Ϊ����ֵ������������䣺
url=����PATH?...& wfc_<�ֶ�id>=��+ oc.getMainTableColumnInput(<�ֶ�id>)+��&wfc_<�ֶ�id2>=��+oc. getInlineTableColumnInput (<�ֶ�id2>)

������������ݣ��ں�̨ͳһУ�飬ʶ��@@��Χ���ֶΣ�����ͬ����ֶΣ������event��û�ҵ����͵����ݿ�ĵ�ǰ��¼��ȥ�ң��������޸Ķ���ʱ�������ڷ�ͬ����ֶΣ�һ����ͨ�����ݿ�Ĳ�ѯ��ȡ�����ݵ�ǰ��ĵ�һ�����ӵ������������ֶε�id�ҵ����ű��Ӧ�ļ�¼��Ȼ���ȡ���ֵ����ƥ�䡣

     * @param table foreign key table
     * @param acceptorId so that script can get content by document.getElementById(acceptorId),
     * normally acceptor is Input text type 
     * @param column main table's column that show as dropdown or search window, can be null
	 * @param fixedColumns key: ColumnLink/String for that, Value:String. Will filter query with pt, pt will be converted to nds.query.Expression
	 * @see nds.query.Expression#paresePairTable
	 */
	public FKObjectQueryModel( boolean isDropdown, boolean isInTitleArea, Table table, String acceptorId,  Column searchOnColumn, PairTable fixedColumns){
		boolean isSingle=true;
		this.isDropdown=isDropdown;
		this.acceptorId= acceptorId;
		this.isSingle_temp=true;
		try {
			url=("'" +WebKeys.NDS_URI+"/query/"+(isDropdown?"dropdown.jsp":"search.jsp") +"?table="+
			table.getId()+"&return_type="+ (isSingle?"s":"m") + (searchOnColumn==null?"":"&column="+searchOnColumn.getId())+"&accepter_id="+acceptorId+
			(fixedColumns==null?"":"&fixedcolumns="+ 
			java.net.URLEncoder.encode(fixedColumns.toURLQueryString(""), "UTF-8"))+
			"&qdata='+encodeURIComponent(document.getElementById('"+acceptorId+"').value)");
			
			if(searchOnColumn.isFilteredByWildcard()){
				int tableId= searchOnColumn.getTable().getId();
				JSONObject jo=new JSONObject();
				org.json.JSONArray ja=new JSONArray();
				org.json.JSONArray jap=new JSONArray();
				
				jo.put("ta", isInTitleArea);
				//{ta:true|false, rc:[<columnId>,<columnId>...], prc:[<columnId>,<columnId>...]}
				List al=searchOnColumn.getReferenceColumnsInWildcardFilter();
				for(int i=0;i< al.size();i++){
					Column col=(Column)al.get(i);
					if(col.getTable().getId()== tableId)ja.put(col.getId());
					else jap.put(col.getId());
				}
				jo.put("rc", ja);
				jo.put("prc",jap);
				options= jo.toString().replaceAll("\"", "&quot;");
			}
		} catch (Throwable e) {
		    // The system should always have the platform default "UTF-8"
			logger.error("Fail to init FKObjectQueryModel for column:"+searchOnColumn, e );
			throw new NDSRuntimeException(e.getMessage());
		}
	}
	public FKObjectQueryModel( boolean isInTitleArea, Table table, String acceptorId,  Column searchOnColumn, PairTable fixedColumns){
		this(table.isDropdown(),isInTitleArea,table,acceptorId,searchOnColumn,fixedColumns);
	}
	public FKObjectQueryModel(Table table, String acceptorId,  Column searchOnColumn, PairTable fixedColumns, boolean isSingle){
		if(isSingle)isDropdown= table.isDropdown();
		else isDropdown=false;
		this.acceptorId= acceptorId;
		if(!isSingle&&table.isDropdown()){
		    this.isSingle_temp=true;
		}else{
			this.isSingle_temp=isSingle;
		}
		try {
			url="'" +WebKeys.NDS_URI+"/query/"+(isDropdown?"dropdown.jsp":"search.jsp") +"?table="+
			table.getId()+"&return_type="+ (isSingle?"s":"m") + (searchOnColumn==null?"":"&column="+searchOnColumn.getId())+"&accepter_id="+acceptorId+
			(fixedColumns==null?"":"&fixedcolumns="+ 
			java.net.URLEncoder.encode(fixedColumns.toURLQueryString(""), "UTF-8"))+
			"&qdata='+encodeURIComponent(document.getElementById('"+acceptorId+"').value)";
		} catch (UnsupportedEncodingException e) {
		    // The system should always have the platform default "UTF-8"
			logger.error("Fail to encode using UTF-8", e);
		}
	}	
	/**
	 * Single object search
     * @param table foreign key table
     * @param acceptorId so that script can get content by document.getElementById(acceptorId),
     * normally acceptor is Input text type 
     * @param column main table's column that show as dropdown or search window, can be null
	 * @param fixedColumns key: ColumnLink/String for that, Value:String. Will filter query with pt, pt will be converted to nds.query.Expression
	 * @see nds.query.Expression#paresePairTable
	 */
	public FKObjectQueryModel(Table table, String acceptorId,  Column searchOnColumn, PairTable fixedColumns){
		this(table,acceptorId,searchOnColumn,fixedColumns,true );
	}
	/**
     * @param table foreign key table
     * @param acceptorId so that script can get content by document.getElementById(acceptorId),
     * normally acceptor is Input text type 
     * @param column main table's column that show as dropdown or search window
	 */
	public FKObjectQueryModel(Table table, String acceptorId){
		this(table,acceptorId, null, null );
	}	
	/**
     * @param table foreign key table
     * @param acceptorId so that script can get content by document.getElementById(acceptorId),
     * normally acceptor is Input text type 
     * @param column main table's column that show as dropdown or search window
	 */
	public FKObjectQueryModel(Table table, String acceptorId, Column column){
		this(table,acceptorId, column, null );
	}

	/**
	 * script for key event handling
	 * 
	 */
	public String getKeyEventScript(){
		return  isDropdown?"handleDropdownInputKey(event,"+url+");":"handleObjectInputKey(event,"+url+");";
	}
	/**
	 * 
	 * @return button event script when user clicked
	 * @param mustBeActive if false, will inculde data that has "isactive" set to "N"
	 */
	public String getButtonClickEventScript(boolean mustBeActive){
		String bUrl= url;
		String oqUrl= url;
		if(!mustBeActive) {
			bUrl= bUrl+"+'&mustbeactive=N'";
			oqUrl=oqUrl+"+'&mustbeactive=N'";
		}
		oqUrl=oqUrl+"+'&queryindex='+encodeURIComponent(document.getElementById('queryindex_"+this.queryindex+"').value)";
		return (isDropdown? "dq.toggle("+bUrl:(this.isSingle_temp)?"oq.toggle("+oqUrl:"oq.toggle_m("+oqUrl)+",'"+acceptorId +"'"+ 
			(options==null?"":","+options)+ ")";
	}	
	/**
	 * 
	 * @return button event script when user clicked
	 */
	public String getButtonClickEventScript(){
		return getButtonClickEventScript(true);
	}
	/**
	 * 
	 * @return Image url to show the query form
	 */
	public String getImageURL(){
		return WebKeys.NDS_URI+"/images/"+ (isDropdown?"dropdown.gif":
			(this.isSingle_temp? "find.gif": "filterobj.gif"));
	}
	
	
    public static int getListThreshold(){
    	if(listThreshold==-1){
    	}
    	return listThreshold;
    }
	public void setQueryindex(int queryindex) {
		this.queryindex = queryindex;
	}
}
