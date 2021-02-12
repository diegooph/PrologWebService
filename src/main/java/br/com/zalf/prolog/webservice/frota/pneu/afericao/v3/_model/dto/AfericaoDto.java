package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 2021-02-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Getter
@AllArgsConstructor
public abstract class AfericaoDto {

    @NotNull
    protected final Long codigo;

    @NotNull
    protected final Long codUnidade;

    @NotNull
    protected final LocalDateTime dataHora;

    @NotNull
    protected final TipoMedicaoColetadaAfericao tipoMedicaoColetada;

    @NotNull
    protected final Long tempoRealizacao;

    @NotNull
    protected final FormaColetaDadosAfericaoEnum formaColetaDados;

    @NotNull
    protected final String cpfAferidor;

    @NotNull
    protected final String nomeAferidor;

    @NotNull
    protected final Long kmVeiculo;
}
