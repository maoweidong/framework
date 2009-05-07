package com.tenpay.bean;

/**
 * ֧��Ӧ��bean,֧��Ӧ����������������ڱ�����,ͨ��get/set�������л�ȡ���߸�ֵ.
 * @version v1.0
 */
public class PayResponse {

	private String cmdno;
	
	private String pay_result;
	
	private String pay_info;
	
	private String date;
	
	private String bargainor_id;
	
	private String transaction_id;
	
	private String sp_billno;
	
	private long total_fee;
	
	private String fee_type;
	
	private String attach;
	
	private String sign;

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
	 * ��ȡ֧�����
	 * @return String
	 */
	public String getPay_result() {
		return pay_result;
	}

	/**
	 * ����֧�����
	 * @param pay_result ֧�����
	 */
	public void setPay_result(String pay_result) {
		this.pay_result = pay_result;
	}

	/**
	 * ��ȡ֧�������Ϣ��֧���ɹ�ʱΪ��
	 * @return String
	 */
	public String getPay_info() {
		return pay_info;
	}

	/**
	 * ����֧�������Ϣ��֧���ɹ�ʱΪ��
	 * @param pay_info ֧�������Ϣ��֧���ɹ�ʱΪ��
	 */
	public void setPay_info(String pay_info) {
		this.pay_info = pay_info;
	}

	/**
	 * ��ȡ�̼�����
	 * @return String
	 */
	public String getDate() {
		return date;
	}

	/**
	 * �����̼�����
	 * @param date �̼�����
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * ��ȡ�̻���
	 * @return String
	 */
	public String getBargainor_id() {
		return bargainor_id;
	}

	/**
	 * �����̻���
	 * @param bargainor_id �̻���
	 */
	public void setBargainor_id(String bargainor_id) {
		this.bargainor_id = bargainor_id;
	}

	/**
	 * ��ȡ���׵���
	 * @return String
	 */
	public String getTransaction_id() {
		return transaction_id;
	}

	/**
	 * ���ý��׵���
	 * @param transaction_id ���׵���
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

	/**
	 * ��ȡǩ����
	 * @return String
	 */
	public String getSign() {
		return sign;
	}

	/**
	 * ����ǩ����
	 * @param sign ǩ����
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}
	
}
