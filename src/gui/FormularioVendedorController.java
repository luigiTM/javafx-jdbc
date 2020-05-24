package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import bancoDeDados.excexoes.BancoDeDadosException;
import gui.listeners.AlteracaoDeDadosListener;
import gui.util.Alertas;
import gui.util.Restricoes;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.exceptions.ValidacaoException;
import modelo.entidades.Departamento;
import modelo.entidades.Vendedor;
import modelo.servicos.DepartamentoService;
import modelo.servicos.VendedorService;

public class FormularioVendedorController implements Initializable {

	private Vendedor vendedor;

	private VendedorService servicoVendedor;

	private DepartamentoService departamentoServico;

	private List<AlteracaoDeDadosListener> ouvintesDeMudancaDeDados = new ArrayList<>();

	@FXML
	private TextField textoId;
	@FXML
	private TextField textoNome;
	@FXML
	private TextField textoEmail;
	@FXML
	private DatePicker dpDataNascimento;
	@FXML
	private TextField textoSalarioBase;
	@FXML
	private ComboBox<Departamento> comboBoxDepartamento;
	@FXML
	private Label erroNome;
	@FXML
	private Label erroEmail;
	@FXML
	private Label erroDataNascimento;
	@FXML
	private Label erroSalarioBase;
	@FXML
	private Button botaoSalvar;
	@FXML
	private Button botaoCancelar;

	private ObservableList<Departamento> listaDepartamentos;

	@FXML
	public void acaoBotaoSalvar(ActionEvent evento) {
		if (vendedor == null) {
			throw new IllegalStateException("Vendedor nulo");
		}
		if (servicoVendedor == null) {
			throw new IllegalStateException("Serviço nulo");
		}
		try {
			vendedor = pegarDadosFormulario();
			servicoVendedor.salvarAtualizarVendedor(vendedor);
			notificarOuvintesDeMudancaDeDados();
			Utils.palcoAtual(evento).close();
		} catch (BancoDeDadosException e) {
			Alertas.mostrarAlertas("Erro salvando objeto", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidacaoException e) {
			informarErros(e.getErros());
		}
	}

	private void notificarOuvintesDeMudancaDeDados() {
		for (AlteracaoDeDadosListener alteracaoDeDadosListener : ouvintesDeMudancaDeDados) {
			alteracaoDeDadosListener.emMudancaDeDados();
		}
	}

	private Vendedor pegarDadosFormulario() {
		Vendedor vendedor = new Vendedor();
		ValidacaoException validacoes = new ValidacaoException("Erro de validação");
		vendedor.setId(Utils.converterInteiro(textoId.getText()));
		if (textoNome.getText() == null || textoNome.getText().trim().equals("")) {
			validacoes.adicionarErro("Nome", "Campo não pode ser vazio");
		}
		vendedor.setNome(textoNome.getText());
		if (textoEmail.getText() == null || textoEmail.getText().trim().equals("")) {
			validacoes.adicionarErro("Email", "Campo não pode ser vazio");
		}
		vendedor.setEmail(textoEmail.getText());
		if (dpDataNascimento.getValue() == null) {
			validacoes.adicionarErro("DataNascimento", "Campo não pode ser vazio");
		} else {
			Instant instante = Instant.from(dpDataNascimento.getValue().atStartOfDay(ZoneId.systemDefault()));
			vendedor.setDataNascimento(Date.from(instante));
		}
		if (textoSalarioBase.getText() == null || textoSalarioBase.getText().trim().equals("")) {
			validacoes.adicionarErro("SalarioBase", "Campo não pode ser vazio");
		}
		vendedor.setSalarioBase(Utils.converterDouble(textoSalarioBase.getText()));
		vendedor.setDepartamento(comboBoxDepartamento.getValue());
		if (validacoes.getErros().size() > 0) {
			throw validacoes;
		}
		return vendedor;
	}

	@FXML
	public void acaoBotaoCancelar(ActionEvent evento) {
		Utils.palcoAtual(evento).close();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		inicialiarNodos();
	}

	private void inicialiarNodos() {
		Restricoes.setTextFieldDouble(textoId);
		Restricoes.setTextFieldMaxLength(textoNome, 70);
		Restricoes.setTextFieldDouble(textoSalarioBase);
		Restricoes.setTextFieldMaxLength(textoEmail, 60);
		Utils.formatarDatePicker(dpDataNascimento, "dd/MM/yyyy");
		inicializarComboBoxDepartmento();
	}

	public void atualizarDadosFormulario() {
		if (vendedor == null) {
			throw new IllegalStateException("Vendedor nulo");
		}
		textoId.setText(String.valueOf(vendedor.getId()));
		textoNome.setText(vendedor.getNome());
		textoEmail.setText(vendedor.getEmail());
		textoSalarioBase.setText(String.format("%.2f", vendedor.getSalarioBase()));
		if (vendedor.getDataNascimento() != null) {
			dpDataNascimento.setValue(LocalDate.ofInstant(vendedor.getDataNascimento().toInstant(), ZoneId.systemDefault()));
		}
		if (vendedor.getDepartamento() == null) {
			comboBoxDepartamento.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartamento.setValue(vendedor.getDepartamento());
		}
	}

	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}

	public void setServicos(VendedorService servicoVendedor, DepartamentoService departamentoServico) {
		this.servicoVendedor = servicoVendedor;
		this.departamentoServico = departamentoServico;
	}

	public void inscreverOuvinteDeMudancaDeDados(AlteracaoDeDadosListener ouvinte) {
		ouvintesDeMudancaDeDados.add(ouvinte);
	}

	private void informarErros(Map<String, String> erros) {
		Set<String> campos = erros.keySet();
		if (campos.contains("Nome")) {
			erroNome.setText(erros.get("Nome"));
		} else {
			erroNome.setText("");
		}
		if (campos.contains("Email")) {
			erroEmail.setText(erros.get("Email"));
		} else {
			erroEmail.setText("");
		}
		if (campos.contains("SalarioBase")) {
			erroSalarioBase.setText(erros.get("SalarioBase"));
		} else {
			erroSalarioBase.setText("");
		}
		if (campos.contains("DataNascimento")) {
			erroDataNascimento.setText(erros.get("DataNascimento"));
		} else {
			erroDataNascimento.setText("");
		}
	}

	public void carregarObjetosAssociados() {
		if (departamentoServico == null) {
			throw new IllegalStateException("Servoço Departamento nulo");
		}
		List<Departamento> departamentos = departamentoServico.buscarTodos();
		listaDepartamentos = FXCollections.observableArrayList(departamentos);
		comboBoxDepartamento.setItems(listaDepartamentos);
	}

	private void inicializarComboBoxDepartmento() {
		Callback<ListView<Departamento>, ListCell<Departamento>> factory = lv -> new ListCell<Departamento>() {
			@Override
			protected void updateItem(Departamento item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxDepartamento.setCellFactory(factory);
		comboBoxDepartamento.setButtonCell(factory.call(null));
	}

}
