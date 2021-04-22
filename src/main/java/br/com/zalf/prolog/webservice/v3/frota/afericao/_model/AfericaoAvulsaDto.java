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
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class AfericaoAvulsaDto {
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
    @Nullable
    List<MedidaDto> medidas;
}
