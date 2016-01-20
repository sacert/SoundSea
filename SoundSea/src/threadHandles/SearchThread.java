package threadHandles;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import application.FXController;
import intnet.Connection;

public class SearchThread extends Thread {

	public static Image image;
	
	private TextField getSearchField;
	private TextArea songLabelText;
	private ImageView albumArt;
	private ImageView loadingImage;
	private boolean quickDownload;
	private ProgressBar progressBar;

	public SearchThread(TextField getSearchField, TextArea songLabelText, ImageView albumArt, ImageView loadingImage, boolean quickDownload, ProgressBar progressBar) {
		this.getSearchField = getSearchField;
		this.songLabelText = songLabelText;
		this.albumArt = albumArt;
		this.loadingImage = loadingImage;
		this.quickDownload = quickDownload;
		this.progressBar = progressBar;
	}
	
	public void run() {
		if(getSearchField.getText().isEmpty()) {
			return;
		}
		try {
			
			
			boolean validSong;
			image = null;
			// reset GUI view
			albumArt.setImage(FXController.greyImage);
			if(songLabelText.toString() != "") 
				songLabelText.setText("");
			loadingImage.setVisible(true);
			
			// parse itunes info for song
			String songInfoQuery = getSearchField.getText();
			try {
				Connection.getiTunesSongInfo(songInfoQuery);
				
				// grab cover art image
				CoverArtThread cat = new CoverArtThread();
				cat.start();
				
				// get download link for song
				Connection.getSongFromPleer();
			} catch (NullPointerException e) {
				System.out.println("No itunes info");
			}
		
			try {
				songLabelText.setText(FXController.fullTitleList.get(0));
				validSong = true;
			} catch(IndexOutOfBoundsException e)  {
				songLabelText.setText("Song not found");
				validSong = false;
			}
			
			if(validSong) {
				if(quickDownload) {
					FXController.downloadSong(progressBar);
				}
				
				// if the cover art hasn't been displayed yet, spin until it has
				while(image == null) {
					System.out.println("grabbing");
					//spin
				}
				
				albumArt.setImage(null);
				loadingImage.setVisible(false);
				albumArt.setImage(image);
			}
			else {
				File imgFile = new File("src/resources/placeholder.png");
				System.out.println(imgFile.toURI().toString());
				Image image = new Image(imgFile.toURI().toString());
				albumArt.setImage(image);
				loadingImage.setVisible(false);
			}
		} catch (IOException | InterruptedException e) {
			loadingImage.setVisible(false);
			e.printStackTrace();
		}
				
	}
	
}
