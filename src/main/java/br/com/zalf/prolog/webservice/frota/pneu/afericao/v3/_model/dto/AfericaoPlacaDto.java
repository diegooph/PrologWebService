package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-02-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class AfericaoPlacaDto {

    @NotNull
    Long kmVeiculo;
    @NotNull
    String placaVeiculo;
    @Nullable
    String identificadorFrota;
    @NotNull
    DadosGeraisAfericao dadosGerais;
}