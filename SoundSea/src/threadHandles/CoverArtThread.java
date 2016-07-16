package threadHandles;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import application.FXController;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class CoverArtThread extends Thread {

    public static byte[] imageByte;

    @Override
    public void run() {
        try {
            // set cover art album image in window
            Image image;

            URL coverArtUrl = new URL(FXController.coverArtUrl);
            BufferedImage img = ImageIO.read(coverArtUrl);

            if (img.getHeight() != img.getWidth()) {
                int newSize;
                if (img.getHeight() < img.getWidth()) {
                    newSize = img.getHeight();
                } else {
                    newSize = img.getWidth();
                }

                BufferedImage newImage = new BufferedImage(newSize, newSize, BufferedImage.TYPE_INT_RGB);

                Graphics g = newImage.createGraphics();
                g.drawImage(img, 0, 0, newSize, newSize, null);
                g.dispose();

                image = SwingFXUtils.toFXImage(newImage, null);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(newImage, "jpeg", baos);
                imageByte = baos.toByteArray();

            } else {
                image = SwingFXUtils.toFXImage(img, null);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(img, "jpeg", baos);
                imageByte = baos.toByteArray();
            }

            SearchThread.image = image;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
