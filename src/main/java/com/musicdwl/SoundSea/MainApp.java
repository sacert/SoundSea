package com.musicdwl.SoundSea;

import java.io.IOException;
import java.util.prefs.BackingStoreException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {

	double x, y;

	private void addDragListeners(final Node n, Stage primaryStage) {

		n.setOnMousePressed((mouseEvent) -> {
			this.x = n.getScene().getWindow().getX() - mouseEvent.getScreenX();
			this.y = n.getScene().getWindow().getY() - mouseEvent.getScreenY();
		});

		n.setOnMouseDragged((mouseEvent) -> {
			primaryStage.setX(mouseEvent.getScreenX() + this.x);
			primaryStage.setY(mouseEvent.getScreenY() + this.y);
		});
	}

	@Override
	public void start(Stage primaryStage) {
		try {

			Pane root = (Pane) FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
			Scene scene = new Scene(root, 463, 198);

			//scene.getStylesheets().add(getClass().getResource("/styles/Styles.css").toExternalForm());
                        scene.getStylesheets().add("/styles/Styles.css");

			Rectangle rect = new Rectangle(463, 198);
			rect.setArcHeight(10);
			rect.setArcWidth(10);
			root.setClip(rect);
			primaryStage.setScene(scene);
			primaryStage.setTitle("SoundSea");
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon/placeholder.png")));
			primaryStage.initStyle(StageStyle.TRANSPARENT);
			scene.setFill(Color.TRANSPARENT);
			addDragListeners(root, primaryStage);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException, BackingStoreException {

		com.musicdwl.settings.UserPreferences.getPreferences();
		launch(args);

	}
}