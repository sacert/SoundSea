package application;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class SearchThread extends Thread {

	TextField getSearchField;
	Text songLabelText;
	ImageView albumArt;
	ImageView loadingImage;
	boolean quickDownload;

	SearchThread(TextField getSearchField, Text songLabelText, ImageView albumArt, ImageView loadingImage, boolean quickDownload) {
		this.getSearchField = getSearchField;
		this.songLabelText = songLabelText;
		this.albumArt = albumArt;
		this.loadingImage = loadingImage;
		this.quickDownload = quickDownload;
	}
	
	public void run() {
		if(getSearchField.getText().isEmpty()) {
			return;
		}
		try {
			loadingImage.setVisible(true);
			String query = getSearchField.getText();
			List<String> googleURLResults = null;
			
			// get lyrics for song
			googleURLResults = FXController.googleSearchQueryResults(FXController.azlyrics,query);
			List<String> lyricsURL = null;
			lyricsURL = FXController.getSongLyricsFromAZLyrics(googleURLResults.get(0));
				
			// Parse from the FIRST result.
			FXController.printLyricsToUI(lyricsURL);
			
			// get youtube link for song
			googleURLResults = FXController.googleSearchQueryResults(FXController.youtube,query);
			FXController.YoutubeURL.add(googleURLResults.get(0));
			FXController.YoutubeURL.set(0, FXController.YoutubeURL.get(0).replace("https://www.youtube.com/watch%3Fv%3D", "")); 
				FXController.googleImgURLResults = FXController.googleImageSearchQueryResults();
			songLabelText.setText(FXController.songFullTitle);	
			
			if(quickDownload) {
				FXController.downloadSong(FXController.YoutubeURL.get(0));
			}
			
			// set cover art album image in window
			Image image;
			int imageUrlCounter = 0;
			do {
				URL coverArtUrl = null;
				coverArtUrl = new URL(FXController.imageURLs.get(imageUrlCounter));
				BufferedImage img = null;
				img = ImageIO.read(coverArtUrl);
				image = SwingFXUtils.toFXImage(img, null);
				imageUrlCounter++;
			} while( image.getWidth()/image.getHeight() < 0.99 || image.getWidth()/image.getHeight() > 1.01);
			
			FXController.imageIndex = imageUrlCounter - 1;
			
			albumArt.setImage(null);
			loadingImage.setVisible(false);
			albumArt.setImage(image);
		} catch (IOException e1) {
			loadingImage.setVisible(false);
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
}
