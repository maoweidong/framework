package nds.control.ejb.command;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.Locale;
import java.util.Vector;

import nds.control.ejb.Command;
import nds.control.ejb.command.pub.Pub;
import nds.control.event.DefaultWebEvent;
import nds.control.util.SecurityUtils;
import nds.control.util.ValueHolder;
import nds.query.QueryEngine;
import nds.query.QueryUtils;
import nds.schema.Table;
import nds.schema.TableManager;
import nds.security.Directory;
import nds.security.User;
import nds.util.JNDINames;
import nds.util.NDSException;
import nds.util.Tools;
/**
*   ListDelete is a module to delete the record from the web
*   delete in batch

*   yfzhu 2010-5-20 record must be void before delete if table has void action set
*/
public class ListDelete extends Command{
  private TableManager manager;
  public ValueHolder execute(DefaultWebEvent event) throws NDSException ,RemoteException{
  	/**
  	 * 2005-11-15 �����˶�status �ֶε��жϣ����status�ֶ�Ϊ2 ��ǰ��¼������ɾ�������������Ҫ����
  	 * ��֧����Ƶĵ���ͼģʽ�¡���ģʽ�����еĵ��ݶ���һ�����棬���������ͨ���ĵ���Ӧ��ֹɾ����ϵͳ����ͨ��
  	 * ��Ƶĵ������޸�status=2����Ϊ�ж�������
  	 */
       manager = helper.getTableManager() ;
       
       Table table = manager.findTable(event.getParameterValue("table",true));
       int tableId = table.getId();

       String[] itemidStr = event.getParameterValues("itemid", true);

       User user= helper.getOperator(event);
       String operatorDesc=user.getDescription();
       String tableName = table.getRealTableName() ;
       String tableDesc  = table.getDescription(event.getLocale());
       
       if (itemidStr==null) itemidStr= new String[0];
       java.sql.Connection con=null;
       QueryEngine engine =QueryEngine.getInstance();
       con= engine.getConnection();
       try{
       // get parent table ids, for later after-modify trigger
       Table parent= helper.getParentTable(table,event);
       //logger.debug(" parent table of " + table + " is " + parent);
       int[] oids=new int[itemidStr.length];
       for(int i=0;i<oids.length;i++) oids[i]= Integer.parseInt(itemidStr[i]);
       int[] poids= helper.getParentTablePKIDs(table, oids, con);
       logger.debug(" parent id of " + table + " is " + Tools.toString(poids));
       // ��鸸��Ĵ����ԣ���������ڻ�δ�ҵ����׳���������������������������
       // �����״̬���ı��ˣ����ӱ�Ľ�����Ȼ������������û����Զ��ӱ���в���������
       // ������ִ���
       //������  m_v_inout ���ύ������ m_v_2_inout, ���û��Կ��Զ�m_v_inoutitem��
       //�е����ݽ����޸ġ����ǲ�����ġ�
       helper.checkTableRows(parent, poids, con, helper.PARENT_NOT_FOUND);
       
       ValueHolder v = new ValueHolder();
       
       // ���ڽ������޷��������еĶ��󶼾�����ͬ��ε�Ȩ�ޣ�����Ҫ�ڴ˽���Ȩ����֤
       if(parent==null){
       		// check permissions on all objects
       		if (!SecurityUtils.hasPermissionOnAll(user.getId().intValue(), user.getName(), 
       				table.getName(), itemidStr, Directory.WRITE, event.getQuerySession())){
       			v.put("message", "@no-delete-permission-on-all-pls-delete-one-by-one@");
       			return v;
       		}
       }
       
        // check status 2005-11-15
       boolean bCheckStatus= (table.getColumn("status") !=null);
       boolean bCheckVoid= table.isActionEnabled(Table.VOID);
        String res = "", s; int errCount=0;
        for(int i = 0;i<itemidStr.length ;i++){
            int itemid = Tools.getInt(itemidStr[i],-1) ;
            s =deleteOne( table,itemid,operatorDesc,con,bCheckStatus,bCheckVoid );
            if (s !=null) {
                res += s+ "<br>";
                errCount ++;
            }else{
                logger.info("deleted table="+ table+", id="+itemid+" by "+ user.name+" of id "+ user.id);
            }
        }
        if(parent !=null)
        	helper.doTrigger("AM", parent, poids, con);
        
        String message = null;

        message =itemidStr.length + "@line@ @request-to-delete@";
        if ( errCount > 0) message +=", @failed-count@:"+ errCount +", @detail-msg@:" + res;
        else message +=",@complete@";
        v.put("message",message) ;
        
        
        return v;
       }catch(Exception t){
       		if( t instanceof NDSException ) throw (NDSException)t;
       		else{
       			logger.error("Failed", t);
       			throw new NDSException(t.getMessage(), t);
       		}
       }finally{
       		try{ con.close();}catch(Exception e){}
       }

  }
  /**
   * 
   * @param table
   * @param itemid
   * @param isDispatch
   * @param operatorDesc
   * @param con
   * @param checkStatus if true, will not delete when column "status" is 2
   * @param checkVoid if true, will check isactive column, should be N when delete
   * @return
   */
  private String deleteOne(Table table,int itemid, 
  		String operatorDesc,Connection con, boolean checkStatus, boolean checkVoid){
      try{
          Vector vec = new Vector();
          String sql = "";
          QueryEngine engine =QueryEngine.getInstance();
          
          QueryUtils.lockRecord(table,itemid,con);
          
          QueryUtils.checkStatus(table,itemid,con);
          /**
           * should be void
           */
          QueryUtils.checkVoid(table,itemid,"N",con);
          
          sql = getSql(table.getRealTableName(),itemid);
          vec.addElement(sql);
          
          helper.doTrigger("BD", table, itemid, con);
          int count = engine.doUpdate(vec,con);
          // notify
          /*try{
              helper.Notify(TableManager.getInstance().getTable(tableName),
                            itemid,operatorDesc,JNDINames.STATUS_DELETE ,con);
          }catch(Exception ee){}finally{
              
          }*/

          return null;
      }catch(Exception e){
          logger.error("Could not delete record(table="+table.getName()+",id="+ itemid+")", e );
          return e.getMessage();
      }
  }

 /*  form the String sql
  *  tablename: the name of the table
  *  id : the primary key
  */
   private String getSql(String tableName,int id){
     String sql = "delete from "+tableName+" where id = "+id;
     return sql;
    }
    private String getDispatchDeleteSQL(String tableName,int id){
     String sql = "delete from "+manager.getTable(tableName).getDispatchTableName() +" where id = "+id;
     return sql;
    }
}