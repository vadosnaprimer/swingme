package org.me4se.psi.java1.gcf.https;

import javax.microedition.pki.Certificate;

public class CertificateImpl implements Certificate {

	java.security.cert.Certificate cert;
	
	public CertificateImpl(java.security.cert.Certificate certificate) {
		this.cert = certificate;
	}

	public String getIssuer() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getNotAfter() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getNotBefore() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getSerialNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSigAlgName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSubject() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getType() {
		return cert.getType();
	}

	public String getVersion() {
		return null;
	}

}
