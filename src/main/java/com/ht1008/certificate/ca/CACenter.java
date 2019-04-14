package com.ht1008.certificate.ca;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultAlgorithmNameFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;

import com.ht1008.certificate.utils.CertUtils;
import com.ht1008.certificate.utils.KeyUtils;


/**
 * 
 * @ClassName:  CACenter   
 * @Description:TODO(CA证书签发中心)   
 * @author: 朱建华-ASUS
 * @date:   2019年4月14日 下午10:20:00   
 *     
 * @Copyright: 2019 www.ht1008.com Inc. All rights reserved. 
 * 注意：本内容仅限于华悦信息技术股份有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
public class CACenter extends Subject {
	// 序列号
	private static int serialNumber = 1;

	// 有效期
	public static int year = 10;

	public CACenter() throws Exception {
		this("CN=CA,OU=CA,O=liuzy,L=shanghai,ST=shanghai,C=cn", 2048, "SHA1withRSA");
	}

	public CACenter(String subjectDN) throws Exception {
		this(subjectDN, 2048, "SHA1withRSA");
	}

	/**
	 * 自己生成CA私钥和CA自签证书
	 */
	public CACenter(String subjectDN, int keyLength, String signatureAlgorithm) throws NoSuchAlgorithmException, CertIOException, OperatorCreationException, CertificateException, InvalidKeyException, NoSuchProviderException, SignatureException {
		super(subjectDN, keyLength, signatureAlgorithm);
		Calendar calendar = Calendar.getInstance();
		Date notBefore = calendar.getTime();
		calendar.add(Calendar.YEAR, year);
		Date notAfter = calendar.getTime();

		JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
		AuthorityKeyIdentifier auth = extUtils.createAuthorityKeyIdentifier(publicKey);
		SubjectKeyIdentifier subj = extUtils.createSubjectKeyIdentifier(publicKey);

		JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(new X500Name(subjectDN), new BigInteger("1"), notBefore, notAfter, new X500Name(subjectDN), publicKey);
		certBuilder.addExtension(Extension.authorityKeyIdentifier, false, auth);
		certBuilder.addExtension(Extension.basicConstraints, false, new BasicConstraints(true));
		certBuilder.addExtension(Extension.subjectKeyIdentifier, false, subj);

		ContentSigner caSigner = new JcaContentSignerBuilder(signatureAlgorithm).setProvider("BC").build(privateKey);
		cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(caSigner));

		cert.checkValidity(new Date());
		cert.verify(publicKey);
	}

	/**
	 * 加载CA证书和CA私钥
	 * 
	 * @param cacertFile
	 * @param capemFile
	 */
	public CACenter(String cacertFile, String capemFile) throws IOException, InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
		cert = (X509Certificate) CertUtils.read(cacertFile);
		subjectDN = cert.getSubjectDN().toString();
		if (!isCA(cert)) {
			throw new RuntimeException("该证书不是CA证书");
		}
		publicKey = cert.getPublicKey();
		privateKey = KeyUtils.read(capemFile).getPrivate();
		cert.checkValidity(new Date());
		cert.verify(publicKey);
		year = new Long((cert.getNotAfter().getTime() - cert.getNotBefore().getTime()) / 1000 / 60 / 60 / 24 / 365).intValue();
	}

	/**
	 * 判断证书是否是CA证书
	 * 
	 * @param cert
	 * @return
	 * @throws IOException
	 */
	public boolean isCA(X509Certificate cert) throws IOException {
		ASN1InputStream in1 = null;
		ASN1InputStream in2 = null;
		try {
			if (cert.getSubjectDN().toString().equals(cert.getIssuerDN().toString())) {
				return true;
			}
			byte[] basicConstraints = cert.getExtensionValue("2.5.29.19");
			if (basicConstraints == null) {
				return false;
			}
			in1 = new ASN1InputStream(basicConstraints);
			Object obj = in1.readObject();
			basicConstraints = ((DEROctetString) obj).getOctets();
			in2 = new ASN1InputStream(basicConstraints);
			obj = in2.readObject();
			return BasicConstraints.getInstance(obj).isCA();
		} finally {
			if (in1 != null) {
				in1.close();
			}
			if (in2 != null) {
				in2.close();
			}
		}
	}

	/**
	 * 根据使用者公钥、使用者信息、CA默认签名算法，给别人签发证书
	 * 
	 * @param hisPublicKey
	 * @param SubjectDN
	 * @return
	 */
	public X509Certificate sign(PublicKey hisPublicKey, String subjectDN)
			throws InvalidKeyException, NoSuchAlgorithmException, CertIOException, OperatorCreationException, CertificateException, NoSuchProviderException, SignatureException {
		return sign(hisPublicKey, new X500Name(subjectDN), getSignatureAlgorithm());
	}

	public X509Certificate sign(PublicKey hisPublicKey, String subjectDN, String signatureAlgorithm)
			throws InvalidKeyException, NoSuchAlgorithmException, CertIOException, OperatorCreationException, CertificateException, NoSuchProviderException, SignatureException {
		return sign(hisPublicKey, new X500Name(subjectDN), signatureAlgorithm);
	}

	/**
	 * 根据使用者公钥、使用者信息、签名算法，给别人签发证书
	 * 
	 * @param hisPublicKey
	 * @param subjectDN
	 * @param signatureAlgorithm
	 * @return
	 */
	public X509Certificate sign(PublicKey hisPublicKey, X500Name subject, String signatureAlgorithm)
			throws NoSuchAlgorithmException, CertIOException, OperatorCreationException, CertificateException, InvalidKeyException, NoSuchProviderException, SignatureException {
		Calendar calendar = Calendar.getInstance();
		Date notBefore = calendar.getTime();
		calendar.add(Calendar.YEAR, year);
		Date notAfter = calendar.getTime();

		JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
		AuthorityKeyIdentifier auth = extUtils.createAuthorityKeyIdentifier(publicKey);
		SubjectKeyIdentifier subj = extUtils.createSubjectKeyIdentifier(hisPublicKey);

		JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(new X500Name(subjectDN), new BigInteger("" + serialNumber++), notBefore, notAfter, subject, hisPublicKey);
		certBuilder.addExtension(Extension.authorityKeyIdentifier, false, auth);
		certBuilder.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));
		certBuilder.addExtension(Extension.subjectKeyIdentifier, false, subj);

		ContentSigner caSigner = new JcaContentSignerBuilder(signatureAlgorithm).setProvider("BC").build(privateKey);
		X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(caSigner));

		cert.checkValidity(new Date());
		cert.verify(publicKey);
		return cert;
	}

	/**
	 * 根据证书签发请求，给别人签发证书
	 * 
	 * @param csr
	 * @return
	 */
	public X509Certificate sign(PKCS10CertificationRequest csr) throws Exception {
		JcaPKCS10CertificationRequest req = new JcaPKCS10CertificationRequest(csr);
		ASN1ObjectIdentifier OID = req.getSignatureAlgorithm().getAlgorithm();
		DefaultAlgorithmNameFinder finder = new DefaultAlgorithmNameFinder();
		return sign(req.getPublicKey(), req.getSubject(), finder.getAlgorithmName(OID));
	}

}
