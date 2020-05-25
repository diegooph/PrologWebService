package br.com.zalf.prolog.webservice.entrega.mapa._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-05-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class CelulaPlanilhaMapaErro {
    @NotNull
    private final String mensagemErro;
    @NotNull
    private final String valorRecebidoComErro;
    @NotNull
    private final String exemplosValorEsperado;
}
