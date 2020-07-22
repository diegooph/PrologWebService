package br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.*;
import br.com.zalf.prolog.webservice.integracao.praxio.utils.UnidadePraxioValidator;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 13/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiCadastroPneuService extends BaseIntegracaoService {
    @NotNull
    private static final String TAG = ApiCadastroPneuService.class.getSimpleName();
    @NotNull
    private final ApiCadastroPneuDao dao = new ApiCadastroPneuDaoImpl();

    @NotNull
    public List<ApiPneuCargaInicialResponse> inserirCargaInicialPneu(
            final String tokenIntegracao,
            final List<ApiPneuCargaInicial> pneusCargaInicial) throws ProLogException {
        try {
            // Removemos da carga inicial os pneus que tem código de unidade bloqueado. Estes não serão processados.
            pneusCargaInicial.removeAll(
                    pneusCargaInicial.stream()
                            .filter(pneu -> UnidadePraxioValidator.isUnidadeBloqueada(pneu.getCodUnidadePneu()))
                            .collect(Collectors.toList()));
            ensureValidToken(tokenIntegracao, TAG);
            return dao.inserirCargaInicialPneu(tokenIntegracao, pneusCargaInicial);
        } catch (final Throwable t) {
            Log.e(TAG, "Não foi possível processar a carga inicial de pneus:\n" +
                    "tokenIntegracao: " + tokenIntegracao, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível processar a carga inicial de pneus no Sistema ProLog");
        }
    }

    @NotNull
    public SuccessResponseIntegracao inserirPneuCadastro(final String tokenIntegracao,
                                                         final ApiPneuCadastro pneuCadastro) throws ProLogException {
        try {
            if (UnidadePraxioValidator.isUnidadeBloqueada(pneuCadastro.getCodUnidadePneu())) {
                throw new GenericException(
                        String.format("Unidade (%s) está com a integração bloqueada",
                                pneuCadastro.getCodUnidadePneu()));
            }
            ensureValidToken(tokenIntegracao, TAG);
            return new SuccessResponseIntegracao(
                    "Pneu cadastrado com sucesso no Sistema ProLog",
                    dao.inserirPneuCadastro(tokenIntegracao, pneuCadastro));
        } catch (final Throwable t) {
            Log.e(TAG, "Não foi possível cadastrar o pneu:\n" +
                    "tokenIntegracao: " + tokenIntegracao, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível cadastrar o pneu no Sistema ProLog");
        }
    }

    @NotNull
    SuccessResponseIntegracao atualizarPneuEdicao(final String tokenIntegracao,
                                                  final ApiPneuEdicao pneuEdicao) throws ProLogException {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            return new SuccessResponseIntegracao(
                    "Pneu atualizado com sucesso no Sistema ProLog",
                    dao.atualizarPneuEdicao(tokenIntegracao, pneuEdicao));
        } catch (final Throwable t) {
            Log.e(TAG, "Não foi possível atualizar o pneu:\n" +
                    "tokenIntegracao: " + tokenIntegracao, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível atualizar o pneu no Sistema ProLog");
        }
    }

    @NotNull
    public SuccessResponseIntegracao transferirPneu(
            final String tokenIntegracao,
            final ApiPneuTransferencia pneuTransferencia) throws ProLogException {
        if (StringUtils.isNullOrEmpty(StringUtils.trimToNull(
                pneuTransferencia.getCpfColaboradorRealizacaoTransferencia()))) {
            throw new GenericException("O CPF do colaborador deve ser informado na transferência de pneu");
        }
        try {
            if (UnidadePraxioValidator.isUnidadeBloqueada(pneuTransferencia.getCodUnidadeOrigem())) {
                throw new GenericException(
                        String.format("Unidade (%s) está com a integração bloqueada",
                                pneuTransferencia.getCodUnidadeOrigem()));
            }
            ensureValidToken(tokenIntegracao, TAG);
            return new SuccessResponseIntegracao(
                    "Transferência de pneus realizada com sucesso no Sistema ProLog",
                    dao.transferirPneu(tokenIntegracao, pneuTransferencia));
        } catch (final Throwable t) {
            Log.e(TAG, "Não foi possível realizar a transferência de pneus:\n" +
                    "tokenIntegracao: " + tokenIntegracao, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível realizar a transferência de pneus no Sistema ProLog");
        }
    }
}
