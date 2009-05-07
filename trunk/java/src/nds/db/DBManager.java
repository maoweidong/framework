package nds.db;

import java.sql.Connection;
import java.util.Collection;
import java.util.Properties;

import nds.query.QueryException;
import nds.query.QueryRequestImpl;
import nds.query.*;
import nds.util.DestroyListener;

public interface DBManager extends DestroyListener  {
    public void init(Properties props);
    public Collection executeFunction ( String fncName, Collection params, Collection results,Connection conn) throws QueryException ;
    public SPResult executeStoredProcedure(String spName, Collection params, boolean hasReturnValue) throws QueryException;
    public SPResult executeStoredProcedure(String spName, Collection params, boolean hasReturnValue, Connection con) throws QueryException;

    /**  ���ú���GET_EMPLOYEEID(int):����Oracle�еĺ���
     *   ������operateid(User ���е�ID)
     *   ����: employee���е�id
     */
    public int getEmployeeId( int operateid) throws QueryException ;
    public int getSequence( String tableName, Connection conn) throws QueryException ;
    /**  ���ú���GETMAXID(int):����Oracle�еĺ���
         *   ������operateid(User ���е�ID)
         *   ����: employee���е�id
         */
    public String getSheetNo(String tableName, int clientId) throws QueryException   ;
    /**  ���ú���GETSheeetStatus(int):����Oracle�еĺ���
     *   ������operateid(User ���е�ID)
     *   ����: employee���е�id
     */
    public int getSheetStatus(String tableName,int tableId) throws QueryException ;

    public QueryRequestImpl createRequest();
    public QueryRequestImpl createRequest(QuerySession session);
}