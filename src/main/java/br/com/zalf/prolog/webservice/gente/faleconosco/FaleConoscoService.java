package br.com.zalf.prolog.webservice.gente.faleconosco;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

/**
 * Classe FaleConoscoService responsavel por comunicar-se com a interface DAO
 */
public class FaleConoscoService {
    private static final String TAG = FaleConoscoService.class.getSimpleName();
    private final FaleConoscoDao dao = Injection.provideFaleConoscoDao();

    public AbstractResponse insert(@NotNull final FaleConosco faleConosco,
                                   @NotNull final Long codUnidade) {
        try {
            return ResponseWithCod.ok("Fale conosco inserido com sucesso.", dao.insert(faleConosco, codUnidade));
        } catch (final Exception e) {
            Log.e(TAG, String.format("Erro ao inserir o fale conosco. \n" +
                                             "codUnidade: %d", codUnidade), e);
            return Response.error("Erro ao inserir fale conosco.");
        }
    }

    public boolean insertFeedback(@NotNull final FaleConosco faleConosco,
                                  @NotNull final Long codUnidade) {
        try {
            return dao.insertFeedback(faleConosco, codUnidade);
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao inserir o feedback no fale conosco. \n" +
                                             "codUnidade: %d", codUnidade), e);
            return false;
        }
    }

    public List<FaleConosco> getAll(final long dataInicial,
                                    final long dataFinal,
                                    final int limit,
                                    final int offset,
                                    final Long codColaborador,
                                    final String equipe,
                                    final Long codUnidade,
                                    final String status,
                                    final String categoria) {

        try {
            return dao.getAll(dataInicial,
                              dataFinal,
                              limit,
                              offset,
                              codColaborador,
                              equipe,
                              codUnidade,
                              status,
                              categoria);
        } catch (final Exception e) {
            Log.e(TAG, String.format("Erro ao buscar os fale conosco. \n" +
                                             "codUnidade: %d \n" +
                                             "equipe: %s \n" +
                                             "codColaborador: %s \n" +
                                             "status: %s \n" +
                                             "categoria: %s \n" +
                                             "limit: %d \n" +
                                             "offset: %d \n" +
                                             "dataInicial: %s \n" +
                                             "dataFinal: %s",
                                     codUnidade,
                                     equipe,
                                     codColaborador,
                                     status,
                                     categoria,
                                     limit,
                                     offset,
                                     new Date(dataInicial),
                                     new Date(dataFinal)), e);
            return null;
        }
    }

    public List<FaleConosco> getByColaborador(@NotNull final Long codColaborador,
                                              @NotNull final String status) {
        try {
            return dao.getByColaborador(codColaborador,
                                        status);
        } catch (final Exception e) {
            Log.e(TAG, String.format("Erro ao buscar os fale conosco do colaborador. \n" +
                                             "codigoColaborador: %d \n" +
                                             "status: %s", codColaborador, status), e);
            return null;
        }
    }
}