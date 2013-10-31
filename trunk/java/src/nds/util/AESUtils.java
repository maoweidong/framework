package nds.util;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import nds.log.LoggerManager;


/**
 * AES Coder<br/>
 * secret key length:	128bit, default:	128 bit<br/>
 * mode:	ECB/CBC/PCBC/CTR/CTS/CFB/CFB8 to CFB128/OFB/OBF8 to OFB128<br/>
 * padding:	Nopadding/PKCS5Padding/ISO10126Padding/
 * @author Aub
 * 
 */
public class AESUtils{
	
	/**
	 * ��Կ�㷨
	*/
	private static final String KEY_ALGORITHM = "AES";
	
	private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
	
	private static AESUtils instance;
	
	private static String pwd;
	
	
    /**
     * @return the unique singleton instance
     */
	private static synchronized AESUtils getInstance()
    {
        if (instance == null) {
            instance = new AESUtils();
        }
        return instance;
    }
    
	/**
	 * ��ʼ����Կ
	 * 
	 * @return byte[] ��Կ 
	 * @throws Exception
	 */
	public static byte[] initSecretKey() {
		//��������ָ���㷨��������Կ�� KeyGenerator ����
		KeyGenerator kg = null;
		try {
			kg = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new byte[0];
		}
		//��ʼ������Կ��������ʹ�����ȷ������Կ��С
		//AES Ҫ����Կ����Ϊ 128
		kg.init(128);
		//����һ����Կ
		//SecretKeySpec skeySpec=new SecretKeySpec(key, "AES");
		//SecretKey  secretKey = kg.generateKey();
		String password=getPwd();
		if(password==null ) throw new java.lang.IllegalArgumentException("Password should not be null");
		byte[] passwd= StringUtils.hash(password).getBytes(); // 32 bytes
		byte[] key= new byte[16]; // 128 bits
		int i=0,j;
		while( i< 16){
			for(j=0;i< 16 && j< passwd.length;i++,j++ ) key[i]= passwd[j];
		}
		//kg.generateKey()
		SecretKeySpec skeySpec=new SecretKeySpec(key, "AES");
		return skeySpec.getEncoded();
	}
	
	/**
	 * ת����Կ
	 * 
	 * @param key	��������Կ
	 * @return ��Կ
	 */
	private static Key toKey(byte[] key){
		//������Կ
		return new SecretKeySpec(key, KEY_ALGORITHM);
	}
	
	/**
	 * ����
	 * 
	 * @param data	����������
	 * @param key	��Կ
	 * @return byte[]	��������
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data,Key key) throws Exception{
		return encrypt(data, key,DEFAULT_CIPHER_ALGORITHM);
	}
	
	/**
	 * ����
	 * 
	 * @param data	����������
	 * @param key	��������Կ
	 * @return byte[]	��������
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data,byte[] key) throws Exception{
		return encrypt(data, key,DEFAULT_CIPHER_ALGORITHM);
	}
	
	
	/**
	 * ����
	 * 
	 * @param data	����������
	 * @param key	��������Կ
	 * @param cipherAlgorithm	�����㷨/����ģʽ/��䷽ʽ
	 * @return byte[]	��������
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data,byte[] key,String cipherAlgorithm) throws Exception{
		//��ԭ��Կ
		Key k = toKey(key);
		return encrypt(data, k, cipherAlgorithm);
	}
	
	/**
	 * ����
	 * 
	 * @param data	����������
	 * @param key	��Կ
	 * @param cipherAlgorithm	�����㷨/����ģʽ/��䷽ʽ
	 * @return byte[]	��������
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data,Key key,String cipherAlgorithm) throws Exception{
		//ʵ����
		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		//ʹ����Կ��ʼ��������Ϊ����ģʽ
		cipher.init(Cipher.ENCRYPT_MODE, key);
		//ִ�в���
		return cipher.doFinal(data);
	}
	
	
	
	/**
	 * ����
	 * 
	 * @param data	����������
	 * @param key	��������Կ
	 * @return byte[]	��������
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data,byte[] key) throws Exception{
		return decrypt(data, key,DEFAULT_CIPHER_ALGORITHM);
	}
	
	/**
	 * ����
	 * 
	 * @param data	����������
	 * @param key	��Կ
	 * @return byte[]	��������
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data,Key key) throws Exception{
		return decrypt(data, key,DEFAULT_CIPHER_ALGORITHM);
	}
	
	/**
	 * ����
	 * 
	 * @param data	����������
	 * @param key	��������Կ
	 * @param cipherAlgorithm	�����㷨/����ģʽ/��䷽ʽ
	 * @return byte[]	��������
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data,byte[] key,String cipherAlgorithm) throws Exception{
		//��ԭ��Կ
		Key k = toKey(key);
		return decrypt(data, k, cipherAlgorithm);
	}

	/**
	 * ����
	 * 
	 * @param data	����������
	 * @param key	��Կ
	 * @param cipherAlgorithm	�����㷨/����ģʽ/��䷽ʽ
	 * @return byte[]	��������
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data,Key key,String cipherAlgorithm) throws Exception{
		//ʵ����
		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		//ʹ����Կ��ʼ��������Ϊ����ģʽ
		cipher.init(Cipher.DECRYPT_MODE, key);
		//ִ�в���
		return cipher.doFinal(data);
	}
	
	private static String  showByteArray(byte[] data){
		if(null == data){
			return null;
		}
		StringBuilder sb = new StringBuilder("{");
		for(byte b:data){
			sb.append(b).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("}");
		return sb.toString();
	}
	
	public static void main(String[] args) throws Exception {
		setPwd("burgeon");
		byte[] key = initSecretKey();
		System.out.println("key��"+showByteArray(key));
		
		Key k = toKey(key);
		//System.out.println(k.toString());
		String data ="�й����ô ʲôʱ�򰡣��������˰���";
		String pky="44203d0987be73627e17da94fe34c72fa5de8942ae137a24a459f703433556ca45c430eb01965728ac36ce61fe7ce3bf";
		System.out.println("����ǰ����: string:"+data);
		System.out.println("����ǰ����: byte[]:"+showByteArray(data.getBytes()));
		System.out.println();
		byte[] encryptData = encrypt(data.getBytes(), k);
		System.out.println("���ܺ�����: byte[]:"+showByteArray(encryptData));
		System.out.println("���ܺ�����: hexStr:"+Hex.encodeHexStr(encryptData));
		String str1=Hex.encodeHexStr(encryptData);
		byte[] decryptData = decrypt(Hex.decodeHex(pky.toCharArray()), k);
		System.out.println("���ܺ�����: byte[]:"+showByteArray(decryptData));
		System.out.println("���ܺ�����: string:"+new String(decryptData));
		
	}

	public static String getPwd() {
		return pwd;
	}

	public static void setPwd(String pwd) {
		AESUtils.pwd = pwd;
	}
}