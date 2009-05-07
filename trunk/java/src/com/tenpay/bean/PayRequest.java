package com.tenpay.bean;

/**
 * ֧������bean,֧��������������������ڱ�����,ͨ��get/set�������л�ȡ���߸�ֵ.
 * @version v1.0
 */
public class PayRequest {

	private String cmdno;
	
	private String date;
	
	private String bank_type;
	
	private String desc;
	
	private String purchaser_id;
	
	private String bargainor_id;
	
	private String transaction_id;
	
	private String sp_billno;
	
	private long total_fee;
	
	private String fee_type;
	
	private String return_url;
	
	private String attach;
	
	private String spbill_create_ip;

	/**
	 * ��ȡҵ�����
	 * @return String
	 */
	public String getCmdno() {
		return cmdno;
	}

	/**
	 * ����ҵ�����
	 * @param cmdno ҵ�����
	 */
	public void setCmdno(String cmdno) {
		this.cmdno = cmdno;
	}

	/**
	 * ��ȡ�̻�����
	 * @return String
	 */
	public String getDate() {
		return date;
	}

	/**
	 * �����̻�����
	 * @param date �̻�����
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * ��ȡ��������
	 * @return String
	 */
	public String getBank_type() {
		return bank_type;
	}

	/**
	 * ������������
	 * @param bank_type ��������
	 */
	public void setBank_type(String bank_type) {
		this.bank_type = bank_type;
	}

	/**
	 * ��ȡ��Ʒ����
	 * @return String
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * ������Ʒ����
	 * @param desc ��Ʒ����
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * ��ȡ�򷽲Ƹ�ͨ�ʺ�,����Ϊ��
	 * @return String
	 */
	public String getPurchaser_id() {
		return purchaser_id;
	}

	/**
	 * �����򷽲Ƹ�ͨ�ʺ�
	 * @param purchaser_id �򷽲Ƹ�ͨ�ʺ�
	 */
	public void setPurchaser_id(String purchaser_id) {
		this.purchaser_id = purchaser_id;
	}

	/**
	 * ��ȡ�̼ҵ��̻���,��12��ͷ��10λ���ִ�
	 * @return String
	 */
	public String getBargainor_id() {
		return bargainor_id;
	}

	/**
	 * �����̼ҵ��̻���, ��12��ͷ��10λ���ִ�
	 * @param bargainor_id �̼ҵ��̻���, ��12��ͷ��10λ���ִ�
	 */
	public void setBargainor_id(String bargainor_id) {
		this.bargainor_id = bargainor_id;
	}

	/**
	 * ��ȡ�Ƹ�ͨ���׵���
	 * @return String
	 */
	public String getTransaction_id() {
		return transaction_id;
	}

	/**
	 * ���òƸ�ͨ���׵���
	 * @param transaction_id �Ƹ�ͨ���׵���
	 */
	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	/**
	 * ��ȡ�̼Ҷ�����
	 * @return String
	 */
	public String getSp_billno() {
		return sp_billno;
	}

	/**
	 * �����̼Ҷ�����
	 * @param sp_billno �̼Ҷ�����
	 */
	public void setSp_billno(String sp_billno) {
		this.sp_billno = sp_billno;
	}

	/**
	 * ��ȡ�ܽ��,�Է�Ϊ��λ.
	 * @return long
	 */
	public long getTotal_fee() {
		return total_fee;
	}

	/**
	 * �����ܽ��,�Է�Ϊ��λ.�����˷�,���������
	 * @param total_fee �ܽ��,�Է�Ϊ��λ.�����˷�,���������
	 */
	public void setTotal_fee(long total_fee) {
		this.total_fee = total_fee;
	}

	/**
	 * ��ȡ�ֽ�֧������
	 * @return String
	 */
	public String getFee_type() {
		return fee_type;
	}

	/**
	 * �����ֽ�֧������
	 * @param fee_type �ֽ�֧������
	 */
	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}

	/**
	 * ��ȡ���ղƸ�ͨ���ؽ����URL
	 * @return String
	 */
	public String getReturn_url() {
		return return_url;
	}

	/**
	 * ���ý��ղƸ�ͨ���ؽ����URL
	 * @param return_url ���ղƸ�ͨ���ؽ����URL,�Ծ��Ե�ַ��ʽ����,��:http://www.xxxx.com
	 */
	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}

	/**
	 * ��ȡ�̼����ݰ�,ԭ������
	 * @return String
	 */
	public String getAttach() {
		return attach;
	}

	/**
	 * �����̼����ݰ�,ԭ������
	 * @param attach �̼����ݰ�,ԭ������
	 */
	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}

	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}
	
	
	
	
	
}
