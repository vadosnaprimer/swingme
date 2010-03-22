package javax.microedition.m3g;

import javax.microedition.khronos.opengles.GL11;

import javax.microedition.m3g.util.Color;

public class Fog extends Object3D {

	public static final int EXPONENTIAL = 80;
	public static final int LINEAR 		= 81;

	private int color 			= 0;
	private int mode 			= LINEAR;
	private float density 		= 1.0f;
	private float nearDistance 	= 0.0f;
	private float farDistance 	= 1.0f;

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return mode;
	}

	public void setLinear(float near, float far)
	{
		this.nearDistance = near;
		this.farDistance = far;
	}

	public void setDensity(float density) {
		this.density = density;
	}

	public float getDensity() {
		return density;
	}

	public void setNearDistance(float nearDistance) {
		this.nearDistance = nearDistance;
	}

	public float getNearDistance() {
		return nearDistance;
	}

	public void setFarDistance(float farDistance) {
		this.farDistance = farDistance;
	}

	public float getFarDistance() {
		return farDistance;
	}

	void setupGL(GL11 gl)
	{
//JP        gl.glFogi(GL11.GL_FOG_MODE,getGLFogMode(this.mode));
		gl.glFogx(GL11.GL_FOG_MODE,getGLFogMode(this.mode));
        gl.glFogfv(GL11.GL_FOG_COLOR, Color.intToFloatArray(this.color), 0);
        gl.glFogf(GL11.GL_FOG_DENSITY, this.density);
        gl.glFogf(GL11.GL_FOG_START, this.nearDistance);
        gl.glFogf(GL11.GL_FOG_END, this.farDistance);
        gl.glEnable(GL11.GL_FOG);
	}

	int getGLFogMode(int mode)
	{
		switch(mode)
		{
			case EXPONENTIAL:
				return GL11.GL_EXP;
			default:
				return GL11.GL_LINEAR;
		}
	}
}
