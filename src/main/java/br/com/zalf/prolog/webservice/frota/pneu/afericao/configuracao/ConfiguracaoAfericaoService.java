package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.autenticacao._model.token.TokenCleaner;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.*;
import br.com.zalf.prolog.webservice.gente.colaborador.ColaboradorService;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 03/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class ConfiguracaoAfericaoService {
    private static final String TAG = ConfiguracaoAfericaoService.class.getSimpleName();
    @NotNull
    private final ConfiguracaoAfericaoDao dao = Injection.provideConfiguracaoAfericaoDao();

    @NotNull
    Response updateConfiguracaoTiposVeiculosAferiveis(
            @NotNull final Long codUnidade,
            @NotNull final List<ConfiguracaoTipoVeiculoAferivelInsercao> configuracoes) throws ProLogException {
        try {
            dao.insertOrUpdateConfiguracoesTiposVeiculosAferiveis(codUnidade, configuracoes);
            return Response.ok("Configurações atualizadas com sucesso!");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao atualizar configurações dos tipos de veículo da aferição", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível atualizar as configurações, tente novamente");
        }
    }

    @NotNull
    List<ConfiguracaoTipoVeiculoAferivelListagem> getConfiguracoesTipoAfericaoVeiculo(
            @NotNull final Long codUnidade) throws ProLogException {
        try {
            return dao.getConfiguracoesTipoAfericaoVeiculo(codUnidade);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar configurações de tipos de veículo da aferição", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível buscar as configurações, tente novamente");
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
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível atualizar as configurações, tente novamente");
        }
    }

    @NotNull
    List<ConfiguracaoAlertaColetaSulco> getConfiguracoesAlertaColetaSulco(
            @NotNull final Long codColaborador) throws ProLogException {
        try {
            return dao.getConfiguracoesAlertaColetaSulco(codColaborador);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar configurações de alerta na coleta de sulco", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível buscar as configurações, tente novamente");
        }
    }

    @NotNull
    Response upsertConfiguracoesCronogramaServicos(@NotNull final String userToken,
                                                   @NotNull final List<ConfiguracaoCronogramaServicoUpsert> configuracoes) {
        try {
            final ColaboradorService colaboradorService = new ColaboradorService();
            final Colaborador colaborador = colaboradorService.getByToken(TokenCleaner.getOnlyToken(userToken));
            dao.upsertConfiguracoesCronogramaServicos(colaborador.getCodigo(), configuracoes);
            return Response.ok("Configurações de abertura de serviços de pneus atualizadas com sucesso!");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao configurar restrições de abertura de serviços de pneus", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao configurar restrições de abertura de serviços de pneus");
        }
    }

    @NotNull
    List<ConfiguracaoCronogramaServico> getConfiguracoesCronogramaServicos(@NotNull final Long codColaborador) {
        try {
            return dao.getConfiguracoesCronogramaServicos(codColaborador);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar configurações de cronograma e serviços de pneus\n" +
                    "codColaborador: " + codColaborador, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar configurações de cronograma e serviços");
        }
    }

    @NotNull
    List<ConfiguracaoCronogramaServicoHistorico> getConfiguracoesCronogramaServicosHistorico(
            @NotNull final Long codRestricaoUnidade) {
        try {
            return dao.getConfiguracoesCronogramaServicosHistorico(codRestricaoUnidade);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar configurações de cronograma e abertura de serviços de pneus\n" +
                    "codRestricaoUnidade: " + codRestricaoUnidade, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar histórico de configurações de cronograma e serviços");
        }
    }
}