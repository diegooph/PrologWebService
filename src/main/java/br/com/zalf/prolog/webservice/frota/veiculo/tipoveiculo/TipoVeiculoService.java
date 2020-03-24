package br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.integracao.router.RouterTipoVeiculo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 22/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class TipoVeiculoService {
    @NotNull
    private static final String TAG = TipoVeiculoService.class.getSimpleName();
    @NotNull
    private final TipoVeiculoDao dao = Injection.provideTipoVeiculoDao();

    @NotNull
    public ResponseWithCod insertTipoVeiculoPorEmpresa(@NotNull final String userToken,
                                                       @NotNull final TipoVeiculo tipoVeiculo) throws ProLogException {
        try {
            final Long codTipoVeiculoInserido = RouterTipoVeiculo.create(dao, userToken).insertTipoVeiculo(tipoVeiculo);
            return ResponseWithCod.ok(
                    "Tipo de veículo inserido com sucesso",
                    codTipoVeiculoInserido);
        } catch (@NotNull Throwable t) {
            Log.e(TAG, "Erro ao inserir o tipo de veículo", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir o tipo de veículo, tente novamente");
        }
    }

    @NotNull
    public Response updateTipoVeiculo(@NotNull final String userToken,
                                      @NotNull final TipoVeiculo tipoVeiculo) throws ProLogException {
        try {
            RouterTipoVeiculo.create(dao, userToken).updateTipoVeiculo(tipoVeiculo);
            return Response.ok("Tipo de veículo atualizado com sucesso");
        } catch (@NotNull final Throwable t) {
            Log.e(TAG, "Erro ao atualizar o tipo de veículo", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar o tipo de veículo, tente novamente");
        }
    }

    @NotNull
    public List<TipoVeiculo> getTiposVeiculosByEmpresa(final String userToken, final Long codEmpresa) throws ProLogException {
        try {
            return dao.getTiposVeiculosByEmpresa(codEmpresa);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar os tipos de veículos ativos da empresa.\n" +
                    "Empresa: %d\n" +
                    "userToken: %s", codEmpresa, userToken), throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar os tipos de veículos, tente novamente");
        }
    }

    @NotNull
    public TipoVeiculo getTipoVeiculo(final Long codTipoVeiculo) throws ProLogException {
        try {
            return dao.getTipoVeiculo(codTipoVeiculo);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar tipo de veículo: %d", codTipoVeiculo), throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar o tipo de veículo, tente novamente");
        }
    }

    @NotNull
    public Response deleteTipoVeiculoByEmpresa(final Long codEmpresa, final Long codTipoVeiculo) throws ProLogException {
        try {
            dao.deleteTipoVeiculoByEmpresa(codEmpresa, codTipoVeiculo);
            return Response.ok("Tipo de veículo deletado com sucesso");
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao deletar tipo de veículo", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao deletar tipo de veículo, tente novamente");
        }
    }

    /**
     * @deprecated at 2019-01-10.
     * Método depreciado pois não será mais utilizado o código da unidade.
     * Em seu lugar será utilizado o código da empresa.
     * Utilize {@link #getTiposVeiculosByEmpresa(String, Long)}.
     */
    @Deprecated
    List<TipoVeiculo> getTiposVeiculosByUnidade(final String userToken, final Long codUnidade) {
        try {
            return dao.getTiposVeiculosByEmpresa(Injection.provideEmpresaDao().getCodEmpresaByCodUnidade(codUnidade));
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar os tipos de veículos ativos da unidade. \n" +
                    "Unidade: %d\n" +
                    "userToken: %s", codUnidade, userToken), t);
            throw new RuntimeException("Erro ao buscar os tipos de veículo da unidade: " + codUnidade);
        }
    }
}
