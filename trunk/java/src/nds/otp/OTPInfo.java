/*
 * Agile Control Technologies Ltd,. CO.
 * http://www.agileControl.com
 */
package nds.otp;

import java.util.Random;

/**
 * �޸�OTP�㷨��ǰ�㷨������������Ϊ����������ݣ�<br>
 * ��Կ�����ܲ����ݿ�ͬ�����������ش洢����ʱ�䴰�ڣ������������ش洢����֤�ֻ���Ψһ�ԣ�ÿ����һ�ο���μ�1)��<br>
 * ���㷨�ļ������ݣ�<br>
 * ��Կ�����ܲ����ݿ�ͬ���������ش洢������֤�루�ɵ�¼������ʾ���൱��ʱ�䴰��Ϊ�������������ϵͳ����Ա�趨�������кţ����ش洢����֤�ֻ���Ψһ�ԣ��㶨����)
 * 
 * Ϊ�����ֻ��㷨��������ʹ�ã��û��������������õ�¼����(�ֻ��ϵļ򵥱���)
 * 
 * @author yfzhu@agilecontrol.com
 */

public class OTPInfo{
	private int tokenLength;
	private StringBuffer secret=null;
	private long counter;
	private int checkNo;
	private String tag; // can store anything, for instance, OTPClientMIDlet stores login password in it

	// this depends on length of the (SHA-512) hash result:
	static final int hashStringLength = 128;

	static final int maxTokenLength = hashStringLength - 1;
	public OTPInfo(){
		
	}
	
	/**
	 * 
	 * @param len tokenLength
	 * @param cnt counter
	 * @param sb secret
	 */
	public OTPInfo(int len,long counter,int checkNo, String sb ){
		tokenLength=len;
		this.counter=counter;
		this.checkNo= checkNo;
		secret= new StringBuffer(sb);
	}	
	public void setSecret(String s){
		clearSecret();
		if(s!=null)	secret= new StringBuffer(s);
	}
	public String getSecret(){
		return secret==null?"":secret.toString();
	}
	public int getSecretLength(){
		if(secret!=null) return secret.length();
		return 0;
	}
	public void clearSecret(){
		if(secret==null) return;
		int alength = secret.length();
		for (int c = 0; c < alength; c++) {
			secret.setCharAt(c, ' ');
		}
		secret = null;
	}
	/**
	 * 
	 * @return otp
	 * @throws IllegalArgumentException when secret is null
	 */
	public String computPassword() throws IllegalArgumentException {
		if(secret==null) throw new IllegalArgumentException("secret not found");
		
		long round =checkNo + counter;

		Random r = new Random(round);
		int n = Math.abs(r.nextInt());
		int cnt = Math.abs((n + 1) % 999);
		//StringBuffer  debugString=new StringBuffer("r:"+round+",n:"+n); 

		while (cnt-- >= 0) {
			n = r.nextInt();
		}
		int p = Math.abs(r.nextInt()) % (hashStringLength - tokenLength);
		//debugString.append(",p:"+p);
		r.setSeed(0L);
		r = null;

		// a und n verkn��pfen...
		StringBuffer s = new StringBuffer(Long.toString(n));
		//debugString.append(",n:"+n);

		n = 0;

		// weave'em together
		int slength = s.length();
		int alength = secret.length();
		StringBuffer sb = new StringBuffer(alength + slength);
		int afilling = alength / slength;
		int sfilling = slength / alength;
		if (afilling <= 0) {
			afilling = 1;
		}
		if (sfilling <= 0) {
			sfilling = 1;
		}
		int i = 0;
		int j = 0;
		boolean i_finished = false;
		boolean j_finished = false;
		// interleave'em
		while (!(i_finished && j_finished)) {
			if (i + sfilling < slength) {
				sb.append(s.toString().substring(i, i + sfilling));
				i += sfilling;
			} else {
				if (i < slength) {
					sb.append(s.toString().substring(i, s.length()));
					i = slength;
				}
				i_finished = true;
			}
			if (j + afilling < alength) {
				sb.append(secret.toString().substring(j, j + afilling));
				j += afilling;
			} else {
				if (j < alength) {
					sb.append(secret.toString().substring(j, secret.length()));
					j = alength;
				}
				j_finished = true;
			}
		}

		// overwrite s
		for (int c = 0; c < slength; c++) {
			s.setCharAt(c, ' ');
		}
		s = null;

		IMessageDigest hashObject = new Sha512();
		hashObject.update(sb.toString().getBytes(), 0, sb.length());

		// overwrite sb
		for (int c = 0; c < sb.length(); c++) {
			sb.setCharAt(c, ' ');
		}
		sb = null;

		// only keep part of it...
		String hashedstring = Util.toString(hashObject.digest()).substring(p,
				p + tokenLength);
		p = 0;

		hashObject.reset();
		hashObject = null;

		System.gc();
		return hashedstring ;//+ debugString.toString();
	}	
	
	
	/**
	 * @return Returns the counter.
	 */
	public long getCounter() {
		return counter;
	}
	
	
	/**
	 * @return Returns the tokenLength.
	 */
	public int getTokenLength() {
		return tokenLength;
	}
	
	/**
	 * @param counter The counter to set.
	 */
	public void setCounter(long counter) {
		this.counter = counter;
	}
	
	/**
	 * @param tokenLength The tokenLength to set.
	 */
	public void setTokenLength(int tokenLength) {
		this.tokenLength = tokenLength;
	}
	/**
	 * @return Returns the tag.
	 */
	public String getTag() {
		return tag==null?"":tag;
	}
	/**
	 * @param tag The tag to set.
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}
	/*public String toString(){
		return "tokenlength="+tokenLength+",counter="+ counter+",secret="+secret+",checkno="+checkNo+",tag="+tag;
	}*/	
	/**
	 * @return Returns the checkNo.
	 */
	public int getCheckNo() {
		return checkNo;
	}
	/**
	 * @param checkNo The checkNo to set.
	 */
	public void setCheckNo(int checkNo) {
		this.checkNo = checkNo;
	}
}
