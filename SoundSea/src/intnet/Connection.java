package intnet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import application.FXController;

public class Connection {
	
	public static void getSongFromPleer() throws IOException {

		String fullURLPath = "http://www.pleer.com/browser-extension/search?q=%" + FXController.bandArtist.replaceAll("[^a-zA-Z0-9 ]+", "").replace(" ", "+") + "+" +FXController.songTitle.replace(" ", "+");
		
		System.out.println(fullURLPath);
		
		URL url = new URL(fullURLPath);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		request.connect();
		
		JsonParser jp = new JsonParser();
		JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
		JsonObject rootobj = root.getAsJsonObject();
		JsonArray arr = rootobj.getAsJsonArray("tracks");
		rootobj = arr.get(0).getAsJsonObject();
		
		List<String> fileList = new ArrayList<String>();
		List<String> fullTitleList = new ArrayList<String>();
		
		for(int i = 0; i < arr.size(); i++) {
			rootobj = arr.get(i).getAsJsonObject();
			fileList.add(rootobj.get("file").toString().replace("\"", ""));
		}
		
		FXController.fileList = fileList;
		
		for(int i = 0; i < arr.size(); i++) {
			rootobj = arr.get(i).getAsJsonObject();
			fullTitleList.add(rootobj.get("artist").toString().replace("\"", "") + " - " + rootobj.get("track").toString().replace("\"", ""));
		}
		
		FXController.fullTitleList = fullTitleList;
	}

	public static void getiTunesSongInfo(String songInfoQuery) throws IOException {

		String fullURLPath = "https://itunes.apple.com/search?term=" + songInfoQuery.replace(" ", "+");
		
		URL url = new URL(fullURLPath);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		request.connect();
		
		JsonParser jp = new JsonParser();
		JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
		JsonObject rootobj = root.getAsJsonObject();
		JsonArray arr = rootobj.getAsJsonArray("results");
		rootobj = arr.get(0).getAsJsonObject();
		
		System.out.println(arr);
		
		// parse artist
		rootobj = arr.get(0).getAsJsonObject();
		FXController.bandArtist = (rootobj.get("artistName").toString().replace("\"", ""));
		
		// parse song title
		rootobj = arr.get(0).getAsJsonObject();
		FXController.songTitle = (rootobj.get("trackName").toString().replace("\"", ""));
		
		// parse cover art
		rootobj = arr.get(0).getAsJsonObject();
		FXController.coverArtUrl = (rootobj.get("artworkUrl100").toString().replace("\"", "").replace("100x100bb.jpg", "500x500bb.jpg"));
		
		FXController.songFullTitle = FXController.bandArtist + " - " + FXController.songTitle;
		
	} 
}
