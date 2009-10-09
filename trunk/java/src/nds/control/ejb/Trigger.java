package nds.control.ejb;
import java.sql.Connection;
import nds.query.SPResult;
public interface Trigger {
    /**
     * @return return SPResult, with code and message
			���� p_code:
			�����������뵥������水ť/�˵���Ĵ���ʽһ�£�
			0 ��ˢ��
			1 ˢ�µ�ǰ����ҳ
			2 �رյ�ǰ���󴰿�
			3 ����ˢ�µ�ǰ�������ϸ��ǩҳ�����ʧ�ܣ��磺��������ϸ��ǩҳ����ˢ�µ�ǰҳ��
			4 ��p_message������Ϊ�µ�URL����URLĿ��ҳ�����滻��ǰҳ���DIV���߹���HREF
			5 ��p_message������Ϊ�µ�JAVASCRIPT, ������ִ��
			99 �رյ�ǰ����
     */
    public SPResult execute(int objectId, Connection conn);
}