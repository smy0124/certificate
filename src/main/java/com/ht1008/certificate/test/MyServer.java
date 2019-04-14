package com.ht1008.certificate.test;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.ht1008.certificate.ca.CACenter;
import com.ht1008.certificate.ca.Subject;


/**
 * 
 * @ClassName:  MyServer   
 * @Description:TODO(生成CA，签发nginx、tomcat、client)   
 * @author: 朱建华-ASUS
 * @date:   2019年4月14日 下午10:25:38   
 *     
 * @Copyright: 2019 www.ht1008.com Inc. All rights reserved. 
 * 注意：本内容仅限于华悦信息技术股份有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
public class MyServer {
	static String testDir = "E:/KS/";

	public static void main(String[] args) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		signAll();
	}

	public static void signAll() throws Exception {
		CACenter CA = new CACenter("CN=CA,OU=CA,O=liuzy,L=shanghai,ST=shanghai,C=cn");
		CA.saveCert2Jks("ca", "123456", testDir + "tomcat_trust.jks");
		CA.saveCert(testDir + "ca.crt");
		CA.saveRsaKey(testDir + "ca.pem");

		Subject nginx = new Subject("CN=*.liuzy.com,OU=nginx,O=liuzy,L=shanghai,ST=shanghai,C=cn");
		nginx.setCert(CA.sign(nginx.getPublicKey(), nginx.getSubjectDN()));
		nginx.saveCert(testDir + "nginx.crt");
		nginx.saveRsaKey(testDir + "nginx.pem");

		Subject tomcat = new Subject("CN=*.liuzy.com,OU=tomcat,O=liuzy,L=shanghai,ST=shanghai,C=cn");
		tomcat.setCert(CA.sign(tomcat.getPublicKey(), tomcat.getSubjectDN()));
		tomcat.saveCert(testDir + "tomcat.crt");
		tomcat.saveKey2Jks("tomcat", "123456", "123456", testDir + "tomcat.jks");

		Subject client = new Subject("CN=client1,OU=client,O=liuzy,L=shanghai,ST=shanghai,C=cn");
		client.setCert(CA.sign(client.getPublicKey(), client.getSubjectDN()));
		client.saveCert(testDir + "client.crt");
		client.saveRsaKey(testDir + "client.pem");

	}

	public static void signClient() throws Exception {
		CACenter CA = new CACenter(testDir + "ca.crt", testDir + "ca.pem");

		Subject client = new Subject("CN=client2,OU=client,O=liuzy,L=shanghai,ST=shanghai,C=cn");
		client.setCert(CA.sign(client.getPublicKey(), client.getSubjectDN()));
		client.setCert(CA.sign(client.getPublicKey(), client.getSubjectDN()));
		client.saveCert(testDir + "client2.crt");
		client.saveRsaKey(testDir + "client2.pem");
	}
}
