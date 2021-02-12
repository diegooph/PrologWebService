package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public class AfericaoAvulsaDto extends AfericaoDto {

    @Builder(toBuilder = true)
    private AfericaoAvulsaDto(@NotNull final Long codigo,
                              @NotNull final Long codUnidade,
                              @NotNull final LocalDateTime dataHora,
                              @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetada,
                              @NotNull final Long tempoRealizacao,
                              @NotNull final FormaColetaDadosAfericaoEnum formaColetaDados,
                              @NotNull final String cpfAferidor,
                              @NotNull final String nomeAferidor,
                              @NotNull final Long kmVeiculo) {
        super(codigo,
              codUnidade,
              dataHora,
              tipoMedicaoColetada,
              tempoRealizacao,
              formaColetaDados,
              cpfAferidor,
              nomeAferidor,
              kmVeiculo);
    }
}
