package net.yura.mobile.gui.components;

import net.yura.mobile.gui.KeyEvent;




public class Gallery extends Frame {

    private ScrollPane scrollPane = new ScrollPane();
    private ImageView imgView;


    public Gallery() {
        scrollPane.setBackground(0xff000000);
    }

    public Gallery(String name) {
        this();
        setName(name);
    }

    // TODO: Gallery should probably work like a list,
    // and have a way to tell it's data producer, which cell it wants,
    // if it's focus, etc, etc... For now we have a single cell, and it's a ImageView...
    public void setCellRenderer(ImageView imgView) {
        this.imgView = imgView;

        scrollPane.removeAll();

        if (imgView != null) {
            scrollPane.add(imgView);
        }

        this.getContentPane().add(scrollPane);
    }


    // Override
    public void processMouseEvent(int type, int x, int y, KeyEvent keys) {
        System.out.println("Gallery: processMouseEvent");

//        if (scrollPane != null) {
//            scrollPane.processMouseEvent(type, x, y, keys);
//
//            // TODO: Who should repaint... or invalidate?
//            repaint();
//        }
    }

    // Override
    public void pointerEvent(int[] type, int[] x, int[] y) {
        System.out.println("Gallery: pointerEvent");
//        if (imgView != null) {
//            imgView.pointerEvent(type, x, y);
//
//            // TODO: Who should repaint... or invalidate?
//            repaint();
//        }
    }

}
