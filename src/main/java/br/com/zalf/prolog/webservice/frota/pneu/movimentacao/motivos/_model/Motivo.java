package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 24/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class Motivo {
    @NotNull
    @Exclude
    private String tipo;
    private Long codigo;
    private String motivo;
    private boolean ativo;

    public Motivo() {
    }

    public Motivo(@NotNull final String tipo,
                  @NotNull final Long codigo,
                  @NotNull final String motivo,
                  final boolean ativo) {
        this.tipo = tipo;

        this.codigo = codigo;
        this.motivo = motivo;
        this.ativo = ativo;
    }

    @NotNull
    public String getTipo() {
        return tipo;
    }

    public void setTipo(@NotNull final String tipo) {
        this.tipo = tipo;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(@NotNull final Long codigo) {
        this.codigo = codigo;
    }

    @NotNull
    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(@NotNull final String motivo) {
        this.motivo = motivo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(final boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Motivo{" +
                "tipo='" + tipo + '\'' +
                ", codigo=" + codigo +
                ", motivo='" + motivo + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}
