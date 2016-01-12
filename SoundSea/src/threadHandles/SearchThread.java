package threadHandles;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import application.FXController;
import intnet.Connection;

public class SearchThread extends Thread {

	TextField getSearchField;
	Text songLabelText;
	ImageView albumArt;
	ImageView loadingImage;
	boolean quickDownload;

	public SearchThread(TextField getSearchField, Text songLabelText, ImageView albumArt, ImageView loadingImage, boolean quickDownload) {
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
			// reset GUI view
			albumArt.setImage(FXController.greyImage);
			if(songLabelText.toString() != "") 
				songLabelText.setText("");
			loadingImage.setVisible(true);
			
			String songInfoQuery = getSearchField.getText();
			List<String> googleURLResults = null;
			
			if(songInfoQuery.contains("|")) 
				songInfoQuery = songInfoQuery.substring(0, songInfoQuery.indexOf("|"));
			
			// get lyrics for song
			googleURLResults = Connection.googleSearchQueryResults(FXController.azlyrics,songInfoQuery);
			Connection.getSongLyricsFromAZLyrics(googleURLResults.get(0));
			
			// get download link for song
			Connection.getSongFromPleer();
			
			// get cover art 
			FXController.googleImgURLResults = Connection.googleImageSearchQueryResults();
			
			songLabelText.setText(FXController.fullTitleList.get(0));	
			
			if(quickDownload) {
				FXController.downloadSong();
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
			e.printStackTrace();
		}
				
	}
	
}
