//JP package javax.microedition.m3g;
//
//import javax.media.opengl.GLAutoDrawable;
//import javax.media.opengl.GLEventListener;
//import javax.microedition.khronos.opengles.GL11;
//
//public abstract class RenderEventListener implements GLEventListener
//{
//	public void display( GLAutoDrawable glDrawable )
//    {
//		Graphics3D.getInstance().setGL(glDrawable.getGL());
//		paint();
//    }
//
//    public void init( GLAutoDrawable glDrawable )
//    {
//		Graphics3D.getInstance().setGL(glDrawable.getGL());
//		glDrawable.getGL().glShadeModel(GL11.GL_SMOOTH);              // Enable Smooth Shading
//
//		initialize();
//    }
//
//    public void reshape( GLAutoDrawable glDrawable, int i0, int i1, int i2, int i3 )
//    {
//    }
//
//    public void displayChanged( GLAutoDrawable glDrawable, boolean b, boolean b1 )
//    {
//    }
//
//    public abstract void paint();
//    public abstract void initialize();
//}