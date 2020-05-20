package modelo.dao.implementacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import bancoDeDados.BancoDeDados;
import bancoDeDados.excexoes.BancoDeDadosException;
import modelo.dao.DepartamentoDao;
import modelo.entidades.Departamento;

public class DepartamentoDaoJdbc implements DepartamentoDao {

	private Connection conexao;

	public DepartamentoDaoJdbc(Connection conexao) {
		this.conexao = conexao;
	}

	@Override
	public void inserir(Departamento departamento) {
		PreparedStatement declaracaoPreparada = null;
		try {
			declaracaoPreparada = conexao.prepareStatement("INSERT INTO departamento (Nome)  VALUES  (?)", Statement.RETURN_GENERATED_KEYS);
			declaracaoPreparada.setString(1, departamento.getNome());
			Integer linhasIncluidas = declaracaoPreparada.executeUpdate();
			if (linhasIncluidas > 0) {
				ResultSet resultado = declaracaoPreparada.getGeneratedKeys();
				if (resultado.next()) {
					Integer id = resultado.getInt(1);
					departamento.setId(id);
				}
				BancoDeDados.fecharResultado(resultado);
			} else {
				throw new BancoDeDadosException("Erro! Nenhuma linha incluída");
			}
		} catch (SQLException e) {
			throw new BancoDeDadosException(e.getMessage());
		} finally {
			BancoDeDados.fecharDeclaracao(declaracaoPreparada);
		}
	}

	@Override
	public void atualizar(Departamento departamento) {
		PreparedStatement declaracaoPreparada = null;
		try {
			declaracaoPreparada = conexao.prepareStatement("UPDATE departamento SET Nome = ? WHERE Id = ? ");
			declaracaoPreparada.setString(1, departamento.getNome());
			declaracaoPreparada.setInt(2, departamento.getId());
			declaracaoPreparada.executeUpdate();
		} catch (SQLException e) {
			throw new BancoDeDadosException(e.getMessage());
		} finally {
			BancoDeDados.fecharDeclaracao(declaracaoPreparada);
		}
	}

	@Override
	public void deletarPorId(Integer id) {
		PreparedStatement declaracaoPreparada = null;
		try {
			declaracaoPreparada = conexao.prepareStatement("DELETE FROM departamento WHERE Id = ?");
			declaracaoPreparada.setInt(1, id);
			declaracaoPreparada.executeUpdate();
		} catch (SQLException e) {
			throw new BancoDeDadosException(e.getMessage());
		} finally {
			BancoDeDados.fecharDeclaracao(declaracaoPreparada);
		}
	}

	@Override
	public Departamento buscarPorId(Integer id) {
		PreparedStatement declaracaoPreparada = null;
		ResultSet resultado = null;
		try {
			declaracaoPreparada = conexao.prepareStatement("SELECT * FROM departamento WHERE Id = ? ORDER BY Nome");
			declaracaoPreparada.setInt(1, id);
			resultado = declaracaoPreparada.executeQuery();
			if (resultado.next()) {
				Departamento departamento = instanciarDepartamento(resultado);
				return departamento;
			}
			return null;
		} catch (SQLException e) {
			throw new BancoDeDadosException(e.getMessage());
		} finally {
			BancoDeDados.fecharDeclaracao(declaracaoPreparada);
			BancoDeDados.fecharResultado(resultado);
		}
	}

	@Override
	public List<Departamento> buscarTodos() {
		PreparedStatement declaracaoPreparada = null;
		ResultSet resultado = null;
		try {
			declaracaoPreparada = conexao.prepareStatement("SELECT * FROM departamento ORDER BY Nome");
			resultado = declaracaoPreparada.executeQuery();
			List<Departamento> departamentos = new ArrayList<>();
			while (resultado.next()) {
				Departamento departamento = instanciarDepartamento(resultado);
				departamentos.add(departamento);
			}
			return departamentos;
		} catch (SQLException e) {
			throw new BancoDeDadosException(e.getMessage());
		} finally {
			BancoDeDados.fecharDeclaracao(declaracaoPreparada);
			BancoDeDados.fecharResultado(resultado);
		}
	}

	private Departamento instanciarDepartamento(ResultSet resultado) throws SQLException {
		Departamento departamento = new Departamento();
		departamento.setId(resultado.getInt("Id"));
		departamento.setNome(resultado.getString("Nome"));
		return departamento;
	}

}
