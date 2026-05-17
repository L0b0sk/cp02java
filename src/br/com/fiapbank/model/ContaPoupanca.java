package br.com.fiapbank.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Conta Poupança: subclasse de Conta.
 * Após cada saque, aplica rendimento mensal de 1,1% sobre o saldo restante.
 * O rendimento é registrado como movimentação do tipo RENDIMENTO.
 */
public class ContaPoupanca extends Conta {

    private static final Double RENDIMENTO_MENSAL = 1.1;

    public ContaPoupanca(Cliente cliente, Dinheiro saldo) {
        super(cliente, new ContaAcesso(""), saldo, RENDIMENTO_MENSAL);
    }

    public ContaPoupanca(Cliente cliente, ContaAcesso contaAcesso, Dinheiro saldo) {
        super(cliente, contaAcesso, saldo, RENDIMENTO_MENSAL);
    }

    /**
     * Aplica rendimento de 1,1% sobre o saldo após cada saque.
     * Registra a movimentação do tipo RENDIMENTO.
     */
    @Override
    protected void aplicarRegraDeTaxa() {
        if (saldo.maiorQue(Dinheiro.zero())) {
            BigDecimal percentual = new BigDecimal("0.011");
            BigDecimal rendimento = saldo.getValor()
                    .multiply(percentual)
                    .setScale(2, RoundingMode.HALF_UP);

            Dinheiro rendimentoDinheiro = new Dinheiro(rendimento);
            saldo = saldo.somar(rendimentoDinheiro);
            registrarMovimentacao(TipoMovimentacao.RENDIMENTO, rendimentoDinheiro);
        }
    }
}
