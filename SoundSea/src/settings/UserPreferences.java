package settings;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.io.File;

import application.FXController;

public class UserPreferences {

	static Preferences userPrefs = Preferences.userNodeForPackage(UserPreferences.class);
	static String osName = System.getProperty("os.name");

	public static void getPreferences() throws BackingStoreException {

		String[] keys = userPrefs.keys();
		if (keys == null || keys.length == 0) {
			userPrefs.put("qualityLevel", "high");
			userPrefs.put("folderDirectory",
					System.getProperty("user.home") + File.separatorChar + "Music" + File.separatorChar);
		}
		FXController.folderDirectory = userPrefs.get("folderDirectory", null);
		FXController.qualityLevel = userPrefs.get("qualityLevel", null);

	}

	public static void setDirectory(String dir) {
		userPrefs.put("folderDirectory", dir + File.separatorChar);
		FXController.folderDirectory = userPrefs.get("folderDirectory", null);
	}

	static void setQuality(String quality) {

		userPrefs.put("qualityLevel", quality);
		FXController.qualityLevel = userPrefs.get("qualityLevel", null);
	}
}
