package modelo.dao;

import java.util.List;

import modelo.entidades.Departamento;
import modelo.entidades.Vendedor;

public interface VendedorDao {

	void inserir(Vendedor vendedor);

	void atualizar(Vendedor vendedor);

	void deletarPorId(Integer id);

	Vendedor buscarPorId(Integer id);

	List<Vendedor> buscarTodos();

	List<Vendedor> buscarPorDepartamento(Departamento departamento);

}
