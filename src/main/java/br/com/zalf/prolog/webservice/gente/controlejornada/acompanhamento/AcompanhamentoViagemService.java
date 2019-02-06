package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.andamento.ViagemEmAndamento;
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
    public ViagemEmDescanso getColaboradoresEmDescanso(final Long codUnidade,
                                                       final List<Long> codCargos) throws ProLogException {
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

    @NotNull
    public ViagemEmAndamento getViagensEmAndamento(final Long codUnidade,
                                                   final List<Long> codCargos) throws ProLogException {
        try {
            return dao.getViagensEmAndamento(codUnidade, codCargos);
        } catch (final Throwable t) {
            final String errorMessage = String.format(
                    "Erro ao buscar viagens em andamento da unidade %d e cargos %s",
                    codUnidade,
                    codCargos);
            Log.e(TAG, errorMessage, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar viagens em andamento, tente novamente");
        }
    }

    @NotNull
    public MarcacaoAgrupadaAcompanhamento getMarcacaoInicioFim(final Long codUnidade,
                                                               final Long codInicio,
                                                               final Long codFim) throws ProLogException {
        try {
            if (codInicio == null && codFim == null) {
                throw new IllegalArgumentException("codInicio e codFim não podem ser ambos nulos!");
            }

            return dao.getMarcacaoInicioFim(codUnidade, codInicio, codFim);
        } catch (final Throwable t) {
            final String errorMessage = String.format(
                    "Erro ao buscar marcações de início e fim\ncodUnidade: %d\ncodInicio: %d\ncodFim: %d",
                    codUnidade,
                    codInicio,
                    codFim);
            Log.e(TAG, errorMessage, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar marcação(ões), tente novamente");
        }
    }
}