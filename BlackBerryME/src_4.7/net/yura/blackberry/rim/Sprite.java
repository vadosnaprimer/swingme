package net.yura.blackberry.rim;

public class Sprite {

    public static final int TRANS_NONE = javax.microedition.lcdui.game.Sprite.TRANS_NONE;
    public static final int TRANS_ROT90 = javax.microedition.lcdui.game.Sprite.TRANS_ROT90;
    public static final int TRANS_ROT180 = javax.microedition.lcdui.game.Sprite.TRANS_ROT180;
    public static final int TRANS_ROT270 = javax.microedition.lcdui.game.Sprite.TRANS_ROT270;
    public static final int TRANS_MIRROR = javax.microedition.lcdui.game.Sprite.TRANS_MIRROR;
    public static final int TRANS_MIRROR_ROT90 = javax.microedition.lcdui.game.Sprite.TRANS_MIRROR_ROT90;
    public static final int TRANS_MIRROR_ROT180 = javax.microedition.lcdui.game.Sprite.TRANS_MIRROR_ROT180;
    public static final int TRANS_MIRROR_ROT270 = javax.microedition.lcdui.game.Sprite.TRANS_MIRROR_ROT270;


    int x;
    int y;
    int width;
    int height;
    

    Sprite(int x,int y,int w,int h, boolean vis) {
        this.x=x;
        this.y=y;
        this.width=w;
        this.height=h;
    }
    void setSize(int w,int h) {
        this.width=w;
        this.height=h;
    }
    
    /**
     * @see javax.microedition.lcdui.game.Layer#getWidth()
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * @see javax.microedition.lcdui.game.Layer#getHeight()
     */
    public int getHeight() {
            return height;
    }
    
    /**
     * @see javax.microedition.lcdui.game.Layer#getX()
     */
    public int getX() {
        return x;
    }
    
    /**
     * @see javax.microedition.lcdui.game.Layer#getY()
     */
    public int getY() {
        return y;
    }

    /**
     * @see javax.microedition.lcdui.game.Layer#setPosition(int, int)
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @see javax.microedition.lcdui.game.Layer#isVisible()
     */
    public boolean isVisible() {
        return true;
    }



	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	    // current frame index (within the sequence, not the absolut index)
	    private int frame;

	    // the frame sequence
	    // null if the default is used
	    private int [] sequence;

	    // coordinate of the reference pixel
	    private int refX;
	    private int refY;

	    // number of cols and rows within the image
	    private int cols;
	    private int rows;

	    // the transform aplied to this sprite
	    private int transform;

	    // the image containg the frames
	    private Image img;

	    // the collision rectangle
	    private int collX;
	    private int collY;
	    private int collWidth;
	    private int collHeight;

	    // arrays for the collision detection at pixel level
	    private int []rgbData;
	    private int []rgbDataAux;


	    public Sprite(Image img) {
	        this(img, img.getWidth(), img.getHeight());
	    }

	    public Sprite(Image img, int frameWidth, int frameHeight) {
	        // initial state is visible, positioned at 0, 0
	        // with a bound rectangle the same as the frame
	        this(0, 0, frameWidth, frameHeight, true);

	        // implicit check for null img
	        if (img.getWidth() % frameWidth != 0 ||
	                img.getHeight() % frameHeight != 0)
	            throw new IllegalArgumentException();
	        this.img = img;
	        cols = img.getWidth() / frameWidth;
	        rows = img.getHeight() / frameHeight;
	        collX = collY = 0;
	        collWidth = frameWidth;
	        collHeight = frameHeight;
	    }

	    public Sprite(Sprite otherSprite) {
	        // copy the otherSprite
	        this(otherSprite.getX(), otherSprite.getY(),
	                otherSprite.getWidth(), otherSprite.getHeight(),
	                otherSprite.isVisible());
	        this.frame = otherSprite.frame;
	        this.sequence = otherSprite.sequence;
	        this.refX = otherSprite.refX;
	        this.refY = otherSprite.refY;
	        this.cols = otherSprite.cols;
	        this.rows = otherSprite.rows;
	        this.transform = otherSprite.transform;
	        this.img = otherSprite.img;
	        this.collX = otherSprite.collX;
	        this.collY = otherSprite.collY;
	        this.collWidth = otherSprite.collWidth;
	        this.collHeight = otherSprite.collHeight;
	    }






	    public void defineReferencePixel(int x, int y) {
	        refX = x;
	        refY = y;
	    }

	    public int getRefPixelX() {
	        return getX() + refX;
	    }

	    public int getRefPixelY() {
	        return getY() + refY;
	    }

	    public void setRefPixelPosition(int x, int y) {
	        int curRefX, curRefY;
	        int width = getWidth();
	        int height = getHeight();

	        switch(transform) {
	            case TRANS_NONE:
	                curRefX = refX;
	                curRefY = refY;
	                break;
	            case TRANS_MIRROR_ROT180:
	                curRefX = width - refX;
	                curRefY = height - refY;
	                break;
	            case TRANS_MIRROR:
	                curRefX = width - refX;
	                curRefY = refY;
	                break;
	            case TRANS_ROT180:
	                curRefX = refX;
	                curRefY = height - refY;
	                break;
	            case TRANS_MIRROR_ROT270:
	                curRefX = height - refY;
	                curRefY = refX;
	                break;
	            case TRANS_ROT90:
	                curRefX = height - refY;
	                curRefY = width - refX;
	                break;
	            case TRANS_ROT270:
	                curRefX = refY;
	                curRefY = refX;
	                break;
	            case TRANS_MIRROR_ROT90:
	                curRefX = refY;
	                curRefY = width - refX;
	                break;
	            default: // cant really happen, but the return keeps the
	                    // compiler happy (otherwise it'll report variable
	                    // may not be initialized)
	                return;
	        }

	        setPosition(x - curRefX, y - curRefY);
	    }

	    public void defineCollisionRectangle(int x, int y, int width, int height) {
	        if (width < 0 || height < 0)
	            throw new IllegalArgumentException();
	        collX = x;
	        collY = y;
	        collWidth = width;
	        collHeight = height;
	    }

	    public void setFrameSequence(int []sequence) {
	        if (sequence == null) {
	            // return to default sequence
	            this.sequence = null;
	            return;
	        }

	        int max = (rows*cols)-1;

	        int l = sequence.length;

	        if (l == 0)
	            throw new IllegalArgumentException();

	        for (int i = 0; i < l; i++) {
	            int value = sequence[i];
	            if (value > max || value < 0)
	                throw new ArrayIndexOutOfBoundsException();
	        }

	        this.sequence = sequence;
	        // the frame number has to be reseted
	        this.frame = 0;
	    }

	    public final int getFrame() {
	                return frame;
	    }

	    public int getFrameSequenceLength() {
	        return (sequence == null) ? rows*cols : sequence.length;
	    }

	    public void setFrame(int frame) {
	        int l = (sequence == null)? rows*cols : sequence.length;
	        if (frame < 0 || frame >= l) {
	            throw new IndexOutOfBoundsException();
	        }
	        this.frame = frame;
	    }

	    public void nextFrame() {
	        if (frame == ((sequence == null)? rows*cols : sequence.length) - 1)
	            frame = 0;
	        else
	            frame++;
	    }

	    public void prevFrame() {
	        if (frame == 0)
	            frame = ((sequence == null)? rows*cols : sequence.length) - 1;
	        else
	            frame--;
	    }

	    public void setImage(Image img, int frameWidth, int frameHeight) {
	        synchronized (this) {
	                int oldW = getWidth();
	                int oldH = getHeight();
	                int newW = img.getWidth();
	                int newH = img.getHeight();

	                // implicit size check
	                setSize(frameWidth, frameHeight);

	                if (img.getWidth() % frameWidth != 0 ||
	                        img.getHeight() % frameHeight != 0)
	                    throw new IllegalArgumentException();
	                this.img = img;

	                int oldFrames = cols*rows;
	                cols = img.getWidth() / frameWidth;
	                rows = img.getHeight() / frameHeight;

	                if (rows*cols < oldFrames) {
	                    // there are fewer frames
	                    // reset frame number and sequence
	                    sequence = null;
	                    frame = 0;
	                }

	                if (frameWidth != getWidth() || frameHeight != getHeight()) {
	                    // size changed
	                    // reset collision rectangle and collision detection array
	                    defineCollisionRectangle(0, 0, frameWidth, frameHeight);
	                    rgbData = rgbDataAux = null;

	                    // if necessary change position to keep the reference pixel in place

	                    if (transform != TRANS_NONE) {
	                        int dx, dy;
	                        switch(transform) {
	                            case TRANS_MIRROR_ROT180:
	                                dx = newW - oldW;
	                                dy = newH - oldH;
	                                break;
	                            case TRANS_MIRROR:
	                                dx = newW - oldW;
	                                dy = 0;
	                                break;
	                            case TRANS_ROT180:
	                                dx = 0;
	                                dy = newH - oldH;
	                                break;
	                            case TRANS_MIRROR_ROT270:
	                                dx = newH - oldH;
	                                dy = 0;
	                                break;
	                            case TRANS_ROT90:
	                                dx = newH - oldH;
	                                dy = newW - oldW;
	                                break;
	                            case TRANS_ROT270:
	                                dx = 0;
	                                dy = 0;
	                                break;
	                            case TRANS_MIRROR_ROT90:
	                                dx = 0;
	                                dy = newW - oldW;
	                                break;
	                            default: // cant really happen, but the return keeps the
	                                    // compiler happy (otherwise it'll report variable
	                                    // may not be initialized)
	                                return;
	                        }
	                        // now change position to keep the refPixel in place
	                        move(dx, dy);
	                    }
	                }
	        }
	    }

	    public void paint(Graphics g) {
	        if (!isVisible())
	            return;

	        int f = (sequence == null)? frame : sequence[frame];
	        int w = getWidth();
	        int h = getHeight();
	        int fx = w * (f % cols);
	        int fy = h * (f / cols);

	        g.drawRegion(img, fx, fy, w, h, transform, getX(), getY(), Graphics.TOP | Graphics.LEFT);
	    }

	    public int getRawFrameCount() {
	        return cols * rows;
	    }

	    public void setTransform (int transform) {
	        if (this.transform == transform)
	            return;

	        int width = getWidth();
	        int height = getHeight();
	        int currentTransform = this.transform;

	        // calculate the coordinates of refPixel in the new transform
	        // relative to x, y

	        int newRefX, newRefY;

	        switch(transform) {
	            case TRANS_NONE:
	                newRefX = refX;
	                newRefY = refY;
	                break;
	            case TRANS_MIRROR_ROT180:
	                newRefX = width - refX;
	                newRefY = height - refY;
	                break;
	            case TRANS_MIRROR:
	                newRefX = width - refX;
	                newRefY = refY;
	                break;
	            case TRANS_ROT180:
	                newRefX = refX;
	                newRefY = height - refY;
	                break;
	            case TRANS_MIRROR_ROT270:
	                newRefX = height - refY;
	                newRefY = refX;
	                break;
	            case TRANS_ROT90:
	                newRefX = height - refY;
	                newRefY = width - refX;
	                break;
	            case TRANS_ROT270:
	                newRefX = refY;
	                newRefY = refX;
	                break;
	            case TRANS_MIRROR_ROT90:
	                newRefX = refY;
	                newRefY = width - refX;
	                break;
	            default:
	                throw new IllegalArgumentException();
	        }

	        // calculate the coordinates of refPixel in the current transform
	        // relative to x, y

	        int curRefX, curRefY;

	        switch(currentTransform) {
	            case TRANS_NONE:
	                curRefX = refX;
	                curRefY = refY;
	                break;
	            case TRANS_MIRROR_ROT180:
	                curRefX = width - refX;
	                curRefY = height - refY;
	                break;
	            case TRANS_MIRROR:
	                curRefX = width - refX;
	                curRefY = refY;
	                break;
	            case TRANS_ROT180:
	                curRefX = refX;
	                curRefY = height - refY;
	                break;
	            case TRANS_MIRROR_ROT270:
	                curRefX = height - refY;
	                curRefY = refX;
	                break;
	            case TRANS_ROT90:
	                curRefX = height - refY;
	                curRefY = width - refX;
	                break;
	            case TRANS_ROT270:
	                curRefX = refY;
	                curRefY = refX;
	                break;
	            case TRANS_MIRROR_ROT90:
	                curRefX = refY;
	                curRefY = width - refX;
	                break;
	            default: // cant really happen, but the return keeps the
	                    // compiler happy (otherwise it'll report variable
	                    // may not be initialized)
	                return;
	        }

	        // now change position to keep the refPixel in place
	        move(curRefX - newRefX, curRefY - newRefY);
	        this.transform = transform;
	    }
	

	    
	    public void move(int dx, int dy) {
	        setPosition( getX() + dx, getY() + dy );
	    }

	    
}
