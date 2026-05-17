package br.com.fiapbank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Value Object que representa uma movimentação no histórico da conta.
 * Armazena data/hora exatas, tipo da operação e valor.
 * Para movimentações do tipo CAMBIO, armazena informações adicionais.
 */
public class Movimentacao {

    private LocalDateTime dataHora;
    private TipoMovimentacao tipo;
    private Dinheiro valor;

    // Campos extras para CAMBIO
    private String moedaOrigem;
    private String moedaDestino;
    private BigDecimal valorEstrangeiro;
    private BigDecimal taxaCambio;
    private String descricaoCambio;

    public Movimentacao(LocalDateTime dataHora, Dinheiro valor, TipoMovimentacao tipo) {
        this.dataHora = dataHora;
        this.valor = valor;
        this.tipo = tipo;
    }

    // Construtor específico para câmbio
    public Movimentacao(LocalDateTime dataHora, Dinheiro valorBRL, TipoMovimentacao tipo,
                        String moedaOrigem, String moedaDestino,
                        BigDecimal valorEstrangeiro, BigDecimal taxaCambio) {
        this.dataHora = dataHora;
        this.valor = valorBRL;
        this.tipo = tipo;
        this.moedaOrigem = moedaOrigem;
        this.moedaDestino = moedaDestino;
        this.valorEstrangeiro = valorEstrangeiro;
        this.taxaCambio = taxaCambio;
        this.descricaoCambio = String.format("BRL -> %s | %.2f %s @ taxa %.6f",
                moedaDestino, valorEstrangeiro, moedaDestino, taxaCambio);
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public Dinheiro getValor() {
        return valor;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public String getMoedaOrigem() { return moedaOrigem; }
    public String getMoedaDestino() { return moedaDestino; }
    public BigDecimal getValorEstrangeiro() { return valorEstrangeiro; }
    public BigDecimal getTaxaCambio() { return taxaCambio; }
    public String getDescricaoCambio() { return descricaoCambio; }

    public boolean isCambio() {
        return tipo == TipoMovimentacao.CAMBIO;
    }

    public String getDataHoraFormatada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dataHora.format(formatter);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Movimentacao that = (Movimentacao) obj;
        return dataHora != null && dataHora.equals(that.dataHora)
                && tipo == that.tipo
                && valor != null && valor.equals(that.valor);
    }

    @Override
    public int hashCode() {
        Integer result = dataHora != null ? dataHora.hashCode() : 0;
        result = 31 * result + (tipo != null ? tipo.hashCode() : 0);
        result = 31 * result + (valor != null ? valor.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (isCambio()) {
            return String.format("%-12s - %10s  %s  [%s]",
                    "CAMBIO", valor.toString(), getDataHoraFormatada(), descricaoCambio);
        }
        String sinal = (tipo == TipoMovimentacao.DEPOSITO || tipo == TipoMovimentacao.RENDIMENTO) ? "+" : "-";
        return String.format("%-12s %s %10s  %s",
                tipo.name(), sinal, valor.toString(), getDataHoraFormatada());
    }
}
