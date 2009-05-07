/*
 * Agile Control Technologies Ltd,. CO.
 * http://www.agileControl.com
 */
package nds.control.util;
import org.json.*;
import nds.schema.*;
import nds.util.PairTable;

import java.util.Locale;
/**
 * GridColumn contains Grid column information
 * @author yfzhu@agilecontrol.com
 */

public class GridColumn implements JSONString{
	private String name;
	private String description;
	private boolean isVisible; // fk column will be invisible, while fk.ak will be visible.
	private Column col;
	private int type; // Column.Date|NUMBER|STRING	
	private int rTableId;
	private int objIdPos;
	private boolean isUploadWhenCreate;
	private boolean isUploadWhenModify;
	private String defaultValue;
	private Locale locale;
	public String toJSONString()  {
		try{
			return toJSONObject().toString();
		}catch(Throwable t){
			return "";
		}
	}
	public JSONObject toJSONObject() throws JSONException{
		JSONObject jo=new JSONObject();
		jo.put("name",name);
		jo.put("description",description);
		jo.put("isVisible", isVisible);
		jo.put("columnId",col==null? -1: col.getId());
		jo.put("isNullable", col==null?true:col.isNullable());
		if(	col!=null && col.isValueLimited()){
			jo.put("isValueLimited",true);
			jo.put("values",col.getValues(locale).toHashMap());
		}else{
			jo.put("isValueLimited",false);
		}
		jo.put("type",type);
		jo.put("rTableId",rTableId);
		jo.put("objIdPos",objIdPos);
		jo.put("isUploadWhenCreate",isUploadWhenCreate);
		jo.put("isUploadWhenModify",isUploadWhenModify);
		jo.put("defaultValue",defaultValue);
		jo.put("summethod", col==null?null: col.getSubTotalMethod());
		return jo;
	}
	
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public boolean isUploadWhenCreate() {
		return isUploadWhenCreate;
	}
	public boolean isUploadWhenModify() {
		return isUploadWhenModify;
	}
	public void setUploadWhenCreate(boolean isUploadWhenCreate) {
		this.isUploadWhenCreate = isUploadWhenCreate;
	}
	public void setUploadWhenModify(boolean isUploadWhenModify) {
		this.isUploadWhenModify = isUploadWhenModify;
	}
	public int getObjIdPos() {
		return objIdPos;
	}
	public void setObjIdPos(int objIdPos) {
		this.objIdPos = objIdPos;
	}
	public Column getColumn() {
		return col;
	}
	public String getDescription() {
		return description;
	}
	public boolean isVisible() {
		return isVisible;
	}
	public String getName() {
		return name;
	}
	public int getReferenceTableId() {
		return rTableId;
	}
	public int getType() {
		return type;
	}
	public void setColumn(Column col) {
		this.col = col;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setReferenceTableId(int tableId) {
		rTableId = tableId;
	}
	public void setType(int type) {
		this.type = type;
	}
}
