
package nds.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nds.log.Logger;
import nds.log.LoggerManager;
import nds.schema.Column;
import nds.schema.TableManager;
import nds.util.NDSException;
import nds.util.Tools;
/**
 * ��ҪĿ���Ǹ��ݱ�׼��Query.jspҳ���������ݣ��μ�QueryInputHandler)�����where�Ӿ��
 * ���ݡ�Ŀǰ��ʵ�ֻ��ڷ��ص�HttpServletRequest �����"result"���ԣ�Attribute)��ֵΪString
 * ,���磺
 * {columnName:=value}{columnName2:=value} ������value���ܻ����{}�������ַ������
 * columnName�������������Ӧ�ֶΣ����������ĳ�ֶ������Ĵӱ��ĳ�ֶε�ֵ��Ϊ����������ͨ��������referenceTable!=null
 * ��column��������Ȼ������������Ǹ��ֶ�
 *
 * #yfzhu created at 2002-12-23 for PromotionASht.ProductSet column
 */
public class QuerySQLHandler extends HttpServlet {
    private final static String QUERY_SERVLET="/servlets/query";
    private final static String QUERY_ERRORPAGE=nds.util.WebKeys.NDS_URI+"/error.jsp";
    private final static int MAX_SELECT_COUNT=50;
    private final static int MAX_PARAM_COUNT=30;
    private final static int EXCLUDE_VALUE=0;// column.getValues() must be validate, while 0 is default not valid

    private static Logger logger=LoggerManager.getInstance().getLogger(QuerySQLHandler.class.getName());
    /**
     * @roseuid 3B84B0A20211
     */
    public void init() throws ServletException {
        //nds.control.web.MainServlet.initEnvironment(this.getServletContext());

    }
    /**
     * һ������£�ҳ�洴��request����Ϣ���Ƿ���һ��Form�У�������QueryRequest�Զ�
     * ������Ӧ��Form������ҳ����Ժ����׵�����ͬ��query��������¼��һ��ҳ�������ʾ���޼�¼��
     * <form name="formName" method="put" action="/nds/servlets/query">
     *      <input type='hidden' name='return_type' value='n'|'m'|'s'> // n��ʾ�����أ�m��ʾ���ض���У�n����һ��
     *      <input type='hidden' name='accepter_id' value='input control name'>//����ҳ��Ŀؼ�(input type=text)��id
     *
     *      <input type='hidden' name='table' value='13'>            // ��Ҫ�����ı�
     *      <input type='hidden' name='start' value='1115'> // ��ѯ��ʼindex
     *      <input type='hidden' name='range' value='50'>   // ��ѯ�ķ�Χ
     *      <input type='hidden' name='select_count' value='3'>// ����select�ĸ���
     *      <input type='hidden' name='show_maintableid' value='true'> // �Ƿ���ʾ��ѯ��¼��id�����Ϊtrue,����ʾ�ڵ�һ��
     *      <input type='hidden' name='column_selection' value='1,0,3'>// 1,0,3��select�ı�ţ���˳�򣬿�����select/[value]/columns ��þ���selection ��column
     *      <input type='hidden' name='select/0/columns' value='COLUMN1,COLUMN2'>// select/no�����бش�maintable��ĳ���ֶο�ʼ������������referenceTable�ϵ�ĳ��,no��ʾ��ʾ˳����Ϊselect�ܶ�
     *      <input type='hidden' name='select/0/show' value='false'> // ��Ҫѡ�������Ƿ���ҳ������ʾ���ӣ�������colums.size>1��selection��Ч��ȱʡΪtrue
     *      <input type='hidden' name='select/1/columns' value='COLUMN3,COLUMN4,...'>
     *      <input type='hidden' name='select/1/show' value='true'>
     *      <input type='hidden' name='select/1/url' value='/basicinfo/employee.jsp'>// ��Ӧ��url
     *      ...
     *      <input type='hidden' name='param_count' value='3'>// ����parameter�ĸ���
     *      <input type='hidden' name='param/0/columns' value='COLUMN1,COLUMN2'> // ����0��Ӧ��column
     *      <input type='hidden' name='param/0/value' value='encode(>=100)'> // value ��encode��ֹ�б�������
     *      ...
     *      <input type='hidden' name='order_select' value='1'>//order��Ӧ����, value��Ӧselection�ĵڼ���
     *
     *      <input type='hidden' name='order/asc' value='true'> //�Ƿ�����ȱʡ����
     *      <input type='hidden' name='resulthandler' value='../query.jsp'> // result ����ʾҳ�棬ʹ�þ���·����ʾ��contextPath��ʼ����contextPath="/nds"����resulthandlerΪ"/query/result.jsp"������ҳ��Ϊ"/nds/query/result.jsp"
     *
     *
     * </form>
     *  Condition 2:
     *      ? table=xxx&column=xxx&id=xxx&show_all=true&aciontype="modify|add|query, query is default" // this is for objectview.jsp( in TableQueryModel)
     *      note actiontype is for deciding what action command from GUI, @see table.getShowableColumns(int) for details
     */
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        try {
            //logger.debug(toString(req));
        	Locale locale = (Locale)req.getSession(true).getAttribute(org.apache.struts.Globals.LOCALE_KEY);
        	if(locale==null)locale= TableManager.getInstance().getDefaultLocale();
        	TableManager manager=TableManager.getInstance();
            int mainTableId=Tools.getInt(getRequestParameter(req,"table"), -1);
            if( mainTableId == -1) {
                throw new QueryException("Intenal Error: table must be set");
            }
            int startIdx=Tools.getInt(getRequestParameter(req,"start"), 1) -1;
            if( startIdx < 0)
                startIdx=0;
            int range= Tools.getInt(getRequestParameter(req,"range"), QueryUtils.DEFAULT_RANGE);
            SQLBuilder builder=new SQLBuilder();


                String cs;
                String param;
                int[] ids;


                int paramCount= Tools.getInt(getRequestParameter(req,"param_count"), MAX_PARAM_COUNT);
                for( int i=0;i<paramCount;i++) {
                    param="param/"+i+"/columns";
                    cs=getRequestParameter(req,param);
                    if( cs ==null)
                        continue;
                    ids=parseIntArray(cs);
                    if( ids ==null)
                        throw new QueryException("Intenal Error: can not parse '"+ param +"' to int[]");
                    param="param/"+i+"/value";
                    cs=getRequestParameter(req,param);

                    if( cs !=null && !cs.equals("")) {
                        // mind that GUI may send colum of values
                        Column lastColumn= manager.getColumn(ids[ids.length-1]);
                        if(lastColumn !=null && lastColumn.getValues(locale) !=null) {
                           //nmdemo add check for cs value which may not be int, but as " in (10,2)"
                           /* following is orginal one
                            if( (new Integer(cs.trim()).intValue()) ==EXCLUDE_VALUE) {
                                continue;
                            }
                            */
                            try{
                            if( (new Integer(cs.trim()).intValue()) ==EXCLUDE_VALUE) {
                                continue;
                            }}catch(NumberFormatException enfe){}
                        }
                        builder.addParam(manager.getColumn(ids[0]).getName(),cs);
                    }
                }
            req.setAttribute("result",builder.toSQLString() );
            String resultHandler= getRequestParameter(req,"resulthandler");
            this.getServletContext().getRequestDispatcher(resultHandler).forward(req,res);
            return;
        } catch (Exception ex) {
            // direct to Error page to handle
            NDSException e=new NDSException("Error when treating query input from :"+req.getRequestURL()+ ":\n"+ toString(req),ex);
            req.setAttribute("error",ex);
            //Hawke Begin
            if(req.getParameter("formRequest")!=null)
            {
              //request.removeAttribute("error");
              getServletContext().getRequestDispatcher(req.getParameter("formRequest").toString()).forward(req,res);
            }
            //Hawke end
            // there has no flow for this page, direct it to unknown page
            String errorURL= this.QUERY_ERRORPAGE;
            getServletContext().getRequestDispatcher(errorURL).forward(req,res);
        }
    }
    /**
     * Parse <code>s</code> to a boolean vaule, if errors found, return <code>def</code>
     */
    public boolean parseBoolean(String s, boolean def) {
        if( "true".equalsIgnoreCase(s))
            return true;
        else if( "false".equalsIgnoreCase(s))
            return false;
        return def;
    }
    /**
     * Parse <code>s</code> to an int[], s should has following format:
     * "xxx,xxx,..."
     */
    public int[] parseIntArray(String s) {
        try {
            ArrayList is= new ArrayList();
            StringTokenizer st=new StringTokenizer(s,",");
            while(st.hasMoreTokens()) {
                Integer v=new Integer(st.nextToken());
                is.add(v);
            }
            int[] ret=new int[is.size()];
            for(int i=0;i<ret.length;i++) {
                ret[i]=( (Integer)is.get(i)).intValue();
            }
            return ret;
        } catch(Exception e) {
            //logger.debug("can not parse '"+s+"'as int[]");
            return null;
        }
    }
    /**
     * Every element in <code>s</code> should be an int
     */
    public int[] parseIntArray(String[] s) {
        if( s ==null )
            return null;
        try {
            int[] is=new int[s.length];
            for( int i=0;i< s.length;i++) {
                is[i]= (new Integer(s[i])).intValue();
            }
            return is;
        } catch(Exception e) {
            return null;
        }
    }
    private String toString(HttpServletRequest req) {
        StringBuffer buf=new StringBuffer();
        Enumeration enu=req.getAttributeNames();
        buf.append("------Attributes--------\r\n");
        while( enu.hasMoreElements()) {
            String att= (String)enu.nextElement();
            buf.append(att+" = "+ req.getAttribute(att)+"\r\n");
        }
        buf.append("------Parameters--------\r\n");
        enu=req.getParameterNames();
        while( enu.hasMoreElements()) {
            String param= (String)enu.nextElement();
            buf.append(param+" = "+ getRequestParameter(req,param)+"\r\n");
        }
        buf.append("------Headers--------\r\n");
        enu=req.getHeaderNames();
        while( enu.hasMoreElements()) {
            String param= (String)enu.nextElement();
            buf.append(param+" = "+ getRequestParameter(req,param)+"\r\n");
        }
        buf.append("\n\rContext path:"+req.getContextPath());
        buf.append("\n\rLocale:"+req.getLocale());
        buf.append("\n\rMethod:"+req.getMethod());
        buf.append("\n\rPathInfo:"+req.getPathInfo());
        buf.append("\n\rPathTranslated:"+req.getPathTranslated());
        buf.append("\n\rQueryString:"+req.getQueryString());
        buf.append("\n\rRemoteAddr:"+req.getRemoteAddr());
        buf.append("\n\rRemoteHost:"+req.getRemoteHost());
        buf.append("\n\rRequestURI:"+req.getRequestURI());
        buf.append("\n\rRequestURL:"+req.getRequestURL());
        return buf.toString();
    }
    /**
     * @param path like "/nds/show.jsp"
     * @return like "/nds/"
     */
    private String getDirectoryOfPath(String path) {

        int lash= path.lastIndexOf('/');
        if( lash !=-1) {
            return path.substring(0, lash+1);
        } else {
            return "";
        }
    }
    private String getRequestParameter(HttpServletRequest req, String param) {
        String s=req.getParameter(param);
        if( s !=null)
            return s.trim();
        return null;
    }
    /**
     * This class will accept all param input and return a string, currently we will
     * not build SQL(mysql) directly here, only a simple string recording all data
     */
    private class SQLBuilder{
        private ArrayList params=new ArrayList();
        /**
         *@param colName string representing the column name of query where, note in
         * this realization, colname will only be the one in main table
         * @param param the user input
         */
        public void addParam(String colName, String param){

            params.add("{"+colName+":=" + param +"}");
        }

        public String toSQLString() throws NDSException{
            if (params.size()==0) throw new NDSException("������������һ������!");
            StringBuffer sb=new StringBuffer();
            for (int i=0;i< params.size();i++) sb.append((String)params.get(i));
            return sb.toString() ;
        }
    }
}
