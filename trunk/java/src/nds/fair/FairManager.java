package nds.fair;

import nds.control.util.SecurityUtils;
import nds.control.web.*;
import nds.log.Logger;
import nds.log.LoggerManager;
import nds.schema.*;
import nds.util.*;
import nds.query.*;

import java.io.*;
import java.sql.*;
import java.util.*;

public class FairManager {
	private static Logger logger= LoggerManager.getInstance().getLogger(FairManager.class.getName());	
	
	private static FairManager instance=null;
	
	/**
	 * Key: b_fair.id , value: List of categories (element is instanceof ProductCategoryList)
	 */
	private Hashtable fairCategories=new  Hashtable();
	
	/**
	 * Width of thumbnails
	 */
	private int[] thumbWidths;
	
	private String dimCategoryColumnName="m_dim7_id"; // AD_PARAM#portal.fair.category
	
	private String pdtFolderStr; //E:/portal422/server/default/deploy/nds.war/pdt
	private FairManager(){}
	
	public static synchronized FairManager getInstance(){
		if(instance==null){
			instance=new FairManager();
			instance.init();
		}
		return instance;
	}

	/**
	 * Init,loading parameters
	 *
	 */
	public void init(){
        Configurations conf= (Configurations)WebUtils.getServletContextManager().getActor( nds.util.WebKeys.CONFIGURATIONS);
        String thumbnails=conf.getProperty("fair.thumbnails", "");
        thumbWidths=nds.query.QueryUtils.parseIntArray(thumbnails);
        try{
        String cc=(String)QueryEngine.getInstance().doQueryOne("select value from ad_param where name='portal.category.column'");
        if(Validator.isNotNull(cc)) dimCategoryColumnName=cc;
        }catch(Throwable t){
        	logger.error("fail to load portal.category.column from ad_param table", t);
        }
        // path to thumbnail files
        String webRoot=conf.getProperty("web.root","E:/portal422/server/default/deploy/nds.war/html/nds");
        File pdtFolder= new File(webRoot);
        pdtFolder=new File(pdtFolder.getParentFile().getParentFile().getPath()+'/'+"pdt");
        if(!pdtFolder.exists()) pdtFolder.mkdirs();
        pdtFolderStr= pdtFolder.getAbsolutePath();
        fairCategories.clear();
	}
	 
	/**
	 * ���� b_pdt_media �ϵ�ͼƬ��������ͼ�� /pdt Ŀ¼��
	 * �� b_pdt_media ���� imgurl1, imgurl2.. imgurl5 �����ֶΣ�������ԭʼ��ͼƬ��С��
	 * ÿ��ͼ�������趨�ߴ���������������ͼ������ͼ�ߴ���������� portal.properties#fair.thumbnails
	 * 
	 * ����ͼ�ļ�����������m_product.id +"_"+ <ͼƬ�ֶε����(1..5)>+"_"+ <����ͼ��С����(1..3)>+".jpg"
	 * ����: 2934_2_1.jpg ��ʾ product.id=2934, imgurl2 �ֶζ�ӦͼƬ����������ͼ
	 * 
	 * @param b_pdt_media_id b_pdt_media.id
	 * @param m_product_id, m_product.id 
	 * @param clientDomain ad_client.domain
	 * @param force, true ��������ͼ�Ƿ���ڣ���ǿ�д���. ����ֻ�е�����ͼ�����ڣ�������ͼ����ʱ������ԭͼ�Ŵ���
	 */
	public void createThumbnails(int b_pdt_media_id,int m_product_id, String clientDomain, boolean force) throws Exception{
		TableManager tableManager= TableManager.getInstance();
		AttachmentManager attm=(AttachmentManager)WebUtils.getServletContextManager().getActor(nds.util.WebKeys.ATTACHMENT_MANAGER);
		Attachment att;
		File img,pdtImgFile;
		// IMGURL1,IMGURL2,IMGURL3,IMGURL4,IMGURL5
		for(int i=1;i< 6;i++){
			att= attm.getAttachmentInfo(clientDomain+"/B_PDT_MEDIA/IMGURL"+i, String.valueOf(b_pdt_media_id), -1);
			if(att==null){
				continue;
			}
			img= attm.getAttachmentFile(att);
			if(img.exists()){
				String templateThumbnailFileName=m_product_id+"_"+i+"."+ FileUtils.getExtension(img.getName()).toLowerCase();
				// ָ���Ķ������ͼ�еĵ�һ������ͼ�ļ���������ڣ���Ϊ����������ͼҲ����
			pdtImgFile= new File(pdtFolderStr+'/'+ m_product_id+"_"+i+"_1."+ FileUtils.getExtension(img.getName()).toLowerCase());
				//pdtImgFile= new File(clientDomain+"/B_PDT_MEDIA/IMGURL"+1);
				if(force || !pdtImgFile.exists() || pdtImgFile.lastModified() < img.lastModified() ){
					//FileUtils.copyFile(img, pdtImgFile);
					ImageUtils.createThumbnails(img.getAbsolutePath(),pdtFolderStr, templateThumbnailFileName, thumbWidths);
					
				}
			}else{
				// delete existing thumbs?
			}
		}
	}
	/**
	 * ��鶩�����Ӧ��������Ʒ���Ƿ񶼴���������ͼƬ
	 * @param fairId b_fair.id
	 * @param clientDomain ad_client.domain
	 * @throws Exception
	 */
	public void checkFairThumbnails(int fairId, String clientDomain)throws Exception{
		QueryEngine engine=QueryEngine.getInstance();
		Connection conn=null;
		Statement stmt=null;
        ResultSet rs=null;
        String sql="select m.id, m.m_product_id from b_pdt_media m, b_fairitem f where f.b_fair_id="+ fairId+" and m.m_product_id=f.m_product_id";
        logger.debug(sql);
        try{
        	conn= engine.getConnection();
	        stmt= conn.createStatement();
	        
	        rs= stmt.executeQuery(sql);
	        while(rs.next()) {
	        	createThumbnails(rs.getInt(1),rs.getInt(2), clientDomain ,false);
	        }
	        
        }finally{
                try{stmt.close();}catch(Exception ea){}
                try{rs.close();}catch(Exception e){}
                try{conn.close();}catch(Exception e){}
        }    			
	}
	/**
	 * �����û�Ȩ�޼��ص�ǰ���Է��ʵĶ�����
	 * @param user
	 * @return elements are List ( 0: b_fair.id, 1: b_fair.name )
	 * @throws Exception
	 */
	public List loadFairs(UserWebImpl user) throws Exception{
		QueryEngine engine=QueryEngine.getInstance();
		String now= ((java.text.SimpleDateFormat)(QueryUtils.dateNumberFormatter.get())).format(new java.util.Date());
		QueryRequestImpl query=engine.createRequest(user.getSession());
		Table table=TableManager.getInstance().getTable("b_fair");
		query.setMainTable(table.getId());
		query.addSelection(table.getPrimaryKey().getId());
		query.addSelection(table.getAlternateKey().getId());
    	
		Expression expr=new Expression(new ColumnLink("b_fair.datestart"),"<="+now,null);
    	expr=expr.combine(new Expression(new ColumnLink("b_fair.dateend"),">="+now,null),nds.query.SQLCombination.SQL_AND, null);
//    	expr=expr.combine( new Expression(new ColumnLink("b_fair.isactive"),"=Y",null),nds.query.SQLCombination.SQL_AND, null);
    	
    	Expression exprw= SecurityUtils.getSecurityFilter(table.getName(),nds.security.Directory.READ, user.getUserId(), user.getSession() );
    	if (! exprw.isEmpty())
    		query.addParam( expr.combine(exprw, expr.SQL_AND, " AND ") );
    	else
    		query.addParam( expr);
    	String sql=query.toSQL();
    	logger.debug(sql);
    	
    	return engine.doQueryList(sql);
	}
	/**
	 * ��ȡָ��������Ĳ�Ʒ����б�
	 * @param fairId b_fair.id
	 * @return element is instanceof ProductCategoryList
	 * @throws NDSException
	 */
	public List getProductCategoryList(int  fairId) throws NDSException{
		List productCategoryList=(List) fairCategories.get(new Integer(fairId));
		if(productCategoryList!=null) return productCategoryList;
		productCategoryList = new ArrayList();
		ProductCategoryItem pci = null;
		int cnt;
		int categoryId;
		try {
			List al = QueryEngine.getInstance().doQueryList("select a.id, a.attribname, count(distinct b.id) from m_dim a, m_product b, b_fairitem c where c.b_fair_id="+ fairId+ " and b.id=c.m_product_id and a.id(+)= b."+dimCategoryColumnName+" group by a.id, a.attribname order by a.attribname");
			for (int i = 0; i < al.size(); i++) {
				pci = new ProductCategoryItem();
				List item = (List) al.get(i);
				pci.setCategoryId(Tools.getInt(item.get(0), -1));
				if(Tools.getInt(item.get(0), -1)==-1){
					pci.setName("������Ʒ");
				}else{
				pci.setName((String) item.get(1));
				}
				pci.setCnt(Tools.getInt(item.get(2), 0));
				productCategoryList.add(pci);

			}
		} catch (Throwable t) {
			if (t instanceof NDSException)
				throw (NDSException) t;
			throw new NDSException(t.getMessage(), t);
		}
		// add to cache for later usage
		fairCategories.put(new Integer(fairId), productCategoryList);
		return productCategoryList;
	}
	 
	 public  boolean isAgent(int userId) throws NDSException{
			try {
				int count=Tools.getInt(QueryEngine.getInstance().doQueryOne("select count(*) from users u, c_customer p where  p.id=u.c_customer_id and u.id="+userId),-1);
				if(count!=0){
					return true;
				}else{
					return false;
				}	
			} catch (Throwable t) {
				if(t instanceof NDSException) throw (NDSException)t;
		  		throw new NDSException(t.getMessage(), t);
			}
			
		}
	 
	    public void clearCache(int  fairId){
	    	fairCategories.remove(new Integer(fairId));
		}
	    public static void  main(String arg[]){
	    	FairManager fairmanager=FairManager.getInstance();
	    	try {
				fairmanager.createThumbnails(7,38003,"burgeon",true);
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				e.printStackTrace();
			}
	    	
	    }
	    
}
