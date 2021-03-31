package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.busca;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class FiltroAfericaoAvulsa {
    @NotNull
    DadosGeraisFiltro dadosGerais;
}