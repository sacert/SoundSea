package application;


import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import org.json.*;

public class FXController implements Initializable {

	private final static String azlyrics = "azlyrics.com";
	private final static String youtube = "www.youtube.com";

	@FXML private TextArea lyricBox;
	@FXML private TextField getSearchField;
	@FXML private Button downloadButton;
	@FXML private Text songLabelText;
	@FXML private ImageView albumArt;
	
	public static String songFullTitle = "";
	public static String songTitle = "";
	public static String albumTitle = "";
	public static String bandArtist = "";
	public static String albumYear = "";
	
	public static List<String> googleImgURLResults = null;
	private List<String> YoutubeURL = new ArrayList<String>();
	private static List<String> imageURLs = new ArrayList<String>();

	@FXML
	private void handleQuickDownloadAction(ActionEvent event) throws IOException, InterruptedException  {
		
		// To do: add pop-up prompting the user for input
		if(getSearchField.getText().isEmpty()) {
			return;
		}

		String query = getSearchField.getText();

		List<String> googleURLResults = null;
		
		// get lyrics for song
		googleURLResults = googleSearchQueryResults(azlyrics,query);
		List<String> lyricsURL;
		lyricsURL = getSongLyricsFromAZLyrics(googleURLResults.get(0)); // Parse from the FIRST result.
		printLyricsToUI(lyricsURL);
		
		// get youtube link for song
		googleURLResults = googleSearchQueryResults(youtube,query);
		List<String> YoutubeURL = new ArrayList<String>(); // leave in a List so that in the future, we can look at which to download
		YoutubeURL.add(googleURLResults.get(0));
		YoutubeURL.set(0, YoutubeURL.get(0).replace("https://www.youtube.com/watch%3Fv%3D", "")); 
		googleImgURLResults = googleImageSearchQueryResults();
		songLabelText.setText(songFullTitle);
		downloadSong(YoutubeURL.get(0));
		
		albumArt.setImage(null);
		
		// set cover art album image in window
		URL coverArtUrl = new URL(imageURLs.get(0));
		BufferedImage img = ImageIO.read(coverArtUrl);
		Image image = SwingFXUtils.toFXImage(img, null);
		albumArt.setImage(image);
	}
	
	@FXML
	private void handleSearchAction(ActionEvent event) throws IOException, InterruptedException  {

		// To do: add pop-up prompting the user for input
		if(getSearchField.getText().isEmpty()) {
			return;
		}
		
		String query = getSearchField.getText();
		List<String> googleURLResults = null;
		
		// get lyrics for song
		googleURLResults = googleSearchQueryResults(azlyrics,query);
		List<String> lyricsURL;
		lyricsURL = getSongLyricsFromAZLyrics(googleURLResults.get(0)); // Parse from the FIRST result.
		printLyricsToUI(lyricsURL);
		
		// get youtube link for song
		googleURLResults = googleSearchQueryResults(youtube,query);
		YoutubeURL.add(googleURLResults.get(0));
		YoutubeURL.set(0, YoutubeURL.get(0).replace("https://www.youtube.com/watch%3Fv%3D", "")); 
		googleImgURLResults = googleImageSearchQueryResults();
		songLabelText.setText(songFullTitle);
		
		albumArt.setImage(null);

		// set cover art album image in window
		URL coverArtUrl = new URL(imageURLs.get(0));
		BufferedImage img = ImageIO.read(coverArtUrl);
		Image image = SwingFXUtils.toFXImage(img, null);
		albumArt.setImage(image);
	}
	
	@FXML
	private void handleDownloadAction(ActionEvent event) throws IOException, InterruptedException  {
		
		// To do: add pop-up prompting the user for input
		if(songLabelText.getText().isEmpty()) {
			return;
		}

		downloadSong(YoutubeURL.get(0));
	}
	
	static void downloadSong(String youtubeReference) throws IOException, InterruptedException {
		
		DownloadThread dt = new DownloadThread(songFullTitle, youtubeReference);
		dt.start();
		
		// do you need to join threads in Java? 
		// look into it later
	}

	static List<String> googleSearchQueryResults (String websiteToGetLyricsFrom, String lyricsQuery) throws IOException{

		final String usersIPAddres = fetchIpFromAmazon();
		URL url = new URL(
				"https://ajax.googleapis.com/ajax/services/search/web?v=1.0&"
						+ "q="
						+ lyricsQuery.replace(" ", "%20")
						+ "%20site:"
						+ websiteToGetLyricsFrom
						+ "&userip="
						+ usersIPAddres);

		URLConnection connection = url.openConnection();
		connection.addRequestProperty("Referer", "www.referrer.com");

		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while((line = reader.readLine()) != null) {
			builder.append(line);
		}

		JSONObject json = new JSONObject(builder.toString()).getJSONObject("responseData");


		List<String> azLyricsWebsites = new ArrayList<String>();

		JSONArray websiteLinksArray = (JSONArray) json.get("results");

		for(int i = 0 ; i < websiteLinksArray.length() ; i++){
			azLyricsWebsites.add(websiteLinksArray.getJSONObject(i).getString("url"));
		}
		return azLyricsWebsites;
	}
	
	static List<String> googleImageSearchQueryResults () throws IOException{

		albumTitle = albumTitle.replace("\"", "");
		albumYear = albumTitle.substring(albumTitle.length()-5, albumTitle.length()-1);
		albumTitle = albumTitle.substring(0, albumTitle.length()-7);		
		
		imageURLs.clear();
		URL url = new URL(
				"https://www.googleapis.com/customsearch/v1?key=AIzaSyCHoGF1u8RytvaNeCk69iyD9ouwFBmsndQ&cx=007547444199860528947:flga7fapbrm&q="
						+ bandArtist.replace(" ", "%20")
						+ albumTitle.replace(" ", "%20")
						+ "%20album%20cover%20art"
						+ "&userip=&searchType=image&alt=json");
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        while ((output = br.readLine()) != null) {
            if(output.contains("\"link\":")) {
            	imageURLs.add(output.substring(12, output.length()-2));
            }
        }
        
        conn.disconnect();

		return imageURLs;
	}

	public static List<String> getSongLyricsFromAZLyrics(String fullURLPath) throws IOException {

		List<String> lyrics = new ArrayList<String>();		
		Document doc = Jsoup.connect(fullURLPath).get();
		String title = titleFixer(doc.title());
		
		songFullTitle = title;
		
		String[] breakTitle = songFullTitle.split("-");
		bandArtist = breakTitle[0];
		songTitle = breakTitle[1];
		
		Element q = doc.select("div.album-panel").get(0).child(1);
		albumTitle = q.text();

		// get each line
		Element p = doc.select("div").get(22);
		for(Node e: p.childNodes()) {
			if(e instanceof TextNode) {
				lyrics.add(((TextNode)e).text() + "\n");
			}
		}
		// first line is a white space, remove it
		lyrics.remove(0);

		return lyrics;
	}

	public  void printLyricsToUI(List<String> lyrics) {

		for(int i = 0; i < lyrics.size(); i++) {
			//lyricBox.setText(lyricBox.getText() + lyrics.get(i));
		}
	}

	// Put into another class
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
		
		// shouldn't this be within the catch statement?
		return "Failed to get IP from Amazon";
	}
	
	private static String toTitleCase(String givenString) {
	    String[] arr = givenString.split(" ");
	    StringBuffer sb = new StringBuffer();

	    for (int i = 0; i < arr.length; i++) {
	        sb.append(Character.toUpperCase(arr[i].charAt(0)))
	            .append(arr[i].substring(1)).append(" ");
	    }          
	    return sb.toString().trim();
	}  
	
	private static String titleFixer(String title) {
		title = title.substring(0, title.length());
		title = title.toLowerCase();
		title = toTitleCase(title);
		
		title = title.replace("Lyrics", "");
		title = title.replace("  ", " ");
		
		return title;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		getSearchField.setStyle("-fx-text-inner-color: #909090");
		
		Rectangle clip = new Rectangle(albumArt.getFitWidth(), albumArt.getFitHeight());
		clip.setArcWidth(20);
		clip.setArcHeight(20);
		albumArt.setClip(clip);
		
		SnapshotParameters parameters = new SnapshotParameters();
		parameters.setFill(Color.rgb(241, 241, 241));
		WritableImage image = albumArt.snapshot(parameters, null);

		albumArt.setImage(image);
		
	}
	
}


