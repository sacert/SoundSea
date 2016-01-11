package threadHandles;

import java.io.File;
import java.io.IOException;

import application.FXController;

public class DownloadThread extends Thread{
	
	private String songTitle, youtubeReference;

	public DownloadThread(String songTitle, String youtubeReference)
	{
		this.songTitle = songTitle;
		this.youtubeReference = youtubeReference;
	}

	@Override
	public void run()
	{
		// get system info to store temp folder + files
		String userName = System.getProperty("user.name");
		String tmpDir = System.getProperty("java.io.tmpdir");
		
		File path = new File(tmpDir + "/SongSea");
		
		// if tmp directory exists, delete it
		if(path.exists() && path.isDirectory()) {
			try {
				Runtime.getRuntime().exec(new String[] {"rm", "-rf", tmpDir + "/SongSea"});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			Process ytd = null;
			ytd = Runtime.getRuntime().exec(new String[] { "/usr/local/bin/youtube-dl", "--audio-quality", "0", "--output", tmpDir + "/SongSea/temp.mp4", "https://www.youtube.com/watch?v=" + youtubeReference});
			// wait for .mp4 file to be created
			ytd.waitFor();
			// begin ffmpeg conversion to .mp3
			Process ffmp = null;
			ffmp = Runtime.getRuntime().exec(new String[] {"/usr/local/bin/ffmpeg", "-i", tmpDir + "/SongSea/temp.mp4", "-vn", "-acodec", "libmp3lame", "-ac", "2", "-qscale:a", "4", "-ar", "48000", tmpDir + "/SongSea/temp.mp3"});
			// wait for .mp4 file to be created
			ffmp.waitFor();
			Process ffca = null;
			//ffmpeg32 -i in.mp3 -metadata title="The Title You Want" -metadata artist="Artist Name" -metadata album="Name of the Album" out.mp3
			ffca = Runtime.getRuntime().exec(new String[] {"/usr/local/bin/ffmpeg", "-i", tmpDir + "/SongSea/temp.mp3", "-i" , FXController.googleImgURLResults.get(FXController.imageIndex), "-metadata", "title=" + FXController.songTitle, "-metadata", "artist=" + FXController.bandArtist, "-metadata", "album=" + FXController.albumTitle, "-metadata", "date=" + FXController.albumYear, "-map", "0:0" ,"-map", "1:0", "-c", "copy", "-id3v2_version", "3", "/Users/" + userName + "/Desktop/" + songTitle +".mp3",});
			// wait for .mp4 file to be created
			ffca.waitFor();
			Runtime.getRuntime().exec(new String[] {"rm", "-rf", tmpDir + "/SongSea"});
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
