/*
 * Agile Control Technologies Ltd,. CO.
 * http://www.agileControl.com
 */
package nds.schema;

import java.util.*;
import java.util.Collections;
import nds.util.PairTable;
/**
 * 
 * ���ϱ�m_productӵ��һ��֧�����ϱ����ı� m_product_alias, �����������������Ϊ�����ʱ��
 * ϵͳ������m_product_alias���ҵ���Ӧ��¼������ȡ���е�m_product_id��Ϊ���ݶ�Ӧ��m_product���fk
 * �ֶΣ�ͨ��Ҳ��m_product_id����)��ֵ�������á�
 * 
 * ͬʱ��ͨ��m_product��ָ���Ĺ����ֶ�(m_attributesetinstance_id��,ϵͳ����m_product_alias��¼�ϵ�
 * m_attributesetinstance_id��ֵ���ݸ����ݵĶ�Ӧ�ֶΣ����������ͨ�� AliasSupportTable#getAssociatedColumnsInAliasTable
 * ����ɵ�.
 * @since 3.0
 */
public interface AliasSupportTable extends Table {
	/**
	 * Get the alias table name, e.g, for m_product table, it should return m_product_alias
	 * @return table name
	 */
	public String getAliasTable();
	
	/**
	 * Return assoicated column name in alias table for the pk of this table
	 * @return
	 */
	public String getAssociatedColumnInAliasTable(); 
	/**
	 * Return array of columns in reference table, which contains one aliassupporttable column, 
	 * and the correlation column in alias table.
	 * For instance, for m_product table, it should return 
	 * 	{
	 * 		{"m_product_alias.m_attributesetinstance_id","m_attributesetinstance_id"}
	 *	}
	 *@return PairTable null if no other columns
	 *	key: String the column name in alias table 
	 *	value: String the column name in reference table
	 */
	public PairTable getOtherAssociatedColumnsInAliasTable();


}
