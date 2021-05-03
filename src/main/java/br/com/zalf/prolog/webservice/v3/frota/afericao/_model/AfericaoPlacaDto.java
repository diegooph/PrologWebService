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
    Long codAfericao;
    @NotNull
    Long codUnidadeAfericao;
    @NotNull
    Long codColaborador;
    @NotNull
    String cpfColaborador;
    @NotNull
    String nomeColaborador;
    @NotNull
    Long codVeiculo;
    @NotNull
    String placaVeiculo;
    @Nullable
    String identificadorFrota;
    @NotNull
    Long kmVeiculoMomentoAfericao;
    @NotNull
    LocalDateTime dataHoraAfericaoUtc;
    @NotNull
    LocalDateTime dataHoraAfericaoTimeZoneAplicado;
    @NotNull
    TipoMedicaoColetadaAfericao tipoMedicaoColetada;
    @NotNull
    TipoProcessoColetaAfericao tipoProcessoColeta;
    @NotNull
    Long tempoRealizacaoAfericaoEmMilisegundos;
    @NotNull
    FormaColetaDadosAfericaoEnum formaColetaDados;
    @Nullable
    List<MedidaDto> medidasColetadas;
}