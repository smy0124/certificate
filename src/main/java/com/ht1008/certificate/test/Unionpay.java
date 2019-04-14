package com.ht1008.certificate.test;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.ht1008.certificate.ca.CACenter;
import com.ht1008.certificate.utils.CertUtils;
import com.ht1008.certificate.utils.KeyUtils;


/**
 * 
 * @ClassName:  Unionpay   
 * @Description:TODO(模拟银联POS机证书签发（注意：openssl产生的证书和私钥再用JAVA签发是不能用的！！！）)   
 * @author: 朱建华-ASUS
 * @date:   2019年4月14日 下午10:25:54   
 *     
 * @Copyright: 2019 www.ht1008.com Inc. All rights reserved. 
 * 注意：本内容仅限于华悦信息技术股份有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
public class Unionpay {
	// PM CA证书
	static String PM_CA_CRT = "E:/KS/me_cert1.crt";
	// PM CA私钥
	static String PM_CA_PEM = "E:/KS/client.pem";

	public static void main(String[] args) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		// 创建一个终端
		Terminal terminal = new Terminal("123456789004500", "12345601");
		// 创建PM环境CA中心
		CACenter pmCA = new CACenter(PM_CA_CRT, PM_CA_PEM);
		// 签发
		X509Certificate cert = pmCA.sign(terminal.getPublicKey(), terminal.getSubjectDN(), "SHA1withRSA");
		terminal.setCert(cert);
		// 输出
		terminal.saveToDir("E:/KS/ks");

	}

}

// 终端对象
class Terminal {
	private String merId;
	private String terId;
	private PublicKey publicKey;
	private PrivateKey privateKey;
	private X509Certificate cert;

	public Terminal(String merId, String terId) {
		this.merId = merId;
		this.terId = terId;
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair kp = kpg.generateKeyPair();
			publicKey = kp.getPublic();
			privateKey = kp.getPrivate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveToDir(String path) throws IOException {
		String pemFile = path + merId + "_" + terId + ".pem";
		KeyUtils.write2RsaKey(privateKey, pemFile);
		String certFile = path + merId + "_" + terId + ".crt";
		CertUtils.write(cert, certFile);
	}

	public String getSubjectDN() {
		return "CN=" + merId + terId + ",OU=中国银联,O=中国银联,L=shanghai,C=CN";
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getTerId() {
		return terId;
	}

	public void setTerId(String terId) {
		this.terId = terId;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public X509Certificate getCert() {
		return cert;
	}

	public void setCert(X509Certificate cert) {
		this.cert = cert;
	}
}
