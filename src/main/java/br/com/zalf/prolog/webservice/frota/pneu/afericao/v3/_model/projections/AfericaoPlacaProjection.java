package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.projections;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created on 2021-02-09
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public interface AfericaoPlacaProjection extends AfericaoProjection {

    @Value("#{target.PLACA_VEICULO}")
    String getPlacaVeiculo();

    @Value("#{target.IDENTIFICADOR_FROTA}")
    String getIdentificadorFrota();

    @Value("#{target.KM_VEICULO}")
    Long getKmVeiculo();
}
