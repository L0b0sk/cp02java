package br.com.fiapbank.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe abstrata que representa uma conta bancária.
 * Define o Template Method para saque e depósito.
 * O saldo é protegido — nenhuma classe externa pode alterá-lo diretamente.
 * Suporta armazenamento de moedas estrangeiras (USD, EUR, ARS).
 */
public abstract class Conta {

    protected Cliente cliente;
    protected Dinheiro saldo;
    protected Double taxa;
    protected StatusConta status;
    protected LocalDate dataAbertura;
    protected ContaAcesso contaAcesso;
    protected List<Movimentacao> movimentacoes;

    // Saldo em moedas estrangeiras
    protected Map<String, BigDecimal> saldoEstrangeiro;

    protected Conta(Cliente cliente, ContaAcesso contaAcesso, Dinheiro saldo, Double taxaMensal) {
        this.cliente = cliente;
        this.contaAcesso = contaAcesso;
        this.saldo = saldo;
        this.taxa = taxaMensal;
        this.status = StatusConta.ATIVA;
        this.dataAbertura = LocalDate.now();
        this.movimentacoes = new ArrayList<>();
        this.saldoEstrangeiro = new HashMap<>();
        this.saldoEstrangeiro.put("USD", BigDecimal.ZERO);
        this.saldoEstrangeiro.put("EUR", BigDecimal.ZERO);
        this.saldoEstrangeiro.put("ARS", BigDecimal.ZERO);
    }

    // ==================== TEMPLATE METHOD: SAQUE ====================

    public void realizarSaque(Dinheiro valor) {
        verificarContaAtiva();

        if (valor == null || valor.menorQue(Dinheiro.zero()) || valor.igualA(Dinheiro.zero())) {
            throw new IllegalArgumentException("O valor do saque deve ser maior que zero.");
        }

        if (valor.maiorQue(saldo)) {
            throw new IllegalStateException("Saldo insuficiente para realizar o saque.");
        }

        sacar(valor);
        registrarMovimentacao(TipoMovimentacao.SAQUE, valor);
        aplicarRegraDeTaxa();
    }

    // ==================== TEMPLATE METHOD: DEPÓSITO ====================

    public void realizarDeposito(Dinheiro valor) {
        verificarContaAtiva();

        if (valor == null || valor.menorQue(Dinheiro.zero()) || valor.igualA(Dinheiro.zero())) {
            throw new IllegalArgumentException("O valor do depósito deve ser maior que zero.");
        }

        depositar(valor);
        registrarMovimentacao(TipoMovimentacao.DEPOSITO, valor);
    }

    // ==================== CÂMBIO ====================

    /**
     * Converte BRL para moeda estrangeira e registra no histórico.
     * Verifica saldo suficiente antes de efetuar a conversão.
     */
    public void realizarCambio(Dinheiro valorBRL, String moedaDestino,
                                BigDecimal valorEstrangeiro, BigDecimal taxaCambio) {
        verificarContaAtiva();

        if (valorBRL == null || valorBRL.menorQue(Dinheiro.zero()) || valorBRL.igualA(Dinheiro.zero())) {
            throw new IllegalArgumentException("O valor para cambio deve ser maior que zero.");
        }

        if (valorBRL.maiorQue(saldo)) {
            throw new IllegalStateException("Saldo insuficiente para realizar o cambio. Saldo atual: " + saldo.toString());
        }

        // Debita BRL
        sacar(valorBRL);

        // Credita moeda estrangeira
        BigDecimal saldoAtual = saldoEstrangeiro.getOrDefault(moedaDestino, BigDecimal.ZERO);
        saldoEstrangeiro.put(moedaDestino, saldoAtual.add(valorEstrangeiro));

        // Registra movimentação com detalhes do câmbio
        Movimentacao mov = new Movimentacao(LocalDateTime.now(), valorBRL, TipoMovimentacao.CAMBIO,
                "BRL", moedaDestino, valorEstrangeiro, taxaCambio);
        movimentacoes.add(mov);
    }

    // ==================== MÉTODOS INTERNOS ====================

    private void depositar(Dinheiro valor) {
        this.saldo = this.saldo.somar(valor);
    }

    private void sacar(Dinheiro valor) {
        this.saldo = this.saldo.subtrair(valor);
    }

    protected void registrarMovimentacao(TipoMovimentacao tipo, Dinheiro valor) {
        Movimentacao movimentacao = new Movimentacao(LocalDateTime.now(), valor, tipo);
        movimentacoes.add(movimentacao);
    }

    private void verificarContaAtiva() {
        if (status != StatusConta.ATIVA) {
            throw new IllegalStateException("A conta não está ativa.");
        }
    }

    // ==================== MÉTODO ABSTRATO ====================

    protected abstract void aplicarRegraDeTaxa();

    // ==================== GETTERS ====================

    public Dinheiro getSaldo() {
        return saldo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public StatusConta getStatus() {
        return status;
    }

    public ContaAcesso getContaAcesso() {
        return contaAcesso;
    }

    public List<Movimentacao> getMovimentacoes() {
        return new ArrayList<>(movimentacoes);
    }

    public Map<String, BigDecimal> getSaldoEstrangeiro() {
        return new HashMap<>(saldoEstrangeiro);
    }

    public BigDecimal getSaldoMoeda(String moeda) {
        return saldoEstrangeiro.getOrDefault(moeda, BigDecimal.ZERO);
    }
}
