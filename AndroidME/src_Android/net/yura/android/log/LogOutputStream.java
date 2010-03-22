package net.yura.android.log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.util.Log;

public class LogOutputStream extends OutputStream
{
	public static final int LOG_LEVEL = Log.VERBOSE;
	private ByteArrayOutputStream bos = new ByteArrayOutputStream();
	private String name;

	public LogOutputStream( String name )
	{
		this.name = name;
	}

	@Override
	public void write( int b ) throws IOException
	{
//		if( b == '\n' )
//		{
//			String s = new String( this.bos.toByteArray() );
////			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
////			StackTraceElement ste = stackTrace[stackTrace.length - 2];
////
////			// Make Line# clickable in eclipse
////			s = "---\n\t  " + ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber()	+ ")";
//
//			Log.v( this.name, s );
//			Log.println(1, "kkk", "gggg");
//			this.bos = new ByteArrayOutputStream();
//		}
//		else
//		{
//			this.bos.write( b );
//		}
	}

}
