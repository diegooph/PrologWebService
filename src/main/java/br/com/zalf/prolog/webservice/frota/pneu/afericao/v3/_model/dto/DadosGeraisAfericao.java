package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 2021-02-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Builder
@Value
public class DadosGeraisAfericao {

    @NotNull
    Long codigo;

    @NotNull
    Long codUnidade;

    @NotNull
    LocalDateTime dataHora;

    @NotNull
    TipoMedicaoColetadaAfericao tipoMedicaoColetada;

    @NotNull
    Long tempoRealizacao;

    @NotNull
    FormaColetaDadosAfericaoEnum formaColetaDados;

    @NotNull
    String cpfAferidor;

    @NotNull
    String nomeAferidor;
}
