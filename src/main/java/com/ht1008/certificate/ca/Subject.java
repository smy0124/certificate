package com.ht1008.certificate.ca;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.ht1008.certificate.utils.CertUtils;
import com.ht1008.certificate.utils.KeyUtils;
import com.ht1008.certificate.utils.KsUtils;


/**
 * 
 * @ClassName:  Subject   
 * @Description:TODO(证书使用者)   
 * @author: 朱建华-ASUS
 * @date:   2019年4月14日 下午10:22:24   
 *     
 * @Copyright: 2019 www.ht1008.com Inc. All rights reserved. 
 * 注意：本内容仅限于华悦信息技术股份有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
public class Subject {
	protected String subjectDN = "CN=YOU,OU=YOU,O=liuzy,L=shanghai,ST=shanghai,C=cn";
	protected int keyLength = 2048;
	protected PublicKey publicKey;
	protected PrivateKey privateKey;
	protected X509Certificate cert;
	protected String signatureAlgorithm = "SHA1withRSA";

	public Subject() {

	}

	public Subject(String subjectDN) throws NoSuchAlgorithmException {
		this.subjectDN = subjectDN;
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(keyLength);
		KeyPair kp = kpg.generateKeyPair();
		publicKey = kp.getPublic();
		privateKey = kp.getPrivate();
	}

	public Subject(String subjectDN, int keyLength, String signatureAlgorithm) throws NoSuchAlgorithmException {
		this.subjectDN = subjectDN;
		this.keyLength = keyLength;
		this.signatureAlgorithm = signatureAlgorithm;
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(keyLength);
		KeyPair kp = kpg.generateKeyPair();
		publicKey = kp.getPublic();
		privateKey = kp.getPrivate();
	}

	public Subject(String CN, String OU, String O, String L, String ST, String C) {
		subjectDN = String.format("CN=%s,OU=%s,O=%s,L=%s,ST=%s,C=%s", CN, OU, O, L, ST, C);
	}

	/** 保存证书文件 */
	public void saveCert(String path) throws IOException {
		CertUtils.write(cert, path);
	}

	/** 保存私钥到文件 */
	public void saveRsaKey(String path) throws IOException {
		KeyUtils.write2RsaKey(privateKey, path);
	}

	/** 保存PKCS8格式私钥到文件 */
	public void savePkcs8Key(String path) throws IOException {
		KeyUtils.write2PKCS8Key(privateKey, path);
	}

	/** 保存证书到JavaKeyStore文件 */
	public void saveCert2Jks(String alias, String ksPwd, String path) {
		KsUtils.writeJks(cert, alias, ksPwd, path);
	}

	/** 保存证书到BcKeyStore文件 */
	public void saveCert2Bks(String alias, String ksPwd, String path) throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException {
		KsUtils.writeBks(cert, alias, ksPwd, path);
	}

	/** 保存证书和私钥到JavaKeyStore文件 */
	public void saveKey2Jks(String alias, String keyPwd, String ksPwd, String path) {
		KsUtils.writeJks(cert, alias, privateKey, keyPwd, ksPwd, path);
	}

	/** 保存证书和私钥到BcKeyStore文件 */
	public void saveKey2Bks(String alias, String keyPwd, String ksPwd, String path) throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException {
		KsUtils.writeBks(cert, alias, privateKey, keyPwd, ksPwd, path);
	}

	/** 保存证书和私钥到P12文件 */
	public void saveKey2P12(String alias, String keyPwd, String ksPwd, String path) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KsUtils.writeP12(cert, alias, privateKey, keyPwd, ksPwd, path);
	}

	public int getKeyLength() {
		return keyLength;
	}

	public void setKeyLength(int keyLength) {
		this.keyLength = keyLength;
	}

	public X509Certificate getCert() {
		return cert;
	}

	public void setCert(X509Certificate cert) {
		this.cert = cert;
	}

	public String getSignatureAlgorithm() {
		return signatureAlgorithm;
	}

	public void setSignatureAlgorithm(String signatureAlgorithm) {
		this.signatureAlgorithm = signatureAlgorithm;
	}

	public String getSubjectDN() {
		return subjectDN;
	}

	public String getIssuerDN() {
		return cert.getIssuerDN().toString();
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}
}
