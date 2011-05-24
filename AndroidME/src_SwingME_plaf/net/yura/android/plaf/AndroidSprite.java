package net.yura.android.plaf;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

public class AndroidSprite extends Sprite {

    Drawable spin;
    int frame;

    public AndroidSprite(Drawable d, int w, int h) {
        super(w > 1 ? w : d.getIntrinsicWidth(), h > 1 ? h : d.getIntrinsicHeight());
        spin = d;
    }

    @Override
    public void paint(Graphics g) {
        Drawable draw;

        if (spin instanceof AnimationDrawable) {
            draw = ((AnimationDrawable)spin).getFrame(frame);
        }
        else if (spin instanceof Runnable) {
            draw = spin;
        }
//        else if (spin.getClass().getName().equals("android.graphics.drawable.AnimatedRotateDrawable")) {
//            int totalFrame = getFrameSequenceLength();
//            canvas.rotate((frame*360)/totalFrame, x+width/2, y+height/2);
//            draw = spin;
//        }
        else {
            draw = spin;
            System.out.println("DO NOT KNOW HOW TO DRAW "+spin);
        }

        android.graphics.Canvas canvas = g.getCanvas();
        canvas.save();

        draw.setBounds(0, 0, getWidth(), getHeight());
        canvas.translate(getX(), getY());
        draw.draw(canvas);

        canvas.restore();
    }

    @Override
    public int getFrameSequenceLength() {
        if (spin instanceof AnimationDrawable) {
            return ((AnimationDrawable)spin).getNumberOfFrames();
        }
//        if (spin.getClass().getName().equals("android.graphics.drawable.AnimatedRotateDrawable")) {
//            return 12;
//        }
        return 12; // Android default implementation value.
    }

    private long lastSetFrameTime;

    @Override
    public void setFrame(int newFrame) {
        // HACK 1: android.graphics.drawable.AnimatedRotateDrawable class implements
        // Runnable. Calling run() will make it rotate one position. We are
        // ignoring the requesting frame, but that is OK for a spinner.
        if (frame != newFrame && spin instanceof Runnable) {

            // HACK 2: The same Sprite is shared between animations, and the "newFrame"
            // sequence of each of animation is independent, we need to somehow respond
            // to only of them, otherwise the spinner will spin a lot faster than intended.
            // 1 - If the frame set is the next from the previous one, we accept it.
            // 2 - Otherwise we accept it if the latest successful rotation happened
            // more than 200 ms ago.

            int nextFrame = (frame + 1) % getFrameSequenceLength();
            long now = System.currentTimeMillis();

            if (newFrame == nextFrame || (lastSetFrameTime - now) > 200) {
                frame = newFrame;
                // System.out.println(">> frame = " + frame);

                ((Runnable) spin).run();
                lastSetFrameTime = now;
            }
        }
    }
}
