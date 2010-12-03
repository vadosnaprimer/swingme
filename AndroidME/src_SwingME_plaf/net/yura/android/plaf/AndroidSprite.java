package net.yura.android.plaf;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

public class AndroidSprite extends Sprite {

    Drawable spin;
    int frame;

    public AndroidSprite(Drawable d) {
        super(d.getIntrinsicWidth(),d.getIntrinsicHeight());
        spin = d;
    }

    @Override
    public void paint(Graphics g) {
        android.graphics.Canvas canvas = g.getCanvas();
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        //spin.set
        Drawable draw;

        canvas.save();

        if (spin instanceof AnimationDrawable) {
            draw = ((AnimationDrawable)spin).getFrame(frame);
        }
        else if (spin.getClass().getName().equals("android.graphics.drawable.AnimatedRotateDrawable")) {
            int totalFrame = getFrameSequenceLength();
            canvas.rotate((frame*360)/totalFrame, x+width/2, y+height/2);
            draw = spin;
        }
        else {
            draw = spin;
            System.out.println("DO NOT KNOW HOW TO DRAW "+spin);
        }
        draw.setBounds(x, y, x+width, y+height);
        draw.draw(canvas);

        canvas.restore();
    }
    @Override
    public int getFrameSequenceLength() {
        if (spin instanceof AnimationDrawable) {
            return ((AnimationDrawable)spin).getNumberOfFrames();
        }
        if (spin.getClass().getName().equals("android.graphics.drawable.AnimatedRotateDrawable")) {
            return 25;
        }
        return 1;
    }
    @Override
    public void setFrame(int frame) {
        this.frame = frame;
    }

}
