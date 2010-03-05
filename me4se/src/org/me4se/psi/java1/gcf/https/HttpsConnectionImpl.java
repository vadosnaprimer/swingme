package org.me4se.psi.java1.gcf.https;

import java.io.IOException;

import javax.microedition.io.HttpsConnection;
import javax.microedition.io.SecurityInfo;
import javax.microedition.pki.Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

import org.me4se.psi.java1.gcf.http.HttpConnectionImpl;

public class HttpsConnectionImpl extends HttpConnectionImpl implements HttpsConnection, SecurityInfo {

	public SecurityInfo getSecurityInfo() throws IOException {
		return this;
	}

	public String getCipherSuite() {
		return((HttpsURLConnection) con).getCipherSuite();
	}

	public String getProtocolName() {
		
		return "SSL";
	}

	public String getProtocolVersion() {
		
		return "3.0";
	}

	public Certificate getServerCertificate() {
		try {
			return new CertificateImpl(((HttpsURLConnection) con).getServerCertificates()[0]);
		} catch (SSLPeerUnverifiedException e) {
			throw new RuntimeException(e);
		}
			
	}

}
