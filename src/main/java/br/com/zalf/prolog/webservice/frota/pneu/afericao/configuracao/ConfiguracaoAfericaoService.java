package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoAlertaColetaSulco;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoTipoVeiculoAferivel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 03/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
class ConfiguracaoAfericaoService {
    private static final String TAG = ConfiguracaoAfericaoService.class.getSimpleName();
    @NotNull
    private final ConfiguracaoAfericaoDao dao = Injection.provideConfiguracaoAfericaoDao();
    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

    @NotNull
    Response updateConfiguracaoTiposVeiculosAferiveis(
            @NotNull final Long codUnidade,
            @NotNull final List<ConfiguracaoTipoVeiculoAferivel> configuracoes) throws ProLogException {
        try {
            ConfiguracaoAfericaoValidator.validateUpdateTiposVeiculosAferiveis(configuracoes);
            dao.insertOrUpdateConfiguracoesTiposVeiculosAferiveis(codUnidade, configuracoes);
            return Response.ok("Configurações atualizadas com sucesso!");
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao atualizar configurações dos tipos de veículo da aferição", e);
            throw exceptionHandler.map(e, "Não foi possível atualizar as configurações, tente novamente");
        }
    }

    @NotNull
    List<ConfiguracaoTipoVeiculoAferivel> getConfiguracoesTipoAfericaoVeiculo(
            @NotNull final Long codUnidade) throws ProLogException {
        try {
            return dao.getConfiguracoesTipoAfericaoVeiculo(codUnidade);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar configurações de tipos de veículo da aferição", e);
            throw exceptionHandler.map(e, "Não foi possível buscar as configurações, tente novamente");
        }
    }

    @NotNull
    Response updateConfiguracaoAlertaColetaSulco(
            @NotNull final List<ConfiguracaoAlertaColetaSulco> configuracoes) throws ProLogException {
        try {
            dao.insertOrUpdateConfiguracoesAlertaColetaSulco(configuracoes);
            return Response.ok("Configurações atualizadas com sucesso!");
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao atualizar configuração de alerta na coleta de sulco", e);
            throw exceptionHandler.map(e, "Não foi possível atualizar as configurações, tente novamente");
        }
    }

    @NotNull
    List<ConfiguracaoAlertaColetaSulco> getConfiguracoesAlertaColetaSulco(
            @NotNull final Long codColaborador) throws ProLogException {
        try {
            return dao.getConfiguracoesAlertaColetaSulco(codColaborador);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar configurações de alerta na coleta de sulco", e);
            throw exceptionHandler.map(e, "Não foi possível buscar as configurações, tente novamente");
        }
    }
}