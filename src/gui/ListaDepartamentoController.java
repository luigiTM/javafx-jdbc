package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import bancoDeDados.excexoes.BancoDeDadosIntegridadeException;
import gui.listeners.AlteracaoDeDadosListener;
import gui.util.Alertas;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.entidades.Departamento;
import modelo.servicos.DepartamentoService;

public class ListaDepartamentoController implements Initializable, AlteracaoDeDadosListener {

	@FXML
	private TableView<Departamento> tabelaDepartamento;
	@FXML
	private TableColumn<Departamento, Integer> colunaId;
	@FXML
	private TableColumn<Departamento, String> colunaNome;
	@FXML
	private TableColumn<Departamento, Departamento> tabelaEdicao;
	@FXML
	private TableColumn<Departamento, Departamento> tabelaDelecao;
	@FXML
	private Button botaoNovo;

	private DepartamentoService servicoDepartamento;

	private ObservableList<Departamento> departamentosObservaveis;

	@FXML
	public void acaoBotaoNovo(ActionEvent evento) {
		Stage palcoPrincipal = Utils.palcoAtual(evento);
		Departamento departamento = new Departamento();
		criarFormularioDeDialogo(departamento, palcoPrincipal, "/gui/FormularioDepartamento.fxml");
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
		inicializarBotoesDeEdicao();
		inicializarBotosDeRemocao();
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

	private void criarFormularioDeDialogo(Departamento departamento, Stage palcoPrincipal, String caminhoAbsoluto) {
		try {
			FXMLLoader carregador = new FXMLLoader(getClass().getResource(caminhoAbsoluto));
			Pane painel = carregador.load();
			FormularioDepartamentoController controlador = carregador.getController();
			controlador.setDepartamento(departamento);
			controlador.atualizarDadosFormulario();
			controlador.setServicoDepartamento(new DepartamentoService());
			controlador.inscreverOuvinteDeMudancaDeDados(this);
			Stage palcoDialogo = new Stage();
			palcoDialogo.setTitle("Cadastro de departamentos");
			palcoDialogo.setScene(new Scene(painel));
			palcoDialogo.setResizable(false);
			palcoDialogo.initOwner(palcoPrincipal);
			palcoDialogo.initModality(Modality.WINDOW_MODAL);
			palcoDialogo.showAndWait();
		} catch (IOException e) {
			Alertas.mostrarAlertas("IO Exception", "Erro carregando a view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void emMudancaDeDados() {
		atualizarTabela();
	}

	private void inicializarBotoesDeEdicao() {
		tabelaEdicao.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tabelaEdicao.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button botao = new Button("Editar");

			@Override
			protected void updateItem(Departamento departamento, boolean vazio) {
				super.updateItem(departamento, vazio);
				if (departamento == null) {
					setGraphic(null);
					return;
				}
				setGraphic(botao);
				botao.setOnAction(evento -> criarFormularioDeDialogo(departamento, Utils.palcoAtual(evento), "/gui/FormularioDepartamento.fxml"));
			}
		});
	}

	private void inicializarBotosDeRemocao() {
		tabelaDelecao.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tabelaDelecao.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button botao = new Button("Deletar");

			@Override
			protected void updateItem(Departamento departamento, boolean vazio) {
				super.updateItem(departamento, vazio);
				if (departamento == null) {
					setGraphic(null);
					return;
				}
				setGraphic(botao);
				botao.setOnAction(evento -> removerEntidade(departamento));
			}
		});
	}

	private void removerEntidade(Departamento departamento) {
		Optional<ButtonType> resultado = Alertas.mostrarConfirmacao("Confirmação", "Tem certeza que deseja deletar?");
		if (resultado.get() == ButtonType.OK) {
			if (servicoDepartamento == null) {
				throw new IllegalStateException("Serviço está nulo");
			}
			try {
				servicoDepartamento.deletar(departamento);
				atualizarTabela();
			} catch (BancoDeDadosIntegridadeException e) {
				Alertas.mostrarAlertas("Erro removendo objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
