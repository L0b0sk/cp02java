package br.com.fiapbank.infrastructure;

import br.com.fiapbank.model.Conta;

/**
 * Repositório em memória que simula o armazenamento de contas.
 * Camada de infraestrutura — responsável pelos dados em tempo de execução.
 */
public class ContaRepository {

    private static ContaRepository instance;
    private Conta contaAtiva;

    private ContaRepository() {}

    public static ContaRepository getInstance() {
        if (instance == null) {
            instance = new ContaRepository();
        }
        return instance;
    }

    public void salvar(Conta conta) {
        this.contaAtiva = conta;
    }

    public Conta buscarContaAtiva() {
        return contaAtiva;
    }
}
