package nds.schema;

import java.util.ArrayList;
/**
 * ���ֶ��е�ͳ�Ʒ�������.table�ļ���<column> <sum-method></sum-method> </column> ��
 * ���塣
 */
public interface SumMethod {

    /**
     * �������ֵ��ע��data�п��ܰ�����Чֵ����null)
     * @param data ����ֵ���л���
     */
    public double calculate(ArrayList data);

}