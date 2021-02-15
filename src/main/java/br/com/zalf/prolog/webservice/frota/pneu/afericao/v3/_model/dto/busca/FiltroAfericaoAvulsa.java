package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.busca;

import lombok.Builder;
import lombok.Getter;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Getter
public class FiltroAfericaoAvulsa {

    DadosGeraisFiltro dadosGerais;

    @Builder
    private FiltroAfericaoAvulsa(final DadosGeraisFiltro dadosGerais) {
        this.dadosGerais = dadosGerais;
    }
}
