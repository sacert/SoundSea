package com.musicdwl.SoundSea;


import com.musicdwl.intnet.Connection;
import com.musicdwl.threadHandles.CoverArtThread;
import com.musicdwl.threadHandles.DownloadThread;
import com.musicdwl.threadHandles.SearchThread;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javazoom.jl.decoder.JavaLayerException;
import com.musicdwl.threadHandles.SongControl;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;

public class FXMLController implements Initializable {

	@FXML
	private TextField getSearchField;
	@FXML
	private TextArea songLabelText;
	@FXML
	private ImageView albumArt;
	@FXML
	private ImageView loadingImage;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Button playButton;
	@FXML
	private Button pauseButton;
	@FXML
	private Button rightSearch;
	@FXML
	private Button leftSearch;
	@FXML
	private Button getSearchButton;
	@FXML
	private Button downloadButton;
	@FXML
	private Button getLyricsButton;

	public static String songFullTitle = "";
	public static String songTitle = "";
	public static String albumTitle = "";
	public static String bandArtist = "";
	public static String albumYear = "";
	public static String coverArtUrl = "";
	public static String genre = "";

	public static String folderDirectory = "";

	public static List<String> googleImgURLResults = null;
	public static List<String> imageURLs = new ArrayList<>();
	public static List<String> artistList = new ArrayList<>();
	public static List<String> titleList = new ArrayList<>();
	public static List<String> streamList = new ArrayList<>();
	public static List<String> downloadList = new ArrayList<>();

	public static int imageIndex = 0;
	public static WritableImage greyImage;
	public static String qualityLevel;
	public static int songTime = 0;
	public static SongControl sc = null;
	int currSong = 0;
	public static transient boolean isFinished = false;
	public static CountDownLatch latch = null;
	public Thread t = null;
	public static ExecutorService exec = null;
	public static Task<Void> task = null;

	public static int fileCounter = 0;

	@FXML
	private void handleQuickDownloadAction(ActionEvent event) throws IOException, InterruptedException {

		com.musicdwl.threadHandles.SearchThread st = new com.musicdwl.threadHandles.SearchThread(getSearchField, songLabelText, albumArt,
				loadingImage, true, progressBar, playButton, pauseButton, leftSearch, rightSearch);
		st.start();

	}

	@FXML
	private void handleSearchAction(ActionEvent event) throws IOException, InterruptedException {

		if (downloadButton.disableProperty().get())
			downloadButton.disableProperty().setValue(false);
		if (getLyricsButton.disableProperty().get())
			getLyricsButton.disableProperty().setValue(false);
		if (SongControl.playerStatus == SongControl.PLAYING || SongControl.playerStatus == SongControl.PAUSED)
			sc.stop();

		com.musicdwl.threadHandles.SearchThread st = new com.musicdwl.threadHandles.SearchThread(getSearchField, songLabelText, albumArt,
				loadingImage, false, progressBar, playButton, pauseButton, leftSearch, rightSearch);
		st.start();

	}

	@FXML
	private void handleDownloadAction(ActionEvent event) throws IOException, InterruptedException {

		if (songLabelText.getText().isEmpty() || FXMLController.downloadList.size() < 1) {
			return;
		}

		downloadSong(progressBar);
	}

	public static void downloadSong(ProgressBar progressBar) throws IOException, InterruptedException {

		if (DownloadThread.downloading) {
			return;
		}
		com.musicdwl.threadHandles.DownloadThread dt = new com.musicdwl.threadHandles.DownloadThread(titleList.get(0), progressBar);
		dt.start();
	}

	@FXML
	private void handleCloseAction(ActionEvent event) {
		if (sc != null) {
			sc.stop();
			sc.close();
		}
		if (t != null)
			t.interrupt();
		if (task != null) {
			task.cancel();
		}
		if (exec != null)
			exec.shutdownNow();

		          Platform.exit();
	}

	@FXML
	private void handlePlayButton(ActionEvent event) throws MalformedURLException, IOException, JavaLayerException {

		if (exec == null)
			exec = Executors.newSingleThreadExecutor();
		task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				exec.submit(() -> {

					t = new Thread(new Runnable() {

						@Override
						public void run() {

							try {
								sc = new SongControl(streamList.get(fileCounter), songLabelText);

								currSong = fileCounter;

								sc.play();
								latch = new CountDownLatch(1);

								new Thread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										try {
											latch.await();
										} catch (InterruptedException e) {
										}
										Platform.runLater(new Runnable() {
											public void run() {

												pauseButton.setVisible(false);
												playButton.setVisible(true);
											}
										});

									}
								}).start();

							} catch (IOException | JavaLayerException e) {

								e.printStackTrace();
							}
						}
					});
					if (SongControl.playerStatus == SongControl.NOTSTARTED || currSong != fileCounter
							|| SongControl.playerStatus == SongControl.FINISHED) {
						t.start();

						Platform.runLater(new Runnable() {
							public void run() {

								pauseButton.setVisible(true);
								playButton.setVisible(false);

							}
						});
					}

					else if (currSong == fileCounter && SongControl.playerStatus == SongControl.PAUSED) {

						sc.resume();
						Platform.runLater(new Runnable() {
							public void run() {

								pauseButton.setVisible(true);
								playButton.setVisible(false);
							}
						});

					} /* else if(currSong!=fileCounter) */

				});
				return null;
			}

		};
		task.run();

	}

	@FXML
	private void handlePauseButton(ActionEvent event) throws JavaLayerException {
		if (sc != null)
			sc.pause();

		pauseButton.setVisible(false);
		playButton.setVisible(true);

	}

	@FXML
	private void handleLeftSearch(ActionEvent event) throws JavaLayerException, InterruptedException {
		rightSearch.disableProperty().setValue(true);
		leftSearch.disableProperty().setValue(true);
		playButton.disableProperty().setValue(true);
		loadingImage.disableProperty().setValue(true);
		if (fileCounter == 0) {
			fileCounter = titleList.size() - 1;
		} else {
			fileCounter--;
		}
		System.out.println(fileCounter);
		if (sc != null) {
			sc.stop();
			sc.close();
		}
		if (task != null) {
			task.cancel();
		}

		playButton.setVisible(true);
		pauseButton.setVisible(false);
		// songLabelText.setText("[" + qualityList.get(fileCounter) + "] " +
		// fullTitleList.get(fileCounter));
		songLabelText.setText("[" + artistList.get(fileCounter) + "] " + titleList.get(fileCounter));
		songLabelText.setTooltip(new Tooltip(artistList.get(fileCounter) + "-" + titleList.get(fileCounter)));
		String query = FXMLController.artistList.get(fileCounter) + "+" + FXMLController.titleList.get(fileCounter);
		try {
			boolean isValidSong = Connection.getiTunesSongInfo(query, songLabelText);
			if (isValidSong) {
				                        CoverArtThread cat = new CoverArtThread();
				SearchThread.image = null;
				cat.start();
				while (SearchThread.image == null)
					Thread.sleep(300);// countdowntimer
				loadingImage.disableProperty().setValue(false);
				albumArt.setImage(SearchThread.image);
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
		rightSearch.disableProperty().setValue(false);
		leftSearch.disableProperty().setValue(false);
		playButton.disableProperty().setValue(false);
	}

	@FXML
	private void handleRightSearch(ActionEvent event) throws JavaLayerException, InterruptedException {
		rightSearch.disableProperty().setValue(true);
		leftSearch.disableProperty().setValue(true);
		playButton.disableProperty().setValue(true);
		loadingImage.disableProperty().setValue(true);
		if (fileCounter == titleList.size() - 1) {
			fileCounter = 0;
		} else {
			fileCounter++;
		}
		System.out.println(fileCounter);
		if (sc != null) {
			sc.stop();
			sc.close();
		}
		if (task != null)
			task.cancel();

		playButton.setVisible(true);
		pauseButton.setVisible(false);

		songLabelText.setText("[" + artistList.get(fileCounter) + "] " + titleList.get(fileCounter));
		songLabelText.setTooltip(new Tooltip(artistList.get(fileCounter) + "-" + titleList.get(fileCounter)));
		String query = FXMLController.artistList.get(fileCounter) + "+" + FXMLController.titleList.get(fileCounter);
		try {
			boolean isValidSong = Connection.getiTunesSongInfo(query, songLabelText);
			if (isValidSong) {
				CoverArtThread cat = new CoverArtThread();
				SearchThread.image = null;
				cat.start();
				while (SearchThread.image == null)
					Thread.sleep(300);// countdowntimer
				loadingImage.disableProperty().setValue(false);
				albumArt.setImage(SearchThread.image);
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		rightSearch.disableProperty().setValue(false);
		leftSearch.disableProperty().setValue(false);
		playButton.disableProperty().setValue(false);
	}

	@FXML
	private void handleMinimizeAction(ActionEvent event) {
		Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
		stage.setIconified(true);
	}

	@FXML
	private void handleSettings(ActionEvent event) throws IOException {

		Platform.runLater(() -> {
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/settings.fxml"));
				Parent root = fxmlLoader.load();
				Stage stage = new Stage();

				stage.initModality(Modality.NONE);
				stage.initStyle(StageStyle.UTILITY);

				Scene scene = new Scene(root, 444, 159);
				scene.getStylesheets().add(getClass().getResource("/styles/Styles.css").toExternalForm());

				Rectangle rect = new Rectangle(444, 159);
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
		});

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		progressBar.setVisible(false);
		loadingImage.setVisible(false);
		getSearchField.setStyle("-fx-text-inner-color: #909090");

		setCoverArtGreyBlock();

		playButton.setVisible(false);
		pauseButton.setVisible(false);
		songLabelText.setEditable(false);
		rightSearch.setVisible(false);
		leftSearch.setVisible(false);

		getSearchButton.setDefaultButton(true);
		downloadButton.disableProperty().setValue(true);
		getLyricsButton.disableProperty().setValue(true);

		getSearchButton.disableProperty()
				.bind(Bindings.createBooleanBinding(
						() -> getSearchField.getText().trim().isEmpty() || getSearchField.getText().trim().length() < 2,
						getSearchField.textProperty()));

		BufferedImage image = null;
		try {
                         
			image = ImageIO.read(getClass().getResourceAsStream("/icon/placeholder.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
					Image test =convertToFxImage(image);/// SwingFXUtils.toFXImage(img, null);
		albumArt.setImage(test);

		/*
		 * getSearchField.setOnKeyPressed((KeyEvent ke) -> { if
		 * (ke.getCode().equals(KeyCode.ENTER)) { threadHandles.SearchThread st = new
		 * threadHandles.SearchThread(getSearchField, songLabelText, albumArt,
		 * loadingImage, false, progressBar, playButton, pauseButton, leftSearch,
		 * rightSearch); st.start(); } });
		 */

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

	private void addDragListeners(final Node n, Stage primaryStage) {

		n.setOnMousePressed((MouseEvent mouseEvent) -> {
			this.x = n.getScene().getWindow().getX() - mouseEvent.getScreenX();
			this.y = n.getScene().getWindow().getY() - mouseEvent.getScreenY();
		});

		n.setOnMouseDragged((MouseEvent mouseEvent) -> {
			primaryStage.setX(mouseEvent.getScreenX() + this.x);
			primaryStage.setY(mouseEvent.getScreenY() + this.y);
		});
	}

	public void reset() {
		pauseButton.setVisible(false);
		playButton.setVisible(true);
	}
        
       public static Image convertToFxImage(BufferedImage image) {
    WritableImage wr = null;
    if (image != null) {
        wr = new WritableImage(image.getWidth(), image.getHeight());
        PixelWriter pw = wr.getPixelWriter();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                pw.setArgb(x, y, image.getRGB(x, y));
            }
        }
    }

    return new ImageView(wr).getImage();
}

}
