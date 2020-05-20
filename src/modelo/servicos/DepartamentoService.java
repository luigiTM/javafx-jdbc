package modelo.servicos;

import java.util.ArrayList;
import java.util.List;

import modelo.entidades.Departamento;

public class DepartamentoService {

	public List<Departamento> buscarTodos() {
		List<Departamento> departamentos = new ArrayList<>();
		departamentos.add(new Departamento(1, "Livros"));
		departamentos.add(new Departamento(2, "Livros"));
		departamentos.add(new Departamento(3, "Livros"));
		departamentos.add(new Departamento(4, "Livros"));
		departamentos.add(new Departamento(5, "Livros"));
		return departamentos;
	}

}
