package br.com.fiapbank.infrastructure;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Serviço de câmbio em tempo real.
 * Usa a API pública exchangerate-api.com para buscar cotações.
 * Camada de Infraestrutura — responsável por integrações externas.
 */
public class CambioService {

    // API gratuita e sem chave necessária para cotações básicas
    private static final String API_URL =
            "https://api.exchangerate-api.com/v4/latest/BRL";

    // Fallback caso a API esteja indisponível (cotações aproximadas)
    private static final Map<String, BigDecimal> FALLBACK = new LinkedHashMap<>();

    static {
        FALLBACK.put("USD", new BigDecimal("0.1800"));
        FALLBACK.put("EUR", new BigDecimal("0.1650"));
        FALLBACK.put("ARS", new BigDecimal("180.00"));
    }

    private Map<String, BigDecimal> taxasCache;
    private LocalDateTime ultimaAtualizacao;
    private Boolean usouFallback;

    public CambioService() {
        this.taxasCache = null;
        this.usouFallback = false;
    }

    /**
     * Busca as taxas de câmbio. Usa cache se já tiver buscado nessa sessão.
     * Retorna mapa: moeda -> quantos dessa moeda equivalem a 1 BRL.
     */
    public Map<String, BigDecimal> obterTaxas() {
        if (taxasCache != null) {
            return taxasCache;
        }

        try {
            taxasCache = buscarDaApi();
            usouFallback = false;
        } catch (Exception e) {
            taxasCache = new LinkedHashMap<>(FALLBACK);
            usouFallback = true;
        }

        ultimaAtualizacao = LocalDateTime.now();
        return taxasCache;
    }

    private Map<String, BigDecimal> buscarDaApi() throws Exception {
        URL url = URI.create(API_URL).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        if (conn.getResponseCode() != 200) {
            throw new Exception("API retornou status " + conn.getResponseCode());
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder json = new StringBuilder();
        String linha;
        while ((linha = reader.readLine()) != null) {
            json.append(linha);
        }
        reader.close();
        conn.disconnect();

        return parsearRates(json.toString());
    }

    /**
     * Parse manual do JSON para evitar dependência de biblioteca externa.
     * Extrai apenas USD, EUR e ARS do bloco "rates".
     */
    private Map<String, BigDecimal> parsearRates(String json) throws Exception {
        Map<String, BigDecimal> taxas = new LinkedHashMap<>();
        String[] moedas = {"USD", "EUR", "ARS"};

        Integer inicio = json.indexOf("\"rates\"");
        if (inicio == -1) throw new Exception("Campo rates nao encontrado");

        for (String moeda : moedas) {
            String chave = "\"" + moeda + "\":";
            Integer pos = json.indexOf(chave, inicio);
            if (pos == -1) throw new Exception("Moeda " + moeda + " nao encontrada");

            Integer inicioValor = pos + chave.length();
            Integer fimValor = json.indexOf(",", inicioValor);
            if (fimValor == -1) fimValor = json.indexOf("}", inicioValor);

            String valorStr = json.substring(inicioValor, fimValor).trim();
            taxas.put(moeda, new BigDecimal(valorStr).setScale(6, RoundingMode.HALF_UP));
        }

        return taxas;
    }

    /**
     * Converte um valor em BRL para a moeda destino.
     */
    public BigDecimal converterDeBRL(BigDecimal valorBRL, String moedaDestino) {
        Map<String, BigDecimal> taxas = obterTaxas();
        BigDecimal taxa = taxas.get(moedaDestino);
        if (taxa == null) throw new IllegalArgumentException("Moeda nao suportada: " + moedaDestino);
        return valorBRL.multiply(taxa).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Converte um valor em moeda estrangeira para BRL.
     * Taxa de câmbio: 1 BRL = X moeda, então 1 moeda = 1/X BRL.
     */
    public BigDecimal converterParaBRL(BigDecimal valorMoeda, String moedaOrigem) {
        Map<String, BigDecimal> taxas = obterTaxas();
        BigDecimal taxa = taxas.get(moedaOrigem);
        if (taxa == null) throw new IllegalArgumentException("Moeda nao suportada: " + moedaOrigem);
        // taxa = quantidade de moeda por 1 BRL
        // logo: valor em BRL = valorMoeda / taxa
        return valorMoeda.divide(taxa, 2, RoundingMode.HALF_UP);
    }

    public Boolean isUsouFallback() {
        return usouFallback;
    }

    public String getUltimaAtualizacao() {
        if (ultimaAtualizacao == null) return "N/D";
        return ultimaAtualizacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public void limparCache() {
        this.taxasCache = null;
    }
}
