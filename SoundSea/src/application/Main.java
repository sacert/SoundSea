package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.fxml.FXMLLoader;


public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
	
			Pane root = (Pane)FXMLLoader.load(getClass().getResource("FX.fxml"));
			Scene scene = new Scene(root,484,193);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			Rectangle rect = new Rectangle(484, 193);
			rect.setArcHeight(10);
			rect.setArcWidth(10);
			root.setClip(rect);
			primaryStage.setScene(scene);
			primaryStage.initStyle(StageStyle.TRANSPARENT);
			scene.setFill(Color.TRANSPARENT);
			primaryStage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws IOException, InterruptedException {

		launch(args);
		
	}
	
	
	
	
	

	
	
	
	
	
	
	
}
