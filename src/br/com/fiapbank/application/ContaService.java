package br.com.fiapbank.application;

import br.com.fiapbank.model.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Serviço de orquestração das operações bancárias.
 * Recebe as intenções da camada de apresentação e delega ao modelo.
 */
public class ContaService {

    private Conta conta;

    public ContaService(Conta conta) {
        this.conta = conta;
    }

    public void realizarDeposito(Dinheiro valor) {
        conta.realizarDeposito(valor);
    }

    public void realizarSaque(Dinheiro valor) {
        conta.realizarSaque(valor);
    }

    public void realizarCambio(Dinheiro valorBRL, String moedaDestino,
                                BigDecimal valorEstrangeiro, BigDecimal taxaCambio) {
        conta.realizarCambio(valorBRL, moedaDestino, valorEstrangeiro, taxaCambio);
    }

    public Dinheiro obterSaldo() {
        return conta.getSaldo();
    }

    public List<Movimentacao> obterMovimentacoes() {
        return conta.getMovimentacoes();
    }

    public Map<String, BigDecimal> obterSaldoEstrangeiro() {
        return conta.getSaldoEstrangeiro();
    }

    public BigDecimal obterSaldoMoeda(String moeda) {
        return conta.getSaldoMoeda(moeda);
    }

    public boolean temSaldo(Dinheiro valor) {
        return !valor.maiorQue(conta.getSaldo());
    }

    public Conta getConta() {
        return conta;
    }
}
