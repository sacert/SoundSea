package settings;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import application.FXController;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class SettingController implements Initializable {

	@FXML
	private Button fileChooser;
	@FXML
	private Text directoryText;
	@FXML
	private RadioButton highQualityRadio;
	@FXML
	private RadioButton lowQualityRadio;
	@FXML
	private RadioButton VBRQualityRadio;

	private File selectedDirectory;
	private final ToggleGroup group = new ToggleGroup();

	@FXML
	private void handleDirectory(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		selectedDirectory = directoryChooser.showDialog(null);

		if (selectedDirectory == null) {
			directoryText.setText("No Directory selected");
		} else {
			directoryText.setText(selectedDirectory.getAbsolutePath());
		}
	}

	@FXML
	private void handleAccept(ActionEvent event) {
		if (selectedDirectory != null) {
			UserPreferences.setDirectory(selectedDirectory.getAbsolutePath());
		}
		Stage stage = (Stage) fileChooser.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void handleClose(ActionEvent event) {
		Stage stage = (Stage) fileChooser.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void onToggleHandle(ActionEvent event) {
		group.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) -> {
			if (group.selectedToggleProperty().toString().contains("High")) {
				UserPreferences.setQuality("high");
			} else if (group.selectedToggleProperty().toString().contains("Low")) {
				UserPreferences.setQuality("low");
			} else {
				UserPreferences.setQuality("VBR");
			}
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		directoryText.setText(FXController.folderDirectory);

		highQualityRadio.setToggleGroup(group);
		lowQualityRadio.setToggleGroup(group);
		VBRQualityRadio.setToggleGroup(group);

		switch (FXController.qualityLevel) {
		case "high":
			highQualityRadio.fire();
			break;
		case "low":
			lowQualityRadio.fire();
			break;
		default:
			VBRQualityRadio.fire();
			break;
		}

	}
}
