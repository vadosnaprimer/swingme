package javax.microedition.pki;

import java.io.IOException;

/**
 * @API MIDP-2.0 
 */
public class CertificateException extends IOException {

	/**
	 * @API MIDP-2.0 
	 */
	public static final byte BAD_EXTENSIONS = 1;

	/**
	 * @API MIDP-2.0 
	 */
	public static final byte CERTIFICATE_CHAIN_TOO_LONG = 2;

	/**
	 * @API MIDP-2.0 
	 */
	public static final byte EXPIRED = 3;

	/**
	 * @API MIDP-2.0 
	 */
	public static final byte UNAUTHORIZED_INTERMEDIATE_CA = 4;

	/**
	 * @API MIDP-2.0 
	 */
	public static final byte MISSING_SIGNATURE = 5;

	/**
	 * @API MIDP-2.0 
	 */
	public static final byte NOT_YET_VALID = 6;

	/**
	 * @API MIDP-2.0 
	 */
	public static final byte SITENAME_MISMATCH = 7;

	/**
	 * @API MIDP-2.0 
	 */
	public static final byte UNRECOGNIZED_ISSUER = 8;

	/**
	 * @API MIDP-2.0 
	 */
	public static final byte UNSUPPORTED_SIGALG = 9;

	/**
	 * @API MIDP-2.0 
	 */
	public static final byte INAPPROPRIATE_KEY_USAGE = 10;

	/**
	 * @API MIDP-2.0 
	 */
	public static final byte BROKEN_CHAIN = 11;

	/**
	 * @API MIDP-2.0 
	 */
	public static final byte ROOT_CA_EXPIRED = 12;

	/**
	 * @API MIDP-2.0 
	 */
	public static final byte UNSUPPORTED_PUBLIC_KEY_TYPE = 13;

	/**
	 * @API MIDP-2.0 
	 */
	public static final byte VERIFICATION_FAILED = 14;

	/**
	 * @API MIDP-2.0 
	 * @ME4SE UNIMPLEMENTED
	 */
	public CertificateException(Certificate certificate, byte status) {
		System.out.println("CertificateException() constructor called with no effect!");
	}

	/**
	 * @API MIDP-2.0 
	 * @ME4SE UNIMPLEMENTED
	 */
	public CertificateException(String message, Certificate certificate, byte status) {
		System.out.println("CertificateException() constructor called with no effect!");
	}

	/**
	 * @API MIDP-2.0 
	 * @ME4SE UNIMPLEMENTED
	 */
	public Certificate getCertificate() {
		System.out.println("Certificate.getCertificate() called with no effect!");
		return null;
	}

	/**
	 * @API MIDP-2.0 
	 * @ME4SE UNIMPLEMENTED
	 */
	public byte getReason() {
		System.out.println("Certificate.getReason() called with no effect!");
		return 47;
	}
}
