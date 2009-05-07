/*
 * Agile Control Technologies Ltd,. CO.
 * http://www.agileControl.com
 */
package nds.control.util;

import nds.control.web.UserWebImpl;
import nds.log.Logger;
import nds.log.LoggerManager;
import nds.schema.*;
import nds.util.JSONUtils;
import nds.util.MessagesHolder;

import java.util.*;

import org.json.*;
/**
 * Information for constructing editable grid. Grid columns list will be like:
 * 0        1       2       3           4   5       6           7       8
 * rowIdx	state	errmsg	jasonobj	id	column1	column1_id	column2	column3
 * 
 * @author yfzhu@agilecontrol.com
 */
public class EditableGridMetadata { 
	private static Logger logger= LoggerManager.getInstance().getLogger(EditableGridMetadata.class.getName());
	/**
	 * Columns which has any of the bit masks set in specified positions.
     * For instance, getColumns([0,3]) will return columns which
     * is showable when creation form <b>OR</b> modifiable in update form.
     * refer to Column.isMaskSet for mask information. Elements shoule be 0-9
     * 
     * 
	 */
	public final static int[] ITEM_COLUMN_MASKS=new int[]{Column.MASK_QUERY_SUBLIST,Column.MASK_CREATE_EDIT, Column.MASK_MODIFY_EDIT};
	private Table table;
	private ArrayList columns;// elements are GridColumn
	private int[] masks;  
	
	public JSONObject toJSONObject() throws JSONException{
		JSONObject jo=new JSONObject();
		jo.put("table",table.getName());
		jo.put("ismenuobj",table.isMenuObject());
		jo.put("tableId", table.getId());
		jo.put("columns",  JSONUtils.toJSONArray(columns));
		jo.put("column_masks", JSONUtils.toJSONArrayPrimitive(masks));
		jo.put("popupitem", table.isMenuObject()); // identical to ismenuobj
		return jo;
	}
	/**
	 * 
	 * @param allowPopupItem if true, will set popupitem to true
	 * @return
	 * @throws JSONException
	 */
	public JSONObject toJSONObject(boolean allowPopupItem) throws JSONException{
		JSONObject jo=new JSONObject();
		jo.put("table",table.getName());
		jo.put("ismenuobj",table.isMenuObject());
		jo.put("tableId", table.getId());
		jo.put("columns",  JSONUtils.toJSONArray(columns));
		jo.put("column_masks", JSONUtils.toJSONArrayPrimitive(masks));
		jo.put("popupitem", allowPopupItem);
		return jo;
	}
	/**
	 * 
	 * @return elements are GridColumn
	 */
	public ArrayList getColumns(){
		return columns;
	}
	/**
	 * 
	 * @param table the main table whose records are shown in the grid
	 * @param locale used for construct column header
	 * @param userWeb, contains method to get default value for column, key: column name, value: 
	 * default value of that column, may be null for those not care about default value of columns
	 * @param masks Columns which has any of the bit masks set in specified positions.
     * For instance, getColumns([0,3]) will return columns which
     * is showable when creation form <b>OR</b> modifiable in update form.
     * refer to Column.isMaskSet for mask information. Elements shoule be 0-9
     * Note following columns will not be loaded:
     * 		displaytype in {'xml','file','image'}
	 */
	public EditableGridMetadata(Table table, Locale locale, UserWebImpl userWeb, int[] masks){
		this.table=table;
		this.columns=new ArrayList();
		this.masks= masks;
		setup(locale,userWeb);
	}
	private void setup( Locale locale, UserWebImpl userWeb){
		Properties prefs=null;
		try{
			if(userWeb!=null)prefs=userWeb.getPreferenceValues("template."+table.getName().toLowerCase(),false,true);
		}catch(Throwable t){
			logger.error("Could not fetch user preference", t);
		}
		ArrayList cls=table.getColumns(masks,false ); // nerver load displaytype in {'xml','file','image'}
		columns.add(createGridColumn("rowIdx", MessagesHolder.getInstance().getMessage(locale,"rowindex"), true,null,Column.STRING,table.getId(),4,true,true, null,locale));
		columns.add(createGridColumn("state__", MessagesHolder.getInstance().getMessage(locale,"rowstate"), false,null,Column.STRING,-1,-1,false,false,null,locale));
		columns.add(createGridColumn("errmsg", MessagesHolder.getInstance().getMessage(locale,"errmsg"), true,null,Column.STRING,-1,-1,false,false,null,locale));
		columns.add(createGridColumn("jsonobj", MessagesHolder.getInstance().getMessage(locale,"jsonobj"), true,null,Column.STRING,-1,-1,true,true,null,locale));
		Column pk= table.getPrimaryKey();
		columns.add(createGridColumn(pk.getName(),"ID", false, pk,pk.getType(), -1, -1, false, true,null,locale));
		String defaultValue=null;
		for(int i=0;i< cls.size();i++){
			Column col=(Column)cls.get(i);
			if(prefs!=null && userWeb!=null){
				defaultValue=userWeb.replaceVariables(prefs.getProperty(col.getName(), 
						userWeb.getUserOption(col.getName(),  col.getDefaultValue(true))));
			}
			if( col.getReferenceTable() !=null) {
				Column col2=col.getReferenceTable().getAlternateKey();
				columns.add(createGridColumn(col.getName()+"__"+ col2.getName(),col.getDescription(locale),
						true, col,col2.getType(),col.getReferenceTable().getId(), columns.size()+1,
						 col.isMaskSet(Column.MASK_CREATE_EDIT), 
						 col.isMaskSet(Column.MASK_MODIFY_EDIT),defaultValue,locale));
				columns.add(createGridColumn(col.getName()+"__ID" ,col.getDescription(locale),
						false, col, col.getType(),-1,-1, false,false,null,locale));
				
			}else{
				columns.add(createGridColumn(col.getName(),col.getDescription(locale),
					true, col,col.getType(),-1, -1, 
					col.isMaskSet(Column.MASK_CREATE_EDIT), 
					col.isMaskSet(Column.MASK_MODIFY_EDIT ),defaultValue,locale));
			}
		}
	}
	private GridColumn createGridColumn(String name, String desc, boolean isVisible, 
			Column col, int type, int rTableId, int objIdPos, 
			boolean uploadWhenCreate, boolean uploadWhenModify, String defaultValue, Locale locale){
		GridColumn c=new GridColumn();
		c.setName(name);
		c.setDescription(desc);
		c.setVisible(isVisible);
		c.setColumn(col);
		c.setType(type);
		c.setReferenceTableId(rTableId);
		c.setObjIdPos(objIdPos);
		c.setUploadWhenCreate(uploadWhenCreate);
		c.setUploadWhenModify(uploadWhenModify);
		c.setDefaultValue(defaultValue);
		c.setLocale(locale);
		return c;
	}
	/**
	 * 
	 * @return column name that is to send column data to server for creation
	 */
	public ArrayList getColumnsWhenCreate(){
		ArrayList al=new ArrayList();
		for(int i=0;i< columns.size();i++){
			GridColumn c=(GridColumn) columns.get(i);
			if(c.isUploadWhenCreate())al.add( c.getName());
		}
		return al;
	}
	/**
	 * 
	 * @return column name that is to send column data to server for modification
	 */
	public ArrayList getColumnsWhenModify(){
		ArrayList al=new ArrayList();
		for(int i=0;i< columns.size();i++){
			GridColumn c=(GridColumn) columns.get(i);
			if(c.isUploadWhenModify())al.add( c.getName());
		}
		return al;
	
	}
	/**
	 * 
	 * @return column name that is to send column data to server for modification
	 */
	public ArrayList getColumnsWhenDelete(){
		ArrayList al=new ArrayList();
//		al.add("rowIdx");
		al.add("ID");
		return al;
	}
}
