package bancoDeDados.excexoes;

public class BancoDeDadosException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BancoDeDadosException(String mensagem) {
		super(mensagem);
	}

}
