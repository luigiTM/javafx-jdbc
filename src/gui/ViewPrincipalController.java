package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alertas;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import modelo.servicos.DepartamentoService;
import modelo.servicos.VendedorService;

public class ViewPrincipalController implements Initializable {

	@FXML
	private MenuItem itemMenuVendedor;
	@FXML
	private MenuItem itemMenuDepartamento;
	@FXML
	private MenuItem itemMenuSobre;

	@FXML
	public void acaoItemMenuVendedor() {
		carregarView("/gui/ListaVendedor.fxml", (ListaVendedorController controlador) -> {
			controlador.setServicoVendedor(new VendedorService());
			controlador.atualizarTabela();
		});
	}

	@FXML
	public void acaoItemMenuDepartamento() {
		carregarView("/gui/ListaDepartamento.fxml", (ListaDepartamentoController controlador) -> {
			controlador.setServicoDepartamento(new DepartamentoService());
			controlador.atualizarTabela();
		});
	}

	@FXML
	public void acaoItemMenuSobre() {
		carregarView("/gui/Sobre.fxml", x -> {
		});
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}

	private synchronized <T> void carregarView(String caminhoAbsoluto, Consumer<T> inicializacao) {
		try {
			FXMLLoader carregador = new FXMLLoader(getClass().getResource(caminhoAbsoluto));
			VBox novoVBox = carregador.load();
			Scene cenaPrincipal = Main.getCenaPrincipal();
			VBox vboxPrincipal = (VBox) ((ScrollPane) cenaPrincipal.getRoot()).getContent();
			Node menuPrincipal = vboxPrincipal.getChildren().get(0);
			vboxPrincipal.getChildren().clear();
			vboxPrincipal.getChildren().add(menuPrincipal);
			vboxPrincipal.getChildren().addAll(novoVBox.getChildren());

			T controlador = carregador.getController();
			inicializacao.accept(controlador);

		} catch (IOException e) {
			Alertas.mostrarAlertas("IOException", "Erro carregando View", e.getMessage(), AlertType.ERROR);
		}
	}

}
