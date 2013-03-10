/******************************************************************
*
*$RCSfile: QueryRequest.java,v $ $Revision: 1.5 $ $Author: Administrator $ $Date: 2006/03/13 01:13:55 $
*
*$Log: QueryRequest.java,v $
*Revision 1.5  2006/03/13 01:13:55  Administrator
*no message
*
*Revision 1.4  2005/10/25 08:12:53  Administrator
*no message
*
*Revision 1.3  2005/08/28 00:27:04  Administrator
*no message
*
*Revision 1.2  2005/05/16 07:34:17  Administrator
*no message
*
*Revision 1.1.1.1  2005/03/15 11:23:23  Administrator
*init
*
*Revision 1.4  2003/09/29 07:37:28  yfzhu
*before removing entity beans
*
*Revision 1.3  2003/05/29 19:40:17  yfzhu
*<No Comment Entered>
*
*Revision 1.2  2003/04/03 09:28:21  yfzhu
*<No Comment Entered>
*
*Revision 1.1.1.1  2002/11/29 02:50:34  yfzhu
*Active POS
*
*Revision 1.3  2001/12/09 03:43:32  yfzhu
*no message
*
*Revision 1.2  2001/11/07 20:58:47  yfzhu
*no message
*
*
********************************************************************/
//Source file: F:\\work2\\tmp\\nds\\query\\QueryRequest.java

package nds.query;

import java.io.Serializable;
import java.util.ArrayList;

import nds.schema.Table;

/**
 * ��װ��ѯ����
 */
public interface QueryRequest extends Serializable {
	/**
	 * QuerySession contains the request environment such as user info
	 * @return QuerySession of that request
	 * @since 2.0
	 */
	public QuerySession getSession();
    /**
     * @return SQL string representing this query request
     * @roseuid 3B8534F101F0
     */
    public String toSQL() throws QueryException;
    /**
     * This method returns a sql only querying data of requested range.<p>
     *
     * If we take an orginal sql( can retrieved from toSQL() ), such as:<br>
     *   select field1,field2,field3���� from table where where_clause order by orderby_clause
     * <p>
     * Then this method will return string as:<br>
     *      select field1,field2,field3���� from ( <br>
     *      select rownum row_num,field1,field2,field3���� from ( <br>
     *      select field1,field2,field3���� from table where where_clause order by orderby_clause))<br>
     *      where row_num between range_start and range_end;<br>
     */
    public String toSQLWithRange() throws QueryException;
    /**
     * If we encapsulate a sql of request as:<br>
     * select t1.c1, t2.c1, t2.c2 from t1,t2 where t1.c1=?? ..<br>
     * then the count sql should be:<br>
     * select count(*) from t1,t2 where...<p>
     *
     * This is very useful for segment display of a large query result
     *
     * @return SQL that count the row that query would get
     *
     */
    public String toCountSQL() throws QueryException;

    /**
     * @param replaceVariables if true, will replace wildcard variables to QuerySession attributes
     * @return description of param conditions
     */
    public String getParamDesc(boolean replaceVariables);
    /**
     * Similiar to #toCountSQL, in such format of return string:
     *   select t1.id from t1,t2 where ...
     * This is used for sub query.
     * @param replaceVariables if true, will replace wildcard variables to QuerySession attributes
     * @return SQL that only select primarky key of main table
     * @throws QueryExpression
     */
    public String toPKIDSQL(boolean replaceVariables) throws QueryException;
    /**
     * ͨ��request���result��������ҳ�������ʾ��һ��result�����
     * HttpServletRequest."result" attribute ��
     * @roseuid 3B8535170118
     */
    public String getResultHandler();

    /**
     * ��ǰ������������ű�
     * @roseuid 3B85352403C0
     */
    public Table getMainTable();
    /**
     * һ�Ų�ѯҳ�������¼��
     */
    public int getRange();
    /**
     * query ���Խ����ҳ��ʾ�����ﷵ�ظò�ѯ�������ʾҳ�У�
     * ��һ����¼�ڲ�ѯ����е��к�
     */
    public int getStartRowIndex();

    /**
     * elements: Column, including both the to-be-displayed columns and those not showing
     * @see getDisplayColumnIndices
     * @roseuid 3B8537350103
     */
    public ArrayList getAllSelectionColumns();

    /**
     * �������е���Select�е��ж���Ҫ��ʾ�����������ڴӱ��column�����Ƕ���ͬʱ
     * ȡ����column���ڱ���������Ա����á�������Щ�������Ͳ���Ҫ��ʾ�����磺
     * select order.name, a.name, a.id from order, employee a where \
     *      order.id=? and a.id=order.auditorID
     *
     * a.id��Ȼ��select ���У�������Ҫ��ʾ�����ݡ����ǹ涨�����еĴӱ�Ҫ��ʾ��
     * �ֶΣ������������ڱ��������
     * ������������ӣ�����ֵΪ [0, 1]
     *
     * @roseuid 3B8537AA027E
     */
    public int[] getDisplayColumnIndices();

    /**
     * @return column name concatenated by references.
     * ������Ҫ��ʾ���Ƕ��������������ڵĲ������ƣ���Խ��column�ǣ�
     *  order.applierID, employee.departmentID, department.name
     *  ��Ӧ��column���Ʒֱ���:������, ���ţ����ơ���ϳɵ�����Ϊ��
     *      �����˲�������
     *  @param showNullableIndicator if true, the not nullable column will have (*) on its desc
     */
    public String[] getDisplayColumnNames(boolean showNullableIndicator);

    public String[] getDisplayColumnNames2(boolean showNullableIndicator);

    /**
     * ��select ������ܹ��ж��ٸ�column
     */
    public int getSelectionCount();
    /**
     * ����<code>position</code>ָ����select �ж�Ӧ���У�position ��0��ʼ��
     * ���Ϊ getSelectionCount()-1��
     * ���ص�int[] ��ÿһ����Column.getId()
     *
     */
    public int[] getSelectionColumnLink(int position);

    public ArrayList getAllSelectionColumnLinks();
    /**
     * Just selection titles
     * @return Selection's description in order as getAllSelectionColumnLinks
     * 
     */
    public ArrayList getAllSelectionDescriptions();

    /**
     *  ��<code>position</code>λ���ϵ�selection�Ƿ���ʾ
     */
    public boolean isSelectionShowable(int position);
    /**
     * ��ѯ��������
     */
    public int getParamCount();
    /**
     * ����<code>position</code>ָ���Ĳ�ѯ�����ж�Ӧ���У�position ��0��ʼ��
     * ���Ϊ getParamCount()-1
     * ���ص�int[] ��ÿһ����Column.getId()
     */
    public int[] getParamColumnLink(int position);
    /**
     * ����<code>position</code>ָ���Ĳ�ѯ�����ж�Ӧ���еĲ���ֵ��position ��0��ʼ��
     * ���Ϊ getParamCount()-1
     * ���ص�StringΪ����������������ͬ��ѯҳ���ϵ�input���������
     */
    public String getParamValue(int position);
    /**
     * ��ѯ�����order by��������������order by ������ж���ֶΣ����ص�һ��
     */
    public boolean isAscendingOrder()  ;
    /**
     * ��ѯ�����order by���ֵ�column�����order by ������ж���ֶΣ����ص�һ��
     * ���ص�int[] ��ÿһ����Column.getId()
     * ��չ����OrderColumn ����������е������ֶ� �����ǵ�һ������Ϊ�����ֶ�����
     */
    public int[] getOrderColumnLink();

    /**
     * ��ѯ�����order by���ֵ�column�����order by ������ж���ֶΣ����ص�һ��
     * ���ص�int[] ��ÿһ����Column.getId()
     * ��չ����OrderColumnlink ����������е������ֶ� �����ǵ�һ������Ϊlink�ֶγ��ȴ���1
     */
    public int[] getOrderColumnLinks();
    
    /**
     * Similiar to #getDisplayColumnIndices, except that when
     * pk and ak are set not show, the indices will also be excluded.
     */
    public int[] getReportDisplayColumnIndices(boolean pk, boolean ak);

    /**
     * ���ص�ǰ��SQL��䣬�������ɱ��棬���Ϊ���еķ��������Ľ��
     */
    public String getSQLForReport(boolean pk, boolean ak) throws QueryException;
    /**
     * ���ص�ǰ��SQL��䣬�������ɱ��棬���Ϊ��ǰҳ�����ʾ������
     */
    public String getSQLForReportWithRange(boolean pk, boolean ak) throws QueryException;

    /**
     * ���maintable �����ֶ� getSumMethod !=null, ������ѯ��ʱ��Ϳ��Ե��ñ�����
     * ����ڲ�ѯ���ȫ��Χ��ͳ�ƣ���ҳͳ����QueryResult�л��)
     * ��Ȼ���ڵ��ñ�����ǰ�������ͨ��#isFullRangeSubTotalEnabled()���ж��Ƿ���Ҫ
     * @return null if not full range sql (schema decide it)
     * @throws QueryException
     */
    public String toFullRangeSubTotalSQL() throws QueryException;

    /**
     * ��QueryRequest�����ʱ�����Ҫ�����ȫ��Χ��ͳ�ƣ���QueryRequestImpl����
     * enableFullRangeSubTotal()������
     * @return
     */
    public boolean isFullRangeSubTotalEnabled();

    //--------- following added by yfzhu at 2003-08-26 to support advanced query

    /**
    * @param columnLinks �����xxxID����������value ������������, ������Ϊ����������
    *  ���в��ֽ��͡�
    * @param value ��value �Ľ������������Ƕ�Ӧ������ݣ�����һ����ѯ�����䣬�磺
    *	��ֵ����  in (data1, data2, ...) data1,data2������IDֵ,
    *   ��ѯ������ء�in ( SELECT ID FROM table2 where table2.no like '%df%') (SELECT ID FROM����)
    * @param condition OR/AND/NOT OR/NOT AND
    */
    //public void addParam(int[] columnLinks, String value,  int condition) throws QueryException;
    /**
    *  ������VB�ķ�ʽ, �����������Ѿ����ý���������������룬�򡢷ǵĹ�ϵ���������ÿ��Լ�һЩ
    */

    public String addParam(Expression expr) throws QueryException;
    /**
     * @return If param is set using addParam(expr), then this fucntion returns input expr,
     *   else return null
     */
    public Expression getParamExpression();
    //--------- above  added by yfzhu at 2003-08-26 to support advanced query

}
