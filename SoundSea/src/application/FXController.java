package application;


import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.json.*;

public class FXController implements Initializable {

	public final static String azlyrics = "azlyrics.com";
	public final static String youtube = "www.youtube.com";

	@FXML private TextArea lyricBox;
	@FXML private TextField getSearchField;
	@FXML private Button getSearchButton;
	@FXML private Button downloadButton;
	@FXML private Text songLabelText;
	@FXML private ImageView albumArt;
	@FXML private Pane searchPopup;
	@FXML private ImageView loadingImage;
	
	public static String songFullTitle = "";
	public static String songTitle = "";
	public static String albumTitle = "";
	public static String bandArtist = "";
	public static String albumYear = "";
	
	public static List<String> googleImgURLResults = null;
	public static List<String> YoutubeURL = new ArrayList<String>();
	public static List<String> imageURLs = new ArrayList<String>();
	
	public static int imageIndex = 0;

	@FXML
	private void handleQuickDownloadAction(ActionEvent event) throws IOException, InterruptedException  {
		
		SearchThread st = new SearchThread(getSearchField, songLabelText, albumArt, loadingImage, true);
		st.start();
	}
	
	@FXML
	private void handleSearchAction(ActionEvent event) throws IOException, InterruptedException  {
		
		SearchThread st = new SearchThread(getSearchField, songLabelText, albumArt, loadingImage, false);
		st.start();
	}
	
	@FXML
	private void handleDownloadAction(ActionEvent event) throws IOException, InterruptedException  {
		
		if(songLabelText.getText().isEmpty()) {
			return;
		}
		downloadSong(YoutubeURL.get(0));
	}
	
	static void downloadSong(String youtubeReference) throws IOException, InterruptedException {
		
		DownloadThread dt = new DownloadThread(songFullTitle, youtubeReference);
		dt.start();
		YoutubeURL.clear();
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

		if(albumTitle != songTitle) {
			albumTitle = albumTitle.replace("\"", "");
			albumYear = albumTitle.substring(albumTitle.length()-5, albumTitle.length()-1);
			albumTitle = albumTitle.substring(0, albumTitle.length()-7);	
		}
		
		imageURLs.clear();
		URL url = new URL(
				"https://www.googleapis.com/customsearch/v1?key=AIzaSyCHoGF1u8RytvaNeCk69iyD9ouwFBmsndQ&cx=007547444199860528947:flga7fapbrm&q="
						+ bandArtist.replace(" ", "%20")
						+ albumTitle.replace(" ", "%20")
						+ "%20"
						+ albumYear.replace(" ", "%20")
						+ "%20album%20cover%20art"
						+ "&userip=&searchType=image&alt=json");
		
		if(albumTitle == songTitle) {
			albumTitle = "";
		}
		
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
		
		String[] breakTitle = songFullTitle.split(" - ");
		bandArtist = breakTitle[0];
		songTitle = breakTitle[1];
		
		// if album title not shown on azlyrics, set album title to blank
		if(doc.select("div.album-panel").size() != 0) {
			Element q = doc.select("div.album-panel").get(0).child(1);
			albumTitle = q.text();
		}
		else {
			albumTitle = songTitle;
		}

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

	public static  void printLyricsToUI(List<String> lyrics) {

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
	
	@FXML
	private void handleCloseAction(ActionEvent event) {
		System.exit(0);
	}
	
	@FXML
	private void handleMinimizeAction(ActionEvent event) {
		Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
		stage.setIconified(true);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		loadingImage.setVisible(false);
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


