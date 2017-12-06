package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoInvalidaException;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Classe ServicoService responsavel por comunicar-se com a interface DAO.
 */
public class ServicoService {

    private static final String TAG = ServicoService.class.getSimpleName();
    private ServicoDao dao = new ServicoDaoImpl();

    public ServicosAbertosHolder getQuantidadeServicosAbertosVeiculo(Long codUnidade,
                                                                     String agrupamento) {
        final AgrupamentoServicosFechados tipoAgrupamento = AgrupamentoServicosFechados.fromString(agrupamento);
        if (tipoAgrupamento.equals(AgrupamentoServicosFechados.POR_VEICULO)) {
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

    public ServicoHolder getServicoHolder(String placa, Long codUnidade) {
        try {
            return dao.getServicoHolder(placa, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os serviços da placa. \n," +
                    "Unidade: %d \n" +
                    "Placa: %s", codUnidade, placa), e);
            return null;
        }
    }

    public List<Servico> getServicosAbertosByPlaca(String placa, String tipoServico) {
        try {
            return dao.getServicosAbertosByPlaca(placa, TipoServico.fromString(tipoServico));
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os serviços abertos da placa. \n," +
                    "TipoServico: %s \n" +
                    "Placa: %s", tipoServico, placa), e);
            return null;
        }
    }

    public boolean insertManutencao(Servico servico, Long codUnidade) {
        try {
            dao.insertManutencao(servico, codUnidade);
            return true;
        } catch (SQLException | OrigemDestinoInvalidaException e) {
            Log.e(TAG, String.format("Erro ao inserir o conserto de um item. \n," +
                    "Unidade: %d \n", codUnidade), e);
            return false;
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
        final AgrupamentoServicosFechados tipoAgrupamento = AgrupamentoServicosFechados.fromString(agrupamento);
        try {
            if (tipoAgrupamento.equals(AgrupamentoServicosFechados.POR_VEICULO)) {
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
                                                 final String codPneu,
                                                 final long dataInicial,
                                                 final long dataFinal) {
        try {
            return dao.getServicosFechadosPneu(codUnidade, codPneu, dataInicial, dataFinal);
        } catch (SQLException e) {
            final String message = String.format("Erro ao buscar os serviços fechados. \n," +
                    "Unidade: %d \n" +
                    "Pneu: %s \n" +
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
    public Veiculo getVeiculoAberturaServico(@NotNull final Long codServico, @NotNull final String placaVeiculo) {
        try {
            return dao.getVeiculoAberturaServico(codServico, placaVeiculo);
        } catch (SQLException e) {
            final String message = String.format("Erro ao buscar o veículo para um serviço. \n" +
                    "Serviço: %d \n" +
                    "Veículo: %s \n", codServico, placaVeiculo);
            Log.e(TAG, message, e);
            throw new RuntimeException(message);
        }
    }
}