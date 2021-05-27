package br.com.zalf.prolog.webservice.v3.frota.afericao.valores._model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created on 2021-05-27
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Data
public final class AfericaoPneuValorId implements Serializable {
    private Long codAfericao;
    private Long codPneu;
}
