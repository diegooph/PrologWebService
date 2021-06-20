package br.com.zalf.prolog.webservice.gente.solicitacaoFolga;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Classe SolicitacaoFolgaService responsavel por comunicar-se com a interface DAO
 */
public class SolicitacaoFolgaService {

    private SolicitacaoFolgaDao dao = Injection.provideSolicitacaoFolgaDao();
    private static final String TAG = SolicitacaoFolgaService.class.getSimpleName();

    public AbstractResponse insert(SolicitacaoFolga solicitacaoFolga) {
        try {
            return dao.insert(solicitacaoFolga);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao inserir a solicitação de folga", e);
            return Response.error("Erro ao inserir a solicitação de folga.");
        }
    }

    public List<SolicitacaoFolga> getByColaborador(Long codColaborador) {
        try {
            return dao.getByColaborador(codColaborador);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar as solicitações de folga do colaborador. \n" +
                                             "codColaborador: %d", codColaborador), e);
            return Collections.emptyList();
        }
    }

    public List<SolicitacaoFolga> getAll(LocalDate dataInicial, LocalDate dataFinal, Long codUnidade,
                                         String codEquipe, String status, Long codColaborador) {
        try {
            return dao.getAll(dataInicial, dataFinal, codUnidade, codEquipe, status, codColaborador);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar as solicitações de folga. \n" +
                                             "codUnidade: %d \n" +
                                             "codEquipe: %s \n" +
                                             "status: %s \n" +
                                             "codColaborador: %s \n" +
                                             "dataInicial: %s \n" +
                                             "dataFinal: %s \n",
                                     codUnidade,
                                     codEquipe,
                                     status,
                                     codColaborador,
                                     dataInicial,
                                     dataFinal), e);
            return Collections.emptyList();
        }
    }

    public boolean update(SolicitacaoFolga solicitacaoFolga) {
        try {
            return dao.update(solicitacaoFolga);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao atualizar a solicitação de folga", e);
            return false;
        }
    }

    public boolean delete(Long codigo) {
        try {
            return dao.delete(codigo);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao deletar a solicitação de folga. \n" +
                    "codigo: %d", codigo), e);
            return false;
        }
    }
}