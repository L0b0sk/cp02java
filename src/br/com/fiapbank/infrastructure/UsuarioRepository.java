package br.com.fiapbank.infrastructure;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Repositório de usuários com persistência em arquivo e criptografia AES-256.
 * Os dados (email, CPF, nome, senha) são criptografados antes de salvar.
 * Camada de Infraestrutura.
 */
public class UsuarioRepository {

    private static final String ARQUIVO_USUARIOS = "usuarios.dat";
    private static final String SEPARADOR = "||";
    private static final String SALT = "FiapBankSalt2025";
    private static final String CHAVE_MESTRA = "FiapBankMasterKey2025!@#";
    private static final String IV = "FiapBankIV123456"; // 16 bytes

    private static UsuarioRepository instance;

    private UsuarioRepository() {}

    public static UsuarioRepository getInstance() {
        if (instance == null) {
            instance = new UsuarioRepository();
        }
        return instance;
    }

    // ==================== MODELO DE USUÁRIO ====================

    public static class UsuarioData {
        public String nome;
        public String email;
        public String cpf;
        public String senhaHash; // senha criptografada
        public String tipoConta; // "1" ou "2"

        public UsuarioData(String nome, String email, String cpf, String senhaHash, String tipoConta) {
            this.nome = nome;
            this.email = email;
            this.cpf = cpf;
            this.senhaHash = senhaHash;
            this.tipoConta = tipoConta;
        }
    }

    // ==================== OPERAÇÕES PRINCIPAIS ====================

    /**
     * Registra um novo usuário, criptografando e salvando no arquivo.
     */
    public boolean registrarUsuario(String nome, String email, String cpf, String senha, String tipoConta) {
        try {
            if (buscarPorEmail(email) != null) {
                return false; // email já cadastrado
            }

            String senhaEncriptada = criptografar(senha);
            String linha = montarLinha(nome, email, cpf, senhaEncriptada, tipoConta);
            String linhaEncriptada = criptografar(linha);

            try (FileWriter fw = new FileWriter(ARQUIVO_USUARIOS, true);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(linhaEncriptada);
                bw.newLine();
            }
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao registrar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Autentica um usuário pelo email e senha.
     * Retorna o UsuarioData se autenticado, null caso contrário.
     */
    public UsuarioData autenticar(String email, String senha) {
        UsuarioData usuario = buscarPorEmail(email);
        if (usuario == null) return null;

        try {
            String senhaEncriptada = criptografar(senha);
            if (senhaEncriptada.equals(usuario.senhaHash)) {
                return usuario;
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar senha: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca um usuário pelo email no arquivo.
     */
    public UsuarioData buscarPorEmail(String email) {
        List<UsuarioData> usuarios = carregarTodos();
        for (UsuarioData u : usuarios) {
            if (u.email.equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Verifica se o arquivo de usuários existe e tem registros.
     */
    public boolean existemUsuarios() {
        File arquivo = new File(ARQUIVO_USUARIOS);
        return arquivo.exists() && arquivo.length() > 0;
    }

    // ==================== MÉTODOS INTERNOS ====================

    private List<UsuarioData> carregarTodos() {
        List<UsuarioData> lista = new ArrayList<>();
        File arquivo = new File(ARQUIVO_USUARIOS);
        if (!arquivo.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linhaEncriptada;
            while ((linhaEncriptada = br.readLine()) != null) {
                if (linhaEncriptada.trim().isEmpty()) continue;
                try {
                    String linha = descriptografar(linhaEncriptada.trim());
                    UsuarioData u = parsearLinha(linha);
                    if (u != null) lista.add(u);
                } catch (Exception e) {
                    // linha corrompida, ignora
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar usuarios: " + e.getMessage());
        }
        return lista;
    }

    private String montarLinha(String nome, String email, String cpf, String senhaHash, String tipoConta) {
        return nome + SEPARADOR + email + SEPARADOR + cpf + SEPARADOR + senhaHash + SEPARADOR + tipoConta;
    }

    private UsuarioData parsearLinha(String linha) {
        String[] partes = linha.split("\\|\\|");
        if (partes.length < 5) return null;
        return new UsuarioData(partes[0], partes[1], partes[2], partes[3], partes[4]);
    }

    // ==================== CRIPTOGRAFIA AES-256 ====================

    private SecretKeySpec gerarChave() throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(
                CHAVE_MESTRA.toCharArray(),
                SALT.getBytes(StandardCharsets.UTF_8),
                65536,
                256
        );
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    private IvParameterSpec gerarIV() {
        return new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
    }

    public String criptografar(String texto) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, gerarChave(), gerarIV());
        byte[] encrypted = cipher.doFinal(texto.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String descriptografar(String textoBase64) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, gerarChave(), gerarIV());
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(textoBase64));
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
