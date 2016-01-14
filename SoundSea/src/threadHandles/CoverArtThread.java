package threadHandles;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import application.FXController;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class CoverArtThread extends Thread{
	
	public static byte[] imageByte;

	public void run()
	{
		try {
			// set cover art album image in window
			Image image;
			
			URL coverArtUrl = null;
			coverArtUrl = new URL(FXController.coverArtUrl);
			BufferedImage img = null;
			img = ImageIO.read(coverArtUrl);
			image = SwingFXUtils.toFXImage(img, null);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "jpeg", baos);
			
			imageByte = baos.toByteArray();
			
			System.out.println(imageByte);
			
			SearchThread.image = image;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
