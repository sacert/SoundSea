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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.musicdwl.SoundSea.FXMLController;
import javafx.scene.control.TextArea;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class SongControl {

	public final static int NOTSTARTED = 0;
	public final static int PLAYING = 1;
	public final static int PAUSED = 2;
	public final static int FINISHED = 3;

	// the player actually doing all the work
	public Player player;

	// locking object used to communicate with player thread
	private final Object playerLock = new Object();

	// status variable what player thread is doing/supposed to do
	public static int playerStatus = NOTSTARTED;

	public SongControl(String song,TextArea songLabelText) throws IOException{
		final URL DOWNLOAD_URL = new URL(song);
		HttpURLConnection request = (HttpURLConnection) DOWNLOAD_URL.openConnection();
		request.addRequestProperty("User-Agent",
				"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:96.0) Gecko/20100101 Firefox/96.0");
		// request.addRequestProperty("Accept", "application/json, text/javascript, */*;
		// q=0.01");
		request.addRequestProperty("Accept", "application/json");
		request.addRequestProperty("Accept-Language", "en-GB,en;q=0.5");
		// request.addRequestProperty("Accept-Encoding" ,"gzip, deflate, br");
	//	request.addRequestProperty("Referer", "https://datmusic.xyz/");
		//request.addRequestProperty("Origin", "https://datmusic.xyz");
	//	request.addRequestProperty("DNT", "1");
		request.addRequestProperty("Connection", "keep-alive");
		request.addRequestProperty("Pragma", "no-cache");
		request.addRequestProperty("Cache-Control", "no-cache");
		request.connect();
		playerStatus = NOTSTARTED;
		FXMLController.isFinished = false;

		try {
			player = new Player(new BufferedInputStream(request.getInputStream()));
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			songLabelText.setText("Error playing the song(server's fault)");
		}
	}

	/**
	 * Starts playback (resumes if paused)
	 */
	public void play() throws JavaLayerException {
		synchronized (playerLock) {
			switch (playerStatus) {
			case NOTSTARTED:
				final Runnable r = new Runnable() {
					public void run() {
						playInternal();
					}
				};
				final Thread t = new Thread(r);
				t.setDaemon(true);
				t.setPriority(Thread.MAX_PRIORITY);
				playerStatus = PLAYING;
				t.start();

				break;
			case PAUSED:
				resume();
				break;

			default:
				break;
			}
		}
	}

	/**
	 * Pauses playback. Returns true if new state is PAUSED.
	 */
	public boolean pause() {
		synchronized (playerLock) {
			if (playerStatus == PLAYING) {
				playerStatus = PAUSED;
			}
			return playerStatus == PAUSED;
		}
	}

	/**
	 * Resumes playback. Returns true if the new state is PLAYING.
	 */
	public boolean resume() {
		synchronized (playerLock) {
			if (playerStatus == PAUSED) {
				playerStatus = PLAYING;
				playerLock.notifyAll();
			}
			return playerStatus == PLAYING;
		}
	}

	/**
	 * Stops playback. If not playing, does nothing
	 */
	public void stop() {
		synchronized (playerLock) {
			playerStatus = FINISHED;
			playerLock.notifyAll();
		}
	}

	private void playInternal() {
		while (playerStatus != FINISHED) {
			try {
				if (!player.play(1)) {
					break;
				}
			} catch (final JavaLayerException e) {
				break;
			}
			// check if paused or terminated
			synchronized (playerLock) {
				while (playerStatus == PAUSED) {
					try {
						playerLock.wait();
					} catch (final InterruptedException e) {
						playerStatus = FINISHED;
						break;
					}
				}
			}
		}
		playerStatus = FINISHED;
		FXMLController.latch.countDown();
		close();
	}

	/**
	 * Closes the player, regardless of current state.
	 */
	public void close() {
		synchronized (playerLock) {
			playerStatus = FINISHED;
		}
		try {
			player.close();
		} catch (final Exception e) {
			// ignore, we are terminating anyway
		}
	}

}
