package com.ht1008.certificate.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

/**
 * 
 * @ClassName:  CertUtils   
 * @Description:TODO(证书)   
 * @author: 朱建华-ASUS
 * @date:   2019年4月14日 下午10:27:18   
 *     
 * @Copyright: 2019 www.ht1008.com Inc. All rights reserved. 
 * 注意：本内容仅限于华悦信息技术股份有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
public class CertUtils {

	/**
	 * 证书写入文件
	 * 
	 * <pre>
	 * -----BEGIN CERTIFICATE-----
	 * xxx
	 * -----END CERTIFICATE-----
	 * </pre>
	 * 
	 * @param cert
	 * @param path
	 * @throws IOException
	 */
	public static void write(X509Certificate cert, String path) throws IOException {
		FileWriter fw = null;
		JcaPEMWriter pw = null;
		try {
			fw = new FileWriter(new File(path));
			pw = new JcaPEMWriter(fw);
			pw.writeObject(cert);
			pw.flush();
			fw.flush();
		} finally {
			try {
				if (pw != null) {
					pw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * 读证书文件
	 * 
	 * @param cerFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws CertificateException
	 */
	public static X509Certificate read(String cerFile) throws FileNotFoundException, CertificateException {
		InputStream crt = null;
		try {
			crt = new FileInputStream(new File(cerFile));
			CertificateFactory cf = CertificateFactory.getInstance("x.509");
			X509Certificate cert = (X509Certificate) cf.generateCertificate(crt);
			return cert;
		} finally {
			try {
				if (crt != null) {
					crt.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * 打印X509证书信息
	 * 
	 * @param cert
	 */
	public static String print(X509Certificate cert) {
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sb.append("版本号:" + cert.getVersion());
		sb.append("\n序列号：" + cert.getSerialNumber().toString(16));
		sb.append("\n使用者：" + cert.getSubjectDN());
		sb.append("\n颁发者：" + cert.getIssuerDN());
		sb.append("\n有效期：" + sdf.format(cert.getNotBefore()) + " 至 " + sdf.format(cert.getNotAfter()));
		sb.append("\n签名算法：" + cert.getSigAlgName());
		return sb.toString();
	}
}
