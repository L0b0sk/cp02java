package br.com.fiapbank.presentation;

import br.com.fiapbank.application.AutorizacaoService;
import br.com.fiapbank.application.ContaService;
import br.com.fiapbank.infrastructure.CambioService;
import br.com.fiapbank.infrastructure.UsuarioRepository;
import br.com.fiapbank.model.Dinheiro;
import br.com.fiapbank.model.Movimentacao;
import br.com.fiapbank.model.TipoMovimentacao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Camada de Apresentação — Controller do Terminal Bancário.
 * Responsável por toda a interface com o usuário: menus, inputs e outputs.
 * Nenhuma regra de negócio financeira reside aqui.
 */
public class TerminalBancarioController {

    private ContaService contaService;
    private AutorizacaoService autorizacaoService;
    private CambioService cambioService;
    private UsuarioRepository usuarioRepository;
    private Scanner scanner;

    public TerminalBancarioController(ContaService contaService, AutorizacaoService autorizacaoService) {
        this.contaService = contaService;
        this.autorizacaoService = autorizacaoService;
        this.cambioService = new CambioService();
        this.usuarioRepository = UsuarioRepository.getInstance();
        this.scanner = new Scanner(System.in);
    }

    // ==================== MÉTODOS AUXILIARES DE UI ====================

    public void digitar(String texto, Integer delay) throws InterruptedException {
        for (char c : texto.toCharArray()) {
            System.out.print(c);
            Thread.sleep(delay);
        }
        System.out.println();
    }

    public void limparTerminal() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }

    public void exibirLogo() throws InterruptedException {
        String[] logo = {
            "",
            "    \u2588\u2588\u2588\u2588\u2588\u2557 \u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2557\u2588\u2588\u2588\u2557   \u2588\u2588\u2588\u2557    \u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2557\u2588\u2588\u2557 \u2588\u2588\u2588\u2588\u2588\u2557 \u2588\u2588\u2588\u2588\u2588\u2588\u2557 ",
            "   \u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2557\u255a\u2550\u2550\u2588\u2588\u2554\u2550\u2550\u255d\u2588\u2588\u2588\u2588\u2557 \u2588\u2588\u2588\u2588\u2551    \u2588\u2588\u2554\u2550\u2550\u2550\u2550\u255d\u2588\u2588\u2551\u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2557\u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2557",
            "   \u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2551   \u2588\u2588\u2551   \u2588\u2588\u2554\u2588\u2588\u2588\u2588\u2554\u2588\u2588\u2551    \u2588\u2588\u2588\u2588\u2588\u2557  \u2588\u2588\u2551\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2551\u2588\u2588\u2588\u2588\u2588\u2588\u2554\u255d",
            "   \u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2551   \u2588\u2588\u2551   \u2588\u2588\u2551\u255a\u2588\u2588\u2554\u255d\u2588\u2588\u2551    \u2588\u2588\u2554\u2550\u2550\u255d  \u2588\u2588\u2551\u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2551\u2588\u2588\u2554\u2550\u2550\u2550\u255d ",
            "   \u2588\u2588\u2551  \u2588\u2588\u2551   \u2588\u2588\u2551   \u2588\u2588\u2551 \u255a\u2550\u255d \u2588\u2588\u2551    \u2588\u2588\u2551     \u2588\u2588\u2551\u2588\u2588\u2551  \u2588\u2588\u2551\u2588\u2588\u2551     ",
            "   \u255a\u2550\u255d  \u255a\u2550\u255d   \u255a\u2550\u255d   \u255a\u2550\u255d     \u255a\u2550\u255d    \u255a\u2550\u255d     \u255a\u2550\u255d\u255a\u2550\u255d  \u255a\u2550\u255d\u255a\u2550\u255d     ",
            "",
            "   =======================================================",
            "   ============ SISTEMA DE AUTOMACAO BANCARIA ============",
            "   =======================================================",
            ""
        };
        for (String linha : logo) {
            digitar(linha, 3);
            Thread.sleep(30);
        }
        Thread.sleep(800);
    }

    // ==================== TELA INICIAL: LOGIN / REGISTRO / ENCERRAR ====================

    /**
     * Loop principal da tela inicial. Só sai quando:
     * - usuário faz login com sucesso -> retorna UsuarioData
     * - usuário se registra com sucesso -> retorna UsuarioData
     * - usuário escolhe Encerrar -> System.exit(0)
     *
     * Erros de validação, login incorreto, registro inválido: volta ao menu, NUNCA encerra.
     */
    public UsuarioRepository.UsuarioData exibirTelaInicial() throws InterruptedException {
        limparTerminal();
        exibirLogo();

        while (true) {
            System.out.println("\n-------------------------------------------------------------");
            System.out.println("                  BEM-VINDO AO BANCO FIAP                  ");
            System.out.println("-------------------------------------------------------------");
            System.out.println("  [1]  LOGIN          (acessar conta existente)             ");
            System.out.println("  [2]  REGISTRAR      (criar nova conta)                    ");
            System.out.println("  [3]  ENCERRAR       (sair do programa)                    ");
            System.out.println("-------------------------------------------------------------");
            System.out.print("Escolha uma opcao: ");

            Integer opcao = lerInteiroSeguro();

            switch (opcao) {
                case 1: {
                    UsuarioRepository.UsuarioData usuario = fluxoLogin();
                    if (usuario != null) return usuario; // login OK -> inicia sessao
                    // login falhou -> loop volta ao menu automaticamente
                    break;
                }
                case 2: {
                    UsuarioRepository.UsuarioData novo = fluxoRegistro();
                    if (novo != null) return novo; // registro OK -> inicia sessao
                    // registro falhou/cancelado -> loop volta ao menu automaticamente
                    break;
                }
                case 3:
                    System.out.println("\n-------------------------------------------------------------");
                    System.out.println("    O Banco FIAP agradece sua visita! Ate logo!           ");
                    System.out.println("-------------------------------------------------------------");
                    System.exit(0);
                    break;
                default:
                    System.out.println("\n  Opcao invalida! Escolha 1, 2 ou 3.");
            }
        }
    }

    // ==================== FLUXO DE LOGIN ====================

    /**
     * Tenta autenticar o usuário. Permite até 3 tentativas de email/senha.
     * Retorna UsuarioData em caso de sucesso, ou null para voltar ao menu inicial.
     */
    private UsuarioRepository.UsuarioData fluxoLogin() throws InterruptedException {
        System.out.println("\n=============================================================");
        System.out.println("                         LOGIN                              ");
        System.out.println("=============================================================");
        System.out.println("  (deixe em branco e pressione Enter para cancelar)         ");
        System.out.println("=============================================================");

        int tentativas = 0;
        while (tentativas < 3) {
            System.out.print("\n  Email: ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                System.out.println("\n  Login cancelado. Voltando ao menu...");
                Thread.sleep(1000);
                return null;
            }

            System.out.print("  Senha: ");
            String senha = scanner.nextLine().trim();

            UsuarioRepository.UsuarioData usuario = usuarioRepository.autenticar(email, senha);

            if (usuario != null) {
                System.out.println();
                digitar("  Login realizado! Bem-vindo(a), " + usuario.nome.split(" ")[0] + "!", 40);
                Thread.sleep(800);
                return usuario;
            }

            tentativas++;
            int restantes = 3 - tentativas;
            System.out.println("\n  [!] Email ou senha incorretos!" +
                    (restantes > 0
                            ? " Tentativas restantes: " + restantes
                            : " Limite atingido."));
        }

        System.out.println("\n  Muitas tentativas incorretas. Voltando ao menu inicial...");
        Thread.sleep(1500);
        return null; // volta ao while do exibirTelaInicial
    }

    // ==================== FLUXO DE REGISTRO ====================

    /**
     * Coleta dados do novo usuário passo a passo.
     * Qualquer entrada inválida exibe erro e repete o campo (não volta ao menu).
     * Só retorna null se o usuário cancelar explicitamente ou o email já existir.
     */
    private UsuarioRepository.UsuarioData fluxoRegistro() throws InterruptedException {
        System.out.println("\n=============================================================");
        System.out.println("                     NOVO CADASTRO                          ");
        System.out.println("=============================================================");
        System.out.println("  (deixe em branco e pressione Enter para cancelar)         ");
        System.out.println("=============================================================");

        // --- NOME ---
        String nome;
        while (true) {
            System.out.print("\n  Nome completo: ");
            nome = scanner.nextLine().trim();
            if (nome.isEmpty()) { System.out.println("\n  Cadastro cancelado. Voltando ao menu..."); Thread.sleep(1000); return null; }
            if (nome.split(" ").length < 2) { System.out.println("  [!] Digite nome E sobrenome."); continue; }
            break;
        }

        // --- EMAIL ---
        String email;
        while (true) {
            System.out.print("  Email: ");
            email = scanner.nextLine().trim();
            if (email.isEmpty()) { System.out.println("\n  Cadastro cancelado. Voltando ao menu..."); Thread.sleep(1000); return null; }
            if (!email.contains("@") || !email.contains(".")) { System.out.println("  [!] Email invalido. Ex: nome@email.com"); continue; }
            if (usuarioRepository.buscarPorEmail(email) != null) {
                System.out.println("  [!] Este email ja esta cadastrado. Use a opcao Login.");
                Thread.sleep(1500);
                return null;
            }
            break;
        }

        // --- CPF ---
        String cpf;
        while (true) {
            System.out.print("  CPF (apenas numeros): ");
            String entrada = scanner.nextLine().trim();
            if (entrada.isEmpty()) { System.out.println("\n  Cadastro cancelado. Voltando ao menu..."); Thread.sleep(1000); return null; }
            cpf = entrada.replaceAll("[^0-9]", "");
            if (cpf.length() != 11) { System.out.println("  [!] CPF invalido. Digite os 11 digitos."); continue; }
            break;
        }

        // --- SENHA ---
        String regexSenha = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%\u00a8&*()\\-_+=?><]).{8,}$";
        System.out.println("\n  Requisitos de senha:");
        System.out.println("   - Minimo 8 caracteres");
        System.out.println("   - 1 numero, 1 letra MAIUSCULA, 1 caractere especial (!@#$%&*)");

        String senha;
        while (true) {
            System.out.print("\n  Senha: ");
            senha = scanner.nextLine();
            if (senha.isEmpty()) { System.out.println("\n  Cadastro cancelado. Voltando ao menu..."); Thread.sleep(1000); return null; }
            if (!senha.matches(regexSenha)) {
                System.out.println("  [!] Senha fraca! Verifique os requisitos acima e tente novamente.");
                continue;
            }
            System.out.print("  Confirme a senha: ");
            String confirmacao = scanner.nextLine();
            if (!senha.equals(confirmacao)) { System.out.println("  [!] As senhas nao conferem. Tente novamente."); continue; }
            break;
        }

        // --- TIPO DE CONTA ---
        String tipoConta;
        while (true) {
            System.out.println("\n  Tipo de conta:");
            System.out.println("  [1]  CONTA CORRENTE  (taxa de R$ 25,00 por saque)");
            System.out.println("  [2]  CONTA POUPANCA  (rendimento de 1,1% apos saques)");
            System.out.print("  Escolha: ");
            tipoConta = scanner.nextLine().trim();
            if (tipoConta.equals("1") || tipoConta.equals("2")) break;
            System.out.println("  [!] Opcao invalida! Digite 1 ou 2.");
        }

        // --- SALVAR ---
        boolean salvo = usuarioRepository.registrarUsuario(nome, email, cpf, senha, tipoConta);

        if (salvo) {
            System.out.println();
            digitar("  Cadastro realizado! Seja bem-vindo(a), " + nome.split(" ")[0] + "!", 40);
            Thread.sleep(800);
            return new UsuarioRepository.UsuarioData(nome, email, cpf, "", tipoConta);
        } else {
            System.out.println("\n  [!] Erro ao salvar cadastro. Tente novamente.");
            Thread.sleep(1500);
            return null;
        }
    }

    // ==================== MENU PRINCIPAL ====================

    public void exibirMenuPrincipal(String primeiroNome) throws InterruptedException {
        limparTerminal();
        digitar("=============================================================", 30);
        digitar("              BEM-VINDO AO SEU BANCO DIGITAL              ", 30);
        digitar("=============================================================", 30);
        Thread.sleep(600);
        exibirLogo();
        digitar("Ola " + primeiroNome + "! Como podemos ajudar voce hoje?", 45);
        Thread.sleep(800);

        Integer opcao;
        do {
            System.out.println("\n-------------------------------------------------------------");
            System.out.println("                      MENU PRINCIPAL                      ");
            System.out.println("-------------------------------------------------------------");
            System.out.println("  [1]  SALDO E HISTORICO DE MOVIMENTACOES                 ");
            System.out.println("  [2]  FAZER DEPOSITO                                      ");
            System.out.println("  [3]  FAZER SAQUE                                         ");
            System.out.println("  [4]  CAMBIO DE MOEDAS                                    ");
            System.out.println("  [5]  ENCERRAR SESSAO                                     ");
            System.out.println("-------------------------------------------------------------");
            System.out.print("Escolha uma opcao: ");

            opcao = lerInteiroSeguro();

            switch (opcao) {
                case 1: exibirSaldoComHistorico(); break;
                case 2: realizarDeposito();        break;
                case 3: realizarSaque();           break;
                case 4: exibirMenuCambio();        break;
                case 5:
                    System.out.println("\n-------------------------------------------------------------");
                    System.out.println("    Sessao encerrada. O Banco FIAP agradece!             ");
                    System.out.println("-------------------------------------------------------------");
                    break;
                default:
                    System.out.println("Opcao invalida! Escolha entre 1 e 5.");
            }
        } while (opcao != 5);

        // Sessão encerrada -> volta à tela inicial para novo login/registro
        System.out.println("\nRedirecionando para tela inicial...");
        Thread.sleep(1200);
        UsuarioRepository.UsuarioData proximo = exibirTelaInicial();
        if (proximo != null) {
            // reinicia sessão para o novo usuário via Main
            br.com.fiapbank.application.Main.iniciarSessao(proximo);
        }
    }

    // ==================== OPERAÇÕES BANCÁRIAS ====================

    public void exibirSaldoComHistorico() {
        List<Movimentacao> movimentacoes = contaService.obterMovimentacoes();
        Map<String, BigDecimal> saldoExt = contaService.obterSaldoEstrangeiro();

        System.out.println("\n=============================================================");
        System.out.println("            SALDO E HISTORICO DE MOVIMENTACOES               ");
        System.out.println("=============================================================");
        System.out.printf("%n  SALDO EM REAIS:   %s%n", contaService.obterSaldo().toString());

        boolean temMoedaEstrangeira = saldoExt.values().stream().anyMatch(v -> v.compareTo(BigDecimal.ZERO) > 0);
        if (temMoedaEstrangeira) {
            System.out.println("\n  MOEDAS ESTRANGEIRAS:");
            System.out.println("  ---------------------------------------------------------");
            BigDecimal usd = saldoExt.getOrDefault("USD", BigDecimal.ZERO);
            BigDecimal eur = saldoExt.getOrDefault("EUR", BigDecimal.ZERO);
            BigDecimal ars = saldoExt.getOrDefault("ARS", BigDecimal.ZERO);
            if (usd.compareTo(BigDecimal.ZERO) > 0)
                System.out.printf("  Dolar Americano (USD):   $ %,.2f%n", usd);
            if (eur.compareTo(BigDecimal.ZERO) > 0)
                System.out.printf("  Euro (EUR):              \u20ac %,.2f%n", eur);
            if (ars.compareTo(BigDecimal.ZERO) > 0)
                System.out.printf("  Peso Argentino (ARS):    $ARS %,.2f%n", ars);
        } else {
            System.out.println("\n  Sem moedas estrangeiras em carteira.");
        }

        System.out.println("\n-------------------------------------------------------------");
        System.out.println("                  ULTIMAS MOVIMENTACOES                      ");
        System.out.println("-------------------------------------------------------------");

        if (movimentacoes.isEmpty()) {
            System.out.println("\n  Nenhuma movimentacao realizada ainda.");
        } else {
            System.out.println("\n  TIPO              VALOR            DATA E HORA");
            System.out.println("  ---------------------------------------------------------");
            for (Movimentacao m : movimentacoes) {
                if (m.isCambio()) {
                    System.out.printf("  %-14s - %13s  %s%n",
                            "CAMBIO", m.getValor().toString(), m.getDataHoraFormatada());
                    System.out.printf("     -> %.2f %s @ taxa %.6f%n",
                            m.getValorEstrangeiro(), m.getMoedaDestino(), m.getTaxaCambio());
                } else {
                    String sinal = (m.getTipo() == TipoMovimentacao.DEPOSITO
                            || m.getTipo() == TipoMovimentacao.RENDIMENTO) ? "+" : "-";
                    System.out.printf("  %-14s %s %13s  %s%n",
                            m.getTipo().name(), sinal, m.getValor().toString(), m.getDataHoraFormatada());
                }
            }
            System.out.println("\n  Total de movimentacoes: " + movimentacoes.size());
        }
        System.out.println("\n=============================================================");
    }

    public void realizarDeposito() {
        System.out.print("\nDigite o valor do deposito: R$ ");
        Double valorDigitado = lerDoubleSeguro();
        if (valorDigitado == null) { System.out.println("Valor invalido!"); return; }
        try {
            Dinheiro valor = new Dinheiro(BigDecimal.valueOf(valorDigitado));
            contaService.realizarDeposito(valor);
            System.out.printf("Deposito de R$ %.2f realizado com sucesso!%n", valorDigitado);
            System.out.printf("Novo saldo: %s%n", contaService.obterSaldo().toString());
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void realizarSaque() {
        System.out.print("\nDigite o valor para saque: R$ ");
        Double valorDigitado = lerDoubleSeguro();
        if (valorDigitado == null) { System.out.println("Valor invalido!"); return; }
        try {
            Dinheiro valor = new Dinheiro(BigDecimal.valueOf(valorDigitado));
            contaService.realizarSaque(valor);
            System.out.printf("Saque de R$ %.2f realizado com sucesso!%n", valorDigitado);
            System.out.printf("Novo saldo: %s%n", contaService.obterSaldo().toString());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // ==================== CÂMBIO ====================

    public void exibirMenuCambio() {
        Integer opcao;
        do {
            System.out.println("\n=============================================================");
            System.out.println("                    CAMBIO DE MOEDAS                        ");
            System.out.println("=============================================================");
            System.out.printf("  Saldo disponivel: %s%n", contaService.obterSaldo().toString());
            System.out.println("=============================================================");
            System.out.println("  [1]  CONVERTER BRL -> MOEDA ESTRANGEIRA                   ");
            System.out.println("  [2]  CONSULTAR MOEDA ESTRANGEIRA -> BRL                   ");
            System.out.println("  [3]  TABELA COMPLETA DE COTACOES                          ");
            System.out.println("  [4]  ATUALIZAR COTACOES (nova consulta API)               ");
            System.out.println("  [0]  VOLTAR AO MENU PRINCIPAL                             ");
            System.out.println("=============================================================");
            System.out.print("Escolha uma opcao: ");
            opcao = lerInteiroSeguro();
            switch (opcao) {
                case 1: exibirConversaoBRLparaMoeda(); break;
                case 2: exibirConversaoMoedaParaBRL(); break;
                case 3: exibirTabelaCotacoes();        break;
                case 4:
                    cambioService.limparCache();
                    System.out.println("\n  Cache limpo! Proxima operacao buscara cotacoes atualizadas.");
                    break;
                case 0: break;
                default: System.out.println("Opcao invalida!");
            }
        } while (opcao != 0);
    }

    private void exibirConversaoBRLparaMoeda() {
        Dinheiro saldoAtual = contaService.obterSaldo();
        System.out.println("\n=============================================================");
        System.out.println("           CONVERTER BRL -> MOEDA ESTRANGEIRA               ");
        System.out.println("=============================================================");
        System.out.printf("  Seu saldo atual: %s%n", saldoAtual.toString());

        if (saldoAtual.igualA(Dinheiro.zero())) {
            System.out.println("\n  [!] Voce nao possui saldo disponivel para conversao.");
            System.out.println("      Realize um deposito primeiro.");
            return;
        }

        System.out.print("\nDigite o valor em Reais (BRL) a converter: R$ ");
        Double valorDigitado = lerDoubleSeguro();
        if (valorDigitado == null || valorDigitado <= 0) { System.out.println("Valor invalido!"); return; }

        Dinheiro valorBRL = new Dinheiro(BigDecimal.valueOf(valorDigitado));
        if (!contaService.temSaldo(valorBRL)) {
            System.out.println("\n  [!] SALDO INSUFICIENTE!");
            System.out.printf("      Voce tentou converter: R$ %.2f%n", valorDigitado);
            System.out.printf("      Seu saldo disponivel:  %s%n", saldoAtual.toString());
            return;
        }

        System.out.println("\n  Selecione a moeda de destino:");
        System.out.println("  [1]  USD - Dolar Americano");
        System.out.println("  [2]  EUR - Euro");
        System.out.println("  [3]  ARS - Peso Argentino");
        System.out.print("  Escolha: ");
        Integer escolha = lerInteiroSeguro();
        String cod, simbol;
        switch (escolha) {
            case 1: cod = "USD"; simbol = "$";    break;
            case 2: cod = "EUR"; simbol = "\u20ac"; break;
            case 3: cod = "ARS"; simbol = "$ARS"; break;
            default: System.out.println("  Opcao invalida!"); return;
        }

        System.out.println("\n  Buscando cotacoes em tempo real...");
        try {
            Map<String, BigDecimal> taxas = cambioService.obterTaxas();
            BigDecimal taxa = taxas.get(cod);
            BigDecimal resultado = cambioService.converterDeBRL(BigDecimal.valueOf(valorDigitado), cod);
            exibirCabecalhoCambio();
            System.out.printf("%n  CONVERSAO: R$ %.2f -> %s %.2f%n", valorDigitado, simbol, resultado);
            System.out.printf("  Taxa aplicada: 1 BRL = %.6f %s%n", taxa, cod);
            System.out.print("\n  Confirmar conversao? [S/N]: ");
            String confirmacao = scanner.nextLine().trim().toUpperCase();
            if (confirmacao.equals("S")) {
                contaService.realizarCambio(valorBRL, cod, resultado, taxa);
                System.out.println("\n  [OK] Conversao realizada com sucesso!");
                System.out.printf("  Debitado:  R$ %.2f%n", valorDigitado);
                System.out.printf("  Creditado: %s %.2f%n", simbol, resultado);
                System.out.printf("  Novo saldo BRL: %s%n", contaService.obterSaldo().toString());
            } else {
                System.out.println("\n  Conversao cancelada.");
            }
        } catch (Exception e) {
            System.out.println("  Erro ao buscar cotacoes: " + e.getMessage());
        }
    }

    private void exibirConversaoMoedaParaBRL() {
        System.out.println("\n  Selecione a moeda de origem:");
        System.out.println("  [1]  USD - Dolar Americano");
        System.out.println("  [2]  EUR - Euro");
        System.out.println("  [3]  ARS - Peso Argentino");
        System.out.print("  Escolha: ");
        Integer escolha = lerInteiroSeguro();
        String cod, simbol, nome;
        switch (escolha) {
            case 1: cod = "USD"; nome = "Dolar Americano"; simbol = "$";    break;
            case 2: cod = "EUR"; nome = "Euro";            simbol = "\u20ac"; break;
            case 3: cod = "ARS"; nome = "Peso Argentino";  simbol = "$ARS"; break;
            default: System.out.println("  Opcao invalida!"); return;
        }
        System.out.printf("%n  Digite o valor em %s (%s): %s ", nome, cod, simbol);
        Double valorDigitado = lerDoubleSeguro();
        if (valorDigitado == null || valorDigitado <= 0) { System.out.println("  Valor invalido!"); return; }

        System.out.println("\n  Buscando cotacoes...");
        try {
            Map<String, BigDecimal> taxas = cambioService.obterTaxas();
            BigDecimal resultadoBRL = cambioService.converterParaBRL(BigDecimal.valueOf(valorDigitado), cod);
            exibirCabecalhoCambio();
            System.out.printf("%n  CONSULTA: %s %.2f (%s) equivale a R$ %.2f%n", simbol, valorDigitado, cod, resultadoBRL);
            System.out.println("  (Consulta informativa - nenhum valor movimentado)");

            System.out.println("\n  [ 1 unidade de cada moeda em BRL ]");
            System.out.println("  +------------------------+----------------------+");
            System.out.println("  |       MOEDA            |   1 UNIDADE = R$     |");
            System.out.println("  +------------------------+----------------------+");
            for (Object[] m : new Object[][]{{"USD","Dolar (USD)    "},{"EUR","Euro (EUR)     "},{"ARS","Peso Arg (ARS) "}}) {
                BigDecimal t = taxas.get(m[0]);
                BigDecimal inv = BigDecimal.ONE.divide(t, 4, RoundingMode.HALF_UP);
                System.out.printf("  | %-22s | R$ %-17.4f |%n", m[1], inv);
            }
            System.out.println("  +------------------------+----------------------+");
        } catch (Exception e) {
            System.out.println("  Erro: " + e.getMessage());
        }
    }

    private void exibirTabelaCotacoes() {
        System.out.println("\n  Buscando cotacoes...");
        try {
            Map<String, BigDecimal> taxas = cambioService.obterTaxas();
            exibirCabecalhoCambio();
            System.out.println("\n  +===========================================================+");
            System.out.println("  |             TABELA COMPLETA DE COTACOES                  |");
            System.out.println("  +---------------------+--------------------+----------------+");
            System.out.println("  |       MOEDA         |  1 BRL equivale a  | 1 unid. em R$  |");
            System.out.println("  +---------------------+--------------------+----------------+");
            for (Object[] m : new Object[][]{{"USD","Dolar (USD)    "},{"EUR","Euro (EUR)     "},{"ARS","Peso Arg (ARS) "}}) {
                BigDecimal taxa = taxas.get(m[0]);
                BigDecimal inv = BigDecimal.ONE.divide(taxa, 4, RoundingMode.HALF_UP);
                System.out.printf("  | %-19s | %10.6f %-7s | R$ %10.4f |%n", m[1], taxa, m[0], inv);
            }
            System.out.println("  +---------------------+--------------------+----------------+");

            BigDecimal cem = new BigDecimal("100.00");
            System.out.println("\n  [ SIMULACAO: R$ 100,00 equivalem a... ]");
            System.out.println("  +------------------------+--------------------+");
            for (Object[] m : new Object[][]{{"USD","Dolar Americano (USD)","$   "},{"EUR","Euro (EUR)           ","\u20ac   "},{"ARS","Peso Argentino (ARS) ","$ARS"}}) {
                BigDecimal res = cambioService.converterDeBRL(cem, (String) m[0]);
                System.out.printf("  | %-22s | %s %-13.2f |%n", m[1], m[2], res);
            }
            System.out.println("  +------------------------+--------------------+");
        } catch (Exception e) {
            System.out.println("  Erro: " + e.getMessage());
        }
    }

    private void exibirCabecalhoCambio() {
        System.out.println("\n  =============================================================");
        if (cambioService.isUsouFallback()) {
            System.out.println("  [!] API indisponivel. Usando cotacoes de referencia.");
        } else {
            System.out.println("  [OK] Cotacoes em tempo real (exchangerate-api.com).");
        }
        System.out.println("       Atualizado em: " + cambioService.getUltimaAtualizacao());
        System.out.println("  =============================================================");
    }

    // ==================== LEITURA SEGURA ====================

    private Integer lerInteiroSeguro() {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    private Double lerDoubleSeguro() {
        try { return Double.parseDouble(scanner.nextLine().trim().replace(",", ".")); }
        catch (NumberFormatException e) { return null; }
    }
}
