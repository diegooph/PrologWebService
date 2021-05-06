package br.com.zalf.prolog.webservice.gente.faleConosco;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.gente.colaborador.ColaboradorService;
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
                                    final String cpf,
                                    final String equipe,
                                    final Long codUnidade,
                                    final String status,
                                    final String categoria) {

        try {
            return dao.getAll(dataInicial, dataFinal, limit, offset, cpf, equipe, codUnidade, status, categoria);
        } catch (final Exception e) {
            Log.e(TAG, String.format("Erro ao buscar os fale conosco. \n" +
                                             "codUnidade: %d \n" +
                                             "equipe: %s \n" +
                                             "cpf: %s \n" +
                                             "status: %s \n" +
                                             "categoria: %s \n" +
                                             "limit: %d \n" +
                                             "offset: %d \n" +
                                             "dataInicial: %s \n" +
                                             "dataFinal: %s",
                                     codUnidade,
                                     equipe,
                                     cpf,
                                     status,
                                     categoria,
                                     limit,
                                     offset,
                                     new Date(dataInicial),
                                     new Date(dataFinal)), e);
            return null;
        }
    }

    public List<FaleConosco> getByColaborador(@NotNull final String token,
                                              @NotNull final Long cpf,
                                              @NotNull final String status) {
        try {
            return dao.getByColaborador(new ColaboradorService().getByToken(token).getCodEmpresa(),
                                        cpf,
                                        status);
        } catch (final Exception e) {
            Log.e(TAG, String.format("Erro ao buscar os fale conosco do colaborador. \n" +
                                             "cpf: %d \n" +
                                             "status: %s", cpf, status), e);
            return null;
        }
    }
}