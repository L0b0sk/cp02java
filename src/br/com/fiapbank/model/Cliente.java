package br.com.fiapbank.model;

/**
 * Entidade que representa o cliente do banco.
 * Um cliente não pode ser criado sem nome completo.
 */
public class Cliente extends BaseEntity {

    private String nomeCompleto;

    public Cliente(String nomeCompleto) {
        super();
        if (nomeCompleto == null || nomeCompleto.isBlank()) {
            throw new IllegalArgumentException("O nome completo do cliente é obrigatório.");
        }
        this.nomeCompleto = nomeCompleto.trim();
    }

    public String obterPrimeiroNome() {
        return nomeCompleto.split(" ")[0];
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
