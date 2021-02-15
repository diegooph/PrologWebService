package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value
public class AfericaoAvulsaDto {

    @NotNull
    DadosGeraisAfericao dadosGerais;

    @Builder(toBuilder = true)
    private AfericaoAvulsaDto(@NotNull final DadosGeraisAfericao dadosGerais) {
        this.dadosGerais = dadosGerais;
    }
}
