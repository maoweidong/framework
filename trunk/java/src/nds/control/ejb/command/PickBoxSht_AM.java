package nds.control.ejb.command;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

import jonelo.jacksum.algorithm.Crc16;
import nds.control.ejb.Trigger;
import nds.log.Logger;
import nds.log.LoggerManager;
import nds.query.QueryEngine;
import nds.query.QueryException;
import nds.util.ColumnInterpretException;
import nds.util.ColumnInterpreter;

/**
 * Ϊ���ڼ��װ�䵥�ϼ���У���룬�����˴�ColumnInterpreter����Ȼ������ʵ������Ҫ��õ��ݵ�id
�Ա���м��㡣���������oid �ֶ�ʵ�ʶ�Ӧ��id �ֶΣ���pickboxsht_aa/am�����б����ý�����ֵ��
Ȼ��ͨ��ColumnInterpreter ����ֵת��Ϊcrc�룬����ʾ�ڽ����ϡ�

���ҽ��˷������õ�<after-modify/> trigger �С�trigger ��DefaultWebEvent ��triggerAfterModify
�����б����ã������ڣ����ų⣩oracle �洢���̡�����ֻҪ���ݣ�������ϸ�����Ķ���crc�ͻᱻ����

��У����ֻ�ڵ������Ϊ�������ת�Ƶ������ҶԷ���װ��pos�����������
 */
public class PickBoxSht_AM implements Trigger, ColumnInterpreter{
    private Logger  logger=LoggerManager.getInstance().getLogger(this.getClass().getName());

//    private final static String HEAD="select d.id, d.no, d.customerid, w.customerid from DisShipTransSht d, WAREHOUSE w where d.id=? and w.id(+)=d.warehouseid";
    private final static String HEAD="select d.id, d.parentno, d.customerid,d.parentSort from pickboxsht d  where d.id=?  ";
    private final static String ITEM="select productid, sum(qty) from pickboxshtitem where pickboxshtid=? group by productid order by productid asc";
    private final static String POSINSTALL="select customerid from posinfo where customerid=?";

    public PickBoxSht_AM(){

    }
    /**parse specified value to string that can be easily interpreted by users
     * @throws ColumnInterpretException if input value is not valid
     */
    public String parseValue(Object value, Locale locale) throws ColumnInterpretException{
        Connection conn=null;
        try {
            conn= QueryEngine.getInstance().getConnection();
            return this.execute( (new Integer(""+value)).intValue() , conn);
        }
        catch (NumberFormatException ex) {
            logger.error("Could not parse "+ value +" into integer", ex);
            return "";
        }catch (QueryException ex) {
            logger.error("Could not get connection from db", ex);
            return null;
        }finally{
            if( conn !=null) try{ conn.close();}catch(Exception e){}
        }
    }
    /**
    * parse input string to column accepted int value
    * @throws ColumnInterpretException if input string is not valid
    */
    public Object getValue(String str,Locale locale) throws ColumnInterpretException{
        return str;
    }

    public String execute(int objectId, Connection conn){
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        Crc16 crcHead, crcItem;
        crcHead=new Crc16();
        crcItem=new Crc16();
        // still execute pickboxsht_am stored procedure first
        try {
            ArrayList al=new ArrayList();
            al.add(new Integer(objectId));
            QueryEngine.getInstance().executeStoredProcedure("PickBoxSht_AM",al, false,conn);
        }
        catch (QueryException ex) {
            logger.error("Error doing PickBoxSht_AM", ex);
        }

        try {
            pstmt= conn.prepareStatement(HEAD);
            pstmt.setInt(1, objectId);
            rs= pstmt.executeQuery();
            if(rs.next() ){
                String no=checkNull(rs.getString(2));
                int shopId= rs.getInt(3);
                int whId= 111484 ;// currently we use ZZZ001 as fixed value, since pickboxsht does not contains warehouseId
                int sort= rs.getInt(4);
                /*������ⵥ</desc><value>1</value>
                <desc>�˻���ⵥ</desc><value>2</value>
                <desc>�������ת�Ƶ�</desc><value>3</value>
                */
                if ( sort !=3){
                    logger.debug("Not a disshiptranssht for pickboxsht id="+objectId );
                    return "";
                }
                if (posInstalled(shopId,conn)==false) {
                    logger.debug("peer shop id="+shopId+" has not installed pos (pickboxsht.id="+ objectId+")" );
                    return "";
                }
                // 2 means return to center
                updateCRC(crcHead, no+",2,"+ whId+","+ shopId);
            }
            rs.close();
            pstmt= conn.prepareStatement(ITEM);
            pstmt.setInt(1, objectId);
            rs= pstmt.executeQuery();
            while (rs.next()){
                int productId= rs.getInt(1);
                int qty= rs.getInt(2);
                updateCRC(crcItem, productId+","+ qty);

            }
            rs.close();
            String crc= formatCRC(crcHead)+formatCRC(crcItem);
            logger.debug("crc for pickboxsht id="+ objectId+" is '"+ crc+"'.");
            conn.createStatement().executeUpdate("update pickboxsht set crc='"+crc+"', crcmodifieddate=sysdate where id="+ objectId );


            return crc;
        }
        catch (SQLException ex) {
            logger.error("Could not generate crc for pickboxsht id="+ objectId, ex);
            try{
                conn.createStatement().executeUpdate("update pickboxsht set crc=null, crcmodifieddate=null where id="+ objectId );
            }catch(Exception ee){}
            return "";
        }finally{
            try{if( rs!=null) rs.close();}catch(Exception e){}
        }

    }
    private String formatCRC(Crc16 crc){
        String c=""+ crc.getValue();
        String s="00000";
        if ( c.length() < 5) c= s.substring(0,5-c.length() )+c;
        return c;
    }
    private void updateCRC(Crc16 crc, String raw){
//        logger.debug("crc="+ raw);
        byte[] b= raw.getBytes();
        crc.update(b);
    }
    private boolean posInstalled(int shopId, Connection conn){
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try {
            pstmt=  conn.prepareStatement(POSINSTALL);
            pstmt.setInt(1,shopId );
            rs= pstmt.executeQuery();
            return ( rs.next());
        }
        catch (SQLException ex) {
            return false;
        }finally{
            try{if( rs!=null) rs.close();}catch(Exception e){}
            try{if ( pstmt!=null ) pstmt.close();}catch(Exception e){}
        }

    }
    private String checkNull(String s){
        return s==null?"": s;
    }
    public static void main(String[] args) throws Exception{
        String[] s=new String[]{"-a", "crc16", "f:/tt.log"};
//        jonelo.jacksum.Jacksum.main(s);
        jonelo.jacksum.algorithm.Crc16  crc= new jonelo.jacksum.algorithm.Crc16();
        crc.setHex(false);
        crc.readFile("f:/bsh.dmp");
        crc.setFilename("f:/bsh.dmp");
        System.out.println(crc.toString());
    }

}