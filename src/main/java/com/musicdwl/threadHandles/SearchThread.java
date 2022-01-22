/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musicdwl.threadHandles;

/**
 *
 * @author spectral369
 */

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

import com.musicdwl.SoundSea.FXMLController;
import com.musicdwl.intnet.Connection;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

	public SearchThread(TextField getSearchField, TextArea songLabelText, ImageView albumArt, ImageView loadingImage,
			boolean quickDownload, ProgressBar progressBar, Button playButton, Button pauseButton, Button leftSearch,
			Button rightSearch) {
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
			if (!FXMLController.artistList.isEmpty()) {
				FXMLController.artistList.remove(0);
			}

			if (!FXMLController.titleList.isEmpty()) {
				FXMLController.titleList.remove(0);
			}

			boolean validSong = false;
			image = null;
			// reset GUI view
			playButton.setVisible(false);
			pauseButton.setVisible(false);
			albumArt.setImage(FXMLController.greyImage);
			if (!"".equals(songLabelText.toString())) {
				songLabelText.setText("");
			}
			loadingImage.setVisible(true);
			rightSearch.setVisible(false);
			leftSearch.setVisible(false);

			// parse itunes info for song
			String songInfoQuery = getSearchField.getText().replaceAll("[!@#$%^&*(){}:\"<>?]", "");
			try {

				boolean isSongFound = Connection.getSongFromPleer(songLabelText, songInfoQuery);

				boolean isValidSong = false;
				if (isSongFound) {
					String query = FXMLController.artistList.get(0).replace("-", "").trim() + " "
							+ FXMLController.titleList.get(0).replace("-", "").trim();

					isValidSong = Connection.getiTunesSongInfo(query, songLabelText);

				}

				// grab cover art image
				if (isValidSong) {
					CoverArtThread cat = new CoverArtThread();
					cat.start();
				}
				// get download link for song
				// if(isValidSong)

			} catch (UnknownHostException uhe) {
				openMessage();
			} catch (NullPointerException e) {
				songLabelText.setText("Song not found!");
			} catch (IllegalStateException ex) {
				songLabelText.setText("Server Side Error. Please try again later");
			}

			if (FXMLController.artistList.size() < 1) {
				// songLabelText.setText("Song not found or Error on server's side !");
				// if (FXController.bandArtist != "" || !FXController.bandArtist.isEmpty()) {

				while (image == null) {
					Thread.sleep(600);// countdowntimer

				}
				albumArt.setImage(image);
				loadingImage.setVisible(false);
				// }
			} else {
				// if the cover art hasn't been displayed yet, spin until it has
				while (image == null) {
					Thread.sleep(500);
				}
				try {
					songLabelText.setText("[" + FXMLController.artistList.get(0) + "] " + FXMLController.titleList.get(0));
					validSong = true;
				} catch (IndexOutOfBoundsException e) {
					songLabelText.setText("Song not found!!!!");
					validSong = false;
				}

				if (validSong) {
					if (quickDownload) {
						FXMLController.downloadSong(progressBar);
					}

					/*
					 * // if the cover art hasn't been displayed yet, spin until it has while (image
					 * == null) { Thread.sleep(500); }
					 */

					FXMLController.fileCounter = 0;

					albumArt.setImage(null);
					loadingImage.setVisible(false);
					albumArt.setImage(image);
					playButton.setVisible(true);
					rightSearch.setVisible(true);
					leftSearch.setVisible(true);

					if (SongControl.playerStatus == SongControl.PLAYING
							|| SongControl.playerStatus == SongControl.PAUSED
							|| SongControl.playerStatus == SongControl.FINISHED) {
						FXMLController.sc.stop();
						if (FXMLController.exec != null)
							FXMLController.exec.shutdownNow();
						FXMLController.task.cancel();
						FXMLController.exec = null;

					}
				} else {

					BufferedImage img = ImageIO
							.read(getClass().getClassLoader().getResource("/icon/placeholder.png"));
                                    
					Image test =FXMLController.convertToFxImage(img);// SwingFXUtils.toFXImage(img, null);
					albumArt.setImage(test);
					loadingImage.setVisible(false);
					rightSearch.setVisible(false);
					leftSearch.setVisible(false);
				}
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
					// System.out.println(loader.getLocation().getPath());
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
