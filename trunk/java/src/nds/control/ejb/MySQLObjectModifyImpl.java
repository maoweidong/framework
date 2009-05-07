package nds.control.ejb;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import nds.control.ejb.command.pub.Pub;
import nds.control.event.DefaultWebEvent;
import nds.control.event.NDSEventException;
import nds.schema.Column;
import nds.schema.Table;
import nds.util.NDSException;
import nds.util.Tools;
/**
 * Title:        NDS Project
 * Description:  San gao shui yuan, mu xiang ren jia
 * Copyright:    Copyright (c) 2001
 * Company:      aic
 * @author yfzhu
 * @version 1.0
 */

public class MySQLObjectModifyImpl extends SqlGenerateSupport{
  public Vector getSqlArray(HashMap hashColValue,DefaultWebEvent event,Table table,int length) throws NDSException,RemoteException{
      //######### dispatch column id
      int dispatchColumnId= -1;
      if (table.getDispatchColumn() !=null) dispatchColumnId=table.getDispatchColumn().getId();

        int objectid = Tools.getInt(event.getParameterValue("id"),-1 ) ;
        String[] itemidStr = event.getParameterValues("itemid");

        int[] itemId = new int[length];
        if((itemidStr!=null)&&(!"".equals("itemidStr") )){
            if(itemidStr.length !=length){
                throw new NDSEventException("the length of the paramter is wrong");
            }
            for(int i = 0;i<itemidStr.length ;i++){
                itemId[i] = Tools.getInt(itemidStr[i],-1) ;
            }
        }
        /** -- yfzhu modified 2003-07-27 to support pos security update --*/
        //String tableName = table.getName() ;
        String tableName = table.getDispatchTableName() ;
//        logger.debug("the value of tableName is:"+tableName) ;
        Column column;
        String columnName = null;
        ArrayList editColumnList = table.getModifiableColumns((event.getParameterValue("arrayItemSelecter")!=null? nds.schema.Column.QUERY_SUBLIST:Column.MODIFY));

        Vector vec = new Vector();
        for(int i = 0;i<length;i++){
            Iterator colIte = editColumnList.iterator() ;
            String sql = "update "+tableName+" set ";

            //########### dispath customer id ############
            int dispatchCustomerId= -1;

            while(colIte.hasNext() ){
               column = (Column)colIte.next();
               columnName = column.getName();
               logger.debug("The value of columnName is:"+columnName) ;
               Vector value = (Vector)hashColValue.get(columnName) ;
               Object[] valBig = (Object[])value.get(0);
               if(columnName.equalsIgnoreCase("passwordhash") ){
                   if(((String)valBig[i]).equals("04bd41567b3c269944321d77e24713a2")){
                       continue;
                   }
               }


               sql +=  columnName+" =";
               int colType = column.getType();
                  if(colType == column.NUMBER|| colType==column.DATENUMBER){
//                       Object[] valBig = (Object[])value.get(0);
                       sql += valBig[i]+",";
                       // ########### dispatch column id
                       if ( column.getId() == dispatchColumnId) {
                           dispatchCustomerId=( new Integer(""+ valBig[i])).intValue() ;
                       }

                   }else if( colType == column.DATE ){
//                       Object[] valDate = (Object[])value.get(0) ;
                       if(valBig[i]==null)
                           sql += "null,";
                       else
                           // differe to oracle
                           sql +="'"+valBig[i]+"',";
                   }else if( colType == column.STRING ){
//                       Object[] valStr = (Object[])value.get(0) ;
                       sql += "'"+Pub.getDoubleQuote((String)valBig[i])+"',";
                   }
           }
           // ȥ��sql�������","��
           sql = Pub.removeLastString(sql,",");
           if(itemidStr!=null){
               sql += " where id = "+itemId[i];
           }else{
               sql += " where id = "+objectid;
           }
//           logger.debug("the value of sql for the end is:"+sql) ;

           if ( table.getDispatchType()== table.DISPATCH_ALL  ){
               vec.addElement(Pub.getExpDataRecord(-1, sql));
           }else if ( table.getDispatchType()== table.DISPATCH_SPEC   ){
               vec.addElement(Pub.getExpDataRecord(dispatchCustomerId, sql));
           }
       }
       return vec;
  }
}