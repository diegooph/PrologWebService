package br.com.zalf.prolog.webservice.v3.frota.afericao._model;

import lombok.Value;

import java.math.BigDecimal;

/**
 * Created on 2021-04-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class MedidaDto {
    Long codPneu;
    Integer posicao;
    BigDecimal psi;
    Integer vidaMomentoAfericao;
    BigDecimal alturaSulcoInterno;
    BigDecimal alturaSulcoCentralInterno;
    BigDecimal alturaSulcoCentralExterno;
    BigDecimal alturaSulcoExterno;
}
