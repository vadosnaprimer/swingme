package javax.microedition.lcdui.game;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


/**
 * @API MIDP-2.0 
 */
public class TiledLayer extends Layer {

  /**
   * @API MIDP-2.0 
   */

  int columns;
  int rows;
  int[][] cells;
  //  Image image;
  Vector animatedTiles = new Vector();        


  public TiledLayer(int columns, int rows, Image image, int tileWidth, int tileHeight) {
    this.columns = columns;
    this.rows = rows;
    this.image = image;
    this.frameWidth = tileWidth;
    this.frameHeight = tileHeight;
    this.cells = new int[rows][columns];
    this.w = columns * tileWidth;
    this.h = rows * tileHeight;
  }

  /**
   * @API MIDP-2.0 
   */
  public int createAnimatedTile(int staticTileIndex) {
    animatedTiles.addElement(new Integer(staticTileIndex));
    return -animatedTiles.size();
  }

  /**
   * @API MIDP-2.0 
   */
  public void setAnimatedTile(int animatedtileIndex, int staticTileIndex) {
    animatedTiles.setElementAt(new Integer(staticTileIndex), -animatedtileIndex - 1);
  }

  /**
   * @API MIDP-2.0 
   */
  public int getAnimatedTile(int animatedTileIndex) {
    return ((Integer) animatedTiles.elementAt(-animatedTileIndex - 1)).intValue();
  }

  /**
   * @API MIDP-2.0 
   */
  public void setCell(int col, int row, int tileIndex) {
    cells[row][col] = tileIndex;
  }

  /**
   * @API MIDP-2.0 
   */
  public int getCell(int col, int row) {
    return cells[row][col];
  }

  /**
   * @API MIDP-2.0 
   */
  public void fillCells(int col, int row, int numCols, int numRows, int tileIndex) {

    for(int r = row; r < row+numRows; r++){
      for(int c = col; c < col+numCols; c++){
        cells[r][c] = tileIndex;
      }
    }
  }

  /**
   * @API MIDP-2.0 
   */
  public final int getCellWidth() {
    return frameWidth;
  }

  /**
   * @API MIDP-2.0 
   */
  public final int getCellHeight() {
    return frameHeight;
  }

  /**
   * @API MIDP-2.0 
   */
  public final int getColumns() {
    return columns;
  }

  /**
   * @API MIDP-2.0 
   */
  public final int getRows() {
    return rows;
  }

  /**
   * @API MIDP-2.0 
   */

  public void setStaticTileSet(Image image, int tileWidth, int tileHeight) {
    if((image.getWidth() / tileWidth) * (image.getHeight() / tileHeight) < getFrameCount()){
      fillCells(0, 0, columns, rows, 0);
      animatedTiles = new Vector();
    }
    this.image = image;
    this.frameHeight = tileHeight;
    this.frameWidth = tileWidth;
  }

  /**
   * @API MIDP-2.0 
   */

  public final void paint(Graphics g) {
//  System.out.println("clY "+g.getClipY()+ " clh: "+g.getClipHeight());
    /*
        System.out.println("x: "+x+" cols: "+columns+" frameWidth:"+frameWidth+ " imgw: "+image.getWidth()); 
        System.out.println("y: "+y+" rows: "+rows+ " frameHeight:"+frameHeight+ " imgh: "+image.getHeight()); 	

        for(int r = 0; r < rows; r++){
    			for(int c = 0; c < columns; c++){
                drawImage(g, c*frameWidth, r*frameHeight, cells[r][c], 0);
                g.setColor(0x0ffffff);
                g.drawString(""+cells[r][c], x+c*frameWidth, y+r*frameHeight, 0);
//    				g.drawRect(x+c*frameWidth, y+r*frameHeight, frameWidth, frameHeight);
    			}
    		}

        System.out.println("Survived");

     */
    int maxX = g.getClipX() + g.getClipWidth();
    int col0 = (g.getClipX() - x) / frameWidth;
    if(col0 < 0){
      col0 = 0;
    }

    int maxY = g.getClipY() + g.getClipHeight();
    int row0 = (g.getClipY() - y) / frameHeight;
    if(row0 < 0){
      row0 = 0;
    }

    //  System.out.println("clY "+g.getClipY()+ " clh: "+g.getClipHeight());

    //  System.out.println("x: "+x+" maxX: "+maxX+" col0: "+col0+" cols: "+columns); 
    //  System.out.println("y: "+y+" maxY: "+maxY+" row0: "+row0+" rows: "+rows); 

    int cy = y + row0 * frameHeight;
    for(int row = row0; row < rows && cy < maxY; row++){
      int cx = x + col0 * frameWidth;
      for(int col = col0; col < columns && cx < maxX; col++){
        int idx = cells[row][col];
        if(idx != 0){
          if(idx < 0){
            idx = getAnimatedTile(idx);
          }
          drawImage(g, cx, cy, idx-1, 0);
          g.setColor(0x0ffffff);
          //    g.drawString(""+idx, cx+frameWidth/2, cy, Graphics.TOP|Graphics.HCENTER);
        }
        //      g.setColor(0x0ffffff);
        //      g.drawRect(cx, cy, frameWidth, frameHeight);
        cx += frameWidth;
      }
      cy += frameHeight;
    }
  }
}
