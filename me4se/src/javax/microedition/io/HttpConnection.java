// ME4SE - A MicroEdition Emulation for J2SE 
//
// Copyright (C) 2001 Stefan Haustein, Oberhausen (Rhld.), Germany
//
// Contributors: Geoff Hubbard
//
// STATUS: 
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation; either version 2 of the
// License, or (at your option) any later version. This program is
// distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details. You should have received a copy of the
// GNU General Public License along with this program; if not, write
// to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
// Boston, MA 02111-1307, USA.

package javax.microedition.io;

import java.io.*;

/**
 * @API MIDP-1.0 
 */

public interface HttpConnection extends ContentConnection {

	/**
	 * @API MIDP-1.0 
	 */
	public static final String HEAD = "HEAD";

	/**
	 * @API MIDP-1.0 
	 */
	public static final String GET = "GET";

	/**
	 * @API MIDP-1.0 
	 */
	public static final String POST = "POST";

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_OK = 200;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_CREATED = 201;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_ACCEPTED = 202;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_NOT_AUTHORITATIVE = 203;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_NO_CONTENT = 204;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_RESET = 205;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_PARTIAL = 206;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_MULT_CHOICE = 300;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_MOVED_PERM = 301;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_MOVED_TEMP = 302;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_SEE_OTHER = 303;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_NOT_MODIFIED = 304;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_USE_PROXY = 305;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_TEMP_REDIRECT = 307;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_BAD_REQUEST = 400;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_UNAUTHORIZED = 401;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_PAYMENT_REQUIRED = 402;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_FORBIDDEN = 403;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_NOT_FOUND = 404;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_BAD_METHOD = 405;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_NOT_ACCEPTABLE = 406;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_PROXY_AUTH = 407;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_CLIENT_TIMEOUT = 408;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_CONFLICT = 409;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_GONE = 410;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_LENGTH_REQUIRED = 411;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_PRECON_FAILED = 412;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_ENTITY_TOO_LARGE = 413;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_REQ_TOO_LONG = 414;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_UNSUPPORTED_TYPE = 415;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_UNSUPPORTED_RANGE = 416;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_EXPECT_FAILED = 417;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_INTERNAL_ERROR = 500;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_NOT_IMPLEMENTED = 501;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_BAD_GATEWAY = 502;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_UNAVAILABLE = 503;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_GATEWAY_TIMEOUT = 504;

	/**
	 * @API MIDP-1.0 
	 */
	public static final int HTTP_VERSION = 505;

	/**
	 * @API MIDP-1.0 
	 */
	public long getDate() throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public long getExpiration() throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public String getFile();

	/**
	 * @API MIDP-1.0 
	 */
	public String getHeaderField(String key) throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public String getHeaderField(int index) throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public int getHeaderFieldInt(String key, int def) throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public String getHeaderFieldKey(int index) throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public long getHeaderFieldDate(String key, long def) throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public String getHost();

	/**
	 * @API MIDP-1.0 
	 */
	public long getLastModified() throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public int getPort();

	/**
	 * @API MIDP-1.0 
	 */
	public String getProtocol();

	/**
	 * @API MIDP-1.0 
	 */
	public String getQuery();

	/**
	 * @API MIDP-1.0 
	 */
	public String getRef();

	/**
	 * @API MIDP-1.0 
	 */
	public int getResponseCode() throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public String getRequestProperty(String name);

	/**
	 * @API MIDP-1.0 
	 */
	public String getURL();

	/**
	 * @API MIDP-1.0 
	 */
	public String getRequestMethod();

	/**
	 * @API MIDP-1.0 
	 */
	public String getResponseMessage() throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public void setRequestMethod(String method) throws IOException;

	/**
	 * @API MIDP-1.0 
	 */
	public void setRequestProperty(String key, String value) throws IOException;
}
