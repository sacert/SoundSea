package application;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import threadHandles.DownloadThread;

public class FXController implements Initializable {

	@FXML private TextArea lyricBox;
	@FXML private TextField getSearchField;
	@FXML private Button getSearchButton;
	@FXML private Button downloadButton;
	@FXML private TextArea songLabelText;
	@FXML private ImageView albumArt;
	@FXML private Pane searchPopup;
	@FXML private ImageView loadingImage;
	@FXML private ProgressBar progressBar;
	@FXML private Button songSeaLogo;
	@FXML private Pane songLabelPane;
	
	public static String songFullTitle = "";
	public static String songTitle = "";
	public static String albumTitle = "";
	public static String bandArtist = "";
	public static String albumYear = "";
	public static String coverArtUrl = "";
	public static String genre = "";
	
	public static String folderDirectory = "";
	
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
		
		if( DownloadThread.downloading) {
			return;
		}
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
	
	@FXML
	private void handleSettings(ActionEvent event) throws IOException {
		
		Platform.runLater( new Runnable() {
			public void run() {
				
				try {
					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("settings.fxml"));
					Parent root = fxmlLoader.load();
					Stage stage = new Stage();
					stage.initModality(Modality.NONE);
					stage.initStyle(StageStyle.UNDECORATED);
					
					Scene scene = new Scene(root,444,78);
					scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					
					Rectangle rect = new Rectangle(444, 78);
					rect.setArcHeight(10);
					rect.setArcWidth(10);
					root.setClip(rect);
					scene.setFill(Color.TRANSPARENT);
					stage.setScene(scene);
					stage.initStyle(StageStyle.TRANSPARENT);
					addDragListeners(root, stage);
					stage.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		});

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		progressBar.setVisible(false);
		loadingImage.setVisible(false);
		getSearchField.setStyle("-fx-text-inner-color: #909090");
		
		setCoverArtGreyBlock();
		
		songLabelText.setEditable(false);
		
		getSearchField.setOnKeyPressed(new EventHandler<KeyEvent>()
	    {
	        @Override
	        public void handle(KeyEvent ke)
	        {
	            if (ke.getCode().equals(KeyCode.ENTER))
	            {
	            	threadHandles.SearchThread st = new threadHandles.SearchThread(getSearchField, songLabelText, albumArt, loadingImage, false, progressBar);
	        		st.start();
	            }
	        }
	    });
		
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
	
	double x, y;

	private void addDragListeners(final Node n, Stage primaryStage){

	    n.setOnMousePressed((MouseEvent mouseEvent) -> {
	        this.x = n.getScene().getWindow().getX() - mouseEvent.getScreenX();
	        this.y = n.getScene().getWindow().getY() - mouseEvent.getScreenY();
	    });

	    n.setOnMouseDragged((MouseEvent mouseEvent) -> {
	        primaryStage.setX(mouseEvent.getScreenX() + this.x);
	        primaryStage.setY(mouseEvent.getScreenY() + this.y);
	    });
	}
	
}


