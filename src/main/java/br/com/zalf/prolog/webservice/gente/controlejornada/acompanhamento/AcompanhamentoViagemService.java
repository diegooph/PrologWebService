package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.descanso.ViagemEmDescanso;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 31/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AcompanhamentoViagemService {
    @NotNull
    private static final String TAG = AcompanhamentoViagemService.class.getSimpleName();
    @NotNull
    private final AcompanhamentoViagemDao dao = Injection.provideAcompanhamentoViagemDao();

    @NotNull
    public ViagemEmDescanso getColaboradoresEmDescanso(@NotNull final Long codUnidade,
                                                       @NotNull final List<Long> codCargos) throws ProLogException {
        try {
            return dao.getColaboradoresEmDescanso(codUnidade, codCargos);
        } catch (final Throwable t) {
            final String errorMessage = String.format(
                    "Erro ao buscar colaboradores em descanso da unidade %d e cargos %s",
                    codUnidade,
                    codCargos);
            Log.e(TAG, errorMessage, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar colaboradores em descanso, tente novamente");
        }
    }
}