package application;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class UserPreferences {
	
	static Preferences userPrefs = Preferences.userNodeForPackage(UserPreferences.class);
	
	static void getPreferences() {
		 
	     try {
	         String[] keys = userPrefs.keys();
	          
	         if (keys == null || keys.length == 0) {
	        	 
	        	 String osName = System.getProperty("os.name");
	        	 
	        	 if(osName.contains("Windows")) 
	        		 userPrefs.put("folderDirectory", System.getProperty("user.home") + "\\Music\\");
	        	 else 
	        		 userPrefs.put("folderDirectory", System.getProperty("user.home") + "/Music/");
	        	 
	         } 
	         FXController.folderDirectory = userPrefs.get("folderDirectory", null);
	             
	         System.out.println(FXController.folderDirectory);
	     } catch (BackingStoreException ex) {
	         System.err.println(ex);
	     }
	}
	
	static void setDirectory(String dir) {
		userPrefs.put("folderDirectory", dir + "/");
		FXController.folderDirectory = userPrefs.get("folderDirectory", null);
	}
}
