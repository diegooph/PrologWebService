package br.com.zalf.prolog.webservice.v3.frota.afericao._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 2021-02-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class AfericaoPlacaDto {
    @NotNull
    Long kmVeiculo;
    @NotNull
    String placaVeiculo;
    @Nullable
    String identificadorFrota;
    @NotNull
    Long codigo;
    @NotNull
    Long codUnidade;
    @NotNull
    LocalDateTime dataHoraAfericaoUtc;
    @NotNull
    LocalDateTime dataHoraAfericaoTzAplicado;
    @NotNull
    TipoMedicaoColetadaAfericao tipoMedicaoColetada;
    @NotNull
    TipoProcessoColetaAfericao tipoProcessoColeta;
    @NotNull
    Long tempoRealizacao;
    @NotNull
    FormaColetaDadosAfericaoEnum formaColetaDados;
    @NotNull
    Long codColaboradorAferidor;
    @NotNull
    String cpfAferidor;
    @NotNull
    String nomeAferidor;
    @NotNull
    List<MedidaDto> medidas;
}