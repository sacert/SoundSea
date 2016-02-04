package threadHandles;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v23Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

import application.FXController;
import javafx.scene.control.ProgressBar;

public class DownloadThread extends Thread{
	
	private ProgressBar progressBar;
	public static boolean downloading;

	public DownloadThread(String songTitle, ProgressBar progressBar)
	{
		this.progressBar = progressBar;
	}

	@Override
	public void run()
	{
		downloading = true;
		System.getProperty("user.name");
		String tmpDir = System.getProperty("java.io.tmpdir");
		
		File path = new File(tmpDir + "/SongSea");
		
		// if tmp directory exists, delete it
		if(path.exists() && path.isDirectory()) {
			new File(tmpDir + "/SongSea").delete();
		}
		
		try {
			String bandArtist = FXController.bandArtist;
			String songTitle = FXController.songTitle;
			String albumTitle = FXController.albumTitle;
			String albumYear = FXController.albumYear;
			String genre = FXController.genre;
			byte[] coverArt = CoverArtThread.imageByte;
			
			progressBar.setVisible(true);
			
			File file = new File(tmpDir + "/SongSea/");
			file.mkdirs();
			
			long time1 = System.currentTimeMillis();
			// download file
			URL url = new URL(FXController.fileList.get(FXController.fileCounter));
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
			ID3v2 id3v2Tag = new ID3v23Tag();
			mp3file.setId3v2Tag(id3v2Tag);
			id3v2Tag.setArtist(bandArtist);
			id3v2Tag.setTitle(songTitle);
			if(FXController.albumTitle != "") 
				id3v2Tag.setAlbum(albumTitle);
			id3v2Tag.setYear(albumYear);
			try {
				id3v2Tag.setGenreDescription(genre);
			} catch(IllegalArgumentException e) {
				System.out.println("Can't set genre");
			}
			id3v2Tag.setAlbumImage(coverArt, "image/jpeg");
			
			mp3file.save(FXController.folderDirectory + songTitle +".mp3");

			new File(tmpDir + "/SongSea").delete();
			
			progressBar.setVisible(false);
			progressBar.setProgress(0);
			downloading = false;

		} catch (IOException | UnsupportedTagException | InvalidDataException | NotSupportedException e) {
			e.printStackTrace();
		}
	}

}
