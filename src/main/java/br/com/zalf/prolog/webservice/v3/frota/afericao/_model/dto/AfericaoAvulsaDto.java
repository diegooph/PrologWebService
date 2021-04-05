package br.com.zalf.prolog.webservice.v3.frota.afericao._model.dto;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class AfericaoAvulsaDto {

    @NotNull
    DadosGeraisAfericao dadosGerais;
}
