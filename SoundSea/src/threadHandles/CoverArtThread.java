package threadHandles;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import application.FXController;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class CoverArtThread extends Thread{

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
			
			SearchThread.image = image;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
