
package nds.web.interpreter;
import java.util.Locale;

import nds.util.ColumnInterpretException;
import nds.util.*;
/**
 * ����������url, ��web����ʾΪ <a href='$url'>url</a> ����ʽ 
 */
 
public class URLInterpreter implements ColumnInterpreter,java.io.Serializable {
    public URLInterpreter() {}
    /** Add <a href=''></a> to the value
     *  Set max display length to 60 (yfzhu 2005-05-07)
     * @throws ColumnInterpretException if input value is not valid
     */
    public String parseValue(Object value,Locale locale) {
    	if(value==null) return "";
    	
        return "<a href=\""+ value+"\">"+ StringUtils.shorten(value.toString(), 60,"..") +"</a>";
    }
    /**
    * Just the str
    */
    public Object getValue(String str,Locale locale) {
        return str;
    }
}
