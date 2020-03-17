package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.*;
import br.com.zalf.prolog.webservice.integracao.router.RouterAfericaoServico;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Classe ServicoService responsavel por comunicar-se com a interface DAO.
 */
public class ServicoService {

    private static final String TAG = ServicoService.class.getSimpleName();
    private ServicoDao dao = Injection.provideServicoDao();

    public ServicosAbertosHolder getQuantidadeServicosAbertosVeiculo(Long codUnidade,
                                                                     String agrupamento) {
        final AgrupamentoQuantidadeServicos tipoAgrupamento = AgrupamentoQuantidadeServicos.fromString(agrupamento);
        if (tipoAgrupamento.equals(AgrupamentoQuantidadeServicos.POR_VEICULO)) {
            try {
                return dao.getQuantidadeServicosAbertosVeiculo(codUnidade);
            } catch (SQLException e) {
                Log.e(TAG, String.format("Erro ao buscar quantidade de serviços abertos por veículo. \n," +
                        "Unidade: %d", codUnidade), e);
                return null;
            }
        } else {
            throw new IllegalArgumentException("O único tipo de agrupamento suportado na busca dos serviços abertos é " +
                    "por veículo. Agrupamento recebido: " + agrupamento);
        }
    }

    @NotNull
    public ServicoHolder getServicoHolder(@NotNull final String placa, @NotNull final Long codUnidade) {
        try {
            return dao.getServicoHolder(placa, codUnidade);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar os serviços da placa.\n" +
                    "Unidade: %d\n" +
                    "Placa: %s", codUnidade, placa), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar serviços, tente novamente");
        }
    }

    public List<Servico> getServicosAbertosByPlaca(String placa, String tipoServico) {
        try {
            return dao.getServicosAbertosByPlaca(placa, tipoServico != null ? TipoServico.fromString(tipoServico) : null);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os serviços abertos da placa. \n," +
                    "TipoServico: %s \n" +
                    "Placa: %s", tipoServico, placa), e);
            return null;
        }
    }

    @NotNull
    public Response fechaServico(@NotNull final String userToken,
                                 @NotNull final Long codUnidade,
                                 @NotNull final Servico servico) throws ProLogException {
        try {
            RouterAfericaoServico
                    .create(dao, userToken)
                    .fechaServico(codUnidade, Now.offsetDateTimeUtc(), servico);
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
        } catch (SQLException e) {
            final String message = String.format("Erro ao buscar os serviço. \n," +
                    "Unidade: %d \n" +
                    "Código serviço: %d", codUnidade, codServico);
            Log.e(TAG, message, e);
            throw new RuntimeException(message);
        }
    }

    public ServicosFechadosHolder getQuantidadeServicosFechados(Long codUnidade,
                                                                long dataInicial,
                                                                long dataFinal,
                                                                String agrupamento) {
        final AgrupamentoQuantidadeServicos tipoAgrupamento = AgrupamentoQuantidadeServicos.fromString(agrupamento);
        try {
            if (tipoAgrupamento.equals(AgrupamentoQuantidadeServicos.POR_VEICULO)) {
                return dao.getQuantidadeServicosFechadosByVeiculo(codUnidade, dataInicial, dataFinal);
            } else {
                return dao.getQuantidadeServicosFechadosByPneu(codUnidade, dataInicial, dataFinal);
            }
        } catch (SQLException e) {
            final String message = String.format("Erro ao buscar os serviços fechados. \n" +
                    "Unidade: %d \n" +
                    "Data Inicial: %d \n" +
                    "Data Final: %d", codUnidade, dataInicial, dataFinal);
            Log.e(TAG, message, e);
            throw new RuntimeException(message);
        }
    }

    public List<Servico> getServicosFechados(Long codUnidade,
                                             long dataInicial,
                                             long dataFinal) {
        try {
            return dao.getServicosFechados(codUnidade, dataInicial, dataFinal);
        } catch (SQLException e) {
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
        } catch (SQLException e) {
            final String message = String.format("Erro ao buscar os serviços fechados. \n," +
                    "Unidade: %d \n" +
                    "Pneu: %d \n" +
                    "Data Inicial: %d \n" +
                    "Data Final: %d", codUnidade, codPneu, dataInicial, dataFinal);
            Log.e(TAG, message, e);
            throw new RuntimeException(message);
        }
    }

    public List<Servico> getServicosFechadosVeiculo(final Long codUnidade,
                                                    final String placaVeiculo,
                                                    final long dataInicial,
                                                    final long dataFinal) {
        try {
            return dao.getServicosFechadosVeiculo(codUnidade, placaVeiculo, dataInicial, dataFinal);
        } catch (SQLException e) {
            final String message = String.format("Erro ao buscar os serviços fechados. \n," +
                    "Unidade: %d \n" +
                    "Veículo: %s \n" +
                    "Data Inicial: %d \n" +
                    "Data Final: %d", codUnidade, placaVeiculo, dataInicial, dataFinal);
            Log.e(TAG, message, e);
            throw new RuntimeException(message);
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