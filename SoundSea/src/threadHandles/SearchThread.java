package threadHandles;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
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
	private Button playButton;
	private Button pauseButton;
	private Button leftSearch;
	private Button rightSearch;

	public SearchThread(TextField getSearchField, TextArea songLabelText, ImageView albumArt, ImageView loadingImage, boolean quickDownload, ProgressBar progressBar, Button playButton, Button pauseButton, Button leftSearch, Button rightSearch) {
		this.getSearchField = getSearchField;
		this.songLabelText = songLabelText;
		this.albumArt = albumArt;
		this.loadingImage = loadingImage;
		this.quickDownload = quickDownload;
		this.progressBar = progressBar;
		this.playButton = playButton;
		this.pauseButton = pauseButton;
		this.leftSearch = leftSearch;
		this.rightSearch = rightSearch;
	}
	
	public void run() {
		if(getSearchField.getText().isEmpty()) {
			return;
		}
		try {
			if(!FXController.fileList.isEmpty())
				FXController.fileList.remove(0);
			
			if(!FXController.fullTitleList.isEmpty())
				FXController.fullTitleList.remove(0);
			
			boolean validSong;
			image = null;
			// reset GUI view
			playButton.setVisible(false);
			pauseButton.setVisible(false);
			albumArt.setImage(FXController.greyImage);
			if(!"".equals(songLabelText.toString())) 
				songLabelText.setText("");
			loadingImage.setVisible(true);
			rightSearch.setVisible(false);
			leftSearch.setVisible(false);
			
			// parse itunes info for song
			String songInfoQuery = getSearchField.getText();
			try {
				Connection.getiTunesSongInfo(songInfoQuery, songLabelText);
				
				// grab cover art image
				CoverArtThread cat = new CoverArtThread();
				cat.start();
				
				// get download link for song
				Connection.getSongFromPleer(songLabelText);
			} catch (NullPointerException e) {
				songLabelText.setText("Song not found");
			}
		
			try {
				songLabelText.setText("[" + FXController.qualityList.get(0) + "] "+ FXController.fullTitleList.get(0));
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
					//spin
				}
				
				FXController.fileCounter = 0;
				
				albumArt.setImage(null);
				loadingImage.setVisible(false);
				albumArt.setImage(image);
				playButton.setVisible(true);
				rightSearch.setVisible(true);
				leftSearch.setVisible(true);
				
				if(FXController.songPlaying == true) {
					FXController.songPlaying = false;
					SongControl.stopSong();
				}
			}
			else {
				BufferedImage image = ImageIO.read(getClass().getClassLoader().getResource("resources/placeholder.png"));
				Image test = SwingFXUtils.toFXImage(image, null);
				albumArt.setImage(test);
				loadingImage.setVisible(false);
				rightSearch.setVisible(false);
				leftSearch.setVisible(false);
			}
		} catch (IOException | InterruptedException e) {
			loadingImage.setVisible(false);
			e.printStackTrace();
		}
				
	}
	
}
