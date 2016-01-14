package application;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FXController implements Initializable {

	@FXML private TextArea lyricBox;
	@FXML private TextField getSearchField;
	@FXML private Button getSearchButton;
	@FXML private Button downloadButton;
	@FXML private Text songLabelText;
	@FXML private ImageView albumArt;
	@FXML private Pane searchPopup;
	@FXML private ImageView loadingImage;
	@FXML private ProgressBar progressBar;
	
	
	public static String songFullTitle = "";
	public static String songTitle = "";
	public static String albumTitle = "";
	public static String bandArtist = "";
	public static String albumYear = "";
	public static String coverArtUrl = "";
	public static String genre = "";
	
	public static List<String> googleImgURLResults = null;
	public static List<String> imageURLs = new ArrayList<String>();
	public static List<String> fileList = new ArrayList<String>();
	public static List<String> fullTitleList = new ArrayList<String>();
	
	public static int imageIndex = 0;
	public static WritableImage greyImage;

	@FXML
	private void handleQuickDownloadAction(ActionEvent event) throws IOException, InterruptedException  {
		
		threadHandles.SearchThread st = new threadHandles.SearchThread(getSearchField, songLabelText, albumArt, loadingImage, true, progressBar);
		st.start();
	}
	
	@FXML
	private void handleSearchAction(ActionEvent event) throws IOException, InterruptedException  {
		
		threadHandles.SearchThread st = new threadHandles.SearchThread(getSearchField, songLabelText, albumArt, loadingImage, false, progressBar);
		st.start();
	}
	
	@FXML
	private void handleDownloadAction(ActionEvent event) throws IOException, InterruptedException  {
		
		if(songLabelText.getText().isEmpty()) {
			return;
		}
		downloadSong(progressBar);
	}
	
	public static void downloadSong(ProgressBar progressBar) throws IOException, InterruptedException {
		
		threadHandles.DownloadThread dt = new threadHandles.DownloadThread(fullTitleList.get(0), progressBar);
		dt.start();
	}
	
	@FXML
	private void handleCloseAction(ActionEvent event) {
		System.exit(0);
	}
	
	@FXML
	private void handleMinimizeAction(ActionEvent event) {
		Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
		stage.setIconified(true);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		progressBar.setVisible(false);
		loadingImage.setVisible(false);
		getSearchField.setStyle("-fx-text-inner-color: #909090");
		
		setCoverArtGreyBlock();
	
		
	}
	
	public void setCoverArtGreyBlock() {
		Rectangle clip = new Rectangle(albumArt.getFitWidth(), albumArt.getFitHeight());
		clip.setArcWidth(20);
		clip.setArcHeight(20);
		albumArt.setClip(clip);
		
		SnapshotParameters parameters = new SnapshotParameters();
		parameters.setFill(Color.rgb(241, 241, 241));
		greyImage = albumArt.snapshot(parameters, null);

		albumArt.setImage(greyImage);
	}
	
}


