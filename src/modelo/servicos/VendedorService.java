package modelo.servicos;

import java.util.List;

import modelo.dao.VendedorDao;
import modelo.dao.fabrica.FabricaDao;
import modelo.entidades.Vendedor;

public class VendedorService {

	private VendedorDao dao = FabricaDao.criarVendedorDao();

	public List<Vendedor> buscarTodos() {
		return dao.buscarTodos();
	}

	public void salvarAtualizarVendedor(Vendedor vendedor) {
		if (vendedor.getId() == null) {
			dao.inserir(vendedor);
		} else {
			dao.atualizar(vendedor);
		}
	}

	public void deletar(Vendedor vendedor) {
		dao.deletarPorId(vendedor.getId());
	}

}
