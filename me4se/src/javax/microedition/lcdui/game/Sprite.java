package javax.microedition.lcdui.game;

import java.awt.Rectangle;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


/**
 * @API MIDP-2.0 
 */
public class Sprite extends Layer {

  int refX;
  int refY;
  int[] sequence;
  int frameIndex;
  int transformation;
  int refXTransformed;
  int refYTransformed;
  /** untransformed collistion rect x offset */
  int collX;
  /** untransformed collistion rect y offset */
  int collY;
  /** untransformed collistion rect width */
  int collW;
  /** untransformed collistion rect height */
  int collH;
  /** Including the transformation and x/y sprite position */
  java.awt.Rectangle currentCollRect = new Rectangle();

  /**
   * @API MIDP-2.0 
   */	
  public static final int TRANS_NONE = 0;

  /**
   * @API MIDP-2.0 
   */
  public static final int TRANS_ROT90 = 5;

  /**
   * @API MIDP-2.0 
   */
  public static final int TRANS_ROT180 = 3;

  /**
   * @API MIDP-2.0 
   */
  public static final int TRANS_ROT270 = 6;

  /**
   * @API MIDP-2.0 
   */
  public static final int TRANS_MIRROR = 2;

  /**
   * @API MIDP-2.0 
   */
  public static final int TRANS_MIRROR_ROT90 = 7;

  /**
   * @API MIDP-2.0 
   */
  public static final int TRANS_MIRROR_ROT180 = 1;

  /**
   * @API MIDP-2.0 
   */
  public static final int TRANS_MIRROR_ROT270 = 4;

  /**
   * @API MIDP-2.0 
   */
  public Sprite(Image image) {
    this(image, image.getWidth(), image.getHeight());
  }

  /**
   * @API MIDP-2.0 
   */
  public Sprite(Image image, int frameWidth, int frameHeight) {
    this.image = image;
    this.frameWidth = frameWidth;
    this.frameHeight = frameHeight;
    w = collW = frameWidth;
    h = collH = frameHeight;
    currentCollRect.width = collW;
    currentCollRect.height = collH;
    setTransform(0);  // set calculated vars
  }


  public Sprite(Sprite sprite) {
    this(sprite.image, sprite.frameWidth, sprite.frameHeight);
    sequence = sprite.sequence;
    frameIndex = sprite.frameIndex;
    refX = sprite.refX;
    refY = sprite.refY;
    x = sprite.x;
    y = sprite.y;
    collX = sprite.collX;
    collY = sprite.collY;
    collW = sprite.collW;
    collH = sprite.collH;
    transformation = sprite.transformation;
    setTransform(sprite.transformation);
  }

  /**
   * @API MIDP-2.0 
   */
  public void defineReferencePixel(int x, int y) {
    int saveTransform = transformation;
    setTransform(0); // correct x/y
    refX = x;
    refY = y;
    setTransform(saveTransform);
  }

  /**
   * @API MIDP-2.0 
   */
  public void setRefPixelPosition(int x, int y) {
    setPosition(x - refXTransformed, y - refYTransformed);
  }

  /**
   * @API MIDP-2.0 
   */
  public int getRefPixelX() {
    return refX;
  }

  /**
   * @API MIDP-2.0 
   */
  public int getRefPixelY() {
    return refY;
  }

  /**
   * @API MIDP-2.0 
   */
  public void setFrame(int sequenceIndex) {
    frameIndex = sequenceIndex;
  }

  /**
   * @API MIDP-2.0 
   */
  public final int getFrame() {
    return frameIndex;
  }

  /**
   * @API MIDP-2.0 
   */
  public int getRawFrameCount() {
    return getFrameCount();
  }

  /**
   * @API MIDP-2.0 
   */
  public int getFrameSequenceLength() {
    return sequence == null ? getFrameCount() : sequence.length;
  }


  /**
   * @API MIDP-2.0 
   */
  public void nextFrame() {
    frameIndex++;
    if(frameIndex >= getFrameSequenceLength()){
      frameIndex = 0;
    }
  }

  /**
   * @API MIDP-2.0 
   */
  public void prevFrame() {
    frameIndex--;
    if(frameIndex < 0){
      frameIndex = getFrameSequenceLength()-1;
    }
  }

  /**
   * @API MIDP-2.0 
   */
  public final void paint(Graphics g) {
    drawImage(g, 0, 0, sequence == null ? frameIndex : sequence[frameIndex], transformation);
  }

  public void setPosition(int x, int y){
    currentCollRect.x += x - this.x;
    currentCollRect.y += y - this.y;
    super.setPosition(x, y);
  }

  /**
   * @API MIDP-2.0 
   */
  public void setFrameSequence(int[] sequence) {
    if(sequence == null){
      this.sequence = null;
    }
    else{
      this.sequence = new int[sequence.length];
      System.arraycopy(sequence, 0, this.sequence, 0, sequence.length);
    }
    frameIndex = 0;
  }

  /**
   * @API MIDP-2.0 
   */

  public void setImage(Image image, int frameWidth, int frameHeight) {
    int oldFrameCount = getRawFrameCount();
    this.image = image;

    if(frameWidth != this.frameWidth || frameHeight != this.frameHeight){
      this.frameWidth = frameWidth;
      this.frameHeight = frameHeight;
      collX = 0;
      collY = 0;
      collW = frameWidth;
      collH = frameHeight;
    }

    if(getRawFrameCount() < oldFrameCount){
      setFrameSequence(null);
    }

    setTransform(transformation);
  }

  /**
   * @API MIDP-2.0 
   */

  public void defineCollisionRectangle(int cx, int cy, int cw, int ch) {
    collX = cx;
    collY = cy;
    collW = cw;
    collH = ch;
    setTransform(transformation);
  }

  /**
   * @API MIDP-2.0 
   */
  public void setTransform(int transform) {

    int oldRefX = refXTransformed;
    int oldRefY = refYTransformed;

    switch(transformation){
    case TRANS_NONE:
    case TRANS_ROT180:
    case TRANS_MIRROR:
    case TRANS_MIRROR_ROT180:
      currentCollRect.width = collW;
      currentCollRect.height = collH;
      break;
    default:
      currentCollRect.width = collH;
    currentCollRect.height = collW;
    }


    switch(transformation){
    case TRANS_NONE:
      refXTransformed = refX;
      refYTransformed = refY;
      break;

    case TRANS_ROT90:
      refXTransformed = refY;
      refYTransformed = refX;
      break;

    case TRANS_ROT180:
      refXTransformed = frameWidth - refX;
      refYTransformed = frameHeight - refY;
      break;

    case TRANS_ROT270:
      refXTransformed = frameHeight - refY;
      refYTransformed = frameWidth - refX;

    case TRANS_MIRROR:
      refXTransformed = frameWidth - refX;
      refYTransformed = refY;
      break;

    case TRANS_MIRROR_ROT90:
      refXTransformed = refY;
      refYTransformed = frameWidth - refX;
      break;

    case TRANS_MIRROR_ROT180:
      refXTransformed = refX;
      refYTransformed = frameHeight - refY;
      break;

    case TRANS_MIRROR_ROT270:
      refXTransformed = frameHeight - refY;
      refYTransformed = refX;

    default:
      throw new IllegalArgumentException();
    }

    this.transformation = transform;

    x += oldRefX - refXTransformed;
    y += oldRefY - refYTransformed;

    // TODO fix collision rect x/y
  }

  /**
   * TODO: Still need to care about pixel level..
   * @API MIDP-2.0 
   */

  public final boolean collidesWith(Sprite s, boolean pixelLevel) {
    return visible && s.visible && currentCollRect.intersects(s.currentCollRect);
  }

  /**
   * @API MIDP-2.0 
   * @ME4SE UNIMPLEMENTED
   */
  public final boolean collidesWith(TiledLayer t, boolean pixelLevel) {
    System.out.println("Sprite.collidesWithTitle(TitledLayer, boolean) called with no effect!");
    return false;
  }

  /**
   * TODO: Still need to care about pixel level..
   * @API MIDP-2.0 
   */
  public final boolean collidesWith(Image image, int x, int y, boolean pixelLevel) {
    if(!visible) return false;
    Rectangle cr2 = new Rectangle(x, y, image.getWidth(), image.getHeight());
    return cr2.intersects(currentCollRect);
  }
}
