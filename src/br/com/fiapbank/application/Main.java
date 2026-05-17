package br.com.fiapbank.application;

import br.com.fiapbank.infrastructure.ContaRepository;
import br.com.fiapbank.infrastructure.UsuarioRepository;
import br.com.fiapbank.model.*;
import br.com.fiapbank.presentation.TerminalBancarioController;

/**
 * Ponto de entrada da aplicação.
 * Orquestra a inicialização: tela de login/registro -> criação de conta -> menu principal.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        // ========== APRESENTAÇÃO: Tela inicial (Login / Registro / Encerrar) ==========

        TerminalBancarioController telaInicial =
                new TerminalBancarioController(null, null);

        // Exibe tela de login/registro e retorna o usuário autenticado
        UsuarioRepository.UsuarioData usuarioData = telaInicial.exibirTelaInicial();

        if (usuarioData == null) {
            return; // encerrou pelo menu
        }

        iniciarSessao(usuarioData);
    }

    /**
     * Inicia uma sessão bancária para o usuário autenticado.
     * Cria as entidades de domínio e exibe o menu principal.
     */
    public static void iniciarSessao(UsuarioRepository.UsuarioData usuarioData) throws InterruptedException {

        // ========== MODEL: Criação das entidades via Factory ==========

        Cliente cliente = new Cliente(usuarioData.nome);
        ContaAcesso contaAcesso = new ContaAcesso(usuarioData.senhaHash.isEmpty() ? "temp" : usuarioData.senhaHash);
        ContaFactory factory = ContaFactory.getInstance();
        Conta conta;

        if ("1".equals(usuarioData.tipoConta)) {
            conta = factory.criarContaCorrente(cliente, contaAcesso, Dinheiro.zero());
        } else {
            conta = factory.criarContaPoupanca(cliente, contaAcesso, Dinheiro.zero());
        }

        // ========== INFRA: Persiste a conta em memória ==========
        ContaRepository.getInstance().salvar(conta);

        // ========== APPLICATION: Serviços de orquestração ==========
        ContaService contaService = new ContaService(conta);
        AutorizacaoService autorizacaoService = new AutorizacaoService(conta);

        // ========== APRESENTAÇÃO: Menu principal ==========
        TerminalBancarioController terminal =
                new TerminalBancarioController(contaService, autorizacaoService);

        terminal.exibirMenuPrincipal(cliente.obterPrimeiroNome());
    }
}
