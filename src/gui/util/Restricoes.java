package gui.util;

import javafx.scene.control.TextField;

public class Restricoes {

	public static void setTextFieldInteger(TextField campoTexto) {
		campoTexto.textProperty().addListener((observadorervador, valorAntigo, valorNovo) -> {
			if (valorNovo != null && !valorNovo.matches("\\d*")) {
				campoTexto.setText(valorAntigo);
			}
		});
	}

	public static void setTextFieldMaxLength(TextField campoTexto, int max) {
		campoTexto.textProperty().addListener((observador, valorAntigo, valorNovo) -> {
			if (valorNovo != null && valorNovo.length() > max) {
				campoTexto.setText(valorAntigo);
			}
		});

	}

	public static void setTextFieldDouble(TextField campoTexto) {
		campoTexto.textProperty().addListener((observador, valorAntigo, valorNovo) -> {
			if (valorNovo != null && !valorNovo.matches("\\d*([\\.]\\d*)?")) {
				campoTexto.setText(valorAntigo);
			}
		});
	}
}
