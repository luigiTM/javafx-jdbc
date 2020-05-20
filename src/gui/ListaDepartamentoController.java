package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import modelo.entidades.Departamento;
import modelo.servicos.DepartamentoService;

public class ListaDepartamentoController implements Initializable {

	@FXML
	private TableView<Departamento> tabelaDepartamento;
	@FXML
	private TableColumn<Departamento, Integer> colunaId;
	@FXML
	private TableColumn<Departamento, String> colunaNome;
	@FXML
	private Button botaoNovo;

	private DepartamentoService servicoDepartamento;

	private ObservableList<Departamento> departamentosObservaveis;

	@FXML
	public void acaoBotaoNovo() {
		System.out.println("Botão");
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		incializarNodos();

	}

	public void atualizarTabela() {
		if (servicoDepartamento == null) {
			throw new IllegalStateException("Servico nulo");
		}
		List<Departamento> departamentos = servicoDepartamento.buscarTodos();
		departamentosObservaveis = FXCollections.observableArrayList(departamentos);
		tabelaDepartamento.setItems(departamentosObservaveis);
	}

	private void incializarNodos() {
		colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		Stage palco = (Stage) Main.getCenaPrincipal().getWindow();
		tabelaDepartamento.prefHeightProperty().bind(palco.heightProperty());
	}

	public void setServicoDepartamento(DepartamentoService servicoDepartamento) {
		this.servicoDepartamento = servicoDepartamento;
	}

}
