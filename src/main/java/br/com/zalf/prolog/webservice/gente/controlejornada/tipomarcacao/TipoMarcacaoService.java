package br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import com.google.common.annotations.VisibleForTesting;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 20/12/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@VisibleForTesting
public final class TipoMarcacaoService {
    @NotNull
    private final String TAG = TipoMarcacaoService.class.getSimpleName();
    @NotNull
    private final TipoMarcacaoDao dao = Injection.provideTipoMarcacaoDao();

    @NotNull
    @VisibleForTesting
    public AbstractResponse insertTipoMarcacao(@NotNull final TipoMarcacao tipoMarcacao) throws ProLogException {
        try {
            return ResponseWithCod.ok(
                    "Tipo de marcação inserido com sucesso",
                    dao.insertTipoMarcacao(tipoMarcacao,
                            Injection.provideDadosIntervaloChangedListener()));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir o tipo de marcação", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir tipo de marcação, tente novamente");
        }
    }

    @NotNull
    @VisibleForTesting
    public Response updateTipoMarcacao(@NotNull final TipoMarcacao tipoMarcacao) throws ProLogException {
        try {
            dao.updateTipoMarcacao(tipoMarcacao, Injection.provideDadosIntervaloChangedListener());
            return Response.ok("Tipo de marcação atualizada com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao atualizar o tipo de marcação", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar tipo de marcação, tente novamente");
        }
    }

    @NotNull
    List<TipoMarcacao> getTiposMarcacoes(@NotNull final Long codUnidade,
                                         final boolean apenasAtivos,
                                         final boolean withCargos) throws ProLogException {
        try {
            return dao.getTiposMarcacoes(codUnidade, apenasAtivos, withCargos);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar os tipos de marcações\n" +
                    "codUnidade: %d\n" +
                    "apenasAtivos: %b\n" +
                    "withCargos: %b", codUnidade, apenasAtivos, withCargos), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar os tipos de marcações, tente novamente");
        }
    }

    @NotNull
    @VisibleForTesting
    public TipoMarcacao getTipoMarcacao(@NotNull final Long codTipoMarcacao) throws ProLogException {
        try {
            return dao.getTipoMarcacao(codTipoMarcacao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar tipo de marcação\n" +
                    "codTipoMarcacao: %d", codTipoMarcacao), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar o tipo de marcação, tente novamente");
        }
    }

    @NotNull
    Response updateStatusAtivo(@NotNull final Long codTipoMarcacao,
                               @NotNull final TipoMarcacao tipoMarcacao) throws ProLogException {
        try {
            dao.updateStatusAtivoTipoMarcacao(
                    codTipoMarcacao,
                    tipoMarcacao,
                    Injection.provideDadosIntervaloChangedListener());
            final String successMessage = tipoMarcacao.isAtivo()
                    ? "Tipo de marcação ativada com sucesso"
                    : "Tipo de marcação inativada com sucesso";
            return Response.ok(successMessage);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao inativar o tipo de marcação\n" +
                    "codTipoMarcacao: %d", codTipoMarcacao), t);
            final String errorMessage = tipoMarcacao.isAtivo()
                    ? "Erro ao ativar tipo de marcação, tente novamente"
                    : "Erro ao inativar tipo de marcação, tente novamente";
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, errorMessage);
        }
    }
}