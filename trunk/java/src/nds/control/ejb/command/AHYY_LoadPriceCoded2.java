package nds.control.ejb.command;

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.*;
import java.util.ArrayList;

import nds.control.ejb.Command;
import nds.control.event.DefaultWebEvent;
import nds.control.event.NDSEventException;
import nds.control.util.ValueHolder;
import nds.query.*;
import nds.schema.*;
import nds.util.*;

import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.*;
/*import com.liferay.portal.ejb.UserManagerUtil;
import com.liferay.portal.ejb.UserManager;
import com.liferay.portal.ejb.UserManagerFactory;

*/
import nds.security.User;
import nds.ahyy.*;
import org.json.*;

/**
 * �����Ѿ��ɹ���Ա���ܵļ��ܼ۸�
 	�����㷨��ƣ���һ����Ա�����Կ�����ܷ�ʽ������ϵͳ�У�������ͨ��usb-keyӲ�����ܣ�
 	����������ΪAES�㷨����Կ���������̵ļ��ܲ�Ʒ���۽��ж��μ��ܡ������USB-KEY�ֱ𱣹������������ϡ�
 	������������ɿ����̷����ȡ��key������CA���ƣ���֤�˹���Ա�������������£��������룬��ʧkey�������깤����������*
 */

public class AHYY_LoadPriceCoded2 extends Command {
  /**
   * How many records for each page at most
   */
  private static int COUNT_PER_PAGE=10; 
	/**
	 * ������Ϊ������Ŀ�͵�ǰ�û���ȡδ���ܵ����ݣ����ص��ͻ�����ɻ���CA�Ľ��ܡ�����ͻ��˴������Ѿ����ܵ����ݣ�����Ҫ
	 * д�뵽���ݿ���
	 * @param event
	 *    "projectid*" - ��Ӧ��Ŀid
	 *    "prices" ���飬�ڲ�ΪJSONObject ���󣬺����������ԣ�id, price, ��Ϊ�յ�ʱ�򣬱�ʾ�ͻ���û�н�������
	 * @return      
	 *    
	 */
  public ValueHolder execute(DefaultWebEvent event) throws RemoteException, NDSException {
  	User usr=helper.getOperator(event);
  	int userId= usr.id.intValue();
  	QueryEngine engine=QueryEngine.getInstance();
	TableManager manager= TableManager.getInstance();
  	java.util.Locale locale= event.getLocale();
  	JSONObject jo= event.getJSONObject();
	StringBuffer message=new StringBuffer();
	boolean hasError=false;
	PreparedStatement stmt=null;
	PreparedStatement stmt2=null;
    ResultSet rs=null;
	Connection conn= engine.getConnection();
	String sql;
	JSONObject returnObj=new JSONObject();
	JSONArray  returnPrices=new JSONArray();
	String msg=nds.util.MessagesHolder.getInstance().translateMessage("@complete@",locale);
	try{

	  	int applierId= userId;// Tools.getInt(engine.doQueryOne("select C_BPARTNER_ID from users where id="+ userId,conn), -1);
	  	//if(applierId==-1) throw new NDSException("��ǰ�û�("+ usr.name+")δ������������");
	  	int projectId=jo.getInt("projectid");
	  	
	  	// Get band
	  	//int band= Tools.getInt(engine.doQueryOne("select bank from c_project_ctrl where c_project_id="+ projectId, conn), -1);
	  	//if(band ==-1) throw new NDSException("��Ŀ�ִ�δ�趨����֪ͨ��Ŀ����Ա");
	  	
		JSONArray prices= jo.optJSONArray("prices");
		if(prices==null || JSONObject.NULL.equals(prices)){
		}else{
			// ���ܼ۸��hashֵ�������ʼ��pricehash ֵһ��
			sql= "update b_prj_token set price=?, state_bidprice='P',DECODEDATE=sysdate, modifieddate=sysdate, modifierid="+ userId+" where id=? AND state_bidprice='H' and pricehash=?";
			
			stmt=conn.prepareStatement(sql);
			for(int i=0;i< prices.length();i++){
				JSONObject po= prices.getJSONObject(i);
				double d= po.getLong("price")/100.0;  // ��ʼ���ܵ�ʱ��Ϊ�˷�ֹС����λ������ͳһ�� Int(price *100) ���棬�������ռ۸�Ҳ�ǰ�price/100���
				int tokenid=po.getInt("id");
				String hash= StringUtils.hash("M"+ tokenid+po.getLong("price") );
				stmt.setDouble(1,d);
				stmt.setInt(2, tokenid);
				stmt.setString(3,  hash);
				if(stmt.executeUpdate()==0){
					conn.createStatement().execute("update b_prj_token set state_bidprice='F',DECODEDATE=sysdate, modifieddate=sysdate, modifierid="+ userId+" where id="+tokenid+" AND state_bidprice='H'");
					logger.error("b_prj_token (id="+ tokenid+") update price failed: from web: price="+ d+", hash="+hash);
					
				}
			}
		}
		int totalCount= Tools.getInt(engine.doQueryOne("select count(*) from b_prj_token where c_project_id=" + projectId+" and ownerid="+ applierId+ " and isactive='Y' and state_bidprice='H'",conn), 0);
		if(totalCount>0){
			// fetch next lines, at most 10 lines one time
			sql= "select id, PRICEDECODE from b_prj_token where c_project_id=" + projectId+" and ownerid="+ applierId+ " and isactive='Y' and state_bidprice='H' and rownum< 11 order by id asc";
			logger.debug(sql);
	        stmt2= conn.prepareStatement(sql);
	        rs= stmt2.executeQuery(sql);
			int tid=-1;
			String pricecoded;
			float price;
			while(rs.next()){
				try{
					tid= rs.getInt(1);
					pricecoded= rs.getString(2);
					JSONObject po=new JSONObject();
					po.put("id", tid);
					po.put("pricecode", pricecoded);
					returnPrices.put(po);
				}catch(Throwable t){
					logger.error("Fail to get PRICEDECODE of b_prj_toke(id="+tid+")", t);
				}
			}
		}
		if(returnPrices.length()==0){
			int fcnt= Tools.getInt(engine.doQueryOne("select count(*) from b_prj_token where c_project_id=" + projectId+" and ownerid="+ applierId+ " and isactive='Y' and state_bidprice='F'",conn), 0);
			if(fcnt>0)msg="���ܽ���������������"+ fcnt+"��Ʒ�ֵ��걨�۸�ͽ��ܼ۸�һ�£�����������ЩƷ�ֽ���ԭΪ���ֱ��ۣ������ϣ�������ϵ����Ա�����";
		}
		returnObj.put("tokenAllCount", totalCount);
		returnObj.put("priceObj", returnPrices);
		returnObj.put("message", msg);
	}catch(Throwable t){
  		if(t instanceof NDSException) throw (NDSException)t;
  		logger.error("exception",t);
  		throw new NDSException(t.getMessage(), t);
  	}finally{
        try{stmt.close();}catch(Exception ea){}
        try{stmt2.close();}catch(Exception ea){}
        try{rs.close();}catch(Exception e){}
        try{conn.close();}catch(Exception e){}
  	} 
  	ValueHolder holder= new ValueHolder();
	holder.put("message", msg);
	holder.put("code","0");
	holder.put("data",returnObj );
	return holder;
  }
}