package br.com.zalf.prolog.webservice.frota.pneu;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu._model.*;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu.Dimensao;
import br.com.zalf.prolog.webservice.frota.pneu.error.PneuValidator;
import br.com.zalf.prolog.webservice.frota.pneu.importar.PneuImportReader;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.integracao.router.RouterPneu;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * Classe PneuService responsavel por comunicar-se com a interface DAO
 */
public final class PneuService {
    private static final String TAG = PneuService.class.getSimpleName();
    private final PneuDao dao = Injection.providePneuDao();

    @NotNull
    public AbstractResponse insert(@NotNull final Long codigoColaboradorCadastro,
                                   @NotNull final String userToken,
                                   @NotNull final Long codUnidade,
                                   @NotNull final Pneu pneu,
                                   @NotNull final OrigemAcaoEnum origemCadastro,
                                   final boolean ignoreDotValidation) throws ProLogException {
        try {
            PneuValidator.validacaoAtributosPneu(pneu, codUnidade, ignoreDotValidation);
            return ResponseWithCod.ok(
                    "Pneu inserido com sucesso",
                    RouterPneu
                            .create(dao, userToken)
                            .insert(codigoColaboradorCadastro, pneu, codUnidade, origemCadastro));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir pneu:" +
                    "\nuserToken: " + userToken +
                    "\ncodUnidade: " + codUnidade, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir o pneu, tente novamente");
        }
    }

    @NotNull
    public List<Long> insert(@NotNull final Long codigoColaboradorCadastro,
                             @NotNull final String userToken,
                             @NotNull final InputStream fileInputStream) throws ProLogException {
        try {
            return RouterPneu
                    .create(dao, userToken)
                    .insert(codigoColaboradorCadastro, PneuImportReader.readFromCsv(fileInputStream));
        } catch (final Throwable t) {
            final String errorMessage = "Erro ao inserir pneus -- " + t.getMessage();
            Log.e(TAG, errorMessage, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, errorMessage);
        }
    }

    @NotNull
    public Response update(@NotNull final Long codigoColaboradorEdicao,
                           @NotNull final String userToken,
                           @NotNull final Long codUnidade,
                           @NotNull final Long codOriginal,
                           @NotNull final Pneu pneu) throws ProLogException {
        try {
            PneuValidator.validacaoAtributosPneu(pneu, codUnidade, false);
            RouterPneu
                    .create(dao, userToken)
                    .update(codigoColaboradorEdicao, pneu, codUnidade, codOriginal);
            return Response.ok("Pneu atualizado com sucesso");
        } catch (final Throwable t) {
            final String errorMessage = "Erro ao atualizar pneu: " + codOriginal;
            Log.e(TAG, errorMessage, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, errorMessage);
        }
    }

    @NotNull
    public List<Pneu> getPneusByCodUnidadesByStatus(@NotNull final String userToken,
                                                    @NotNull final List<Long> codUnidades,
                                                    @NotNull final String status) {
        try {
            if (status.equals("%")) {
                return dao.getTodosPneus(codUnidades);
            } else {
                final StatusPneu statusPneu = StatusPneu.fromString(status);
                switch (statusPneu) {
                    case ANALISE:
                        return dao.getPneusAnalise(codUnidades.get(0));
                    case EM_USO:
                    case ESTOQUE:
                    case DESCARTE:
                        return RouterPneu
                                .create(dao, userToken)
                                .getPneusByCodUnidadesByStatus(codUnidades, statusPneu);
                    default:
                        throw new IllegalArgumentException("Status de Pneu não existente: " + status);
                }
            }
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar os pneus com status: " + status +
                    " das unidades " + codUnidades.toString(), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar pneus, tente novamente");
        }
    }

    @NotNull
    public Pneu getPneuByCod(final Long codPneu, final Long codUnidade) throws ProLogException {
        try {
            return dao.getPneuByCod(codPneu, codUnidade);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar pneu com código: " + codPneu + " da unidade: " + codUnidade, e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao buscar o pneu, tente novamente");
        }
    }

    public List<Dimensao> getDimensoes() {
        try {
            return dao.getDimensoes();
        } catch (final SQLException e) {
            Log.e(TAG, "Erro ao buscar dimensões de pneus", e);
            return null;
        }
    }

    public void marcarFotoComoSincronizada(@NotNull final Long codPneu,
                                           @NotNull final String urlFotoPneu) {
        try {
            dao.marcarFotoComoSincronizada(codPneu, urlFotoPneu);
        } catch (final SQLException e) {
            Log.e(TAG, "Erro ao marcar a foto como sincronizada com URL: " + urlFotoPneu, e);
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public PneuRetornoDescarteResponse retornarPneuDescarte(@NotNull final PneuRetornoDescarte pneuRetornoDescarte) {
        try {
            final PneuRetornoDescarteSuccess success = dao.retornarPneuDescarte(pneuRetornoDescarte);
            return new PneuRetornoDescarteResponse(success.getCodPneuRetornado(),
                                                   success.getCodMovimentacaoGerada(),
                                                   "Retorno realizado com sucesso!");
        } catch (final Throwable t) {
            final String message = "Erro ao retornar o pneu do descarte.";
            Log.e(TAG, message, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, message);
        }
    }
}