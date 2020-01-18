package br.com.zalf.prolog.webservice.frota.checklist.mudancaestrutura;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-11-12
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class DadosChecklistInsercaoEstrutraSuporte {
    @NotNull
    private final Long codUnidadeColaborador;
    @NotNull
    private final Long codColaborador;
    @NotNull
    private final Long codVeiculo;

    public DadosChecklistInsercaoEstrutraSuporte(@NotNull final Long codUnidadeColaborador,
                                                 @NotNull final Long codColaborador,
                                                 @NotNull final Long codVeiculo) {
        this.codUnidadeColaborador = codUnidadeColaborador;
        this.codColaborador = codColaborador;
        this.codVeiculo = codVeiculo;
    }

    @NotNull
    public Long getCodUnidadeColaborador() {
        return codUnidadeColaborador;
    }

    @NotNull
    public Long getCodColaborador() {
        return codColaborador;
    }

    @NotNull
    public Long getCodVeiculo() {
        return codVeiculo;
    }
}
