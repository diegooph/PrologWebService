package br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.motivo;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 24/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MotivoDescarte extends Motivo {
    public static final String TIPO_MOTIVO_DESCARTE = "MOTIVO_DESCARTE";

    private Long codEmpresa;

    public MotivoDescarte() {
        setTipo(TIPO_MOTIVO_DESCARTE);
    }

    public MotivoDescarte(@NotNull Long codigo,
                          @NotNull String motivo,
                          boolean ativo,
                          @NotNull Long codEmpresa) {
        super(TIPO_MOTIVO_DESCARTE, codigo, motivo, ativo);
        this.codEmpresa = codEmpresa;
    }

    @NotNull
    public Long getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodEmpresa(@NotNull Long codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    @Override
    public String toString() {
        return "MotivoDescarte{" +
                "codEmpresa=" + codEmpresa +
                '}';
    }
}
