package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.filtro.ServicoHolderBuscaFiltro;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.filtro.ServicosAbertosBuscaFiltro;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.filtro.ServicosFechadosVeiculoFiltro;
import br.com.zalf.prolog.webservice.integracao.router.RouterAfericaoServico;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Classe ServicoService responsavel por comunicar-se com a interface DAO.
 */
public class ServicoService {

    private static final String TAG = ServicoService.class.getSimpleName();
    private final ServicoDao dao = Injection.provideServicoDao();

    public ServicosAbertosHolder getQuantidadeServicosAbertosVeiculo(final Long codUnidade,
                                                                     final String agrupamento) {
        final AgrupamentoQuantidadeServicos tipoAgrupamento = AgrupamentoQuantidadeServicos.fromString(agrupamento);
        if (tipoAgrupamento.equals(AgrupamentoQuantidadeServicos.POR_VEICULO)) {
            try {
                return dao.getQuantidadeServicosAbertosVeiculo(codUnidade);
            } catch (final SQLException e) {
                Log.e(TAG, String.format("Erro ao buscar quantidade de serviços abertos por veículo. \n," +
                                                 "Unidade: %d", codUnidade), e);
                return null;
            }
        } else {
            throw new IllegalArgumentException("O único tipo de agrupamento suportado na busca dos serviços abertos é" +
                                                       " " +
                                                       "por veículo. Agrupamento recebido: " + agrupamento);
        }
    }

    @NotNull
    public ServicoHolder getServicoHolder(@NotNull final ServicoHolderBuscaFiltro filtro) {
        try {
            return dao.getServicoHolder(filtro);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar os serviços da placa.\n" +
                                             "codUnidade: %d\n" +
                                             "codVeiculo: %d\n" +
                                             "placaVeiculo: %s",
                                     filtro.getCodUnidade(),
                                     filtro.getCodVeiculo(),
                                     filtro.getPlacaVeiculo()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar serviços, tente novamente");
        }
    }

    @NotNull
    public List<Servico> getServicosAbertos(@NotNull final ServicosAbertosBuscaFiltro filtro) {
        try {
            return dao.getServicosAbertos(filtro);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar os serviços abertos da placa. \n," +
                                             "codVeiculo: %d \n" +
                                             "placaVeiculo: %s \n" +
                                             "tipoServico: %s",
                                     filtro.getCodVeiculo(),
                                     filtro.getPlacaVeiculo(),
                                     filtro.getTipoServico()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar serviços abertos, tente novamente");
        }
    }

    @NotNull
    public Response fechaServico(@NotNull final String userToken,
                                 @NotNull final Long codUnidade,
                                 @NotNull final Servico servico) throws ProLogException {
        try {
            RouterAfericaoServico
                    .create(dao, userToken)
                    .fechaServico(codUnidade, Now.getOffsetDateTimeUtc(), servico);
            return Response.ok("Serviço consertado com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao inserir o conserto de um item\n" +
                                             "userToken: %s\n" +
                                             "codUnidade: %d", userToken, codUnidade), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao realizar o conserto de um item");
        }
    }

    public Servico getServicoByCod(final Long codUnidade, final Long codServico) {
        try {
            return dao.getServicoByCod(codUnidade, codServico);
        } catch (final SQLException e) {
            final String message = String.format("Erro ao buscar os serviço. \n," +
                                                         "Unidade: %d \n" +
                                                         "Código serviço: %d", codUnidade, codServico);
            Log.e(TAG, message, e);
            throw new RuntimeException(message);
        }
    }

    public ServicosFechadosHolder getQuantidadeServicosFechados(final Long codUnidade,
                                                                final long dataInicial,
                                                                final long dataFinal,
                                                                final String agrupamento) {
        final AgrupamentoQuantidadeServicos tipoAgrupamento = AgrupamentoQuantidadeServicos.fromString(agrupamento);
        try {
            if (tipoAgrupamento.equals(AgrupamentoQuantidadeServicos.POR_VEICULO)) {
                return dao.getQuantidadeServicosFechadosByVeiculo(codUnidade, dataInicial, dataFinal);
            } else {
                return dao.getQuantidadeServicosFechadosByPneu(codUnidade, dataInicial, dataFinal);
            }
        } catch (final SQLException e) {
            final String message = String.format("Erro ao buscar os serviços fechados. \n" +
                                                         "Unidade: %d \n" +
                                                         "Data Inicial: %d \n" +
                                                         "Data Final: %d", codUnidade, dataInicial, dataFinal);
            Log.e(TAG, message, e);
            throw new RuntimeException(message);
        }
    }

    public List<Servico> getServicosFechados(final Long codUnidade,
                                             final long dataInicial,
                                             final long dataFinal) {
        try {
            return dao.getServicosFechados(codUnidade, dataInicial, dataFinal);
        } catch (final SQLException e) {
            final String message = String.format("Erro ao buscar os serviços fechados. \n," +
                                                         "Unidade: %d \n" +
                                                         "Data Inicial: %d \n" +
                                                         "Data Final: %d", codUnidade, dataInicial, dataFinal);
            Log.e(TAG, message, e);
            throw new RuntimeException(message);
        }
    }

    public List<Servico> getServicosFechadosPneu(final Long codUnidade,
                                                 final Long codPneu,
                                                 final long dataInicial,
                                                 final long dataFinal) {
        try {
            return dao.getServicosFechadosPneu(codUnidade, codPneu, dataInicial, dataFinal);
        } catch (final SQLException e) {
            final String message = String.format("Erro ao buscar os serviços fechados. \n," +
                                                         "Unidade: %d \n" +
                                                         "Pneu: %d \n" +
                                                         "Data Inicial: %d \n" +
                                                         "Data Final: %d", codUnidade, codPneu, dataInicial, dataFinal);
            Log.e(TAG, message, e);
            throw new RuntimeException(message);
        }
    }

    @NotNull
    public List<Servico> getServicosFechadosVeiculo(@NotNull final ServicosFechadosVeiculoFiltro filtro) {
        try {
            return dao.getServicosFechadosVeiculo(filtro);
        } catch (final Throwable t) {
            final String message = String.format("Erro ao buscar os serviços fechados.\n," +
                                                         "codUnidade: %d\n" +
                                                         "codVeiculo: %d\n" +
                                                         "placaVeiculo: %s\n" +
                                                         "dataInicial: %s\n" +
                                                         "dataFinal: %s",
                                                 filtro.getCodUnidade(),
                                                 filtro.getCodVeiculo(),
                                                 filtro.getPlacaVeiculo(),
                                                 filtro.getDataInicial(),
                                                 filtro.getDataFinal());
            Log.e(TAG, message, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar serviços fechados, tente novamente");
        }
    }

    @NotNull
    public VeiculoServico getVeiculoAberturaServico(@NotNull final String userToken,
                                                    @NotNull final Long codServico,
                                                    @NotNull final String placaVeiculo) throws ProLogException {
        try {
            return RouterAfericaoServico
                    .create(dao, userToken)
                    .getVeiculoAberturaServico(codServico, placaVeiculo);
        } catch (final Throwable t) {
            final String message = String.format("Erro ao buscar o veículo para um serviço.\n" +
                                                         "userToken: %s\n" +
                                                         "Serviço: %d\n" +
                                                         "Veículo: %s\n", userToken, codServico, placaVeiculo);
            Log.e(TAG, message, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar dados para fechamento de serviço");
        }
    }
}