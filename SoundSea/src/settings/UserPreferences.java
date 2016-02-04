package settings;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import application.FXController;

public class UserPreferences {
	
	static Preferences userPrefs = Preferences.userNodeForPackage(UserPreferences.class);
	static String osName = System.getProperty("os.name");
	
	public static void getPreferences() throws BackingStoreException {
         String[] keys = userPrefs.keys();
         if (keys == null || keys.length == 0) {
 
        	 userPrefs.put("qualityLevel", "high");
        	 if(osName.contains("Windows")) 
        		 userPrefs.put("folderDirectory", System.getProperty("user.home") + "\\Music\\");
        	 else 
        		 userPrefs.put("folderDirectory", System.getProperty("user.home") + "/Music/");
        	 
         } 
         FXController.folderDirectory = userPrefs.get("folderDirectory", null);
         FXController.qualityLevel = userPrefs.get("qualityLevel", null);

	}
	
	static void setDirectory(String dir) {
		if(osName.contains("Windows")) 
			userPrefs.put("folderDirectory", dir + "\\");
   	 	else 
   	 		userPrefs.put("folderDirectory", dir + "/");
	
		FXController.folderDirectory = userPrefs.get("folderDirectory", null);
	}
	
	static void setQuality(String quality) {
		
		userPrefs.put("qualityLevel", quality);
		FXController.qualityLevel = userPrefs.get("qualityLevel", null);
	}
}
