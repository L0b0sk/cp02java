package br.com.fiapbank.model;

import java.math.BigDecimal;

/**
 * Conta Corrente: subclasse de Conta.
 * Possui taxa de manutenção cobrada a cada saque (R$ 25,00).
 * A taxa é registrada como movimentação do tipo TAXA.
 */
public class ContaCorrente extends Conta {

    private static final Double TAXA_MANUTENCAO = -25.00;

    public ContaCorrente(Cliente cliente, Dinheiro saldo) {
        super(cliente, new ContaAcesso(""), saldo, TAXA_MANUTENCAO);
    }

    public ContaCorrente(Cliente cliente, ContaAcesso contaAcesso, Dinheiro saldo) {
        super(cliente, contaAcesso, saldo, TAXA_MANUTENCAO);
    }

    /**
     * Aplica a taxa de manutenção após cada saque.
     * Desconta R$ 25,00 do saldo e registra como TAXA.
     */
    @Override
    protected void aplicarRegraDeTaxa() {
        Dinheiro taxaValor = new Dinheiro(new BigDecimal("25.00"));

        // Só cobra a taxa se houver saldo suficiente
        if (saldo.maiorOuIgualQue(taxaValor)) {
            saldo = saldo.subtrair(taxaValor);
            registrarMovimentacao(TipoMovimentacao.TAXA, taxaValor);
        }
    }
}
