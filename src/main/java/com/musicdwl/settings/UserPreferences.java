/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musicdwl.settings;

/**
 *
 * @author spectral369
 */

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.io.File;

import com.musicdwl.SoundSea.FXMLController;

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
		FXMLController.folderDirectory = userPrefs.get("folderDirectory", null);
		FXMLController.qualityLevel = userPrefs.get("qualityLevel", null);

	}

	public static void setDirectory(String dir) {
		userPrefs.put("folderDirectory", dir + File.separatorChar);
		FXMLController.folderDirectory = userPrefs.get("folderDirectory", null);
	}

	static void setQuality(String quality) {

		userPrefs.put("qualityLevel", quality);
		FXMLController.qualityLevel = userPrefs.get("qualityLevel", null);
	}
}
