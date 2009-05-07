package nds.schema;

import java.util.ArrayList;
public class Sum implements SumMethod{

    public Sum() {
    }
    /**
     * �������ֵ��ע��data�п��ܰ�����Чֵ����null), ��0����
     * @param data ����ֵ���л���, data element can be string
     */
    public double calculate(ArrayList data){
        double d=0;
        if( data ==null) return d;
        double v;
        for( int i=0;i< data.size();i++){
            try{
                v=(new Double(data.get(i)+"")).doubleValue();
            }catch(Exception e){
                v= 0;
            }
            d +=v;
        }
        return d;
    }

}