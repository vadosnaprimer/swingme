/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

package net.yura.mobile.util;

import net.yura.mobile.gui.Graphics2D;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.Image2D;

/**
 * @author Yura Mamyrin
 */
public class ImageUtil {


    public static void fillArea(Graphics2D g,Image img,int src_x,int src_y,int src_w,int src_h,int dest_x,int dest_y,int dest_w,int dest_h,int t) {

        if (src_w<=0 || src_h<=0 || dest_w<=0 || dest_h<=0) {
            // #debug
            //System.out.println("calling tile on a area of size less then 0: src_w=" +src_w  +" src_h="+src_h +" dest_w="+ dest_w +" dest_h="+dest_h );
            return;
        }

        //#mdebug
        if ( ((src_w/dest_w)*(src_h/dest_h))>10 ) {
            System.out.println("going to tile a very small image on a big area: src_w=" +src_w  +" src_h="+src_h +" dest_w="+ dest_w +" dest_h="+dest_h );
        }
        //#enddebug

        final int[] c = g.getClip();

        g.clipRect(dest_x,dest_y,dest_w,dest_h);

        boolean normal = (t==Sprite.TRANS_NONE || t==Sprite.TRANS_MIRROR || t==Sprite.TRANS_ROT180 || t==Sprite.TRANS_MIRROR_ROT180);
        int a = normal?src_w:src_h;
        int b = normal?src_h:src_w;

        for (int pos_x=dest_x;pos_x<(dest_x+dest_w);pos_x=pos_x+a) {
            for (int pos_y=dest_y;pos_y<(dest_y+dest_h);pos_y=pos_y+b) {
                g.drawRegion(img, src_x,  src_y, src_w, src_h, t, pos_x, pos_y,Graphics.TOP|Graphics.LEFT);
            }
        }

        g.setClip(c);
    }

//
//    public static void fillArea(Graphics g,Image img,int src_x,int src_y,int src_w,int src_h,int dest_x,int dest_y,int dest_w,int dest_h) {
//
//        fillArea(new Graphics2D(g,Sprite.TRANS_NONE), img, src_x, src_y, src_w, src_h, dest_x, dest_y, dest_w, dest_h, Sprite.TRANS_NONE );
//
//    }

    public static Image makeImage(int w,int h,int color) {

                int[] rgbBuff = new int[w*h];

                for (int i = 0; i < rgbBuff.length; i++) {

                    rgbBuff[i] = color;

                }

                return Image.createRGBImage(rgbBuff, w, h, true);

    }

    public static final void imageColor(int ai[], int i) {
        int j = (i & 0xff0000) >> 16;
        int k = (i & 0xff00) >> 8;
        int l = (i & 0xff) >> 0;
        for(int i1 = 0; i1 < ai.length; i1++)
        {
            int j1 = (ai[i1] & 0xff000000) >> 24;
            int k1 = (ai[i1] & 0xff) >> 0;
            ai[i1] = j1 << 24 | (k1 * j) / 255 << 16 | (k1 * k) / 255 << 8 | (k1 * l) / 255;
        }

    }

    public static final Image imageColor(Image image, int i) {

        int ai[] = new int[image.getWidth() * image.getHeight()];
        image.getRGB(ai, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        imageColor(ai, i);
        return Image.createRGBImage(ai, image.getWidth(), image.getHeight(), true);
    }



	// ==============================================
	// Raw image PNG encoder optimized for MicroFonts
	// ==============================================
	static private int[] crc_table;
	static private int[] grey_pal;
	static private byte[] trans_pal;

        final static public Image _createRaw(int width, int height, final byte[] raw, int color) {
		final int size = 256*3 + 256 + (width+1)*height + 93; // palette * 3 + trans (256) + w*h + chunk stuff
		final byte[] data = new byte[size];

		// PNG file signature
		data[0] = -119;
		data[1] = 0x50;
		data[2] = 0x4e;
		data[3] = 0x47;
		data[4] = 0x0d;
		data[5] = 0x0a;
		data[6] = 0x1a;
		data[7] = 0x0a;

		// Header 'IHDR' Chunk
		data[11] = 0x0d;			// Chunk length: 4b (13 bytes)
		_wint(0x49484452, data, 12); // Chunk Name 'IHDR'
		data[19] = (byte) width;	// Width: 4b
		data[23] = (byte) height;	// Height: 4b
		data[24] = 0x08; 			// Bitdepth
		data[25] = 0x03; 			// Color Type (RGB + A)
//		data[26] = 0x00; 			// Compression method
//		data[27] = 0x00; 			// Filter method
//		data[28] = 0x00; 			// Interlace method
		_wint(_crc(data, 12, 17), data, 29);

		// Palette 'PLTE' Chunk
		_wint(256 * 3, data, 33); // Chunk length
		_wint(0x504c5445, data, 37); // Chunk Name 'PLTE'
		if(grey_pal == null) {
			grey_pal = new int[256];
			for(int i=0; i<256; i++)
				grey_pal[i] = ((i / 17) << 4);
		}
		for(int i=0, c=41; i<256; i++) {
			final int grey = grey_pal[i];
			data[c++] = (byte) ((grey * ((color & 0x00FF0000) >> 16)) >> 8);
			data[c++] = (byte) ((grey * ((color & 0x0000FF00) >> 8)) >> 8);
			data[c++] = (byte) ((grey * ((color & 0x000000FF) >> 0)) >> 8);
		}
		_wint(_crc(data, 37, 809 - 37), data, 809);

		// Transparency 'tRNS' Chunk
		_wint(256 + 1, data, 813);	 // Chunk length
		_wint(0x74524e53, data, 817); // 'tRNS' Header

                if (trans_pal==null) {
                    trans_pal = new byte[256];
                    for(int i=0; i<256; i++)
                            trans_pal[i] = (byte) ((i % 16) * 16);
                }

		System.arraycopy(trans_pal,0,data,821,256);
		//data[1077] = 0; ??
		_wint(_crc(data, 817, 1078 - 817), data, 1078);

		// Image Data 'IDAT' Chunk
		int compsize = (width + 1) * height;
		_wint(compsize + 11, data, 1082);	// Chunk Length
		_wint(0x49444154, data, 1086);		// Chunk Name 'IDAT'
		data[1090] = (byte) 0x78;			// PNG compression flags
		data[1091] = (byte) 0xda;			// PNG compression flags
		data[1092] = (byte) 0x01;			// PNG final block / No compression
		data[1093] = (byte) (compsize & 0xff);
		data[1094] = (byte) ((compsize >>> 8) & 0xff);
		data[1095] = (byte) (~data[1095 - 2]);
		data[1096] = (byte) (~data[1096 - 2]);

		int p = 1097;
		for(int y=0, i=0; y<height; y++) {	// Data copy
			p++;
			System.arraycopy(raw, i, data, p, width);
			p+=width;
			i+=width;
		}

		int adler1 = 1;
		int adler2 = 0;
		for (int i = 0; i < compsize; i++) {
			adler1 = adler1 + (data[1097+ i] & 0xff);
			adler2 = adler1 + adler2;
			adler1 %= 65521;
			adler2 %= 65521;
		}
		_wint((adler2 << 16) | adler1, data, p);
		p += 4;
		_wint(_crc(data, 1086, p - 1086), data, p);
		p += 4;

		// Footer 'IEND' Chunk
		p += 4; // Four 0 bytes
		data[p++] = (byte) 'I';
		data[p++] = (byte) 'E';
		data[p++] = (byte) 'N';
		data[p++] = (byte) 'D';
		data[p++] = -82;
		data[p++] = 0x42;
		data[p++] = 0x60;
		data[p++] = -126;

		return Image.createImage(data, 0, p);
	}
	final static private void _wint(long crc, byte[] d, int p) {
		d[p+0] = (byte) ((crc >>> 24) & 255);
		d[p+1] = (byte) ((crc >>> 16) & 255);
		d[p+2] = (byte) ((crc >>> 8) & 255);
		d[p+3] = (byte) (crc & 255);
	}
	final static private long _crc(byte[] buf, int off, int len) {

                if (crc_table==null) {
                    crc_table = new int[256];
                    for(int i=0; i<256 ;i++) {
                            int c = i;
                            for(int k=8; --k>= 0;)
                                    c = ((c & 1) != 0)? 0xedb88320 ^ (c >>> 1) : c >>> 1;
                            crc_table[i] = c;
                    }
                }

		int c = ~0;
		while(--len >= 0)
			c = crc_table[(c ^ buf[off++]) & 0xff] ^ (c >>> 8);
		return ~c & 0xffffffffL;
	}

    public static Image scaleDownImage(Image img, int newW, int newH) {
        try {
            // Ensure we have 3D API, otherwise throws exception
            Class.forName("javax.microedition.m3g.Background");
            return scaleDownImage3D(img, newW, newH);
        }
        catch (Throwable e) {
            // Do nothing. Converting with 3D API failed. Use sampling.
        }

        return null;
    }

    private static Image scaleDownImage3D(Image img, int newW, int newH) {

        // Create a mutable image with the requested size
        Image resImg = Image.createImage(newW, newH);
        Graphics g = resImg.getGraphics();

        Image2D image2D = new Image2D(Image2D.RGB, img);
        Background background = new Background();
        background.setColor(0xffffcc); // set the background color
        background.setImage(image2D);

        // get the singleton Graphics3D instance
        Graphics3D iG3D = Graphics3D.getInstance();
        try {
            iG3D.bindTarget(g, true, Graphics3D.TRUE_COLOR);
            iG3D.setViewport(0, 0, newW, newH);
            // clear the color and depth buffers
            iG3D.clear(background);
        }
        finally
        {
            // flush
            iG3D.releaseTarget();
        }

        return resImg;
    }
}
