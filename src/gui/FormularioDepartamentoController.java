package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import bancoDeDados.excexoes.BancoDeDadosException;
import gui.listeners.AlteracaoDeDadosListener;
import gui.util.Alertas;
import gui.util.Restricoes;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.exceptions.ValidacaoException;
import modelo.entidades.Departamento;
import modelo.servicos.DepartamentoService;

public class FormularioDepartamentoController implements Initializable {

	private Departamento departamento;

	private DepartamentoService servicoDepartamento;

	private List<AlteracaoDeDadosListener> ouvintesDeMudancaDeDados = new ArrayList<>();

	@FXML
	private TextField textoId;
	@FXML
	private TextField textoNome;
	@FXML
	private Label erro;
	@FXML
	private Button botaoSalvar;
	@FXML
	private Button botaoCancelar;

	@FXML
	public void acaoBotaoSalvar(ActionEvent evento) {
		if (departamento == null) {
			throw new IllegalStateException("Departamento nulo");
		}
		if (servicoDepartamento == null) {
			throw new IllegalStateException("Serviço nulo");
		}
		try {
			departamento = pegarDadosFormulario();
			servicoDepartamento.salvarAtualizarDepartamento(departamento);
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

	private Departamento pegarDadosFormulario() {
		Departamento departamento = new Departamento();
		ValidacaoException validacoes = new ValidacaoException("Erro de validação");
		departamento.setId(Utils.converterInteiro(textoId.getText()));
		if (textoNome.getText() == null || textoNome.getText().trim().equals("")) {
			validacoes.adicionarErro("Nome", "Campo não pode ser vazio");
		}
		departamento.setNome(textoNome.getText());
		if (validacoes.getErros().size() > 0) {
			throw validacoes;
		}
		return departamento;
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
		Restricoes.setTextFieldMaxLength(textoNome, 30);
	}

	public void atualizarDadosFormulario() {
		if (departamento == null) {
			throw new IllegalStateException("Departamento nulo");
		}
		textoId.setText(String.valueOf(departamento.getId()));
		textoNome.setText(departamento.getNome());
	}

	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}

	public void setServicoDepartamento(DepartamentoService servicoDepartamento) {
		this.servicoDepartamento = servicoDepartamento;
	}

	public void inscreverOuvinteDeMudancaDeDados(AlteracaoDeDadosListener ouvinte) {
		ouvintesDeMudancaDeDados.add(ouvinte);
	}

	private void informarErros(Map<String, String> erros) {
		Set<String> campos = erros.keySet();
		if (campos.contains("Nome")) {
			erro.setText(erros.get("Nome"));
		}
	}

}
