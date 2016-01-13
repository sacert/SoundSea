package threadHandles;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import application.FXController;

public class DownloadThread extends Thread{
	
	private String songTitle;

	public DownloadThread(String songTitle)
	{
		this.songTitle = songTitle;
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
			
			File file = new File(tmpDir + "/SongSea/temp.mp3");
			
			// download file
			URL url = new URL(FXController.fileList.get(0));
			FileUtils.copyURLToFile(url, file);
			
			Process ffca = null;
			
			// debating whether to use metadata from itunes or use the existing metadata from pleer
			//"title=" + FXController.songTitle, "-metadata", "artist=" + FXController.bandArtist, "-metadata", "album=" + FXController.albumTitle, "-metadata", "date=" + FXController.albumYear,
			ffca = Runtime.getRuntime().exec(new String[] {"/usr/local/bin/ffmpeg", "-i", tmpDir + "/SongSea/temp.mp3", "-i" , FXController.coverArtUrl, "-map", "0:0" ,"-map", "1:0", "-c", "copy", "-id3v2_version", "3", "/Users/" + userName + "/Desktop/" + songTitle +".mp3",});
			// wait for .mp4 file to be created
			ffca.waitFor();
			Runtime.getRuntime().exec(new String[] {"rm", "-rf", tmpDir + "/SongSea"});
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
