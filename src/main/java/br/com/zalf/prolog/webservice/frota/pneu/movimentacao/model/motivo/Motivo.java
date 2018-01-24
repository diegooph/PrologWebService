package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.motivo;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 24/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class Motivo {

    private Long codigo;
    private String motivo;
    private boolean ativo;

    public Motivo() {
    }

    public Motivo(@NotNull Long codigo, @NotNull String motivo, boolean ativo) {
        this.codigo = codigo;
        this.motivo = motivo;
        this.ativo = ativo;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(@NotNull Long codigo) {
        this.codigo = codigo;
    }

    @NotNull
    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(@NotNull String motivo) {
        this.motivo = motivo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
