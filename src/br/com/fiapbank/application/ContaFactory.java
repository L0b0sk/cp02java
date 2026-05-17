package br.com.fiapbank.application;

import br.com.fiapbank.model.*;

/**
 * Factory Singleton responsável por criar instâncias de Conta.
 * Garante que apenas uma instância da factory exista em memória.
 */
public class ContaFactory {

    private static ContaFactory instance;

    private ContaFactory() {
        // Construtor privado — Singleton
    }

    public static ContaFactory getInstance() {
        if (instance == null) {
            instance = new ContaFactory();
        }
        return instance;
    }

    /**
     * Cria uma Conta Corrente para o cliente informado.
     */
    public Conta criarContaCorrente(Cliente cliente, Dinheiro saldo) {
        return new ContaCorrente(cliente, saldo);
    }

    /**
     * Cria uma Conta Corrente com credenciais de acesso definidas.
     */
    public Conta criarContaCorrente(Cliente cliente, ContaAcesso contaAcesso, Dinheiro saldo) {
        return new ContaCorrente(cliente, contaAcesso, saldo);
    }

    /**
     * Cria uma Conta Poupança para o cliente informado.
     */
    public Conta criarContaPoupanca(Cliente cliente, Dinheiro saldo) {
        return new ContaPoupanca(cliente, saldo);
    }

    /**
     * Cria uma Conta Poupança com credenciais de acesso definidas.
     */
    public Conta criarContaPoupanca(Cliente cliente, ContaAcesso contaAcesso, Dinheiro saldo) {
        return new ContaPoupanca(cliente, contaAcesso, saldo);
    }
}
