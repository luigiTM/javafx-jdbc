package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {

	private static Scene cenaPrincipal;

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ViewPrincipal.fxml"));
			ScrollPane painel = loader.load();
			painel.setFitToHeight(true);
			painel.setFitToWidth(true);

			cenaPrincipal = new Scene(painel);
			primaryStage.setScene(getCenaPrincipal());
			primaryStage.setTitle("Sample JavaFX application");
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static Scene getCenaPrincipal() {
		return cenaPrincipal;
	}
}
