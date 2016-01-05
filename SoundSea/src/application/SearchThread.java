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

	SearchThread(TextField getSearchField, Text songLabelText, ImageView albumArt) {
		this.getSearchField = getSearchField;
		this.songLabelText = songLabelText;
		this.albumArt = albumArt;
	}
	
	public void run() {
		if(getSearchField.getText().isEmpty()) {
			return;
		}
		
		String query = getSearchField.getText();
		List<String> googleURLResults = null;
		
		// get lyrics for song
		try {
			googleURLResults = FXController.googleSearchQueryResults(FXController.azlyrics,query);
		} catch (IOException e4) {
			e4.printStackTrace();
		}
		List<String> lyricsURL = null;
		try {
			lyricsURL = FXController.getSongLyricsFromAZLyrics(googleURLResults.get(0));
		} catch (IOException e3) {
			e3.printStackTrace();
		} // Parse from the FIRST result.
		FXController.printLyricsToUI(lyricsURL);
		
		// get youtube link for song
		try {
			googleURLResults = FXController.googleSearchQueryResults(FXController.youtube,query);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		FXController.YoutubeURL.add(googleURLResults.get(0));
		FXController.YoutubeURL.set(0, FXController.YoutubeURL.get(0).replace("https://www.youtube.com/watch%3Fv%3D", "")); 
		try {
			FXController.googleImgURLResults = FXController.googleImageSearchQueryResults();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		songLabelText.setText(FXController.songFullTitle);
		
		albumArt.setImage(null);

		
		// set cover art album image in window
		Image image;
		int imageUrlCounter = 0;
		do {
			URL coverArtUrl = null;
			try {
				coverArtUrl = new URL(FXController.imageURLs.get(imageUrlCounter));
			} catch (MalformedURLException e) {
			}
			BufferedImage img = null;
			try {
				img = ImageIO.read(coverArtUrl);
			} catch (IOException e) {
			}
			image = SwingFXUtils.toFXImage(img, null);
			imageUrlCounter++;
		} while( image.getWidth()/image.getHeight() != 1);
		albumArt.setImage(image);
				
	}
}
