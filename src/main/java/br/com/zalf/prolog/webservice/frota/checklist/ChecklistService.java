package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.FarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.VeiculoLiberacao;
import br.com.zalf.prolog.webservice.frota.checklist.model.ModeloChecklist;
import br.com.zalf.prolog.webservice.integracao.router.RouterChecklists;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Classe ChecklistService responsável por comunicar-se com a interface DAO
 */
public class ChecklistService {
    private static final String TAG = ChecklistService.class.getSimpleName();
    private final ChecklistDao dao = Injection.provideChecklistDao();

    public Long insert(Checklist checklist, String userToken) {
        try {
            checklist.setData(LocalDateTime.now(Clock.systemUTC()));
            return RouterChecklists
                    .create(dao, userToken)
                    .insertChecklist(checklist);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao inserir um checklist", e);
            return null;
        }
    }

    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(Long codUnidade, Long codFuncao, String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar os dados de filtro modelo e placa dos veículos", e);
            return null;
        }
    }

    public NovoChecklistHolder getNovoChecklistHolder(Long codUnidade,
                                                      Long codModelo,
                                                      String placa,
                                                      char tipoChecklist,
                                                      String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getNovoChecklistHolder(codUnidade, codModelo, placa, tipoChecklist);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar o modelo de checklist", e);
            return null;
        }
    }

    public Checklist getByCod(Long codigo, String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getChecklistByCodigo(codigo);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar o um checklist específico", e);
            return null;
        }
    }

    public List<Checklist> getAll(Long codUnidade,
                                  Long codEquipe,
                                  Long codTipoVeiculo,
                                  String placaVeiculo,
                                  long dataInicial,
                                  long dataFinal,
                                  int limit,
                                  long offset,
                                  boolean resumido,
                                  String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getTodosChecklists(codUnidade, codEquipe, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal,
                            limit, offset, resumido);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar os checklists", e);
            return null;
        }
    }

    public List<Checklist> getByColaborador(Long cpf, Long dataInicial, Long dataFinal, int limit, long offset,
                                            boolean resumido, String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getChecklistsByColaborador(cpf, dataInicial, dataFinal, limit, offset, resumido);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar os checklists de um colaborador específico", e);
            throw new RuntimeException("Erro ao buscar checklists para o colaborador: " + cpf);
        }
    }

    public FarolChecklist getFarolChecklist(Long codUnidade,
                                            long dataInicial,
                                            long dataFinal,
                                            boolean itensCriticosRetroativos,
                                            String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getFarolChecklist(codUnidade, new Date(dataInicial), new Date(dataFinal), itensCriticosRetroativos);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar o farol de realização dos checklists", e);
            throw new RuntimeException("Erro ao buscar farol do checklist");
        }
    }

    public FarolChecklist getFarolChecklist(Long codUnidade, boolean itensCriticosRetroativos, String userToken) {
        final long hoje = Now.utcMillis();
        return getFarolChecklist(codUnidade, hoje, hoje, itensCriticosRetroativos, userToken);
    }

    @Deprecated
    public List<VeiculoLiberacao> getStatusLiberacaoVeiculos(Long codUnidade) {
        try {
            return dao.getStatusLiberacaoVeiculos(codUnidade);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar o farol de realização dos checklists (@Deprecated)", e);
            throw new RuntimeException("Erro ao buscar farol do checklist");
        }
    }
}