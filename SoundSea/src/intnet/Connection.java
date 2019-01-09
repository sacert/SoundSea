package intnet;

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

import application.FXController;
import javafx.scene.control.TextArea;

public class Connection {

	static Charset asciiEncoder = Charset.forName("US-ASCII");

	public static boolean getSongFromPleer(TextArea songLabelText, String song) throws MalformedURLException {

		/*
		 * String bandArtist = deAccent(FXController.bandArtist); String songTitle =
		 * deAccent(FXController.songTitle);
		 * 
		 * if (FXController.bandArtist.contains("feat")) { bandArtist =
		 * FXController.bandArtist.split("feat")[0]; } if
		 * (FXController.songTitle.contains("feat")) { songTitle =
		 * FXController.songTitle.split("feat")[0]; } if
		 * (FXController.bandArtist.contains("Feat")) { bandArtist =
		 * FXController.bandArtist.split("feat")[0]; } if
		 * (FXController.songTitle.contains("Feat")) { songTitle =
		 * FXController.songTitle.split("feat")[0]; }
		 */
		/// old
		// String fullURLPath =
		/// "http://www.pleer.com/browser-extension/search?limit=100&q=" +
		/// bandArtist.replace(" ", "+").replaceAll("[!@#$%^&*(){}:\"<>?]", "") + "+" +
		/// songTitle.replace(" ", "+").replaceAll("[!@#$%^&*(){}:\"<>?]",
		/// "").replaceAll("\\[.*\\]", "");
		/// new1
		/*
		 * String fullURLPath = "https://api-2.datmusic.xyz/search?q=" +
		 * bandArtist.replace(" ", "+").replaceAll("[!@#$%^&*(){}:\"<>?]", "") + "+" +
		 * songTitle.replace(" ", "+").replaceAll("[!@#$%^&*(){}:\"<>?]",
		 * "").replaceAll("\\[.*\\]", "") + "&page=0";
		 */

		/*
		 * String fullURLPath = "https://slider.kz/vk_auth.php?q=" +
		 * bandArtist.replace(" ", "+").replaceAll("[!@#$%^&*(){}:\"<>?]", "")+ "+" +
		 * songTitle.replace(" ", "+").replaceAll("[!@#$%^&*(){}:\"<>?]",
		 * "").replaceAll("\\[.*\\]", "");
		 */
		String fullURLPath = "https://slider.kz/vk_auth.php?q="
				+ song.replace(" ", "+").replaceAll("[!@#$%^&*(){}:\"<>?]", "");

		final URL url = new URL(fullURLPath);

		HttpURLConnection request = null;

		try {
			request = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {

			e.printStackTrace();
		}

		/// old
		// request.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT
		// 6.1; en-GB; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR
		// 3.5.30729)");
		// new
		/// on my dev machine
		// request.addRequestProperty("User-Agent",
		// "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:59.0) Gecko/20100101
		/// Firefox/59.0");
		request.addRequestProperty("User-Agent",
				"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:64.0) Gecko/20100101 Firefox/64.0");
		// request.addRequestProperty("Accept", "application/json, text/javascript, */*;
		// q=0.01");

		// request.addRequestProperty("Accept", "application/json");
		request.addRequestProperty("Accept-Language", "en-GB,en;q=0.5");

		// request.addRequestProperty("Accept-Encoding" ,"gzip, deflate");
		///// request.addRequestProperty("Referer", "https://datmusic.xyz/");
		// request.addRequestProperty("Origin", "https://datmusic.xyz");
		request.addRequestProperty("DNT", "1");
		// request.addRequestProperty("Host","slider.kz");
		request.addRequestProperty("Connection", "keep-alive");
		request.addRequestProperty("Pragma", "no-cache");
		// request.addRequestProperty("Upgrade-Insecure-Requests","1");
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
				download.add("http://slider.kz/download/" + songData.get(i).get("id").getAsString().trim() + "/" + key
						+ "/" + songData.get(i).get("url").getAsString().trim() + "/" + url1 + ".mp3?extra="
						+ songData.get(i).get("extra").toString().trim().substring(1,
								songData.get(i).get("extra").toString().trim().length() - 1));

				stream.add("http://slider.kz/download/" + songData.get(i).get("id").getAsString().trim() + "/" + key
						+ "/" + songData.get(i).get("url").getAsString().trim() + "/" + url1 + ".mp3?extra="
						+ songData.get(i).get("extra").toString().trim().substring(1,
								songData.get(i).get("extra").toString().trim().length() - 1));

			}

			FXController.artistList = (artist);
			FXController.titleList = (title);
			FXController.streamList = (stream);
			FXController.downloadList = (download);
		} // added last
		return true;
		/*
		 * 
		 * List<String> fileList = new ArrayList<>(); List<String> fullTitleList = new
		 * ArrayList<>(); List<String> qualityList = new ArrayList<>();
		 * 
		 * Pattern p = Pattern.compile("^[\\x20-\\x7d]*$");
		 * 
		 * Matcher m3 = Pattern.compile("rework",
		 * Pattern.CASE_INSENSITIVE).matcher(rootobj.get("track").toString()); Matcher
		 * m4 = Pattern.compile("remix",
		 * Pattern.CASE_INSENSITIVE).matcher(rootobj.get("track").toString()); Matcher
		 * m5 = Pattern.compile("rework",
		 * Pattern.CASE_INSENSITIVE).matcher(rootobj.get("track").toString()); Matcher
		 * m6 = Pattern.compile("cover",
		 * Pattern.CASE_INSENSITIVE).matcher(rootobj.get("track").toString()); Matcher
		 * m8 = Pattern.compile("rework",
		 * Pattern.CASE_INSENSITIVE).matcher(rootobj.get("artist").toString()); Matcher
		 * m9 = Pattern.compile("remix",
		 * Pattern.CASE_INSENSITIVE).matcher(rootobj.get("artist").toString()); Matcher
		 * m10 = Pattern.compile("rework",
		 * Pattern.CASE_INSENSITIVE).matcher(rootobj.get("artist").toString()); Matcher
		 * m11 = Pattern.compile("cover",
		 * Pattern.CASE_INSENSITIVE).matcher(rootobj.get("artist").toString()); Matcher
		 * m13 = Pattern.compile("mix",
		 * Pattern.CASE_INSENSITIVE).matcher(rootobj.get("track").toString());
		 * 
		 * for (int i = 0; i < arr.size(); i++) { rootobj =
		 * arr.get(i).getAsJsonObject(); Matcher m1 =
		 * p.matcher(rootobj.get("artist").toString().replace("\"", "").replace("[",
		 * "~").replace("]", "~")); Matcher m2 =
		 * p.matcher(rootobj.get("track").toString().replace("\"", "").replace("[",
		 * "~").replace("]", "~"));
		 * 
		 * Boolean correctArtist = false; Boolean artistInTrack = false;
		 * 
		 * if (rootobj.get("artist").toString().toLowerCase().contains(bandArtist.
		 * toLowerCase().replaceAll("\\[.*\\]", "").replaceAll("\\(.*\\)", ""))) {
		 * correctArtist = true; }
		 * 
		 * if
		 * (rootobj.get("track").toString().toLowerCase().contains(songTitle.toLowerCase
		 * ().replaceAll("\\[.*\\]", "").replaceAll("\\(.*\\)", "").replaceAll("  ",
		 * " "))) { artistInTrack = true; }
		 * 
		 * // make sure to get only songs that aren't modifications if (m1.find() &&
		 * m2.find() && !m3.find() && !m4.find() && !m5.find() && !m6.find() &&
		 * !m8.find() && !m9.find() && !m10.find() && !m11.find() && !m13.find() &&
		 * correctArtist && artistInTrack) { // searching through either high or low
		 * quality songs, depending on the setting that has been set if
		 * ((rootobj.get("bitrate").toString().contains("VBR") ||
		 * !(Integer.parseInt(rootobj.get("bitrate").toString().substring(1, 2)) >= 4))
		 * && !rootobj.get("bitrate").toString().substring(1, 4).contains(" ")) { switch
		 * (FXController.qualityLevel) { case "high": if
		 * (!rootobj.get("bitrate").toString().contains("VBR") &&
		 * Integer.parseInt(rootobj.get("bitrate").toString().substring(1, 4)) >= 256) {
		 * System.out.println("high");
		 * fileList.add(rootobj.get("id").toString().replace("\"", ""));
		 * fullTitleList.add(rootobj.get("artist").toString().replace("\"", "") + " - "
		 * + rootobj.get("track").toString().replace("\"", ""));
		 * qualityList.add("High"); } break; case "low": if
		 * (!rootobj.get("bitrate").toString().contains("VBR") &&
		 * Integer.parseInt(rootobj.get("bitrate").toString().substring(1, 4)) < 256) {
		 * System.out.println("low");
		 * fileList.add(rootobj.get("id").toString().replace("\"", ""));
		 * fullTitleList.add(rootobj.get("artist").toString().replace("\"", "") + " - "
		 * + rootobj.get("track").toString().replace("\"", "")); qualityList.add("Low");
		 * } break; case "VBR": if (rootobj.get("bitrate").toString().contains("VBR")) {
		 * System.out.println("VBR");
		 * fileList.add(rootobj.get("id").toString().replace("\"", ""));
		 * fullTitleList.add(rootobj.get("artist").toString().replace("\"", "") + " - "
		 * + rootobj.get("track").toString().replace("\"", "")); qualityList.add("VBR");
		 * } break; default: break; } } } }
		 * 
		 * for (int i = 0; i < arr.size(); i++) { rootobj =
		 * arr.get(i).getAsJsonObject(); Matcher m1 =
		 * p.matcher(rootobj.get("artist").toString().replace("\"", "").replace("[",
		 * "~").replace("]", "~")); Matcher m2 =
		 * p.matcher(rootobj.get("track").toString().replace("\"", "").replace("[",
		 * "~").replace("]", "~"));
		 * 
		 * Boolean correctArtist = false; Boolean artistInTrack = false;
		 * 
		 * if (rootobj.get("artist").toString().toLowerCase().contains(bandArtist.
		 * toLowerCase().replaceAll("\\[.*\\]", "").replaceAll("\\(.*\\)", ""))) {
		 * correctArtist = true; }
		 * 
		 * if
		 * (rootobj.get("track").toString().toLowerCase().contains(songTitle.toLowerCase
		 * ().replaceAll("\\[.*\\]", "").replaceAll("\\(.*\\)", "").replaceAll("  ",
		 * " "))) { artistInTrack = true; }
		 * 
		 * // make sure to get only songs that aren't modifications if (m1.find() &&
		 * m2.find() && !m3.find() && !m4.find() && !m5.find() && !m6.find() &&
		 * !m8.find() && !m9.find() && !m10.find() && !m11.find() && !m13.find() &&
		 * correctArtist && artistInTrack) { // searching through either high or low
		 * quality songs, depending on the setting that has been set if
		 * ((rootobj.get("bitrate").toString().contains("VBR") ||
		 * !(Integer.parseInt(rootobj.get("bitrate").toString().substring(1, 2)) >= 4))
		 * && !rootobj.get("bitrate").toString().substring(1, 4).contains(" ")) { if
		 * (FXController.qualityLevel.equals("low") ||
		 * FXController.qualityLevel.equals("VBR")) { if
		 * (!rootobj.get("bitrate").toString().contains("VBR") &&
		 * Integer.parseInt(rootobj.get("bitrate").toString().substring(1, 4)) >= 256) {
		 * System.out.println("high");
		 * fileList.add(rootobj.get("id").toString().replace("\"", ""));
		 * fullTitleList.add(rootobj.get("artist").toString().replace("\"", "") + " - "
		 * + rootobj.get("track").toString().replace("\"", ""));
		 * qualityList.add("High"); } } else if
		 * (FXController.qualityLevel.equals("high")) { if
		 * (!rootobj.get("bitrate").toString().contains("VBR") &&
		 * Integer.parseInt(rootobj.get("bitrate").toString().substring(1, 4)) < 256) {
		 * System.out.println("low");
		 * fileList.add(rootobj.get("id").toString().replace("\"", ""));
		 * fullTitleList.add(rootobj.get("artist").toString().replace("\"", "") + " - "
		 * + rootobj.get("track").toString().replace("\"", "")); qualityList.add("Low");
		 * } } } } }
		 * 
		 * for (int i = 0; i < arr.size(); i++) { rootobj =
		 * arr.get(i).getAsJsonObject(); Matcher m1 =
		 * p.matcher(rootobj.get("artist").toString().replace("\"", "").replace("[",
		 * "~").replace("]", "~")); Matcher m2 =
		 * p.matcher(rootobj.get("track").toString().replace("\"", "").replace("[",
		 * "~").replace("]", "~"));
		 * 
		 * Boolean correctArtist = false; Boolean artistInTrack = false;
		 * 
		 * if (rootobj.get("artist").toString().toLowerCase().contains(bandArtist.
		 * toLowerCase().replaceAll("\\[.*\\]", "").replaceAll("\\(.*\\)", ""))) {
		 * correctArtist = true; }
		 * 
		 * if
		 * (rootobj.get("track").toString().toLowerCase().contains(songTitle.toLowerCase
		 * ().replaceAll("\\[.*\\]", "").replaceAll("\\(.*\\)",
		 * "").replaceAll("\\(.*\\)", "").replaceAll("  ", " "))) { artistInTrack =
		 * true; }
		 * 
		 * // make sure to get only songs that aren't modifications if (m1.find() &&
		 * m2.find() && !m3.find() && !m4.find() && !m5.find() && !m6.find() &&
		 * !m8.find() && !m9.find() && !m10.find() && !m11.find() && !m13.find() &&
		 * correctArtist && artistInTrack) { // searching through either high or low
		 * quality songs, depending on the setting that has been set if
		 * ((rootobj.get("bitrate").toString().contains("VBR") ||
		 * !(Integer.parseInt(rootobj.get("bitrate").toString().substring(1, 2)) >= 4))
		 * && !rootobj.get("bitrate").toString().substring(1, 4).contains(" ")) { if
		 * (FXController.qualityLevel.equals("VBR")) { if
		 * (!rootobj.get("bitrate").toString().contains("VBR") &&
		 * Integer.parseInt(rootobj.get("bitrate").toString().substring(1, 4)) < 256) {
		 * System.out.println("low");
		 * fileList.add(rootobj.get("id").toString().replace("\"", ""));
		 * fullTitleList.add(rootobj.get("artist").toString().replace("\"", "") + " - "
		 * + rootobj.get("track").toString().replace("\"", "")); qualityList.add("Low");
		 * } } else if (FXController.qualityLevel.equals("low") ||
		 * FXController.qualityLevel.equals("high")) { if
		 * (rootobj.get("bitrate").toString().contains("VBR")) {
		 * System.out.println("VBR");
		 * fileList.add(rootobj.get("id").toString().replace("\"", ""));
		 * fullTitleList.add(rootobj.get("artist").toString().replace("\"", "") + " - "
		 * + rootobj.get("track").toString().replace("\"", "")); qualityList.add("VBR");
		 * } } } } }
		 * 
		 * FXController.fileList = (fileList); FXController.fullTitleList =
		 * (fullTitleList); FXController.qualityList = qualityList;
		 */
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
			FXController.bandArtist = (rootobj.get("artistName").toString().replace("\"", ""));

			// parse song title
			rootobj = arr.get(itunesIndex).getAsJsonObject();
			FXController.songTitle = (rootobj.get("trackName").toString().replace("\"", ""));

			// parse album title
			rootobj = arr.get(itunesIndex).getAsJsonObject();
			FXController.albumTitle = (rootobj.get("collectionName").toString().replace("\"", ""));

			// parse year
			rootobj = arr.get(itunesIndex).getAsJsonObject();
			FXController.albumYear = (rootobj.get("releaseDate").toString().replace("\"", "").substring(0, 4));

			// parse genre
			rootobj = arr.get(itunesIndex).getAsJsonObject();
			FXController.genre = (rootobj.get("primaryGenreName").toString().replace("\"", ""));

			// parse cover art
			rootobj = arr.get(itunesIndex).getAsJsonObject();

			FXController.coverArtUrl = (rootobj.get("artworkUrl100").toString().replace("\"", "")
					.replace("100x100bb.jpg", "500x500bb.jpg"));

			FXController.songFullTitle = FXController.bandArtist + " - " + FXController.songTitle;
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
		StringBuffer sb = new StringBuffer();

		for (String str : arr) {
			sb.append(Character.toUpperCase(str.charAt(0))).append(str.substring(1)).append(" ");
		}
		return sb.toString().trim();
	}

	/*
	 * private static String deAccent(String str) { String nfdNormalizedString =
	 * Normalizer.normalize(str, Normalizer.Form.NFD); Pattern pattern =
	 * Pattern.compile("\\p{InCombiningDiacriticalMarks}+"); return
	 * pattern.matcher(nfdNormalizedString).replaceAll(""); }
	 */

}
