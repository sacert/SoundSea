package threadHandles;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import jaco.mp3.player.MP3Player;
import javazoom.jl.decoder.JavaLayerException;

public class SongControl extends Thread {

    static MP3Player mp3player;
    String song;

    public SongControl(MP3Player mp3player, String song) {
        SongControl.mp3player = mp3player;
        this.song = song;
    }

    @Override
    public void run() {

        try {
            final URL DOWNLOAD_URL = new URL("http://pleer.com/site_api/files/get_url?action=download&id=" + song);
            HttpURLConnection request = (HttpURLConnection) DOWNLOAD_URL.openConnection();
            request.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject rootobj = root.getAsJsonObject();
            JsonPrimitive songLink = rootobj.getAsJsonPrimitive("track_link");

            mp3player = new MP3Player(new URL(songLink.getAsString()));
            mp3player.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pauseSong() {
        mp3player.pause();
    }

    public static void resumeSong() throws JavaLayerException {
        mp3player.play();
    }

    public static void stopSong() {
        mp3player.stop();
    }
}
