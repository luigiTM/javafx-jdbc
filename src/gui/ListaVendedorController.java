package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
import modelo.entidades.Vendedor;
import modelo.servicos.DepartamentoService;
import modelo.servicos.VendedorService;

public class ListaVendedorController implements Initializable, AlteracaoDeDadosListener {

	@FXML
	private TableView<Vendedor> tabelaVendedor;
	@FXML
	private TableColumn<Vendedor, Integer> colunaId;
	@FXML
	private TableColumn<Vendedor, String> colunaNome;
	@FXML
	private TableColumn<Vendedor, String> colunaEmail;
	@FXML
	private TableColumn<Vendedor, Date> colunaDataNascimento;
	@FXML
	private TableColumn<Vendedor, Double> colunaSalarioBase;
	@FXML
	private TableColumn<Vendedor, Vendedor> tabelaEdicao;
	@FXML
	private TableColumn<Vendedor, Vendedor> tabelaDelecao;
	@FXML
	private Button botaoNovo;

	private VendedorService servicoVendedor;

	private ObservableList<Vendedor> vendedorsObservaveis;

	@FXML
	public void acaoBotaoNovo(ActionEvent evento) {
		Stage palcoPrincipal = Utils.palcoAtual(evento);
		Vendedor vendedor = new Vendedor();
		criarFormularioDeDialogo(vendedor, palcoPrincipal, "/gui/FormularioVendedor.fxml");
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		incializarNodos();

	}

	public void atualizarTabela() {
		if (servicoVendedor == null) {
			throw new IllegalStateException("Servico nulo");
		}
		List<Vendedor> vendedors = servicoVendedor.buscarTodos();
		vendedorsObservaveis = FXCollections.observableArrayList(vendedors);
		tabelaVendedor.setItems(vendedorsObservaveis);
		inicializarBotoesDeEdicao();
		inicializarBotosDeRemocao();
	}

	private void incializarNodos() {
		colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		colunaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		colunaDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
		Utils.formatarTableColumnData(colunaDataNascimento, "dd/MM/yyyy");
		colunaSalarioBase.setCellValueFactory(new PropertyValueFactory<>("salarioBase"));
		Utils.formatarTableColumnDouble(colunaSalarioBase, 2);
		Stage palco = (Stage) Main.getCenaPrincipal().getWindow();
		tabelaVendedor.prefHeightProperty().bind(palco.heightProperty());
	}

	public void setServicoVendedor(VendedorService servicoVendedor) {
		this.servicoVendedor = servicoVendedor;
	}

	private void criarFormularioDeDialogo(Vendedor vendedor, Stage palcoPrincipal, String caminhoAbsoluto) {
		try {
			FXMLLoader carregador = new FXMLLoader(getClass().getResource(caminhoAbsoluto));
			Pane painel = carregador.load();
			FormularioVendedorController controlador = carregador.getController();
			controlador.setVendedor(vendedor);
			controlador.atualizarDadosFormulario();
			controlador.setServicos(new VendedorService(), new DepartamentoService());
			controlador.carregarObjetosAssociados();
			controlador.inscreverOuvinteDeMudancaDeDados(this);
			Stage palcoDialogo = new Stage();
			palcoDialogo.setTitle("Cadastro de vendedors");
			palcoDialogo.setScene(new Scene(painel));
			palcoDialogo.setResizable(false);
			palcoDialogo.initOwner(palcoPrincipal);
			palcoDialogo.initModality(Modality.WINDOW_MODAL);
			palcoDialogo.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alertas.mostrarAlertas("IO Exception", "Erro carregando a view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void emMudancaDeDados() {
		atualizarTabela();
	}

	private void inicializarBotoesDeEdicao() {
		tabelaEdicao.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tabelaEdicao.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button botao = new Button("Editar");

			@Override
			protected void updateItem(Vendedor vendedor, boolean vazio) {
				super.updateItem(vendedor, vazio);
				if (vendedor == null) {
					setGraphic(null);
					return;
				}
				setGraphic(botao);
				botao.setOnAction(evento -> criarFormularioDeDialogo(vendedor, Utils.palcoAtual(evento), "/gui/FormularioVendedor.fxml"));
			}
		});
	}

	private void inicializarBotosDeRemocao() {
		tabelaDelecao.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tabelaDelecao.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button botao = new Button("Deletar");

			@Override
			protected void updateItem(Vendedor vendedor, boolean vazio) {
				super.updateItem(vendedor, vazio);
				if (vendedor == null) {
					setGraphic(null);
					return;
				}
				setGraphic(botao);
				botao.setOnAction(evento -> removerEntidade(vendedor));
			}
		});
	}

	private void removerEntidade(Vendedor vendedor) {
		Optional<ButtonType> resultado = Alertas.mostrarConfirmacao("Confirmação", "Tem certeza que deseja deletar?");
		if (resultado.get() == ButtonType.OK) {
			if (servicoVendedor == null) {
				throw new IllegalStateException("Serviço está nulo");
			}
			try {
				servicoVendedor.deletar(vendedor);
				atualizarTabela();
			} catch (BancoDeDadosIntegridadeException e) {
				Alertas.mostrarAlertas("Erro removendo objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
