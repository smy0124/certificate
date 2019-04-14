package com.ht1008.certificate.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

/**
 * 
 * @ClassName:  KsUtils   
 * @Description:TODO(KeyStore读写操作)   
 * @author: 朱建华-ASUS
 * @date:   2019年4月14日 下午10:27:48   
 *     
 * @Copyright: 2019 www.ht1008.com Inc. All rights reserved. 
 * 注意：本内容仅限于华悦信息技术股份有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
public class KsUtils {
	public static KeyStore readJks(String jksFile, String ksPwd) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		return readKeyStore("JKS", jksFile, ksPwd);
	}

	public static KeyStore readBks(String bksFile, String ksPwd) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		return readKeyStore("BKS", bksFile, ksPwd);
	}

	public static KeyStore readP12(String p12File, String ksPwd) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		return readKeyStore("PKCS12", p12File, ksPwd);
	}

	public static void writeJks(X509Certificate cert, String alias, PrivateKey privateKey, String keyPwd, String ksPwd, String path) {
		try {
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(null);
			keyStore.setKeyEntry(alias, privateKey, keyPwd.toCharArray(), new X509Certificate[] { cert });
			write(keyStore, ksPwd, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeJks(X509Certificate cert, String alias, String ksPwd, String path) {
		try {
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(null);
			keyStore.setCertificateEntry(alias, cert);
			write(keyStore, ksPwd, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeBks(X509Certificate cert, String alias, PrivateKey privateKey, String keyPwd, String ksPwd, String path)
			throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore keyStore = KeyStore.getInstance("BKS", "BC");
		keyStore.load(null);
		keyStore.setKeyEntry(alias, privateKey, keyPwd.toCharArray(), new X509Certificate[] { cert });
		write(keyStore, ksPwd, path);
	}

	public static void writeBks(X509Certificate cert, String alias, String ksPwd, String path) throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore keyStore = KeyStore.getInstance("BKS", "BC");
		keyStore.load(null);
		keyStore.setCertificateEntry(alias, cert);
		write(keyStore, ksPwd, path);
	}

	public static void writeP12(X509Certificate cert, String alias, PrivateKey privateKey, String keyPwd, String ksPwd, String path) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(null);
		keyStore.setKeyEntry(alias, privateKey, keyPwd.toCharArray(), new X509Certificate[] { cert });
		write(keyStore, ksPwd, path);
	}

	public static KeyStore readKeyStore(String type, String ksFile, String ksPwd) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		InputStream bksIn = null;
		try {
			bksIn = new FileInputStream(new File(ksFile));
			KeyStore keyStore = KeyStore.getInstance(type);
			keyStore.load(bksIn, ksPwd.toCharArray());
			return keyStore;
		} finally {
			try {
				if (bksIn != null) {
					bksIn.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public static void write(KeyStore ks, String ksPwd, String path) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		OutputStream ksOs = null;
		try {
			ksOs = new FileOutputStream(new File(path));
			ks.store(ksOs, ksPwd.toCharArray());
		} finally {
			try {
				if (ksOs != null) {
					ksOs.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public static String print(KeyStore ks, String... keyPwd) {
		StringBuilder sb = new StringBuilder();
		sb.append("KeyStore信息");
		try {
			sb.append("\n\n提供者 : " + ks.getProvider().getName() + "\n类型 : " + ks.getType() + "\n实体个数 : " + ks.size());
			Enumeration<String> en = ks.aliases();
			while (en.hasMoreElements()) {
				String alias = en.nextElement();
				Certificate cert = ks.getCertificate(alias);
				if (cert instanceof X509Certificate) {
					sb.append("\n\n别名为 " + alias + " 的证书信息：\n");
					sb.append(CertUtils.print((X509Certificate) cert));
				}
				if (keyPwd != null) {
					for (String pwd : keyPwd) {
						Key key = ks.getKey(alias, pwd.toCharArray());
						if (key != null) {
							sb.append("\n\n别名为 " + alias + " 的密钥信息：\n");
							sb.append(KeyUtils.print(key));
						}
					}
				}
			}
		} catch (Exception e) {
		}
		return sb.toString();
	}

}
