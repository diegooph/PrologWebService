package br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@AllArgsConstructor(staticName = "of")
@Value
public class AlteracaoKmResponse {
    long kmAntigo;
    boolean kmFoiAlterado;
}
