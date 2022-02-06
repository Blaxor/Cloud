package ro.deiutzblaxo.cloud.images;

import ro.deiutzblaxo.cloud.threads.interfaces.CallBack;
import ro.deiutzblaxo.cloud.threads.interfaces.CloudThread;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class ImagesToPoints extends CloudThread<Point2DColor[]> {
    private File image;
    private CallBack<Point2DColor[]> callback;
    private Point2DColor[] points;
    private float resizeValue;
    float offsetX;
    float offsetY;
    public ImagesToPoints(File image, CallBack<Point2DColor[]> callback, float offsetX,float offsetY) throws IOException {
        this.resizeValue = resizeValue;
        this.callback = callback;
        this.image = image;
        this.offsetX=offsetX;
        this.offsetY=offsetY;

    }


    @Override
    protected void finish(CallBack<Point2DColor[]> callback, Point2DColor[] value) {
        callback.finished(value);
        this.interrupt();
    }

    @Override
    public void run() {
        int i = 0;
        BufferedImage buff;
        try {
            buff = ImageIO.read(image);
        } catch (IOException e) {
            e.printStackTrace();
            finish(callback, null);
            return;
        }
        points = new Point2DColor[buff.getHeight() * buff.getWidth()];
        for (float x = 0; x < buff.getHeight(); x++) {
            for (float y = 0; y < buff.getWidth(); y++) {
                points[i] = new Point2DColor(x+offsetX, y+offsetY, new Color(buff.getRGB((int) x, (int) y)));
            i++;
            }
        }
        finish(callback, points);
        buff.flush();
    }
}
