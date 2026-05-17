DOCUMENTAÇÃO TÉCNICA
SISTEMA FIAP ATM - AUTOMAÇÃO BANCÁRIA

ÍNDICE

1. DESCRIÇÃO DO PROJETO
2. FUNCIONALIDADES
3. TECNOLOGIAS UTILIZADAS
4. ESTRUTURA DO PROJETO
5. GUIA DE USO
6. REGRAS DE NEGÓCIO
7. RECURSOS DE SEGURANÇA
8. CARACTERÍSTICAS TÉCNICAS
9. CONTRIBUIÇÃO


*1. DESCRIÇÃO DO PROJETO*
======================================

O FIAP ATM é um simulador de terminal bancário desenvolvido em Java puro, 
sem utilização de frameworks externos. O projeto aplica conceitos de 
Programação Orientada a Objetos, padrões de projeto e arquitetura em camadas.

O sistema simula operações bancárias completas, incluindo autenticação de 
usuários, gerenciamento de contas, movimentações financeiras, persistência 
de dados criptografados e integração com serviço de câmbio em tempo real.

A aplicação foi estruturada utilizando uma arquitetura inspirada em 
Clean Architecture, promovendo separação de responsabilidades, organização 
do código e facilidade de manutenção.

O projeto também implementa recursos avançados como criptografia AES-256, 
persistência em arquivo local e integração com API externa de cotações.


*2. FUNCIONALIDADES
======================================

O sistema contempla as seguintes funcionalidades:

   - Cadastro de Usuário
        * Registro de nome, email, CPF e senha
        * Validação de senha forte utilizando regex
        * Persistência automática dos dados

   - Sistema de Login
        * Autenticação via email e senha
        * Bloqueio automático após 3 tentativas inválidas
        * Controle de sessão do usuário

   - Sistema de Contas Bancárias
        * Conta Corrente
        * Conta Poupança
        * Controle de saldo em múltiplas moedas

   - Operações Bancárias
        * Consulta de saldo
        * Depósitos
        * Saques com validações
        * Histórico completo de movimentações

   - Sistema de Câmbio (Diferencial do Projeto)
        * Conversão de moedas em tempo real
        * Integração com API pública de câmbio
        * Atualização automática de cotações
        * Suporte para USD, EUR e ARS

   - Persistência de Dados
        * Armazenamento local em arquivo usuarios.dat
        * Criptografia completa dos dados sensíveis

   - Interface Terminal
        * Terminal interativo via console
        * Logo em ASCII art
        * Efeito de digitação animado


*3. TECNOLOGIAS UTILIZADAS
======================================

A seguir estão listadas as principais tecnologias empregadas no desenvolvimento:

   - Java 11+
        * Linguagem principal da aplicação

   - Programação Orientada a Objetos
        * Encapsulamento
        * Herança
        * Polimorfismo
        * Abstração

   - Java.math.BigDecimal
        * Precisão monetária para operações financeiras

   - Java.time
        * Controle de data e hora das movimentações

   - Java.util.UUID
        * Geração de identificadores únicos

   - Java.util.Scanner
        * Entrada de dados via terminal

   - Javax.crypto
        * Implementação de criptografia AES-256

   - Java.net.HttpURLConnection
        * Comunicação com API de câmbio

   - Java.io
        * Persistência de usuários em arquivo


*4. ESTRUTURA DO PROJETO
======================================

A organização dos arquivos do projeto segue a seguinte estrutura:

   src/br/com/fiapbank/
   │
   ├── presentation/
   │   └── TerminalBancarioController.java
   │
   ├── application/
   │   ├── Main.java
   │   ├── ContaService.java
   │   ├── AutorizacaoService.java
   │   └── ContaFactory.java
   │
   ├── model/
   │   ├── Conta.java
   │   ├── ContaCorrente.java
   │   ├── ContaPoupanca.java
   │   ├── Cliente.java
   │   ├── ContaAcesso.java
   │   ├── Dinheiro.java
   │   ├── Movimentacao.java
   │   ├── StatusConta.java
   │   └── TipoMovimentacao.java
   │
   └── infrastructure/
       ├── ContaRepository.java
       ├── UsuarioRepository.java
       └── CambioService.java

Estrutura interna do código:

   - Camada Presentation
        * Responsável pela interação com o usuário
        * Controle dos menus e interface do terminal

   - Camada Application
        * Orquestra regras de negócio e fluxo do sistema
        * Gerencia autenticação e operações bancárias

   - Camada Model
        * Contém entidades do domínio bancário
        * Implementa regras financeiras

   - Camada Infrastructure
        * Persistência de usuários
        * Integração com API externa
        * Gerenciamento de dados do sistema


*5. GUIA DE USO
======================================

5.1. PRÉ-REQUISITOS

   Para execução do sistema é necessário:

   - Java 11 ou superior instalado

5.2. COMPILAÇÃO

   A partir da raiz do projeto:

   ----------------------------------------------------------------------
   find src -name "*.java" > sources.txt
   mkdir -p out
   javac -d out @sources.txt
   ----------------------------------------------------------------------

5.3. EXECUÇÃO

   Execute o sistema com:

   ----------------------------------------------------------------------
   java -cp out br.com.fiapbank.application.Main
   ----------------------------------------------------------------------

5.4. FLUXO DA APLICAÇÃO

   Ao iniciar o sistema:

   1. O usuário escolhe entre login ou registro
   2. Realiza autenticação
   3. Acessa o menu principal
   4. Executa operações bancárias
   5. Pode consultar saldo, realizar câmbio e visualizar histórico

5.5. MENU PRINCIPAL

   O sistema oferece as seguintes opções:

   +-------------------------------------------------------------------+
   | [1] CONSULTAR SALDO E HISTÓRICO                                   |
   | [2] REALIZAR DEPÓSITO                                             |
   | [3] REALIZAR SAQUE                                                |
   | [4] CÂMBIO DE MOEDAS                                              |
   | [5] ENCERRAR SESSÃO                                               |
   +-------------------------------------------------------------------+


*6. REGRAS DE NEGÓCIO
======================================

6.1. CONTA CORRENTE

   A Conta Corrente possui as seguintes regras:

   - Taxa de R$ 25,00 aplicada a cada saque
   - Taxa cobrada apenas se houver saldo suficiente
   - Registro automático como movimentação TAXA

6.2. CONTA POUPANÇA

   A Conta Poupança possui as seguintes regras:

   - Aplicação de rendimento de 1,1% após saque
   - Registro automático como movimentação RENDIMENTO

6.3. AUTENTICAÇÃO

   O sistema exige senha forte contendo:

   - Mínimo de 8 caracteres
   - Pelo menos 1 número
   - Pelo menos 1 letra maiúscula
   - Pelo menos 1 caractere especial

   Além disso:

   - O sistema bloqueia acesso após 3 tentativas inválidas

6.4. CÂMBIO

   O sistema implementa:

   - Conversão BRL para moedas estrangeiras
   - Consulta de cotações em tempo real
   - Atualização automática de valores
   - Fallback para cotações locais caso a API falhe

   Moedas suportadas:

   - USD
   - EUR
   - ARS


*7. RECURSOS DE SEGURANÇA
======================================

O sistema implementa múltiplas camadas de segurança:

   - Criptografia AES-256
        * Proteção completa dos dados armazenados

   - Criptografia de Senhas
        * Senhas criptografadas individualmente

   - PBKDF2WithHmacSHA256
        * Derivação segura da chave AES
        * 65.536 iterações para maior proteção

   - Bloqueio de Login
        * Limite de 3 tentativas inválidas

   - Persistência Segura
        * Dados armazenados no arquivo usuarios.dat


*8. CARACTERÍSTICAS TÉCNICAS
======================================

8.1. ARQUITETURA EM CAMADAS

   O projeto utiliza separação de responsabilidades:

   - Presentation
   - Application
   - Model
   - Infrastructure

   Essa estrutura facilita:

   - Escalabilidade
   - Organização
   - Reutilização
   - Manutenção

8.2. TEMPLATE METHOD

   A classe Conta implementa o padrão Template Method:

   - realizarSaque() define fluxo principal
   - Subclasses aplicam regras específicas

8.3. FACTORY

   ContaFactory centraliza criação das contas:

   - ContaCorrente
   - ContaPoupanca

   Isso reduz acoplamento e facilita expansão.

8.4. SINGLETON

   As seguintes classes utilizam Singleton:

   - ContaFactory
   - ContaRepository
   - UsuarioRepository

8.5. VALUE OBJECT

   A classe Dinheiro encapsula BigDecimal:

   - Precisão financeira
   - Segurança monetária
   - Operações controladas

8.6. INTEGRAÇÃO COM API EXTERNA

   O sistema utiliza HttpURLConnection para:

   - Consulta de cotações
   - Atualização de moedas em tempo real
   - Comunicação HTTP com API pública


*CRÉDITOS
======================================

   - Desenvolvimento Principal: Bruno Lobosque
     Projeto acadêmico voltado para Programação Orientada a Objetos,
     arquitetura em camadas, segurança de dados e integração com APIs


DOCUMENTAÇÃO GERADA EM: 16/05/2026
======================================
