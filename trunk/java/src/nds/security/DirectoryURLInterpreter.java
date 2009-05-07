/******************************************************************
*
*$RCSfile: DirectoryURLInterpreter.java,v $ $Revision: 1.2 $ $Author: Administrator $ $Date: 2005/12/18 14:06:17 $
*
*$Log: DirectoryURLInterpreter.java,v $
*Revision 1.2  2005/12/18 14:06:17  Administrator
*no message
*
*Revision 1.1.1.1  2005/03/15 11:23:24  Administrator
*init
*
*Revision 1.1.1.1  2002/11/29 02:50:35  yfzhu
*Active POS
*
*Revision 1.3  2001/12/09 03:43:32  yfzhu
*no message
*
*Revision 1.2  2001/11/20 22:36:10  yfzhu
*no message
*
*Revision 1.1  2001/11/14 20:00:21  yfzhu
*no message
*
*Revision 1.2  2001/11/07 20:58:47  yfzhu
*no message
*
*
********************************************************************/
package nds.security;
import java.util.Locale;

import nds.util.ColumnInterpretException;
import nds.util.ColumnInterpreter;
import nds.util.JNDINames;
/**����column��intֵ�Ľ�����������û�������������������֮��
*
*  ����ӿ���Ҫ������ʾ�����Ϊ���ӵ��ֶΣ����ں���򵥵��ֶ�һ
*  �����Dictionary ��ķ�ʽ����һЩ���״̬�ֶΡ�������permission�������ֶΣ�
*  ��������н����������Թ��ô��룬����Ҳʡ�洢�ռ䣨���򼸺�ÿ�ű��permission
*  �ֶ���Dictionary���ж�Ҫ��128����¼��
*/
public class DirectoryURLInterpreter implements ColumnInterpreter,java.io.Serializable {
    public DirectoryURLInterpreter() {}
    /**add hyperlink
     * @throws ColumnInterpretException if input value is not valid
     */
    public String parseValue(Object value,Locale locale) {
        return "<a target='_blank' href='"+JNDINames.WEB_ROOT+value+"'>"+value+"</a>";
    }
    /**
    * Just the <code>str</code>
    */
    public Object getValue(String str,Locale locale) {
        return str;
    }
}
