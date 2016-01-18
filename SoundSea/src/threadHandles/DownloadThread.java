package threadHandles;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

import application.FXController;
import javafx.scene.control.ProgressBar;

public class DownloadThread extends Thread{
	
	private String songTitle;
	private ProgressBar progressBar;

	public DownloadThread(String songTitle, ProgressBar progressBar)
	{
		this.songTitle = songTitle;
		this.progressBar = progressBar;
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
			
			progressBar.setVisible(true);
			
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
	            	progressBar.setProgress(sumCount / size);
	            }
	        }
						
			Mp3File mp3file = new Mp3File(tmpDir + "/SongSea/temp.mp3");
			mp3file.removeId3v1Tag();
			mp3file.removeId3v2Tag();
			
			// insert metadata
			ID3v2 id3v2Tag = new ID3v24Tag();
			mp3file.setId3v2Tag(id3v2Tag);
			id3v2Tag.setArtist(FXController.bandArtist);
			id3v2Tag.setTitle(FXController.songTitle);
			if(FXController.albumTitle != "") 
				id3v2Tag.setAlbum(FXController.albumTitle);
			id3v2Tag.setYear(FXController.albumYear);
			id3v2Tag.setGenreDescription(FXController.genre);
			id3v2Tag.setAlbumImage(CoverArtThread.imageByte, "image/jpeg");
			
			mp3file.save(FXController.folderDirectory + songTitle +".mp3");

			Runtime.getRuntime().exec(new String[] {"rm", "-rf", tmpDir + "/SongSea"});
			
			progressBar.setVisible(false);
			progressBar.setProgress(0);
		} catch (IOException | UnsupportedTagException | InvalidDataException | NotSupportedException e) {
			e.printStackTrace();
		}
	}

}
