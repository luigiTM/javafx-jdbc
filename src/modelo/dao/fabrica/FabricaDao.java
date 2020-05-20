package modelo.dao.fabrica;

import bancoDeDados.BancoDeDados;
import modelo.dao.DepartamentoDao;
import modelo.dao.VendedorDao;
import modelo.dao.implementacao.DepartamentoDaoJdbc;
import modelo.dao.implementacao.VendedorDaoJdbc;

public class FabricaDao {

	public static VendedorDao criarVendedorDao() {
		return new VendedorDaoJdbc(BancoDeDados.getConexao());
	}

	public static DepartamentoDao criarDepartamento() {
		return new DepartamentoDaoJdbc(BancoDeDados.getConexao());
	}

}
