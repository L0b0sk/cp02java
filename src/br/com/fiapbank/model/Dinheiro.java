package br.com.fiapbank.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value Object que representa um valor monetário.
 * Encapsula BigDecimal e oferece operações seguras de soma, subtração e comparação.
 */
public class Dinheiro {

    private BigDecimal valor;

    public Dinheiro(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Valor monetário não pode ser nulo.");
        }
        this.valor = valor.setScale(2, RoundingMode.HALF_UP);
    }

    public Dinheiro(String valor) {
        this(new BigDecimal(valor));
    }

    public static Dinheiro zero() {
        return new Dinheiro(BigDecimal.ZERO);
    }

    public Dinheiro somar(Dinheiro outro) {
        return new Dinheiro(this.valor.add(outro.valor));
    }

    public Dinheiro subtrair(Dinheiro outro) {
        return new Dinheiro(this.valor.subtract(outro.valor));
    }

    public Boolean maiorQue(Dinheiro outro) {
        return this.valor.compareTo(outro.valor) > 0;
    }

    public Boolean menorQue(Dinheiro outro) {
        return this.valor.compareTo(outro.valor) < 0;
    }

    public Boolean maiorOuIgualQue(Dinheiro outro) {
        return this.valor.compareTo(outro.valor) >= 0;
    }

    public Boolean igualA(Dinheiro outro) {
        return this.valor.compareTo(outro.valor) == 0;
    }

    public BigDecimal getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Dinheiro dinheiro = (Dinheiro) obj;
        return valor != null && valor.compareTo(dinheiro.valor) == 0;
    }

    @Override
    public int hashCode() {
        return valor != null ? valor.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("R$ %,.2f", valor);
    }
}
