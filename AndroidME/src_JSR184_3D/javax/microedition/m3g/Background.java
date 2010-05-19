package javax.microedition.m3g;

public class Background {

    private int backgroundColor;
    private Image2D backgroundImage;

    public Background() {
    }

    public int getColor() {
        return this.backgroundColor;
    }

    public void setColor(int color) {
        this.backgroundColor = color;
    }

    public void setImage(Image2D image) {
        this.backgroundImage = image;
    }

    public Image2D getImage() {
        return backgroundImage;
    }
}
