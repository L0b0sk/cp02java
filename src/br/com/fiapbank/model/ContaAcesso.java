package br.com.fiapbank.model;

/**
 * Value Object que representa as credenciais de acesso do cliente.
 * Controla autenticação e bloqueio por tentativas inválidas.
 */
public class ContaAcesso {

    public static final Integer MAXIMO_TENTATIVAS = 3;

    private String senha;
    private Integer tentativas;
    private Boolean bloqueado;

    public ContaAcesso(String senha) {
        this.senha = senha == null ? "" : senha;
        this.tentativas = 0;
        this.bloqueado = false;
    }

    public Boolean validarSenha(String senhaTentativa) {
        if (bloqueado) {
            return false;
        }

        if (senha.equals(senhaTentativa)) {
            resetarTentativas();
            return true;
        }

        tentativas++;
        if (tentativas >= MAXIMO_TENTATIVAS) {
            bloqueado = true;
        }
        return false;
    }

    public Boolean isBloqueado() {
        return bloqueado;
    }

    public void resetarTentativas() {
        this.tentativas = 0;
        this.bloqueado = false;
    }

    public Integer getTentativas() {
        return tentativas;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ContaAcesso that = (ContaAcesso) obj;
        return senha != null && senha.equals(that.senha);
    }

    @Override
    public int hashCode() {
        return senha != null ? senha.hashCode() : 0;
    }
}
