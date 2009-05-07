/*
 * Agile Control Technologies Ltd,. CO.
 * http://www.agileControl.com
 */
package nds.schema;

import java.util.*;
import java.util.Collections;
import nds.web.bean.*;
/**
 * 
 * Table which supports Calendar manipuplation
 * This kind of table must have following columns:
 * startdate, starttime, duration, content
 * 
 * ��ʼ���ڣ���ʼʱ�䣬����ʱ�䣬���ݣ����ڳ�����Ϊ�賵�ˣ��¼�������Ŀ��ص㣻���ڻ����ң�
 * ΪԤ���ˣ��¼�����������Ա���ճ̣�Ϊ�¼�������������٣�Ϊ�¼�������
 */

public interface CalendarTable extends Table {
	/**
	 * Which column will be used as filter column on main table, currently only one supported   
	 * @return null if no filter column
	 */
	public Column getFilterColumn();
	/**
	 * if isEventEditable=true, ����ֶ���getAddActionDestTable֮�ϣ����ʱ��
	 * @return
	 */
	public Column getAddActionTimeDestColumn();
	/**
	 * if isEventEditable=true, ���ű���Ϊ������¼���ڵı������ĸ��ֶδ��ʱ��ͨ��getAddActionTimeDestColumnָ��
	 * @return
	 */
	public Table getAddActionDestTable();
	/**
	 * �Ƿ�����������¼�������ļ�¼�������ű���getAddActionDestTableָ��
	 * ���磺������Ԥ�����鿴��¼�ڻ�����Ԥ����¼(oa_roomftp)���ң��������������ڻ���������(oa_roomreq)���н���
	 * @return
	 */
	public boolean isEventEditable();
	/**
	* When adTable has className set to this class name, the DBClassLoader 
	* will instantiate this class instance. But table should have following
	* columns to make sure that the calendar system can run well on it:
	* 
	* id, begindate(date), begintime(number(4)), endtime(number(4)), shortdesc (varchar2(20)),description varchar2((400))
	*
	*/
	public boolean isSupportCalendar();
	

}
