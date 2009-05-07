package nds.control.ejb.command;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import nds.control.event.DefaultWebEvent;
import nds.schema.Table;
import nds.util.NDSException;

/**
 * @author yfzhu
 * @version 1.0
 */

public interface ColumnValue extends Serializable{

    /**get the values of the column, every column is in the vector
     * @parameter: list  ����Ҫ���������е�����
     * @length: the record to be operated
     * return: the key: column name, value: Vector
     */

    public HashMap getColumnHashMap(DefaultWebEvent event,Table table,ArrayList list,int length,Connection conn) throws NDSException,RemoteException;
//    public Vector getObjectId(String tableName);
    // �ú�����Ҫ�ǵõ���ǰ�Ĳ�����Ϊ����:���������޸ĵȵ�
    public void setActionType(String actionType);
    public String getActionType();

}