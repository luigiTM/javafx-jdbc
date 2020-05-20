package modelo.dao;

import java.util.List;

import modelo.entidades.Departamento;

public interface DepartamentoDao {

	void inserir(Departamento departamento);

	void atualizar(Departamento departamento);

	void deletarPorId(Integer id);

	Departamento buscarPorId(Integer id);

	List<Departamento> buscarTodos();

}
