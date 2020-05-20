package modelo.dao.implementacao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bancoDeDados.BancoDeDados;
import bancoDeDados.excexoes.BancoDeDadosException;
import modelo.dao.VendedorDao;
import modelo.entidades.Departamento;
import modelo.entidades.Vendedor;

public class VendedorDaoJdbc implements VendedorDao {

	private Connection conexao;

	public VendedorDaoJdbc(Connection conexao) {
		this.conexao = conexao;
	}

	@Override
	public void inserir(Vendedor vendedor) {
		PreparedStatement declaracaoPreparada = null;
		try {
			declaracaoPreparada = conexao.prepareStatement("INSERT INTO vendedor (Nome, Email, DataNascimento, SalarioBase, IdDepartamento)  VALUES  (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			declaracaoPreparada.setString(1, vendedor.getNome());
			declaracaoPreparada.setString(2, vendedor.getEmail());
			declaracaoPreparada.setDate(3, new Date(vendedor.getDataNascimento().getTime()));
			declaracaoPreparada.setDouble(4, vendedor.getSalarioBase());
			declaracaoPreparada.setInt(5, vendedor.getDepartamento().getId());
			Integer linhasIncluidas = declaracaoPreparada.executeUpdate();
			if (linhasIncluidas > 0) {
				ResultSet resultado = declaracaoPreparada.getGeneratedKeys();
				if (resultado.next()) {
					Integer id = resultado.getInt(1);
					vendedor.setId(id);
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
	public void atualizar(Vendedor vendedor) {
		PreparedStatement declaracaoPreparada = null;
		try {
			declaracaoPreparada = conexao.prepareStatement("UPDATE vendedor SET Nome = ?, Email = ?, DataNascimento = ?, SalarioBase = ?, IdDepartamento = ?  WHERE Id = ? ");
			declaracaoPreparada.setString(1, vendedor.getNome());
			declaracaoPreparada.setString(2, vendedor.getEmail());
			declaracaoPreparada.setDate(3, new Date(vendedor.getDataNascimento().getTime()));
			declaracaoPreparada.setDouble(4, vendedor.getSalarioBase());
			declaracaoPreparada.setInt(5, vendedor.getDepartamento().getId());
			declaracaoPreparada.setInt(6, vendedor.getId());
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
			declaracaoPreparada = conexao.prepareStatement("DELETE FROM vendedor WHERE Id = ?");
			declaracaoPreparada.setInt(1, id);
			declaracaoPreparada.executeUpdate();
		} catch (SQLException e) {
			throw new BancoDeDadosException(e.getMessage());
		} finally {
			BancoDeDados.fecharDeclaracao(declaracaoPreparada);
		}
	}

	@Override
	public Vendedor buscarPorId(Integer id) {
		PreparedStatement declaracaoPreparada = null;
		ResultSet resultado = null;
		try {
			declaracaoPreparada = conexao.prepareStatement("SELECT vendedor.*,departamento.Nome as NomeDepartamento  FROM vendedor INNER JOIN departamento  ON vendedor.IdDepartamento = departamento.Id  WHERE vendedor.Id = ?");
			declaracaoPreparada.setInt(1, id);
			resultado = declaracaoPreparada.executeQuery();
			if (resultado.next()) {
				Departamento departamento = instanciarDepartamento(resultado);
				Vendedor vendedor = instanciarVendedor(resultado, departamento);
				return vendedor;
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
	public List<Vendedor> buscarTodos() {
		PreparedStatement declaracaoPreparada = null;
		ResultSet resultado = null;
		try {
			declaracaoPreparada = conexao.prepareStatement("SELECT vendedor.*,departmento.Nome as NomeDepartamento  FROM vendedor INNER JOIN departamento ON vendedor.IdDepartamento = departamento.Id ORDER BY Nome");
			resultado = declaracaoPreparada.executeQuery();
			List<Vendedor> vendedores = new ArrayList<>();
			Map<Integer, Departamento> mapaDepartamentos = new HashMap<>();
			while (resultado.next()) {
				Departamento departamentoResultado = mapaDepartamentos.get(resultado.getInt("IdDepartamento"));
				if (departamentoResultado == null) {
					departamentoResultado = instanciarDepartamento(resultado);
					mapaDepartamentos.put(resultado.getInt("IdDepartamento"), departamentoResultado);
				}
				Vendedor vendedor = instanciarVendedor(resultado, departamentoResultado);
				vendedores.add(vendedor);
			}
			return vendedores;
		} catch (SQLException e) {
			throw new BancoDeDadosException(e.getMessage());
		} finally {
			BancoDeDados.fecharDeclaracao(declaracaoPreparada);
			BancoDeDados.fecharResultado(resultado);
		}
	}

	private Vendedor instanciarVendedor(ResultSet resultado, Departamento departamento) throws SQLException {
		Vendedor vendedor = new Vendedor();
		vendedor.setId(resultado.getInt("Id"));
		vendedor.setNome(resultado.getString("Nome"));
		vendedor.setEmail(resultado.getString("Email"));
		vendedor.setSalarioBase(resultado.getDouble("SalarioBase"));
		vendedor.setDataNascimento(resultado.getDate("DataNascimento"));
		vendedor.setDepartamento(departamento);
		return vendedor;
	}

	private Departamento instanciarDepartamento(ResultSet resultado) throws SQLException {
		Departamento departamento = new Departamento();
		departamento.setId(resultado.getInt("IdDepartamento"));
		departamento.setNome(resultado.getString("NomeDepartamento"));
		return departamento;
	}

	@Override
	public List<Vendedor> buscarPorDepartamento(Departamento departamento) {
		PreparedStatement declaracaoPreparada = null;
		ResultSet resultado = null;
		try {
			declaracaoPreparada = conexao.prepareStatement("SELECT vendedor.*,departmento.Nome as NomeDepartamento  FROM vendedor INNER JOIN departamento ON vendedor.IdDepartamento = departamento.Id WHERE IdDepartamento = ? ORDER BY Nome");
			declaracaoPreparada.setInt(1, departamento.getId());
			resultado = declaracaoPreparada.executeQuery();
			List<Vendedor> vendedores = new ArrayList<>();
			Map<Integer, Departamento> mapaDepartamentos = new HashMap<>();
			while (resultado.next()) {
				Departamento departamentoResultado = mapaDepartamentos.get(resultado.getInt("IdDepartamento"));
				if (departamentoResultado == null) {
					departamentoResultado = instanciarDepartamento(resultado);
					mapaDepartamentos.put(resultado.getInt("IdDepartamento"), departamentoResultado);
				}
				Vendedor vendedor = instanciarVendedor(resultado, departamentoResultado);
				vendedores.add(vendedor);
			}
			return vendedores;
		} catch (SQLException e) {
			throw new BancoDeDadosException(e.getMessage());
		} finally {
			BancoDeDados.fecharDeclaracao(declaracaoPreparada);
			BancoDeDados.fecharResultado(resultado);
		}
	}
}
