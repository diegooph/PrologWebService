package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoInvalidaException;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Classe ServicoService responsavel por comunicar-se com a interface DAO.
 */
public class ServicoService {

    private static final String TAG = ServicoService.class.getSimpleName();
    private ServicoDao dao = new ServicoDaoImpl();

    public PlacaServicoHolder getConsolidadoListaVeiculos(Long codUnidade) {
        try {
            return dao.getPlacasServico(codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o consolidado de veículos. \n," +
                    "Unidade: %d", codUnidade), e);
            return null;
        }
    }

    public ServicoHolder getServicosByPlaca(String placa, Long codUnidade) {
        try {
            return dao.getServicosByPlaca(placa, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os serviços da placa. \n," +
                    "Unidade: %d \n" +
                    "Placa: %s", codUnidade, placa), e);
            return null;
        }
    }

    public List<Servico> getServicosAbertosByPlaca(String placa, String tipoServico) {
        try {
            return dao.getServicosAbertosByPlaca(placa, tipoServico);
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

    public ServicosFechadosHolder getServicosFechados(Long codUnidade,
                                                      long dataInicial,
                                                      long dataFinal,
                                                      String agrupamento) {
        final AgrupamentoServicosFechados tipoAgrupamento = AgrupamentoServicosFechados.fromString(agrupamento);
        try {
            if (tipoAgrupamento.equals(AgrupamentoServicosFechados.POR_VEICULO)) {
                return dao.getServicosFechadosByPlaca(codUnidade, dataInicial, dataFinal);
            } else {
                return dao.getServicosFechadosByPneu(codUnidade, dataInicial, dataFinal);
            }
        } catch (SQLException e) {
            final String message = String.format("Erro ao buscar os serviços fechados. \n," +
                    "Unidade: %d \n" +
                    "Data Inicial: %d \n" +
                    "Data Final: %d", codUnidade, dataInicial, dataFinal);
            Log.e(TAG, message, e);
            throw new RuntimeException(message);
        }
    }
}