package nds.web.interpreter;
import java.util.Locale;

import nds.log.Logger;
import nds.log.LoggerManager;
import nds.schema.TableManager;
import nds.util.*;
import nds.transport.*;

/**
 * ������ts, ��web����ʾΪ <line></line> ����ʽ 
 */
public class transportStatus implements ColumnInterpreter,java.io.Serializable {
	private static Logger logger= LoggerManager.getInstance().getLogger(transportStatus.class.getName());

  //  private String value;
  //	private Locale locale;
  	
	
    public transportStatus() {}
  	
	public String parseValue(Object val,Locale locale) {
		//System.out.print("value is "+value);
		String value="";
		tsmanger tm=new tsmanger();
		tm.newtmload(val);
		transport ts=tm.getTransport();
		//ts.toLine()
		value=ts.toLine(ts).toString();
		//System.out.print(value);
		return value;
	}
	
    public Object getValue(String str,Locale locale) {
    	 throw new Error("Not supported");
    }
}
