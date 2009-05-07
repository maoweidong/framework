/******************************************************************
*
*$RCSfile: ColumnInterpreter.java,v $ $Revision: 1.2 $ $Author: Administrator $ $Date: 2005/12/18 14:06:17 $
*
*$Log: ColumnInterpreter.java,v $
*Revision 1.2  2005/12/18 14:06:17  Administrator
*no message
*
*Revision 1.1.1.1  2005/03/15 11:23:26  Administrator
*init
*
*Revision 1.1.1.1  2002/11/29 02:50:35  yfzhu
*Active POS
*
*Revision 1.3  2001/11/14 23:31:01  yfzhu
*no message
*
*Revision 1.2  2001/11/07 20:58:47  yfzhu
*no message
*
*
********************************************************************/
package nds.util;

import java.util.Locale;

/**����column��intֵ�Ľ�����������û�������������������֮��
*
*  ����ӿ���Ҫ������ʾ�����Ϊ���ӵ��ֶΣ����ں���򵥵��ֶ�һ
*  �����Dictionary ��ķ�ʽ����һЩ���״̬�ֶΡ�������permission�������ֶΣ�
*  ��������н����������Թ��ô��룬����Ҳʡ�洢�ռ䣨���򼸺�ÿ�ű��permission
*  �ֶ���Dictionary���ж�Ҫ��128����¼��
*/
public interface ColumnInterpreter {
    /**parse specified value to string that can be easily interpreted by users
     * @throws ColumnInterpretException if input value is not valid
     */
    public String parseValue(Object value, Locale locale) throws ColumnInterpretException;
    /**
    * parse input string to column accepted int value
    * @throws ColumnInterpretException if input string is not valid
    */
    public Object getValue(String str, Locale locale) throws ColumnInterpretException;
}
