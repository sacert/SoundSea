package threadHandles;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
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
			
			long time = System.currentTimeMillis();
			
			File file = new File(tmpDir + "/SongSea/");
			file.mkdirs();
			
			long time1 = System.currentTimeMillis();
			// download file
			URL url = new URL(FXController.fileList.get(0));
			int size = url.openConnection().getContentLength();
			//FileUtils.copyURLToFile(url, file);
			
			BufferedInputStream in = new BufferedInputStream(url.openStream());
			FileOutputStream fout = new FileOutputStream(tmpDir + "/SongSea/temp.mp3");
			byte data[] = new byte[1024];
			int count;
			double sumCount = 0.0;
			time1 = System.currentTimeMillis() - time1;
	        while ((count = in.read(data, 0, 1024)) != -1) {
	            fout.write(data, 0, count);

	            sumCount += count;
	            if (size > 0) {
	                System.out.println("Percentace: " + (sumCount / size * 100.0) + "%");
	            }
	        }
			
			long time2 = System.currentTimeMillis();
			Process ffca = null;
			
			// debating whether to use metadata from itunes or use the existing metadata from pleer
			//"title=" + FXController.songTitle, "-metadata", "artist=" + FXController.bandArtist, "-metadata", "album=" + FXController.albumTitle, "-metadata", "date=" + FXController.albumYear,
			ffca = Runtime.getRuntime().exec(new String[] {"/usr/local/bin/ffmpeg", "-i", tmpDir + "/SongSea/temp.mp3", "-i" , FXController.coverArtUrl, "-map", "0:0" ,"-map", "1:0", "-c", "copy", "-id3v2_version", "3", "/Users/" + userName + "/Desktop/" + songTitle +".mp3",});
			// wait for .mp4 file to be created
			ffca.waitFor();
			Runtime.getRuntime().exec(new String[] {"rm", "-rf", tmpDir + "/SongSea"});
			time2 = System.currentTimeMillis() - time2;
			System.out.println(time1);
			System.out.println(time2);
			System.out.println(System.currentTimeMillis() - time);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
