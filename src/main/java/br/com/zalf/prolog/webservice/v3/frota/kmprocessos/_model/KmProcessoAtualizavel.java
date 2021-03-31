package br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-30
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface KmProcessoAtualizavel {
    @NotNull
    EntityKmColetado getEntityKmColetado(@NotNull final Long entityId,
                                         @NotNull final Long codVeiculo);

    void updateKmColetadoProcesso(@NotNull final Long codProcesso,
                                  @NotNull final Long codVeiculo,
                                  final long novoKm);
}
