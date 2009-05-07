package nds.var;
import  nds.var.VAROrder;

import java.util.List;

import com.sun.java.swing.plaf.windows.WindowsBorders.ToolBarBorder;
import nds.query.QueryEngine;
import nds.query.QueryException;
import nds.util.Configurations;
import nds.util.NDSException;
import nds.util.Tools;




public final class VARUtils {
	
	/**
	 * �ж��Ƿ��Ǵ�����
	 *@param userId users.id
	 *@return true:�Ǵ�����,false:���Ǵ�����
	 */
	public static boolean isAgent(int userId) throws NDSException{
		try {
			int count=Tools.getInt(QueryEngine.getInstance().doQueryOne("select count(*) from users u, c_bpartner p where p.isagent='Y' and p.id=u.c_bpartner_id and u.id="+userId),-1);
			if(count!=0){
				return true;
			}
		return false;
		} catch (Throwable t) {
			if(t instanceof NDSException) throw (NDSException)t;
	  		throw new NDSException(t.getMessage(), t);
		}
		
	}
	/**
	 * 
	 * @param bpartnerId c_bpartner.id
	 * @return �������������������ͻ������ۼ�
	 * @throws NDSException
	 */
	public static int getProductPriceByAgentId(int bpartnerId) throws NDSException{
		int price=0;		 
		price=Tools.getInt(QueryEngine.getInstance().doQueryOne("select priceso from m_product_price where c_bpartner_id="+bpartnerId),0);
		if(price==0){
			Configurations conf=(Configurations)nds.control.web.WebUtils.getServletContextManager().getActor(nds.util.WebKeys.CONFIGURATIONS);
			int pdtid=Tools.getInt(conf.getProperty("var.default.product"),-1);
			price=Tools.getInt(QueryEngine.getInstance().doQueryOne("select priceretail from m_product where id="+pdtid),0);
			if(price==0){
				throw new NDSException("δ����Ĭ�����۵���!");
			}
		}
		return price;
	}
    
	/**
	 * ͨ�����������userid������Ǵ����̾�ȡ�������̵ļ۸�
	 * @param userId users.id
	 * @return ��Ʒ�۸�
	 * ͨ�����������userid ȥ�Ҷ�Ӧ��c_bpartner_id ��m_product_price���Ӧ��c_bpartner_idȡ������
	 * ����۸񲻴��ڣ���ȡĬ�ϵĴ����̵�id,��m_productȡĬ�ϵ���ͨ�����
	 */
	public static int getVARPOPrice(int userId) throws NDSException{
	    int price=0;		 
		 try {
			if(isAgent(userId)){
					int c_bpartnerId=Tools.getInt(QueryEngine.getInstance().doQueryOne("select c_bpartner_id from users where id="+userId),-1);
					if(c_bpartnerId!=-1) {
						price=Tools.getInt(QueryEngine.getInstance().doQueryOne("select pricepo from m_product_price where c_bpartner_id="+c_bpartnerId),0);							
							if(price==0){
							Configurations conf=(Configurations)nds.control.web.WebUtils.getServletContextManager().getActor(nds.util.WebKeys.CONFIGURATIONS);
							int pdtid=Tools.getInt(conf.getProperty("var.default.product"),-1);
							price=Tools.getInt(QueryEngine.getInstance().doQueryOne("select pricevar from m_product where id="+pdtid),0);
							if(price==0){
							throw new NDSException("δ������ͨ�����!");
						  }
						}
					}
			}else{
				throw new NDSException("ҳ����ʴ��󣬱�������̲���ʹ�ñ�ҳ��");	
			}
			return price;
		 }catch (Throwable t) {
				if(t instanceof NDSException) throw (NDSException)t;
		  		throw new NDSException(t.getMessage(), t);
		 }
		
	}
	
	/**
	 * @param serialno c_bpartner.serialno
	 * @return ��Ʒ�۸�
	 * ͨ�����������serialno ȥ�Ҷ�Ӧ��c_bpartner_id ��m_product_price���Ӧ��c_bpartner_idȡ�����ۼ۸�
	 * ����۸񲻴��ڣ���ȡĬ�ϵĴ����̵�id,��m_productȡĬ�ϵ����ۼ۸�
	 * ���c_bpartner_id�����ڣ���ȡĬ�ϵĴ����̵�id,��m_productȡĬ�ϵ����ۼ�
	 */
	public static int getRetailPrice(String serialno) throws NDSException{
		 int price=0;
		 try {
		  int varid= Tools.getInt(QueryEngine.getInstance().doQueryOne("select id from c_bpartner where serialno='"+serialno+"'"),-1);
		  Configurations conf=(Configurations)nds.control.web.WebUtils.getServletContextManager().getActor(nds.util.WebKeys.CONFIGURATIONS);
		  int pdtid=Tools.getInt(conf.getProperty("var.default.product"),-1);
		  if(varid==-1){
			 price=Tools.getInt(QueryEngine.getInstance().doQueryOne("select priceretail from m_product where id="+pdtid),0);	
			 if(price==0){
				 throw new NDSException("δ����Ĭ�����۵���!");	  
			 }
		   }else{
				 price=Tools.getInt(QueryEngine.getInstance().doQueryOne("select priceso from m_product_price where c_bpartner_id="+varid),0);
				 if(price==0){
				 price=Tools.getInt(QueryEngine.getInstance().doQueryOne("select priceretail from m_product where id="+pdtid),0);
				 if(price==0){
				 throw new NDSException("δ�������۵���!");
			   }
			 }
		   }
		   return price;
		 }catch (Throwable t) {
				if(t instanceof NDSException) throw (NDSException)t;
		  		throw new NDSException(t.getMessage(), t);
		 }
		 
	}
	
	/**
	 * ͨ�����������к�e_orderid������varorder���ֵ��
	 *
	 */
	public static VAROrder getVAROrder(int id) throws NDSException{
		try {
			List order= QueryEngine.getInstance().doQueryList("select id,docno,doctype,pemail,pname,c_bpartner_id,amt,ptruename,payer_id,status,state from e_order where id="+id);
			VAROrder varorder=new VAROrder();
			if(order.size()!=0){
				varorder.setId(Tools.getInt(((List)order.get(0)).get(0),-1));
            	varorder.setDocno((String)((List)order.get(0)).get(1));
            	varorder.setDoctype((String)((List)order.get(0)).get(2));
            	varorder.setPemail((String)((List)order.get(0)).get(3));
            	varorder.setPname((String)((List)order.get(0)).get(4));
            	varorder.setC_bpartner_id(Tools.getInt(((List)order.get(0)).get(5),-1));
            	varorder.setAmt(Tools.getBigDecimal(((List)order.get(0)).get(6).toString(),false).doubleValue());
            	varorder.setPtruename((String)((List)order.get(0)).get(7));
             	varorder.setPayer_id(Tools.getInt(((List)order.get(0)).get(8),-1));
            	varorder.setStatus(Tools.getInt(((List)order.get(0)).get(9),-1));
            	varorder.setState((String)((List)order.get(0)).get(10));	
            }else {
            	throw new NDSException("����δ�ҵ�������֧���������´���");
            }
            if(varorder.getAmt()<=0){
            	throw new NDSException("��������ȷ�������´���");
            }
            
            if(varorder.getC_bpartner_id()!=-1){
            	String c_bpartnername=(String)QueryEngine.getInstance().doQueryOne("select name from c_bpartner where id="+varorder.getC_bpartner_id());
            	varorder.setC_bpartner_name(c_bpartnername);
            }
     	 return varorder;
 		} catch (Throwable t) {
			if(t instanceof NDSException) throw (NDSException)t;
	  		throw new NDSException(t.getMessage(), t);
		}

	}
	
	/**
	 * ͨ�����������к�e_order�ĵ��ݱ��docno������varorder���ֵ��
	 *
	 */
     public static VAROrder getVAROrder(String  docno) throws NDSException{
    	 try {
 			List order= QueryEngine.getInstance().doQueryList("select id,docno,doctype,pemail,pname,c_bpartner_id,amt,ptruename,payer_id,status,state from e_order where  docno='"+docno+"'");
 			VAROrder varorder=new VAROrder();
 			if(order.size()!=0){
             	varorder.setId(Tools.getInt(((List)order.get(0)).get(0),-1));
             	varorder.setDocno((String)((List)order.get(0)).get(1));
             	varorder.setDoctype((String)((List)order.get(0)).get(2));
             	varorder.setPemail((String)((List)order.get(0)).get(3));
             	varorder.setPname((String)((List)order.get(0)).get(4));
             	varorder.setC_bpartner_id(Tools.getInt(((List)order.get(0)).get(5),-1));
            	varorder.setAmt(Tools.getBigDecimal(((List)order.get(0)).get(6).toString(),false).doubleValue());
             	varorder.setPtruename((String)((List)order.get(0)).get(7));
             	varorder.setPayer_id(Tools.getInt(((List)order.get(0)).get(8),-1));
             	varorder.setStatus(Tools.getInt(((List)order.get(0)).get(9),-1));
             	varorder.setState((String)((List)order.get(0)).get(10));	
             }else {
             	throw new NDSException("����δ�ҵ�������֧���������´���");
             }
             if(varorder.getAmt()<=0){
             	throw new NDSException("��������ȷ�������´���");
             }
             if(varorder.getC_bpartner_id()!=-1){
             	String c_bpartnername=(String)QueryEngine.getInstance().doQueryOne("select name from c_bpartner where id="+varorder.getC_bpartner_id());
             	varorder.setC_bpartner_name(c_bpartnername);
             }
      		return varorder;
  		} catch (Throwable t) {
			if(t instanceof NDSException) throw (NDSException)t;
	  		throw new NDSException(t.getMessage(), t);
 		}
	}
     
    
}
