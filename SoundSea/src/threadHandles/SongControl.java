package threadHandles;

import java.net.MalformedURLException;
import java.net.URL;

import jaco.mp3.player.MP3Player;
import javazoom.jl.decoder.JavaLayerException;

public class SongControl extends Thread {

	static MP3Player mp3player;
	String song;
	
	public SongControl(MP3Player mp3player, String song) {
		SongControl.mp3player = mp3player;
		this.song = song;
	}

	public void run()
	{
		try {
			mp3player = new MP3Player(new URL(song));
			mp3player.play();
		} catch (MalformedURLException e) {
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
