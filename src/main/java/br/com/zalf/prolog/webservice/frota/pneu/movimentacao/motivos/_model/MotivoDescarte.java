package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import org.jetbrains.annotations.NotNull;

/**
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 * @deprecated at 2020-04-02. É necessário refatorar esse objeto para um objeto específico do motivo de descarte,
 * sem herança.
 * <p>
 * Created on 24/01/18.
 */
@Deprecated
public final class MotivoDescarte extends Motivo {
    public static final String TIPO_MOTIVO_DESCARTE = "MOTIVO_DESCARTE";

    private Long codEmpresa;

    public MotivoDescarte() {
        setTipo(TIPO_MOTIVO_DESCARTE);
    }

    public MotivoDescarte(@NotNull final Long codigo,
                          @NotNull final String motivo,
                          final boolean ativo,
                          @NotNull final Long codEmpresa) {
        super(TIPO_MOTIVO_DESCARTE, codigo, motivo, ativo);
        this.codEmpresa = codEmpresa;
    }

    @NotNull
    public Long getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodEmpresa(@NotNull final Long codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    @Override
    public String toString() {
        return "MotivoDescarte{" +
                "codEmpresa=" + codEmpresa +
                '}';
    }
}
