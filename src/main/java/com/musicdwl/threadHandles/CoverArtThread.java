/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musicdwl.threadHandles;

/**
 *
 * @author spectral369
 */

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import com.musicdwl.SoundSea.FXMLController;
import javafx.scene.image.Image;

public class CoverArtThread extends Thread {

	public static byte[] imageByte;

	@Override
	public void run() {
		try {
			// set cover art album image in window
			Image image;
			if (FXMLController.coverArtUrl.equals("") || FXMLController.coverArtUrl.isEmpty()) {
				BufferedImage img = ImageIO.read(getClass().getClassLoader().getResource("/icon/placeholder.png"));
				 
					Image test =FXMLController.convertToFxImage(img);/// SwingFXUtils.toFXImage(img, null);
				SearchThread.image = test;
			} else {

				URL coverArtUrl = new URL(FXMLController.coverArtUrl);
				BufferedImage img = ImageIO.read(coverArtUrl);

				if (img.getHeight() != img.getWidth()) {
					int newSize;
					if (img.getHeight() < img.getWidth()) {
						newSize = img.getHeight();
					} else {
						newSize = img.getWidth();
					}

					BufferedImage newImage = new BufferedImage(newSize, newSize, BufferedImage.TYPE_INT_RGB);

					Graphics g = newImage.createGraphics();
					g.drawImage(img, 0, 0, newSize, newSize, null);
					g.dispose();

					
					image =FXMLController.convertToFxImage(newImage);/// SwingFXUtils.toFXImage(img, null);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(newImage, "jpeg", baos);
					imageByte = baos.toByteArray();

				} else {
					
                                    
					image =FXMLController.convertToFxImage(img);// SwingFXUtils.toFXImage(img, null);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(img, "jpeg", baos);
					imageByte = baos.toByteArray();
				}

				SearchThread.image = image;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}