package net.yura.mobile.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * this class solves a big bug on S60 phones
 * InputStreamReader on S60 tries to read even more even if its got a nice bit of
 * text to process and there is nothing more in the stream to read (but its still open)
 * @author Yura Mamyrin
 */
public class UTF8InputStreamReader extends Reader {

    private InputStream inputStream;
    private byte[] buffer = new byte[0];
    private int x,y,z;
    private int stage;

    public UTF8InputStreamReader(InputStream i) {
        inputStream = i;
    }

    public int read(char[] cbuf, int off, int len) throws IOException {

        int numChars = 0;

        while (numChars == 0) { // we can NOT return a result of 0, so we will keep trying

            if (buffer.length < len) {
                buffer = new byte[len];
            }

            int available = inputStream.available(); // on android can give -1 sometimes

            // dont block trying to over-read, this is the line that the S60 InputStreamReader misses
            int l = inputStream.read(buffer,0,available>len?len: (available<=0?1:available) );

            if (l<0) {
                return l;
            }

            for (int i=0;i<l;i++) {

                int result=-1;

                if (stage == 0) {

                    // get next byte unsigned
                    int b = buffer[ i ] & 0xff;
                    // classify based on the high order 3 bits
                    switch ( b >>> 5 ) {
                        case 6:
                            // two byte encoding
                            // 110yyyyy 10xxxxxx
                            // use low order 6 bits
                            y = b & 0x1f;
                            // use low order 6 bits of the next byte
                            // It should have high order bits 10, which we don't check.
                            stage = 1;
                            continue;
                        case 7:
                            // three byte encoding
                            // 1110zzzz 10yyyyyy 10xxxxxx
                            if ( ( b & 0x10 ) != 0 ) throw new IOException("UTF8Decoder does not handle 32-bit characters");
                            // use low order 4 bits
                            z = b & 0x0f;
                            // use low order 6 bits of the next byte
                            // It should have high order bits 10, which we don't check.
                            stage = 2;
                            continue;
                        default:
                            // one byte encoding
                            // 0xxxxxxx
                            // use just low order 7 bits
                            // 00000000 0xxxxxxx
                            result = ( char ) ( b & 0x7f );
                            break;
                    }
                }
                else if (stage == 1) {

                            x = buffer[ i ] & 0x3f;
                            // 00000yyy yyxxxxxx
                            result = ( char ) ( y << 6 | x );


                }
                else if (stage == 2) {

                            y = buffer[ i ] & 0x3f;
                            // use low order 6 bits of the next byte
                            // It should have high order bits 10, which we don't check.
                            stage = 3;
                            continue;

                }
                else if (stage==3) {

                            x = buffer[ i ] & 0x3f;
                            // zzzzyyyy yyxxxxxx
                            result = ( char ) ( z << 12 | y << 6 | x );

                }

                if (result == -1) throw new IOException();
                cbuf[off + numChars] = (char)result;
                numChars++;
                stage = 0;

            }
        
        }
        
        return numChars;

    }

    public void close() throws IOException {
        buffer = null;
        inputStream.close();
    }

}
