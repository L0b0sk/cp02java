package br.com.fiapbank.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Classe abstrata base para todas as entidades do sistema.
 * Contém ID único (UUID) e data de criação.
 */
public abstract class BaseEntity {

    private UUID id;
    private LocalDate dataCriacao;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.dataCriacao = LocalDate.now();
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BaseEntity that = (BaseEntity) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
