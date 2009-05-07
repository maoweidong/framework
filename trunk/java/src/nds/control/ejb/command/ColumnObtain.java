package nds.control.ejb.command;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.*;

import nds.control.ejb.DefaultWebEventHelper;
import nds.control.event.DefaultWebEvent;
import nds.log.Logger;
import nds.log.LoggerManager;
import nds.schema.Column;
import nds.schema.Table;
import nds.util.NDSException;
/**
 * Title:        NDS Project
 * Description:  San gao shui yuan, mu xiang ren jia
 * Copyright:    Copyright (c) 2001
 * Company:      aic
 * @author yfzhu
 * @version 1.0
 */

public abstract class ColumnObtain {
    protected Logger logger;
    DefaultWebEventHelper webHelper;
    protected boolean isBestEffort ;
    protected HashMap invalidRows;// elements Integer(value=key) (���ڲ�����������ֵ�����кţ�from 0)��¼��invalidRows��)
    protected Connection conn;
    public ColumnObtain(){
        webHelper = new DefaultWebEventHelper();
        logger = LoggerManager.getInstance().getLogger(getClass().getName());

    }
    public void setConnection(Connection conn){
    	this.conn=conn;
    }
    public Connection getConnection(){
    	return conn;
    }
    /**
     * @param row 0 is the first row
     */
    protected boolean isInvalidRow(int row){
        return (invalidRows !=null)  && invalidRows.containsKey(new Integer(row));
    }
    /**
     * @param row 0 is the first row
     * @param msg why the row is invalid
     */
    protected void setRowInvalid(int row, String msg){
        if(invalidRows !=null) invalidRows.put(new Integer(row), msg);
    }
    public void enableBestEffort(boolean b){
        isBestEffort=b;
    }
    /**
     *  ������ø��е�����ֵ�����ڲ�����������ֵ�����кţ�from 0)��¼��invalidRows�У�
     *  ע��invalidRows �����Ѿ�������һЩ���������������ˡ�
     *
     */
    public void setInvalidRows(HashMap rows){
        invalidRows= rows;
    }
    /**
    * ����ĳһ�е�ֵ�����ֵ���ܻ��ж��������Vector�а�������, �ڴ˷����У��д����
    */
   public abstract Vector getColumnValue(DefaultWebEvent event,Table table,Column col,int length) throws NDSException,RemoteException;

}