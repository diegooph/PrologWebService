package br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.OrderByCreator;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.autenticacao._model.token.TokenCleaner;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuTipoServico;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 24/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PneuTipoServicoService {

    private static final String TAG = PneuTipoServicoService.class.getSimpleName();
    @NotNull
    private final PneuTipoServicoDao dao = Injection.providePneuTipoServicoDao();
    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

    public AbstractResponse insertPneuTipoServico(@NotNull final String token,
                                                  @NotNull final PneuTipoServico tipoServico) throws ProLogException {
        try {
            return ResponseWithCod.ok("Tipo de Serviço inserido com sucesso",
                    dao.insertPneuTipoServico(TokenCleaner.getOnlyToken(token), tipoServico));
        } catch (Throwable e) {
            final String errorMessage = "Erro ao inserir o tipo de serviço";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    public Response atualizaPneuTipoServico(@NotNull final String token,
                                            @NotNull final Long codEmpresa,
                                            @NotNull final PneuTipoServico tipoServico) throws ProLogException {
        try {
            dao.atualizaPneuTipoServico(TokenCleaner.getOnlyToken(token), codEmpresa, tipoServico);
            return Response.ok("Tipo de Serviço atualizado com sucesso");
        } catch (Throwable e) {
            final String errorMessage = "Erro ao atualizar o tipo de serviço";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    public List<PneuTipoServico> getPneuTiposServicos(@NotNull final Long codEmpresa,
                                                      @NotNull final List<String> orderBy,
                                                      @Nullable final Boolean ativos) throws ProLogException {
        try {
            return dao.getPneuTiposServicos(codEmpresa, OrderByCreator.createFrom(orderBy), ativos);
        } catch (Throwable e) {
            final String errorMessage = "Erro ao buscar os tipos de serviços";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    public PneuTipoServico getPneuTipoServico(@NotNull final Long codEmpresa,
                                              @NotNull final Long codTipoServico) throws ProLogException {
        try {
            return dao.getPneuTipoServico(codEmpresa, codTipoServico);
        } catch (Throwable e) {
            final String errorMessage = "Erro ao buscar o tipo de serviço";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }

    public Response alterarStatusPneuTipoServico(@NotNull final String token,
                                                 @NotNull final Long codEmpresa,
                                                 @NotNull final PneuTipoServico tipoServico) throws ProLogException {
        try {
            dao.alterarStatusPneuTipoServico(TokenCleaner.getOnlyToken(token), codEmpresa, tipoServico);
            return Response.ok("Status de serviço alterado com sucesso");
        } catch (Throwable e) {
            final String errorMessage = "Erro ao alterar o status do serviço";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }
    }
}