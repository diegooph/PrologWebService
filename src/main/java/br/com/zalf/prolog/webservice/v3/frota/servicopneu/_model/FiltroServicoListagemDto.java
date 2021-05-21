package br.com.zalf.prolog.webservice.v3.frota.servicopneu._model;

import lombok.Getter;
import lombok.Value;

import java.util.List;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
@Getter
public class FiltroServicoListagemDto {
    List<Long> codUnidades;
    Long codVeiculo;
    Long codPneu;
    ServicoPneuStatus status;
    int limit;
    int offset;
}
