/*
 * Agile Control Technologies Ltd,. CO.
 * http://www.agileControl.com
 */
package nds.schema;

import java.util.*;
import java.util.Collections;
import nds.web.bean.*;
import nds.util.PairTable;
/**
 * m_product ���ʵ���࣬��Ҫ��Ϊ��֧��m_alias ������
 * m_alias �洢������� ���ϼ����ԵĶ�Ӧ��ϵ
 * ���ڲ�ʹ��m_alias��Ӧ��ϵͳ�����Բ������������m_product
 * @since 3.0 
 */
public class ProductTableImpl extends TableImpl implements AliasSupportTable{
	private final static String ALIAS_TABLE="M_PRODUCT_ALIAS";
	private final static String ASSOCIATED_COLUMN="M_PRODUCT_ID";
	private static PairTable assocColumns;
	public ProductTableImpl(){
		assocColumns=new PairTable();
		assocColumns.put("M_ATTRIBUTESETINSTANCE_ID", "M_ATTRIBUTESETINSTANCE_ID");
	}
	/**
	 * Get the alias table name, e.g, for m_product table, it should return m_alias
	 * @return table name
	 */
	public String getAliasTable(){
		return ALIAS_TABLE;
	}
	/**
	 * Return assoicated column name in alias table for the pk of this table
	 * @return
	 */
	public String getAssociatedColumnInAliasTable(){
		return ASSOCIATED_COLUMN;
	}
	
	public PairTable getOtherAssociatedColumnsInAliasTable(){
		return assocColumns;
	}


}
