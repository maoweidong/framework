package nds.control.ejb.command;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.ArrayList;

import nds.control.ejb.Command;
import nds.control.event.DefaultWebEvent;
import nds.control.event.NDSEventException;
import nds.control.util.ValueHolder;
import nds.query.QueryEngine;
import nds.query.SPResult;
import nds.util.NDSException;




public class MonthCloseOffStart extends Command{
    public ValueHolder execute(DefaultWebEvent event) throws NDSException, RemoteException {


        String year = (String)event.getParameterValue("year");
        String month = (String)event.getParameterValue("month");
        int monthInt = new Integer(month).intValue();
        if(monthInt<10){
           month = "0"+month;
        }
        String yearMonthStr = year.trim() +month.trim() ;
        // ��ȡ����µ�����ֵ
        Integer yearMonth = new Integer(yearMonthStr);
        logger.debug("The value of yearmonth is:"+yearMonth) ;

        int lastMonth = getLastMonth(new Integer(year).intValue() ,new Integer(month).intValue());
        String lastMonthStr =lastMonth+"";
        if(lastMonth<10)
            lastMonthStr = "0"+new Integer(lastMonth).toString() ;
        int lastYear  = getLastYear(new Integer(year).intValue() ,new Integer(month).intValue() );
        String lastYearMonthStr = new Integer(lastYear).toString() +lastMonthStr;
        //��ȡ�ϸ��µ�����ֵ�籾�µ�ֵ��197604����ôlastYearMonth��ֵ��197603
        Integer lastYearMonth = new Integer(lastYearMonthStr);
        logger.debug("The value of lastYearMonth is:"+lastYearMonth);
        //  �ж��ϸ��µ��½��Ƿ��Ѿ���ɣ�
        String sql = "select count(*) from commrdsfm where yearmon = "+lastYearMonth.intValue();
        QueryEngine engine1 = QueryEngine.getInstance();

        ResultSet res = engine1.doQuery(sql);
        try{
            int count = 0;
            if(res.next() ){
                count  = res.getInt(1) ;
            }
            if(count==0){
                throw new NDSEventException("@month-balance-info@");
            }
        }catch(Exception e){
        	logger.error("error doing query:"+"select count(*) from commrdsfm where yearmon = "+lastYearMonth.intValue(), e);
            throw new NDSEventException("@not-tell-last-month-balance@",e);
        }
        QueryEngine engine = QueryEngine.getInstance() ;
        ArrayList list = new ArrayList();
        list.add(yearMonth) ;
        list.add(helper.getOperator(event).getId());
        SPResult result = helper.executeStoredProcedureBackground("MonthCloseOffStart",list,true, helper.getOperator(event), true);

        ValueHolder v = new ValueHolder();
        if(result.isSuccessful()){
           v.put("message",result.getMessage() ) ;
        }else{
           v.put("message",result.getDebugMessage()  ) ;
        }
        return v;
    }
     //�жϸ��µ��½��Ƿ��Ѿ���� ���δ���,����true,���򷵻�false
    private int getLastMonth(int year,int month) throws NDSException{
        int newMonth = 0;
        if(month==1){
            newMonth = 12;
        }else{
            newMonth = month - 1;
        }
        return newMonth;
    }

    private int getLastYear(int year,int month) throws NDSException{
        int newYear = 0;
        if(month == 1){
            newYear = year - 1;
        }else{
            newYear = year;
        }
        return newYear;
    }
}