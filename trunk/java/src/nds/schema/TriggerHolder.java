package nds.schema;

import java.util.*;
import java.util.regex.*;

import nds.util.*;
/**
 * ���AC/AM/BD�ķ���������
 * AC/AM/BD ����֧��2���ӿڰ汾��
     * ver 1, with only 1 param, that is objectid (int)
     * ver 2, with 3 params, as following:
      		PROC_NAME ( id in number, p_code out number, p_message out varchar2)
			���� p_code:
			�����������뵥������水ť/�˵���Ĵ���ʽһ�£�
			0 ��ˢ��
			1 ˢ�µ�ǰ����ҳ
			2 �رյ�ǰ���󴰿�
			3 ����ˢ�µ�ǰ�������ϸ��ǩҳ�����ʧ�ܣ��磺��������ϸ��ǩҳ����ˢ�µ�ǰҳ��
			4 ��p_message������Ϊ�µ�URL����URLĿ��ҳ�����滻��ǰҳ���DIV���߹���HREF
			5 ��p_message������Ϊ�µ�JAVASCRIPT, ������ִ��
			99 �رյ�ǰ����

 * @version 4.0
 */
public class TriggerHolder {
	/**
	 * �汾����trigger����
	 * @author yfzhu
	 *
	 */
	public class VersionedTrigger {
		private String name;
		private int version;
		/**
		 * Default to version 1
		 * @param name trigger name
		 */
		public VersionedTrigger(String name){
			this.name=name;
			this.version=1;
		}
		/**
		 * 
		 * @param name trigger name
		 * @param ver version code
		 */
		public  VersionedTrigger(String name, int ver){
			this.name=name;
			this.version=ver;
		}
		
		public String getName(){return name;}
		public int getVersion(){return version;};
		public String toString(){
			return name+":"+ version;
		}
	}	
	/**
	 * key: "AC"/"AM"/"BD", value: VersionedTrigger  
	 */
	private HashMap<String, VersionedTrigger> triggers=new HashMap();
	private Pattern ptn;
    public TriggerHolder() {
    	ptn=Pattern.compile("[ ,;:]");
    }
    /**
     * @param t in format like "m_product:2" or just "m_product" the :2 signs for interface of version 2 
     * @param defaultName
     * @return
     */
    private VersionedTrigger parseTrigger(String t, String defaultName){
    	if ( Validator.isNull(t)) t=defaultName;
    	t= t.trim();
    	String[] s=ptn.split(t);
    	VersionedTrigger vt;
    	if(s.length>1) vt=new VersionedTrigger(s[0], Tools.getInt(s[1], 1));
    	else vt=new VersionedTrigger(s[0]);// default to v1
    	
    	return vt;
    }
    void setAfterCreate(String t, String defaultName){
    	
        triggers.put("AC", parseTrigger(t,defaultName));
   }
    void setBeforeModify(String t, String defaultName){
        triggers.put("BM", parseTrigger(t,defaultName));
    }
    void setAfterModify(String t, String defaultName){
         triggers.put("AM", parseTrigger(t,defaultName));
    }
    void setBeforeDelete(String t, String defaultName){
        triggers.put("BD", parseTrigger(t,defaultName));
    }
    /**
     * Get trigger definition accroding to event 
     * @param event currently only support "AC","AM","BD"
     * @return
     */
    public VersionedTrigger getTrigger(String event){
    	return triggers.get(event);
    }
    
}