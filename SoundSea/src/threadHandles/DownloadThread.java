package threadHandles;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v23Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import application.FXController;
import javafx.scene.control.ProgressBar;

public class DownloadThread extends Thread {

    private final ProgressBar progressBar;
    public  static boolean downloading;

    public DownloadThread(String songTitle, ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void run() {
        downloading = true;
        System.getProperty("user.name");
        String tmpDir = System.getProperty("java.io.tmpdir");

        File path = new File(tmpDir + "/SongSea");

        // if tmp directory exists, delete it
        if (path.exists() && path.isDirectory()) {
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

            // download file
            final URL url = new URL("http://pleer.com/browser-extension/files/" + FXController.fileList.get(FXController.fileCounter) + ".mp3");

            URLConnection urlConnection = url.openConnection();
            urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
            urlConnection.connect();

            int size = urlConnection.getContentLength();
            //FileUtils.copyURLToFile(url, file);

            System.out.println(size);

            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
            FileOutputStream fout = new FileOutputStream(tmpDir + "/SongSea/temp.mp3");
            byte data[] = new byte[1024];
            int count;
            double sumCount = 0.0;

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
            if (!"".equals(FXController.albumTitle)) {
                id3v2Tag.setAlbum(albumTitle);
            }
            id3v2Tag.setYear(albumYear);
            try {
                id3v2Tag.setGenreDescription(genre);
            } catch (IllegalArgumentException e) {
                System.err.println("Can't set genre");
            }
            id3v2Tag.setAlbumImage(coverArt, "image/jpeg");

            mp3file.save(FXController.folderDirectory + songTitle + ".mp3");

            new File(tmpDir + "/SongSea").delete();

            progressBar.setVisible(false);
            progressBar.setProgress(0);
            downloading = false;

        } catch (IOException | UnsupportedTagException | InvalidDataException | NotSupportedException e) {
            e.printStackTrace();
        }
    }

}
