package threadHandles;

import java.io.IOException;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import application.FXController;
import intnet.Connection;

public class SearchThread extends Thread {

	public static Image image;
	
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
			
			// parse itunes info for song
			String songInfoQuery = getSearchField.getText();
			Connection.getiTunesSongInfo(songInfoQuery);
			
			// grab cover art image
			CoverArtThread cat = new CoverArtThread();
			cat.start();
			
			// get download link for song
			Connection.getSongFromPleer();
			
			songLabelText.setText(FXController.fullTitleList.get(0));	
			
			if(quickDownload) {
				FXController.downloadSong();
			}
			
			// if the cover art hasn't been displayed yet, spin until it has
			while(image == null) {
				//spin
			}
			
			albumArt.setImage(null);
			loadingImage.setVisible(false);
			albumArt.setImage(image);
		} catch (IOException | InterruptedException e) {
			loadingImage.setVisible(false);
			e.printStackTrace();
		}
				
	}
	
}
