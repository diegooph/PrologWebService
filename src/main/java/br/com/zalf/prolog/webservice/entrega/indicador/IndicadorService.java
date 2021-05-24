package br.com.zalf.prolog.webservice.entrega.indicador;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.IndicadorAcumulado;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zalf on 14/09/16.
 */
public class IndicadorService {
    private static final String TAG = IndicadorService.class.getSimpleName();
    private final IndicadorDao dao = Injection.provideIndicadorDao();

    @NotNull
    public List<IndicadorAcumulado> getAcumuladoIndicadoresIndividual(@NotNull final Long cpf,
                                                                      final long dataInicial,
                                                                      final long dataFinal) {
        try {
            return dao.getAcumuladoIndicadoresIndividual(
                    cpf,
                    DateUtils.toLocalDateUtc(dataInicial),
                    DateUtils.toLocalDateUtc(dataFinal));
        } catch (final SQLException e) {
            Log.e(TAG, "Erro ao buscar os indicadores acumulados de um colaborador", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao buscar indicadores, tente novamente.");
        }
    }

    public List<Indicador> getExtratoIndicador(final Long dataInicial, final Long dataFinal, final String codRegional, final String codEmpresa,
                                               final String codUnidade, final String equipe, final String cpf, final String indicador) {
        try {
            return dao.getExtratoIndicador(dataInicial, dataFinal, codRegional, codEmpresa, codUnidade, equipe, cpf,
                                           indicador);
        } catch (final SQLException e) {
            Log.e(TAG, "Erro ao buscar o extrato de um indicadores de um colaborador", e);
            return null;
        }
    }
}
