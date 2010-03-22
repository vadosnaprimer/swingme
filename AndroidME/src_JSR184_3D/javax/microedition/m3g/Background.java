package javax.microedition.m3g;

import javax.microedition.khronos.opengles.GL11;
import javax.microedition.m3g.util.Color;

import javax.microedition.khronos.*;
import javax.microedition.khronos.opengles.*;
import javax.microedition.khronos.egl.*;

public class Background extends Object3D {

	public static final int BORDER = 32;
	public static final int REPEAT = 33;
	private int backgroundColor = 0;
	private Image2D backgroundImage = null;
	private int backgroundImageModeX = BORDER;
	private int backgroundImageModeY = BORDER;
	private int cropX;
	private int cropY;
	private int cropWidth;
	private int cropHeight;
	private boolean colorClearEnabled = true;
	private boolean depthClearEnabled = true;

	private Texture2D backgroundTexture = null;

	public Background()
	{
	}

	public int getColor()
	{
		return this.backgroundColor;
	}

	public void setColor(int color)
	{
		this.backgroundColor = color;
	}

	public int getCropX()
	{
		return this.cropX;
	}

	public int getCropY()
	{
		return this.cropY;
	}

	public int getCropWidth()
	{
		return this.cropWidth;
	}

	public int getCropHeight()
	{
		return this.cropHeight;
	}

	public void setCrop(int x, int y, int width, int height)
	{
		this.cropX = x;
		this.cropY = y;
		this.cropWidth = width;
		this.cropHeight = height;
	}

	public void setColorClearEnable(boolean enable)
	{
		this.colorClearEnabled = enable;
	}

	public boolean isColorClearEnabled()
	{
		return this.colorClearEnabled;
	}

	public void setDepthClearEnable(boolean enable)
	{
		this.depthClearEnabled = enable;
	}

	public boolean isDepthClearEnabled()
	{
		return this.depthClearEnabled;
	}

	public void setImageMode(int modeX, int modeY)
	{
		this.backgroundImageModeX = modeX;
		this.backgroundImageModeY = modeY;
	}

	public int getImageModeX()
	{
		return this.backgroundImageModeX;
	}

	public int getImageModeY()
	{
		return this.backgroundImageModeY;
	}

	public void setImage(Image2D image)
	{
		this.backgroundImage = image;

	}

	public Image2D getImage()
	{
		return backgroundImage;
	}

	void setupGL(GL11 gl)
	{
        int clearBits = 0;

		Color c = new Color(backgroundColor);
        gl.glClearColor(c.r, c.g, c.b, c.a);

        if(isColorClearEnabled())
        	clearBits |= GL11.GL_COLOR_BUFFER_BIT;
        if(isDepthClearEnabled())
        	clearBits |= GL11.GL_DEPTH_BUFFER_BIT;

        if(clearBits != 0)
        	gl.glClear(clearBits);

        if (backgroundImage != null)
		{
			if (backgroundTexture == null)
			{
				backgroundTexture = new Texture2D(backgroundImage);
				backgroundTexture.setFiltering(Texture2D.FILTER_LINEAR,
		                             Texture2D.FILTER_LINEAR);
				backgroundTexture.setWrapping(Texture2D.WRAP_CLAMP,
		                            Texture2D.WRAP_CLAMP);
				backgroundTexture.setBlending(Texture2D.FUNC_REPLACE);
			}

			gl.glMatrixMode (GL11.GL_MODELVIEW);
			gl.glPushMatrix ();
			gl.glLoadIdentity ();
			gl.glMatrixMode (GL11.GL_PROJECTION);
			gl.glPushMatrix ();
			gl.glLoadIdentity ();

			gl.glColorMask(true, true, true, true);
			gl.glDepthMask(false);
			gl.glDisable(GL11.GL_LIGHTING);
			gl.glDisable(GL11.GL_CULL_FACE);
			gl.glDisable(GL11.GL_BLEND);

			Graphics3D.getInstance().disableTextureUnits();

			gl.glActiveTexture(GL11.GL_TEXTURE0);
			backgroundTexture.setupGL(gl, new float[] {1,0,0,0});

			// calc crop
			int w = Graphics3D.getInstance().getViewportWidth();
			int h = Graphics3D.getInstance().getViewportHeight();

			if (cropWidth <= 0)
				cropWidth = w;
			if (cropHeight <= 0)
				cropHeight = h;
/* JP
			float u0 = (float)cropX/(float)w;
			float u1 = u0 + (float)cropWidth/(float)w;
			float v0 = (float)cropY/(float)h;
			float v1 = v0 + (float)cropHeight/(float)h;

	        gl.glBegin(GL.GL_QUADS);           	// Draw A Quad
	        gl.glTexCoord2f(u0, u0);
	        gl.glVertex3f(-1.0f, 1.0f, 0);	// Top Left
	        gl.glTexCoord2f(u1, v0);
	        gl.glVertex3f(1.0f, 1.0f, 0);	// Top Right
	        gl.glTexCoord2f(u1, v1);
	        gl.glVertex3f(1.0f, -1.0f, 0);	// Bottom Right
	        gl.glTexCoord2f(u0, v1);
	        gl.glVertex3f(-1.0f, -1.0f, 0);	// Bottom Left
	        gl.glEnd();
*/
	        gl.glPopMatrix();
	        gl.glMatrixMode (GL11.GL_MODELVIEW);
	        gl.glPopMatrix();

	        gl.glDisable(GL11.GL_TEXTURE_2D);
		}
	}


}
