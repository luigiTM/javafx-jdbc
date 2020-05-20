package bancoDeDados;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import bancoDeDados.excexoes.BancoDeDadosException;

public class BancoDeDados {

	private static Connection conexao = null;

	public static Connection getConexao() {
		if (conexao == null) {
			try {
				Properties propriedades = carregarPropriedades();
				String url = propriedades.getProperty("dburl");
				conexao = DriverManager.getConnection(url, propriedades);
			} catch (SQLException excecaoSql) {
				throw new BancoDeDadosException(excecaoSql.getMessage());
			}
		}
		return conexao;
	}

	public static void fecharConexao() {
		if (conexao != null) {
			try {
				conexao.close();
			} catch (SQLException excecaoSql) {
				throw new BancoDeDadosException(excecaoSql.getMessage());
			}
		}
	}

	private static Properties carregarPropriedades() {
		try (FileInputStream entradaDeArquivo = new FileInputStream("db.properties")) {
			Properties propridades = new Properties();
			propridades.load(entradaDeArquivo);
			return propridades;
		} catch (IOException execao) {
			throw new BancoDeDadosException(execao.getMessage());
		}
	}

	public static void fecharDeclaracao(Statement declaracao) {
		if (declaracao != null) {
			try {
				declaracao.close();
			} catch (SQLException e) {
				throw new BancoDeDadosException(e.getMessage());
			}
		}
	}

	public static void fecharResultado(ResultSet resultado) {
		if (resultado != null) {
			try {
				resultado.close();
			} catch (SQLException e) {
				throw new BancoDeDadosException(e.getMessage());
			}
		}
	}

}
