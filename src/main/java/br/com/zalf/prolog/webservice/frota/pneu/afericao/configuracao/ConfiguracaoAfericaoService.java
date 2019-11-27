package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.ColaboradorService;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.*;
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
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao atualizar configurações dos tipos de veículo da aferição", t);
            throw exceptionHandler.map(t, "Não foi possível atualizar as configurações, tente novamente");
        }
    }

    @NotNull
    List<ConfiguracaoTipoVeiculoAferivel> getConfiguracoesTipoAfericaoVeiculo(
            @NotNull final Long codUnidade) throws ProLogException {
        try {
            return dao.getConfiguracoesTipoAfericaoVeiculo(codUnidade);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar configurações de tipos de veículo da aferição", t);
            throw exceptionHandler.map(t, "Não foi possível buscar as configurações, tente novamente");
        }
    }

    @NotNull
    Response updateConfiguracaoAlertaColetaSulco(
            @NotNull final List<ConfiguracaoAlertaColetaSulco> configuracoes) throws ProLogException {
        try {
            dao.insertOrUpdateConfiguracoesAlertaColetaSulco(configuracoes);
            return Response.ok("Configurações atualizadas com sucesso!");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao atualizar configuração de alerta na coleta de sulco", t);
            throw exceptionHandler.map(t, "Não foi possível atualizar as configurações, tente novamente");
        }
    }

    @NotNull
    List<ConfiguracaoAlertaColetaSulco> getConfiguracoesAlertaColetaSulco(
            @NotNull final Long codColaborador) throws ProLogException {
        try {
            return dao.getConfiguracoesAlertaColetaSulco(codColaborador);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar configurações de alerta na coleta de sulco", t);
            throw exceptionHandler.map(t, "Não foi possível buscar as configurações, tente novamente");
        }
    }

    @NotNull
    Response upsertConfiguracaoAberturaServico(@NotNull String userToken,
                                               @NotNull final List<ConfiguracaoAberturaServicoUpsert> configuracoes) {
        try {
            final ColaboradorService colaboradorService = new ColaboradorService();
            final Colaborador colaborador;
            try {
                colaborador = colaboradorService.getByToken(TokenCleaner.getOnlyToken(userToken));
            } catch (final Throwable tc) {
                throw exceptionHandler.map(tc,
                        "Erro ao configurar restrições de abertura de serviços de pneus");
            }
            dao.upsertConfiguracaoAberturaServico(colaborador.getCodigo(), configuracoes);
            return Response.ok("Configurações de abertura de serviços de pneus atualizadas com sucesso!");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao configurar restrições de abertura de serviços de pneus", t);
            throw exceptionHandler.map(t, "Erro ao configurar restrições de abertura de serviços de pneus");
        }
    }

    @NotNull
    List<ConfiguracaoAberturaServico> getConfiguracaoAberturaServico(@NotNull final Long codColaborador) {
        try {
            return dao.getConfiguracaoAberturaServico(codColaborador);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar configurações de abertura de serviços de pneus \n" +
                    "codColaborador: " + codColaborador, t);
            throw exceptionHandler.map(t,
                    "Erro ao buscar configurações de abertura de serviços de pneus");
        }
    }


    @NotNull
    List<ConfiguracaoAberturaServicoHistorico> getConfiguracaoAberturaServicoHistorico(@NotNull final Long codPneuRestricao) {
        try {
            return dao.getConfiguracaoAberturaServicoHistorico(codPneuRestricao);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar configurações de abertura de serviços de pneus \n" +
                    "codColaborador: " + codPneuRestricao, t);
            throw exceptionHandler.map(t,
                    "Erro ao buscar configurações de abertura de serviços de pneus");
        }
    }
}