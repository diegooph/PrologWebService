package br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model;

import lombok.Builder;
import lombok.Value;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Builder(setterPrefix = "with")
@Value
public class AlteracaoKmResponse {
    long kmAntigo;
}
