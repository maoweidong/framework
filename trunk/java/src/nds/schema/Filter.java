package nds.schema;
import org.json.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import nds.util.*;
import nds.util.xml.XmlMapper;
import nds.control.ejb.AsyncControllerBean;
import nds.log.Logger;
import nds.log.LoggerManager;
import nds.query.*;
/**
 * ����������
 * 
 * �������ֶδ洢����һ���������Ķ��壬���ݿ����򽫴˹�����Ӧ����ҵ�����ݣ����ڹ���һ�����ݼ��ϡ�����������
����������ʱ����Ҫѡ����빤�����̱������������
���ʱ����������϶�����Ҫ�������Ʒ�ķ�Χ��
ԭ���ϣ����������ý���XML��clob���ṹ���浽���ݿ��ָ���ֶΣ�ͨ��Interpreter��ɽ��湹�죬ͨ��REGEXPRESSION ��Ź���filter ����ʱ��Ҫ�Ķ����������AD_TABLE_ID �ȡ�
�������ֶε�����ֻ��ͨ����ť���ã���ʽ�� file���͵��ֶ�����
�洢�ṹ
filter
filter/desc
filter/sql
filter/expr
desc: ����������������ʾ�ڽ�����
sql:ΪID ������������� select m_product.id from m_product where M_PRODUCT.ISACTIVE=��Y��
expr����װ��Expression ���ʽ
������
<filter>
	<desc>��Ʒ����(1,2,3)</desc>
	<sql> select m_product.id from m_product where id IN (1,2,3)</sql>
	<expr>
<![CDATA[<expr>
    <expr>
        <clink>C_V_PO_ORDER.TOT_SUM</clink>
        <condition>&gt;1000000</condition>
    </expr>
    <oper>1</oper>
    <expr>
        <clink>C_V_PO_ORDER.ISACTIVE</clink>
        <condition>=Y</condition>
    </expr>
</expr>
]]>
</expr>
</filter>

 * 2008-12-18 initiated
 * @author yfzhu
 * @since 4.0
 * 
 */
public class Filter implements java.io.Serializable {
    private static Logger logger= LoggerManager.getInstance().getLogger(Filter.class.getName());
    private static ThreadLocal lxh=new ThreadLocal(){
    	protected synchronized Object initialValue() {
    		XmlMapper xh=new XmlMapper();
            xh.setValidating(true);
            // By using dtdURL you brake most buildrs ( at least xerces )
            String dtdURL = "file:" ;
            xh.register("-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN",
                        dtdURL );
            xh.addRule("filter/desc", xh.methodSetter("setDescription", 0) );
            xh.addRule("filter/sql", xh.methodSetter("setSql", 0) );
            xh.addRule("filter/expr", xh.methodSetter("setExpression", 0) );

    		return xh;
        }
    };	
	private String description;
	private String sql;
	private String expression;
	public Filter(){}
	/**
	 * ���ϣ������xml�����쳣ʱ���׳�����Ӧʹ�� #parse �����������췽�������׳��쳣
	 * @param xml
	 */
	public Filter(String xml) {
		if(nds.util.Validator.isNotNull(xml)){
		try{
            // if from html, which will be wrappered by cdata
            if (xml.startsWith("<![CDATA[") && xml.endsWith( "]]>")){
                xml=  xml.substring(9, xml.length() -3);
            }
            xml ="<?xml version=\"1.0\" encoding=\"GB2312\"?>"+xml;
            byte[] bs=xml.getBytes();
            ByteArrayInputStream bis=new ByteArrayInputStream(bs );
            this.loadMapping(bis);
        }catch(Exception e){
            logger.error("�޷������ַ���ΪFilter����:"+ xml, e);
            this.description="�޷��������������ã�����������";
            this.sql="IS NULL";
            this.expression="";
            //throw new QueryException("�޷������ַ���ΪFilter����:"+ xml, e);
        }
		}
/*		if(nds.util.Validator.isNotNull(xml)){
		try{
			JSONObject jo= org.json.XML.toJSONObject(xml);
			description= jo.getString("desc");
			sql=jo.getString("sql");
			expression= jo.getString("expr");
			
		}catch(Throwable t){
			logger.error("Fail to parse xml as Filter:"+ xml, t);
			throw new NDSException("Fail to parse xml as Filter",t);
		}
		}*/
	}
	/**
	 * �빹�췽����ͬ���ǣ��˷����ڽ���ʧ��ʱ��ֱ���׳��쳣
	 * @param xml
	 * @throws NDSException
	 */
	public void parse(String xml)throws NDSException{
		if(nds.util.Validator.isNotNull(xml)){
			try{
	            // if from html, which will be wrappered by cdata
	            if (xml.startsWith("<![CDATA[") && xml.endsWith( "]]>")){
	                xml=  xml.substring(9, xml.length() -3);
	            }
	            xml ="<?xml version=\"1.0\" encoding=\"GB2312\"?>"+xml;
	            byte[] bs=xml.getBytes();
	            ByteArrayInputStream bis=new ByteArrayInputStream(bs );
	            this.loadMapping(bis);
	        }catch(Exception e){
	            logger.error("�޷������ַ���ΪFilter����:"+ xml, e);
	            throw new QueryException("�޷������ַ���ΪFilter����:"+ xml, e);
	        }
		}
	}
	 private void loadMapping(InputStream stream) throws Exception {
	        String dtdURL = "file:" ;
	        XmlMapper xh= (XmlMapper)lxh.get();
	        xh.readXml(stream, this);
	    }	
	public String toXML() throws org.json.JSONException{
		StringBuffer sb=new StringBuffer("<filter><desc>");
		sb.append(description==null?"":StringUtils.escapeForXML(description)).append("</desc><sql>");
		sb.append(sql==null?"":StringUtils.escapeForXML(sql)).append("</sql><expr>");
		sb.append(expression==null?"":StringUtils.escapeForXML(expression)).append("</expr></filter>");
		
		return sb.toString();
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Expression getExprObject() throws nds.query.QueryException{
		return new Expression(expression);
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	
}
