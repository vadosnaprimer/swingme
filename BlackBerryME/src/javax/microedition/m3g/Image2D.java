package javax.microedition.m3g;

import net.yura.blackberry.rim.Image;

public class Image2D {
	public static final int RGB = 99;

	private Image image;

	public Image2D(int format, Object image) {

		if (image instanceof Image) {
		    this.image = (Image) image;
		}
		else {
			throw new IllegalArgumentException("Unrecognized image object.");
		}
	}

	Image getImage() {
	    return image;
	}
}
