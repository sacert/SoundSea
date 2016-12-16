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
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SearchThread extends Thread {

    public static Image image;

    private final TextField getSearchField;
    private final TextArea songLabelText;
    private final ImageView albumArt;
    private final ImageView loadingImage;
    private boolean quickDownload;
    private final ProgressBar progressBar;
    private final Button playButton;
    private final Button pauseButton;
    private final Button leftSearch;
    private final Button rightSearch;

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

    @Override
    public void run() {
        if (getSearchField.getText().isEmpty()) {
            return;
        }
        try {
            if (!FXController.fileList.isEmpty()) {
                FXController.fileList.remove(0);
            }

            if (!FXController.fullTitleList.isEmpty()) {
                FXController.fullTitleList.remove(0);
            }

            boolean validSong;
            image = null;
            // reset GUI view
            playButton.setVisible(false);
            pauseButton.setVisible(false);
            albumArt.setImage(FXController.greyImage);
            if (!"".equals(songLabelText.toString())) {
                songLabelText.setText("");
            }
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

            } catch (UnknownHostException uhe) {
                openMessage();
            } catch (NullPointerException e) {
                songLabelText.setText("Song not found");
            } 

            try {
                songLabelText.setText("[" + FXController.qualityList.get(0) + "] " + FXController.fullTitleList.get(0));
                validSong = true;
            } catch (IndexOutOfBoundsException e) {
                songLabelText.setText("Song not found");
                validSong = false;
            }

            if (validSong) {
                if (quickDownload) {
                    FXController.downloadSong(progressBar);
                }

                // if the cover art hasn't been displayed yet, spin until it has
                while (image == null) {
                    //spin
                }

                FXController.fileCounter = 0;

                albumArt.setImage(null);
                loadingImage.setVisible(false);
                albumArt.setImage(image);
                playButton.setVisible(true);
                rightSearch.setVisible(true);
                leftSearch.setVisible(true);

                if (FXController.songPlaying == true) {
                    FXController.songPlaying = false;
                    SongControl.stopSong();
                }
            } else {
                BufferedImage img = ImageIO.read(getClass().getClassLoader().getResource("resources/placeholder.png"));
                Image test = SwingFXUtils.toFXImage(img, null);
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

    private void openMessage() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Message.fxml"));
                    System.out.println(loader.getLocation().getPath());
                    Pane root1 = (Pane) loader.load();
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setScene(new Scene(root1));
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
