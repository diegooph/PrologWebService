package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.FarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.VeiculoLiberacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.integracao.router.RouterChecklists;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Classe ChecklistService responsável por comunicar-se com a interface DAO
 */
public class ChecklistService {

    private final ChecklistDao dao = Injection.provideChecklistDao();

    public List<String> getUrlImagensPerguntas(Long codUnidade, Long codFuncao) {
        try {
            return dao.getUrlImagensPerguntas(codUnidade, codFuncao);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long insert(Checklist checklist, String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .insertChecklist(checklist);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(Long codUnidade, Long codFuncao, String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return null;
        }
    }

    public Checklist getByCod(Long codigo, String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getChecklistByCodigo(codigo);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public List<Checklist> getAll(long dataInicial, long dataFinal, String equipe,
//                                  Long codUnidade, String placa, int limit, long offset, boolean resumido, String userToken) {
//        try {
//            return RouterChecklists
//                    .create(dao, userToken)
//                    .getTodosChecklists(new Date(dataInicial), new Date(dataFinal), equipe, codUnidade, placa, limit,
//                            offset, resumido);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

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
            e.printStackTrace();
            return null;
        }
    }

    public List<Checklist> getByColaborador(Long cpf, int limit, long offset, boolean resumido, String userToken) {
        try {
            return RouterChecklists
                    .create(dao, userToken)
                    .getChecklistsByColaborador(cpf, limit, offset, resumido);
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar farol do checklist");
        }
    }

    public FarolChecklist getFarolChecklist(Long codUnidade, boolean itensCriticosRetroativos, String userToken) {
        final long hoje = System.currentTimeMillis();
        return getFarolChecklist(codUnidade, hoje, hoje, itensCriticosRetroativos, userToken);
    }

    @Deprecated
    public List<VeiculoLiberacao> getStatusLiberacaoVeiculos(Long codUnidade) {
        try {
            return dao.getStatusLiberacaoVeiculos(codUnidade);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar farol do checklist");
        }
    }
}
