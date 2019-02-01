package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento;

import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.andamento.ViagemEmAndamento;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.descanso.ViagemEmDescanso;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 31/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface AcompanhamentoViagemDao {

    @NotNull
    ViagemEmDescanso getColaboradoresEmDescanso(@NotNull final Long codUnidade,
                                                @NotNull final List<Long> codCargos) throws Throwable;

    @NotNull
    ViagemEmAndamento getViagensEmAndamento(@NotNull final Long codUnidade,
                                            @NotNull final List<Long> codCargos) throws Throwable;
}