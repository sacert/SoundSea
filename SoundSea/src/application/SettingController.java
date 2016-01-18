package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class SettingController implements Initializable{
	
	@FXML private Button fileChooser;
	@FXML private Button fileAccept;
	@FXML private Text directoryText;
	
	private File selectedDirectory;
	
	@FXML
	private void handleDirectory(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
        selectedDirectory = 
                directoryChooser.showDialog(null);
         
        if(selectedDirectory == null){
        	directoryText.setText("No Directory selected");
        }else{
        	directoryText.setText(selectedDirectory.getAbsolutePath());
        }
	}
	
	@FXML
	private void handleAccept(ActionEvent event) {
		UserPreferences.setDirectory(selectedDirectory.getAbsolutePath());
		Stage stage = (Stage) fileChooser.getScene().getWindow();
		stage.close();
	}
	
	@FXML
	private void handleClose(ActionEvent event) {
		Stage stage = (Stage) fileChooser.getScene().getWindow();
		stage.close();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		directoryText.setText(FXController.folderDirectory);
		
	}
}
