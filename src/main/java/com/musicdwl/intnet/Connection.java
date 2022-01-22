/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musicdwl.intnet;

/**
 *
 * @author spectral369
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import com.musicdwl.SoundSea.FXMLController;
import javafx.scene.control.TextArea;

public class Connection {

	static Charset asciiEncoder = Charset.forName("UTF-8");//Charset.forName("US-ASCII");

	public static boolean getSongFromPleer(TextArea songLabelText, String song) throws MalformedURLException {


		String fullURLPath = "https://slider.kz/vk_auth.php?q="
				+ song.replace(" ", "+").replaceAll("[!@#$%^&*(){}:\"<>?]", "");

		final URL url = new URL(fullURLPath);
		HttpURLConnection request = null;

		try {
			request = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {

			e.printStackTrace();
		}

		
		request.addRequestProperty("User-Agent",
				"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:96.0) Gecko/20100101 Firefox/96.0");
		
		request.addRequestProperty("Accept-Language", "en-GB,en;q=0.5");

		// request.addRequestProperty("Origin", "https://datmusic.xyz");
		request.addRequestProperty("DNT", "1");
		
		request.addRequestProperty("Connection", "keep-alive");
		request.addRequestProperty("Pragma", "no-cache");
		
		request.addRequestProperty("Cache-Control", "no-cache");

		try {
			request.connect();

		} catch (IOException e) {

			e.printStackTrace();
		}

		JsonParser jp = new JsonParser();
		JsonElement root = null;

		try {

			InputStream in = request.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.isEmpty()) {
					result.append(line);

				}
			}

			root = jp.parse(result.toString());
		} catch (JsonIOException | JsonSyntaxException | IOException e) {
			e.printStackTrace();

			throw new IllegalStateException("Server error 504.");
			// return;
		}

		JsonObject rootobj = root.getAsJsonObject();
		JsonElement sts = rootobj.get("audios");// orig status

		// if (!sts.getAsString().equals("ok")) {
		if (sts.isJsonNull() || sts == null) {

			songLabelText.setText("Song not found !");
		} else {
			System.out.println("pirating and drinking rum...");

			JsonObject dataobj = rootobj.getAsJsonObject().getAsJsonObject("audios");

			Set<String> keys = dataobj.keySet();
			String key = keys.iterator().next();

			JsonArray data = dataobj.getAsJsonArray(key);

			List<JsonObject> songData = new ArrayList<>();
			for (int i = 0; i < data.size(); i++) {
				songData.add(data.get(i).getAsJsonObject());
			}

			List<String> artist = new ArrayList<>();
			List<String> title = new ArrayList<>();
			List<String> stream = new ArrayList<>();
			List<String> download = new ArrayList<>();
			// artist,title,duration,download,stream
			for (int i = 0; i < data.size(); i++) {
				artist.add(songData.get(i).get("tit_art").getAsString().trim().substring(0,
						songData.get(i).get("tit_art").getAsString().trim().indexOf(" - ")));
				title.add(songData.get(i).get("tit_art").getAsString().trim()
						.substring(songData.get(i).get("tit_art").getAsString().trim().indexOf(" - ") + 1));
				String url1 = null;
				try {
					url1 = URLEncoder.encode(songData.get(i).get("tit_art").getAsString().trim(), "UTF-8");
				} catch (UnsupportedEncodingException e) {

					url1 = songData.get(i).get("tit_art").getAsString().trim().replaceAll(" ", "%");
				}
				download.add("https://slider.kz/download/" + songData.get(i).get("id").getAsString().trim() + "/" + songData.get(i).get("duration").getAsString().trim()
						+ "/" + songData.get(i).get("url").getAsString().trim() + "/" + url1 + ".mp3?extra="
						+ songData.get(i).get("extra").toString().trim().substring(1,
								songData.get(i).get("extra").toString().trim().length() - 1));

				stream.add("https://slider.kz/download/" + songData.get(i).get("id").getAsString().trim() + "/" +songData.get(i).get("duration").getAsString().trim()
						+ "/" + songData.get(i).get("url").getAsString().trim() + "/" + url1 + ".mp3?extra="
						+ songData.get(i).get("extra").toString().trim().substring(1,
								songData.get(i).get("extra").toString().trim().length()));

			}

			FXMLController.artistList = (artist);
			FXMLController.titleList = (title);
			FXMLController.streamList = (stream);
			FXMLController.downloadList = (download);
		} // added last
		return true;
		
	}

	public static boolean getiTunesSongInfo(String songInfoQuery, TextArea songLabelText)
			throws MalformedURLException, IOException {

		String fullURLPath = "https://itunes.apple.com/search?term=" + songInfoQuery.replace(" ", "+");

		URL url = new URL(fullURLPath);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		request.connect();
		JsonParser jp = new JsonParser();
		JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent(), StandardCharsets.UTF_8));
		JsonObject rootobj = root.getAsJsonObject();
		// "resultCount":0,
		JsonElement resultCount = rootobj.get("resultCount");

		if (resultCount.getAsInt() < 1) {
			songLabelText.setText("Song not found");
			return false;
		} else {
			JsonArray arr = rootobj.getAsJsonArray("results");
			try {
				rootobj = arr.get(0).getAsJsonObject();
			} catch (IndexOutOfBoundsException e) {

				songLabelText.setText("Song not found");
			}

			int itunesIndex = 0;
			String test = "";
			do {
				try {
					rootobj = arr.get(itunesIndex).getAsJsonObject();
				} catch (IndexOutOfBoundsException e) {
					songLabelText.setText("Song not found");
				}
				test = (rootobj.get("kind").toString().replace("\"", ""));
				itunesIndex++;
			} while(!test.equals("song") && itunesIndex<3);//while (!test.equals("song"));

			itunesIndex--;

			// parse artist
			rootobj = arr.get(itunesIndex).getAsJsonObject();
			FXMLController.bandArtist = (rootobj.get("artistName").toString().replace("\"", ""));

			// parse song title
			rootobj = arr.get(itunesIndex).getAsJsonObject();
			FXMLController.songTitle = (rootobj.get("trackName").toString().replace("\"", ""));

			// parse album title
			rootobj = arr.get(itunesIndex).getAsJsonObject();
			FXMLController.albumTitle = (rootobj.get("collectionName").toString().replace("\"", ""));

			// parse year
			rootobj = arr.get(itunesIndex).getAsJsonObject();
			FXMLController.albumYear = (rootobj.get("releaseDate").toString().replace("\"", "").substring(0, 4));

			// parse genre
			rootobj = arr.get(itunesIndex).getAsJsonObject();
			FXMLController.genre = (rootobj.get("primaryGenreName").toString().replace("\"", ""));

			// parse cover art
			rootobj = arr.get(itunesIndex).getAsJsonObject();

			FXMLController.coverArtUrl = (rootobj.get("artworkUrl100").toString().replace("\"", "")
					.replace("100x100bb.jpg", "500x500bb.jpg"));

			FXMLController.songFullTitle = FXMLController.bandArtist + " - " + FXMLController.songTitle;
			return true;
		}

	}

	@SuppressWarnings("unused")
	private static String fetchIpFromAmazon() throws IOException {
		URL url = null;
		url = new URL("http://checkip.amazonaws.com/");
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(url.openStream()));
		try {
			return br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "Failed to get IP from Amazon";
	}

	@SuppressWarnings("unused")
	private static String titleFixer(String title) {
		title = title.substring(0, title.length());
		title = title.toLowerCase();
		title = toTitleCase(title);

		return title.replace("Lyrics", "").replace("  ", " ");
	}

	private static String toTitleCase(String givenString) {
		String[] arr = givenString.split(" ");
		StringBuilder sb = new StringBuilder();

		for (String str : arr) {
			sb.append(Character.toUpperCase(str.charAt(0))).append(str.substring(1)).append(" ");
		}
		return sb.toString().trim();
	}


}