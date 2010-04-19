/*
 * Agile Control Technologies Ltd,. CO.
 * http://www.agileControl.com
 */
package nds.control.util;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.*;
import java.util.Locale;

import nds.control.web.SecurityManagerWebImpl;
import nds.control.web.WebUtils;
import nds.log.Logger;
import nds.log.LoggerManager;
import nds.query.*;
import nds.security.*;
import nds.schema.*;
import nds.util.*;

/**
 * 
 * @author yfzhu@agilecontrol.com
 */

public class SecurityUtils {
    private static Logger logger= LoggerManager.getInstance().getLogger(SecurityUtils.class.getName());
    private static final String GET_SECURITY_FILTER="select sqlfilter, filterdesc,t.name,SEC_FKFILTER_IDS from groupperm, directory, ad_table t where (t.id=directory.ad_table_id) and  groupid in (select groupid from groupuser where userid=? ) and directoryid in (select id from directory where upper(name)=?) and bitand(permission,?)+0=? and directory.id=directoryid";
    private final static String GET_PERMISSION="select GetUserPermission(?,?) from dual";
    private final static String GET_USER_BY_NAME_AND_CLIENT="select u.id, u.name, u.isactive,u.ad_client_id, u.ad_org_id, u.language, c.name, u.isadmin from users u, ad_client c where u.name=? and u.ad_client_id=c.id and c.domain=?";
    private final static String GET_USER_BY_EMAIL_AND_CLIENT="select u.id, u.name, u.isactive,u.ad_client_id, u.ad_org_id, u.language, c.name ,u.isadmin from users u, ad_client c where u.email=? and u.ad_client_id=c.id and c.domain=?";
    private final static String GET_USER_BY_ID="select u.name,c.domain,u.isactive, u.description, u.ad_client_id, u.ad_org_id, u.language, c.name,u.isadmin  from users u,ad_client c where u.ID=? and c.id=u.ad_client_id";
    
    
    /**
    @roseuid 3BF38E580012
    */
    public static int getPermission(String dirName, int userId)throws QueryException,java.rmi.RemoteException {
        Connection con=QueryEngine.getInstance().getConnection();
        PreparedStatement pstmt= null;
        ResultSet rs=null;
        try {
            pstmt= con.prepareStatement(GET_PERMISSION);
            //    private static String GET_PERMISSION="select GetUserPermission(?,?) from dual";
            pstmt.setString(1,dirName);
            pstmt.setInt(2, userId);
            rs= pstmt.executeQuery();
            if( rs.next() ){
                return rs.getInt(1);
            }
        } catch (Exception ce) {
            logger.error("Error getting permission for "+dirName+" of userId="+userId);
            throw new NDSRuntimeException("Error getting SbSecurityManager",ce);
        }finally{
            if( rs !=null){ try{ rs.close();}catch(Exception e){}}
            if( pstmt !=null){ try{ pstmt.close();}catch(Exception e){}}
            if( con !=null){ try{ con.close();}catch(Exception e){}}
        }
        return 0;
    }
    /**
     * write a int array to String, for example,
     * int[4]={1,3,545,3};
     * the return string will be: "1,3,545,3"
     */
    public static String toString(Object[] cs) {
    	if (cs==null ) return "";
        String s="";
        for(int i=0;i< cs.length;i++) {
            if( i==0)
                s += ""+cs[i];
            else
                s +=","+cs[i];
        }
        return s+"";
    }
    public static boolean hasPermissionOnAll(int userId, String userName, String tableName, 
    		Object[] objectIds, int permission, QuerySession qsession)throws Exception{
    	return hasPermissionOnAll(userId, userName, tableName, objectIds, permission, qsession, null);
    }
    /**
     * Check whether has permission on all objects
     * @param userId
     * @param userName
     * @param tableName
     * @param objectIds
     * @param permission
     * @param qsessions
     * @param addtionalFilter in same format as table.getFilter()
     * @return
     * @throws Exception
     */
    public static boolean hasPermissionOnAll(int userId, String userName, String tableName, 
    		Object[] objectIds, int permission, QuerySession qsession, String addtionalFilter)throws Exception{
    	if( /*"root".equals(userName) ||*/ objectIds==null || objectIds.length==0) return true; //root
    	TableManager manager=TableManager.getInstance();
    	QueryEngine engine= QueryEngine.getInstance();
    	Table table=manager.getTable(tableName);
    	//if(permission == nds.security.Directory.SUBMIT|| permission == Directory.EXPORT )throw new NDSException("Internal error: perm is not supported currently");
    	if(!"root".equals(userName) &&  (getPermission(table.getSecurityDirectory(), userId) & permission)!=permission) return false; 
    	QueryRequestImpl query= engine.createRequest(qsession);
    	query.setMainTable(table.getId());
    	query.addSelection(table.getPrimaryKey().getId() );
    	Expression expr= new Expression();
    	expr.setColumnLink(tableName+"." + table.getPrimaryKey().getName());
    	expr.setCondition(" IN (" + toString(objectIds)+")");
    	if(Validator.isNotNull(addtionalFilter)){
    		Expression expr2=new Expression(null, addtionalFilter , null);    		
    		expr= expr.combine(expr2, SQLCombination.SQL_AND, null);
    	}
    	Expression exprw= getSecurityFilter(tableName,permission,userId, qsession );
    	if (! exprw.isEmpty())
    		query.addParam( expr.combine(exprw, expr.SQL_AND, " AND ") );
    	else
    		query.addParam( expr);
    	QueryResult result= engine.doQuery(query);
    	logger.debug(query.toSQL());
    	return result.getTotalRowCount() ==  objectIds.length ;
    	
    }

    /**
     * Check user's permission on specified object
     * @param tableName the table name of the object
     * @param objectId  the pk id of the table
     * @param permission nds.security.Directory.READ/WRITE/AUDIT
     * @return true if current user has that permission on the object
     * @throws Exception
     */
    public static boolean hasObjectPermission(Connection conn, int userId, String userName, String tableName, 
    		int objectId, int permission, QuerySession qsession) throws QueryException, RemoteException{
    	//if( "root".equals(userName) ) return true; //root
    	TableManager manager=TableManager.getInstance();
    	QueryEngine engine= QueryEngine.getInstance();
    	Table table=manager.getTable(tableName);
    	//if(permission == nds.security.Directory.SUBMIT|| permission == Directory.EXPORT )throw new NDSException("Internal error: perm is not supported currently");
    	if( !"root".equals(userName) && (getPermission(table.getSecurityDirectory(), userId) & permission)!=permission) return false; 
    	QueryRequestImpl query= engine.createRequest(qsession);
    	query.setMainTable(table.getId());
    	query.addSelection(table.getPrimaryKey().getId() );
    	Expression expr= new Expression();
    	expr.setColumnLink(tableName+"." + table.getPrimaryKey().getName());
    	expr.setCondition("=" + objectId);
    	expr.setDescription("("+ table.getDescription(qsession.getLocale())+".ID=" + objectId+")");
    	
    	Expression exprw= getSecurityFilter(tableName,permission,userId, qsession );
    	expr= expr.combine(exprw,  expr.SQL_AND, " AND ") ;
    	
    	// check column "status"
    	if(((permission & Directory.WRITE)==Directory.WRITE) &&   table.isActionEnabled(Table.SUBMIT)){
    		exprw=new Expression();
    		exprw.setColumnLink(tableName+"." + "STATUS");
    		exprw.setCondition("<>2");
    		exprw.setDescription("("+ table.getDescription(qsession.getLocale())+".STATUS<>2)");
        	expr= expr.combine(exprw,  expr.SQL_AND, " AND ") ;
    	}
    	query.addParam(expr);
    	
    	QueryResult result= (conn==null?engine.doQuery(query): engine.doQuery(query, conn));
    	logger.debug(query.toSQL());
    	return result.getTotalRowCount()> 0 ;
    	
    }	
    /**
     * Check user's permission on specified object
     * @param tableName the table name of the object
     * @param objectId  the pk id of the table
     * @param permission nds.security.Directory.READ/WRITE/AUDIT
     * @return true if current user has that permission on the object
     * @throws Exception
     */
    public static boolean hasObjectPermission(int userId, String userName, String tableName, 
    		int objectId, int permission, QuerySession qsession) throws QueryException, RemoteException{
    	return hasObjectPermission(null, userId,userName,tableName,objectId,permission,qsession);
    	
    }	
    /**
     * ����tablename �ҵ���Ӧ��directory, �����Ӧ�Ĺ�����������Ϊ���������
     * 1����Щ���綩���У�ʹ���������������İ�ȫ��������
     * 2������һЩ���繩Ӧ�̣�����������������������Ӽ�������Ҳû�й�������
     * 
     * ���ڵ�1����������ڱ��ID��ͬ����Ҫ���¹���������� �����2����ID��ͬ�������ع���
     * ���������1���ع�����
     * ���趩���Ĺ�����Ϊ order.column1=data and order.column2=data2
     * �����Ͷ����д��ڹ�ϵ��order.id=oreritem.orderid, �򶩵��еĹ�����Ϊ
     * exists (select 1 from order where order.id=orderitem.orderid and
     *  order.id in (select id from order where (order.column1=data and order.column2=data2))
     * ��һ����Ϊ��
     * exists (select id from order where order.id=orderitem.orderid and
     * 	order.column1=data and order.column2=data2)
     * 
     * ��׼��ʽ��exists (select ID from <DIR_TABLE> WHERE <RELATIONSHIP> AND
     *   <DIR_FILTER>)
     * 
     * ���ʱӦע�⣺directory �е�tablename ���������ݿ�����ʵ��table �� view
     * @param permission, 1 for read, 3 for write, 5 for submit, 9 for audit, combine, so 7 for read/write/submit
     * @param qs if null, will not construct 'exists' expression (for DefaultWebEventHelper use only)
     * @return empty exprssion if not found, nerver return null!
     *
     */
    public static Expression getSecurityFilter(String tableName, int permission, int userId, QuerySession qs) throws QueryException{
        Connection con= null;
        ResultSet rs=null;
        PreparedStatement pstmt=null;
        try{
            con=QueryEngine.getInstance().getConnection();
            pstmt= con.prepareStatement(GET_SECURITY_FILTER);
            pstmt.setInt(1, userId);
            pstmt.setString(2,TableManager.getInstance().getTable(tableName).getSecurityDirectory() );
            pstmt.setInt(3, permission);
            pstmt.setInt(4, permission);
            rs= pstmt.executeQuery();
            StringHashtable st=new StringHashtable(10, 8000); 
            String sql,desc,tn, fkfilterIds;
            while( rs.next()){
                sql= rs.getString(1);
                desc= rs.getString(2);
                tn= rs.getString(3);
                fkfilterIds=rs.getString(4);
                if ( Validator.isNotNull(sql) ||  Validator.isNotNull(fkfilterIds) ){
                    st.put(sql+"#"+fkfilterIds, new String[]{desc, tn,sql,fkfilterIds});
                    logger.debug("found sql:" + sql +", with desc=" + desc);
                }else{
                	//has at least one group which allows current user to work on all data
                	st.clear();
                	break;
                }

            }
            if( st.size() > 0){
                TableManager tm= TableManager.getInstance();
                Table table= tm.getTable(tableName);
                int[] cids= new int[1]; // contains only pk column
                cids[0]= table.getPrimaryKey().getId() ;
                ColumnLink cl= new ColumnLink( cids);

                // or them
                Enumeration enu= st.keys();// key is just value
                Expression expr1, expr=null;
                while( enu.hasMoreElements() ){
                    String key=(String) enu.nextElement();
                    String[] v=(String[]) st.get(key);
                    desc=v[0];
                    tn= v[1];
                	sql=v[2]; // the sqlfilter is xml=Expression.toString() with CDATA indeed.
                	fkfilterIds=v[3];
                	//logger.debug("desc="+ desc+", tn="+tn+", sql="+ sql+", fkfilterIds="+fkfilterIds);
                    //expr1= new Expression(cl, sql,desc);
                    if( tableName.equalsIgnoreCase(tn)|| nds.util.Validator.isNull(tn)){ 
                    	if(Validator.isNotNull(sql))
                    		expr1=new Expression(sql); // xml
                    	else expr1=null;
                    	
                		//combine expr1 with this one (SQL_AND)
                		Expression e= loadFkFilters(table.getId(),fkfilterIds,qs);
                		
                		if(e!=null && !e.isEmpty())
                			expr1=e.combine(expr1,SQLCombination.SQL_AND,null);
                		
                    }else{
                    	//add fkfilter
                    	//combine expr1 with this one (SQL_AND)
                		Expression e= loadFkFilters(table.getId(),fkfilterIds,qs);
                    	if(qs==null){
                    		//this method will called by DefaultWebEventHelper, which 
                    		//has no value for qs, so just let it be without constructing
                    		//'exists' expression
                    		expr1=e;
                    	}else{
                    		expr1=constructExistsExpr(sql, desc, table, tn, qs);
                    		if(expr1==null || expr1.isEmpty())
                    			expr1=e;
                    		else
                    			expr1=expr1.combine(e,SQLCombination.SQL_AND,null);
                    	}
               			
                		
                    }
                    if(expr1!=null && !expr1.isEmpty()){
                    	if( expr==null) expr= expr1;
                    	else expr= expr.combine(expr1,SQLCombination.SQL_OR, null); // or relationship
                    }
                    logger.debug("expr="+ expr);
                }
                if(expr==null){
                	expr=Expression.EMPTY_EXPRESSION;
                }
                return expr;
            }else{
                logger.debug("getSecurityFilter("+ tableName+","+ userId+","+permission+")= empty");
                //return new Expression(null, "1=1","1=1");
                return Expression.EMPTY_EXPRESSION;
            }
        }catch(SQLException e){
        	throw new QueryException("Error when checking security",e);
        }finally{
            if( rs !=null) try{rs.close();}catch(Exception e){}
            if( pstmt !=null) try{pstmt.close();}catch(Exception e2){}
            if( con!=null) try{con.close();}catch(Exception e3){}
        }
    }
    /**
     * Load all sqlfilter as Expression of sec_fkfilter whose id in list of <param> fkFitlerIds</param>
     * @param tableId table which should have those expressions specified by fkFilterIds as filter for sql construction 
     * @param fkFilterIds 'id1,id2,..,' id of sec_fkfilter
     * @return 
     * @throws QueryException
     */
    public static Expression loadFkFilters(int tableId,String fkFilterIds,QuerySession qsession) throws QueryException,SQLException{
    	if(Validator.isNull(fkFilterIds)) return Expression.EMPTY_EXPRESSION;
    	//will use s.COLUMNS_APPLIED to find out which column of current table should apply the filter 
    	List al=QueryEngine.getInstance().doQueryList("select s.sqlfilter, s.COLUMNS_APPLIED,s.DESCRIPTION  from sec_fkfilter s, table(split_str("+
    				QueryUtils.TO_STRING(fkFilterIds.trim())+",',')) t where t.column_value=s.id");
    	TableManager manager=TableManager.getInstance();
    	Expression expr1,expr=null;
    	for(int i=0;i<al.size();i++){
    		List o=(List)al.get(i);
    		java.sql.Clob s= (java.sql.Clob)o.get(0);
    		String sqlFilter= s.getSubString(1, (int) s.length());
    		Filter filter=new Filter(sqlFilter);
    		Expression sqlExpr=filter.getExprObject(); // this is work on table specified by sec_fkfilter.ad_table_id
    		s= (java.sql.Clob)o.get(1);
    		String columns= s.getSubString(1, (int) s.length());
    		String desc= (String)o.get(2);
    		//which column
    		Filter f=new Filter(columns); // this will get a list of column on which that sqlExpr will apply, including those columns of table specified by tableId
    		List cls=QueryEngine.getInstance().doQueryList("select distinct id from ad_column where ad_table_id="+ tableId+" and id "+f.getSql());
    		for(int j=0;j<cls.size();j++){
    			//we only need the columns on tableId, and can use sqlExpr as expression to do filter
    			int cid= Tools.getInt(cls.get(j),-1);
    			Column c= manager.getColumn(cid);
    			if(c==null){
    				logger.error("find invalid column id="+cls.get(j)+ " in fkFilter id list( "+ fkFilterIds+") for table id="+tableId);
    				continue;
    			}
    			logger.debug("column="+c);
    	    	QueryRequestImpl query=QueryEngine.getInstance().createRequest(qsession);
    	    	query.setMainTable(c.getReferenceTable().getId());
    	    	query.addSelection(c.getReferenceTable().getPrimaryKey().getId());
    	    	
    	    	logger.debug(sqlExpr.toString());
    	    	query.addParam(sqlExpr);
    	    	String exSQL=query.toSQL();
    	    	logger.debug("sql:"+ exSQL);
    			expr1=new Expression(new ColumnLink(new int[]{cid}), "in ("+exSQL+")", c.getDescription(manager.getDefaultLocale())+":"+ desc  );
    			logger.debug(expr1.toString());
    			expr=expr1.combine(expr,SQLCombination.SQL_AND, null);
    		}
    		
    	}
    	return expr;
    }
    /**
     * 2009-12-11 ������������һ�������
     *    ������Ա�ڸ�����ͼ��ʱ�򣬽�A���directory���Ƶ���B����
     *    ��AB��һ����ͬһ��������2����ͼ����AB�����ֵܹ�ϵ������ԭ�����ǵ�ͷ��ϸ���ӹ�ϵ����
     *    ���ڣ����A �����directory  �����˲�ѯ����һ��Ҳ���A���� A���c�ֶεĲ�ѯ��Χ��ĳĳ�������B������
     *    ����ʱ��Ϊ B���c �ֶΣ��ض�ͬ�������û�ҵ��ͱ����Ĳ�ѯ��Χ��ĳĳ��
     *    
     *    ��ǰ�ڲ�ѯ���������ʱ���ᱨ�棺"Could not found relation ship from table " 
    			+ dirTableName +" to sub table " + table
				+ ", while these two tables have same security driectory." ����Ҫ���Զ�ʶ��ת��
				
     *    
     * @param sql used to construct DIR_FILTER, stored in directory.sqlfilter, working on dirTable
     * @param desc sql description
     * @param table the table that return expression will put on, this one is what we want to query
     * @param dirTableName DIR_TABLE 
     * @return Expression
     *  ��׼��ʽ��exists (select ID from <DIR_TABLE> WHERE <RELATIONSHIP> AND
     *   <DIR_FILTER>)
     *   
     */
    private static Expression constructExistsExpr(String sql, String desc, Table table, String dirTableName,QuerySession qsession) throws QueryException{
    	Expression expr=null;
    	Expression dirFilter=new Expression(sql);
    	dirFilter.setDescription(desc);
    	TableManager manager=TableManager.getInstance();
    	Table dirTable=manager.getTable(dirTableName);
    	if(dirTable==null)throw new QueryException("error in directory setting, table " + dirTableName +" not found");
    	// find relationship between dirTable and table
    	// dirTable will be the main table( think it as order, and table as order line)
    	// relationship would like "dirTable.id=table.refbycolumnId"
    	RefByTable rbt=null;
    	for(Iterator it= dirTable.getRefByTables().iterator();it.hasNext();){
    		rbt= (RefByTable) it.next();
    		if( rbt.getTableId() == table.getId() ){
    			//���ӹ�ϵ
    			Column rbc=  manager.getColumn(rbt.getRefByColumnId());
    			ColumnLink cl= new ColumnLink(new int[]{dirTable.getPrimaryKey().getId()});
    			expr=new Expression(cl, "=" + table.getName()+"." + rbc.getName(), null);
    		}
    	}
    	/* 2009-12-11 ע�͵�(yfzhu) ֧���ֵܹ�ϵ
    	 * if(expr ==null) throw new QueryException("Could not found relation ship from table " 
		+ dirTableName +" to sub table " + table
		+ ", while these two tables have same security driectory.");*/
    	if(expr==null){
    		//�ֵܱ����ֻ�����������������ֵܱ������ݿ��ﶨ������ͼ����������AD_TABLE�ﶨ����ͼ�����Զ���PORTAL��˵��
    		//��֪���ֵܹ�ϵ�Ĵ��ڣ�����ֻ��Ĭ��:�û����õ�Ȩ���ֶΣ���dirTable�ϴ��ڣ�Ӧ��ҲҪ��table�ϴ��ڣ���������ڣ�
    		//ҲӦ�������ݿ�����ڣ���Щ�˻ᶨ����ͼʱ��©���ֶΣ���ʱ��������ÿ�����Ա֪����
    		/**
    		 * �ع�expr, ��Ϊexpr���dirTableName����table����������ʵ���Ͼ���ҪӦ�õ�table��ȥ��
    		 */
    		//͵�����������ֶε�����ȶ��ˣ�ֱ���滻���滻�ķ�ʽ��ǰ�󶼲�������ĸ
    		String nsql=sql.replaceAll("\\b"+ dirTableName+"\\b", table.getName());
    		logger.debug("brother relationship found:from "+ sql);
    		logger.debug("replaced to "+ nsql);
    		expr=new Expression(nsql);
    		return expr;// return directly 
    	}else{
    		expr= expr.combine(dirFilter, SQLCombination.SQL_AND, MessagesHolder.getInstance().getMessage(qsession.getLocale(), "correspond-") 
    			+ dirTable.getDescription(qsession.getLocale())+" "+MessagesHolder.getInstance().getMessage(qsession.getLocale(), "-satisfy-")+ desc +")");
    	}
    	QueryRequestImpl query=QueryEngine.getInstance().createRequest(qsession);
    	query.setMainTable(dirTable.getId());
    	query.addSelection(dirTable.getPrimaryKey().getId());
    	query.addParam(expr);
    	String exSQL=query.toSQL();
    	return new Expression(null, "exists (" + exSQL +")" , MessagesHolder.getInstance().getMessage(qsession.getLocale(), "-exists-")+ "(" + expr.getDescription()+")");
    }
    public static User getUser(String lportalUserId){
		String uName, adclientName ;
		int p=lportalUserId.indexOf("@");
		if ( p>0){
			 uName= lportalUserId.substring(0,p );
			 adclientName= lportalUserId.substring(p+1);
			 return getUser(uName, adclientName);
		}
    	User usr=new User();
    	usr.setId(new Integer(-1));
    	return usr;
    }
    /**
     * return -1 if not found
     * @param userNameOrEmail, whether email or user name is base upon 
     * 	nds.control.web.WebUtils#isServerRunInSingleCompanyMode
     * when simple company mode, userNameOrEmail is email, else, user name
     * @param clientName
     * @return
     */
    public static  User getUser(String userNameOrEmail, String clientName){
    	User usr=new User();
    	usr.setId(new Integer(-1));
    	Connection con= null;;
    	ResultSet rs=null;
    	PreparedStatement pstmt=null;
    	try{
    		con=nds.query.QueryEngine.getInstance().getConnection();
    		pstmt=con.prepareStatement(nds.control.web.WebUtils.isServerRunInSingleCompanyMode()?
    				GET_USER_BY_EMAIL_AND_CLIENT:GET_USER_BY_NAME_AND_CLIENT);
	    	pstmt.setString(1, userNameOrEmail);
	    	pstmt.setString(2, clientName);
	    	rs=pstmt.executeQuery();
	    	if(rs.next()){
	    		usr.setId(new Integer( rs.getInt(1)));
	        	usr.setName(rs.getString(2));
	    		usr.setActive(Tools.getYesNo( rs.getString(3), true));
	        	usr.setClientDomain(clientName);
	    		usr.adClientId=rs.getInt(4);
	    		usr.adOrgId =rs.getInt(5);
	    		usr.locale = getLocale( rs.getString(6) );
	    		usr.clientDomainName= rs.getString(7);
	    		usr.setIsAdmin(rs.getInt(8));
	    	}
    	}catch(Exception e){
    		logger.error("Could not fetch user according to userNameOrEmail="+ userNameOrEmail+
    				", client="+ clientName+" with isServerRunInSingleCompanyMode="+
    				nds.control.web.WebUtils.isServerRunInSingleCompanyMode(), e);
    	}finally{
    		if(rs!=null)try{rs.close();}catch(Exception e2){}
    		if(pstmt!=null)try{pstmt.close();}catch(Exception e2){}
    		if(con!=null)try{con.close();}catch(Exception e2){}
    	}
    	return usr;
    	
    }
    public static  User getUser(int userId) throws Exception{
    	User usr=new User();
    	usr.setId(new Integer(userId));
    	Connection con= null;;
    	ResultSet rs=null;
    	PreparedStatement pstmt=null;
    	try{
    		con=nds.query.QueryEngine.getInstance().getConnection();
    		pstmt=con.prepareStatement(GET_USER_BY_ID);
	    	pstmt.setInt(1, userId);
	    	rs=pstmt.executeQuery();
	    	if(rs.next()){
	    		usr.setName(rs.getString(1));
	    		usr.setClientDomain(rs.getString(2));
	    		usr.setActive(Tools.getYesNo( rs.getString(3), true));
	    		usr.setDescription(rs.getString(4));
	    		usr.adClientId= rs.getInt(5);
	    		usr.adOrgId = rs.getInt(6);
	    		usr.locale = getLocale( rs.getString(7) );
	    		usr.clientDomainName= rs.getString(8);
	    		usr.setIsAdmin(rs.getInt(9));
	    	}
    	}/*catch(Exception e){
    		logger.error("Could not fetch user according to user id="+ userId, e);
    	}*/finally{
    		if(rs!=null)try{rs.close();}catch(Exception e2){}
    		if(pstmt!=null)try{pstmt.close();}catch(Exception e2){}
    		if(con!=null)try{con.close();}catch(Exception e2){}
    	}
    	return usr;
    	
    }
    /**
     * 
     * @param loc
     * @return locale or default from tablemanager
     */
    private static Locale getLocale(String loc){
    	try{
    		if(Validator.isNull(loc)) return TableManager.getInstance().getDefaultLocale();
    		return Tools.getLocale(loc);
    	}catch(Throwable t){
    		logger.error("Locale "+ loc +" is not valid");
    		return TableManager.getInstance().getDefaultLocale();
    	}
    }
    
}
