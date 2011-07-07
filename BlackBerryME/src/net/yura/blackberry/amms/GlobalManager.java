package net.yura.blackberry.amms;

public class GlobalManager {

    public static MediaProcessor createMediaProcessor(String string) {
        if ("image/raw".equals(string)) {
            return new BlackBerryJpegEncoder();
        }
        throw new RuntimeException();
    }

}
