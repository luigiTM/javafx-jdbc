package modelo.servicos;

import java.util.List;

import modelo.dao.DepartamentoDao;
import modelo.dao.fabrica.FabricaDao;
import modelo.entidades.Departamento;

public class DepartamentoService {

	private DepartamentoDao dao = FabricaDao.criarDepartamentoDao();

	public List<Departamento> buscarTodos() {
		return dao.buscarTodos();
	}

	public void salvarAtualizarDepartamento(Departamento departamento) {
		if (departamento.getId() == null) {
			dao.inserir(departamento);
		} else {
			dao.atualizar(departamento);
		}
	}

	public void deletar(Departamento departamento) {
		dao.deletarPorId(departamento.getId());
	}

}
