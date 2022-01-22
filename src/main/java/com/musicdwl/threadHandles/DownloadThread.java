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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v23Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

import javafx.scene.control.ProgressBar;
import com.musicdwl.SoundSea.FXMLController;

public class DownloadThread extends Thread {

	private final ProgressBar progressBar;
	public static boolean downloading;
	private BufferedInputStream in;
	private int size;

	public DownloadThread(String songTitle, ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	@SuppressWarnings("static-access")
	@Override
	public void run() {
		downloading = true;
		System.getProperty("user.name");
		String tmpDir = System.getProperty("java.io.tmpdir");

		File path = new File(tmpDir + "/SongSea");

		// if tmp directory exists, delete it
		if (path.exists() && path.isDirectory()) {
			new File(tmpDir + "/SongSea").delete();
		}

		try {
			String bandArtist = FXMLController.bandArtist;
			String songTitle = FXMLController.songTitle;
			String albumTitle = FXMLController.albumTitle;
			String albumYear = FXMLController.albumYear;
			String genre = FXMLController.genre;
			byte[] coverArt = CoverArtThread.imageByte;

			progressBar.setVisible(true);

			File file = new File(tmpDir + "/SongSea/");
			file.mkdirs();

			// download file
			final URL url = new URL(FXMLController.downloadList.get(FXMLController.fileCounter));
		
			// URLConnection urlConnection = url.openConnection();
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			HttpURLConnection.setFollowRedirects(true);
			urlConnection.setFollowRedirects(true);
			urlConnection.addRequestProperty("User-Agent",
					"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:96.0) Gecko/20100101 Firefox/96.0");
			//urlConnection.addRequestProperty("Accept", "application/json, text/javascript, *//*;q=0.01");

		//	urlConnection.setRequestProperty("Content-Type", "application/json");
			// urlConnection.addRequestProperty("Accept", "application/json");
			urlConnection.addRequestProperty("Accept-Language", "en-GB,en;q=0.5");
			//urlConnection.addRequestProperty("Accept-Encoding", "gzip, deflate, br");
			//urlConnection.addRequestProperty("Host", "api-2.datmusic.xyz");
			//urlConnection.addRequestProperty("Origin", "https://datmusic.xyz");
			//urlConnection.addRequestProperty("Referer", "https://datmusic.xyz/");

			urlConnection.addRequestProperty("DNT", "1");
			urlConnection.addRequestProperty("Connection", "keep-alive");
			urlConnection.addRequestProperty("Pragma", "no-cache");
			urlConnection.addRequestProperty("Cache-Control", "no-cache");
			//urlConnection.setRequestMethod("GET");// test

			urlConnection.connect();
			int responseCode = urlConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {

				size = urlConnection.getContentLength();
				in = new BufferedInputStream(urlConnection.getInputStream());
			} else {

				size = urlConnection.getContentLength();
				// FileUtils.copyURLToFile(url, file);

				String ur = urlConnection.getURL().toExternalForm();
			
				URL url2 = new URL(ur);//ur
                                System.out.println(url2);
				urlConnection.disconnect();
				URLConnection downloadURL = url2.openConnection();

				downloadURL.addRequestProperty("User-Agent",
						"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:96.0) Gecko/20100101 Firefox/96.0");
				downloadURL.addRequestProperty("Accept", "application/json, text/javascript, *//*;q=0.01");

				// downloadURL.setRequestProperty("Content-Type", "application/json");
				// urlConnection.addRequestProperty("Accept", "application/json");
				downloadURL.setRequestProperty("Content-Type", "File");
				downloadURL.addRequestProperty("Accept-Language", "en-GB,en;q=0.5");
				downloadURL.addRequestProperty("Accept-Encoding", "gzip, deflate, br");
				//downloadURL.addRequestProperty("Host", "api-2.datmusic.xyz");
			//	downloadURL.addRequestProperty("Origin", "https://datmusic.xyz");
			//	downloadURL.addRequestProperty("Referer", "https://datmusic.xyz/");

				downloadURL.addRequestProperty("DNT", "1");
				downloadURL.addRequestProperty("Connection", "keep-alive");
				downloadURL.addRequestProperty("Pragma", "no-cache");
				downloadURL.addRequestProperty("Cache-Control", "no-cache");
                        
				downloadURL.connect();
				
				/*int size = downloadURL.getContentLength();
				System.out.println(size);*/

				in = new BufferedInputStream(downloadURL.getInputStream());
			}

			if (in.available() > 0) {
				try (FileOutputStream fout = new FileOutputStream(tmpDir + "/SongSea/temp.mp3")) {
                                    byte data[] = new byte[1024];
                                    int count;
                                    double sumCount = 0.0;
                                    while ((count = in.read(data, 0, 1024)) != -1) {
                                        fout.write(data, 0, count);
                                        
                                        sumCount += count;
                                        if (size > 0) {
                                            progressBar.setProgress(sumCount / size);
                                        }
                                    }
                                    Mp3File mp3file = new Mp3File(tmpDir + "/SongSea/temp.mp3");
                                    mp3file.removeId3v1Tag();
                                    mp3file.removeId3v2Tag();
                                    // insert metadata
                                    ID3v2 id3v2Tag = new ID3v23Tag();
                                    mp3file.setId3v2Tag(id3v2Tag);
                                    id3v2Tag.setArtist(bandArtist);
                                    id3v2Tag.setTitle(songTitle);
                                    if (!"".equals(FXMLController.albumTitle)) {
                                        id3v2Tag.setAlbum(albumTitle);
                                    }
                                    id3v2Tag.setYear(albumYear);
                                    try {
                                        id3v2Tag.setGenreDescription(genre);
                                    } catch (IllegalArgumentException e) {
                                        System.err.println("Can't set genre");
                                    }
                                    id3v2Tag.setAlbumImage(coverArt, "image/jpeg");
                                    mp3file.save(FXMLController.folderDirectory +bandArtist+"-"+ songTitle + ".mp3");
                                    new File(tmpDir + "/SongSea").delete();
                                    progressBar.setVisible(false);
                                    progressBar.setProgress(0);
                                    downloading = false;
				}
				in.close();
			//	this.interrupt();//test
			}
		} catch (IOException | UnsupportedTagException | InvalidDataException | NotSupportedException e) {
			e.printStackTrace();
		}
	}

}
