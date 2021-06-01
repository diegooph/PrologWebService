package br.com.zalf.prolog.webservice.v3.frota.servicopneu._model;

import lombok.Getter;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
@Getter
public class FiltroServicoListagemDto {
    @NotNull
    List<Long> codUnidades;
    @Nullable
    Long codVeiculo;
    @Nullable
    Long codPneu;
    @Nullable
    ServicoPneuStatus status;
    int limit;
    int offset;
}
