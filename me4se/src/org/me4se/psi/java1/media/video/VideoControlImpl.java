package org.me4se.psi.java1.media.video;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.VideoControl;
import javax.microedition.midlet.ApplicationManager;

/**
 * @author Michael Kroll, Stefan Haustein
 */
public class VideoControlImpl implements VideoControl {

  public static final String VIDEO_FRAME_1 = "/tux_small1.jpg";
  public static final String VIDEO_FRAME_2 = "/tux_small2.jpg";
  public static final String LARGE_IMAGE = "/tux_large.jpg";

  int frameCount;

  Image[] video;

  Canvas targetCanvas;
  VideoItem videoItem;
  boolean fullScreen;
  int x;
  int y;
  boolean visible = false;

  public VideoControlImpl() {
    try {
      video = new Image[] { Image.createImage(VIDEO_FRAME_1), Image.createImage(VIDEO_FRAME_2) };
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.microedition.media.control.VideoControl#getDisplayHeight()
   */

  public int getDisplayHeight() {
    return video[0].getHeight();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.microedition.media.control.VideoControl#getDisplayWidth()
   */
  public int getDisplayWidth() {
    return video[0].getWidth();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.microedition.media.control.VideoControl#getDisplayX()
   */
  public int getDisplayX() {
    return x;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.microedition.media.control.VideoControl#getDisplayY()
   */
  public int getDisplayY() {
    return y;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.microedition.media.control.VideoControl#getSnapshot(java.lang.String)
   */
  public byte[] getSnapshot(String imageType) throws MediaException {
    // System.out.println("VideoControlImpl.getSnapshot() called with no
    // effect!");
    try {
      InputStream is = ApplicationManager.getInstance().openInputStream(LARGE_IMAGE);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buf = new byte[512];
      while (true) {
        int cnt = is.read(buf);
        if (cnt <= 0)
          break;
        baos.write(buf, 0, cnt);
      }
      return baos.toByteArray();
    } catch (IOException e) {
      throw new MediaException(e.toString());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.microedition.media.control.VideoControl#getSourceHeight()
   */
  public int getSourceHeight() {
    return 240;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.microedition.media.control.VideoControl#getSourceWidth()
   */
  public int getSourceWidth() {
    return 320;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.microedition.media.control.GUIControl#initDisplayMode(int,
   *      java.lang.Object)
   */
  public Object initDisplayMode(int mode, Object arg) {
    switch (mode) {
    case USE_GUI_PRIMITIVE:
      visible = true;
      return new VideoItem(this);
    case USE_DIRECT_VIDEO:
      targetCanvas = (Canvas) arg;
      return targetCanvas;
    }
    throw new IllegalArgumentException("ME4SE: Unsupported video mode: " + mode);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.microedition.media.control.VideoControl#setDisplayFullScreen(boolean)
   */
  public void setDisplayFullScreen(boolean fullScreenMode) throws MediaException {
    this.fullScreen = fullScreenMode;

    setDisplayLocation((targetCanvas.getWidth() - getDisplayWidth()) / 2,
        (targetCanvas.getHeight() - getDisplayHeight()) / 2);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.microedition.media.control.VideoControl#setDisplayLocation(int,
   *      int)
   */
  public void setDisplayLocation(int x, int y) {
    this.x = x;
    this.y = y;
    System.out.println("ME4SE: VideoControl.setDispalyLocation(int x='" + x + "', y='" + y + "') called.");
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.microedition.media.control.VideoControl#setDisplaySize(int, int)
   */
  public void setDisplaySize(int width, int height) throws MediaException {
    // throw new MediaException("ME4SE: Video resizing is not supported.");
    System.out.println("ME4SE: VideoControl.setDispalySize(int width='" + width + "', height='" + height
        + "') called but not supported.");
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.microedition.media.control.VideoControl#setVisible(boolean)
   */
  public void setVisible(boolean visible) {
    this.visible = visible;
    if (!visible && targetCanvas != null) {
      targetCanvas._setVideoControlData(x, y, null);
    }
  }

  Image getCurrentImage() {
    return video[frameCount % 20 == 0 ? 1 : 0];
  }

  public void tick() {
    // System.out.println("tick: "+frameCount);
    frameCount++;
    if (targetCanvas != null && visible) {
      targetCanvas._setVideoControlData(x, y, getCurrentImage());
    } else if (videoItem != null && visible) {
      videoItem.repaint();
    }

  }
}