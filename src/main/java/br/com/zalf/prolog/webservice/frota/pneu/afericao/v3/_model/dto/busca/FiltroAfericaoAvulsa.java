package br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.busca;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

/**
 * Created on 2021-02-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */

@EqualsAndHashCode(callSuper = true)
@Getter
public class FiltroAfericaoAvulsa extends FiltroAfericao {


    @Builder
    private FiltroAfericaoAvulsa(@NotNull final Long codUnidade,
                                @NotNull final LocalDate dataInicial,
                                @NotNull final LocalDate dataFinal,
                                final int limit,
                                final int offset) {
        super(codUnidade, dataInicial, dataFinal, limit, offset);
    }
}
