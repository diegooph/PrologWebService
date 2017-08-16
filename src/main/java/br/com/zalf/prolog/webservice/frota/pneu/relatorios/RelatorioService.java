package br.com.zalf.prolog.webservice.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.Aderencia;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.Faixa;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.ResumoServicos;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe RelatorioService responsavel por comunicar-se com a interface DAO
 */
public class RelatorioService {

    private RelatorioDao dao = new RelatorioDaoImpl();

    public List<Faixa> getQtPneusByFaixaSulco(List<String> codUnidades, List<String> status) {
        try {
            return dao.getQtPneusByFaixaSulco(codUnidades, status);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Faixa> getQtPneusByFaixaPressao(List<String> codUnidades, List<String> status) {
        try {
            return dao.getQtPneusByFaixaPressao(codUnidades, status);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Aderencia> getAderenciaByUnidade(int ano, int mes, Long codUnidade) {
        try {
            return dao.getAderenciaByUnidade(ano, mes, codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ResumoServicos> getResumoServicosByUnidades(int ano, int mes, List<String> codUnidades) {
        try {
            return dao.getResumoServicosByUnidades(ano, mes, codUnidades);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void getPrevisaoTrocaCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream) {
        try {
            dao.getPrevisaoTrocaCsv(codUnidade, dataInicial, dataFinal, outputStream);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public Report getPrevisaoTrocaReport(Long codUnidade, long dataInicial, long dataFinal) {
        try {
            return dao.getPrevisaoTrocaReport(codUnidade, dataInicial, dataFinal);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getPrevisaoTrocaConsolidadoCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream) {
        try {
            dao.getPrevisaoTrocaConsolidadoCsv(codUnidade, dataInicial, dataFinal, outputStream);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public Report getPrevisaoTrocaConsolidadoReport(Long codUnidade, long dataInicial, long dataFinal) {
        try {
            return dao.getPrevisaoTrocaConsolidadoReport(codUnidade, dataInicial, dataFinal);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getAerenciaPlacasCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream) {
        try {
            dao.getAderenciaPlacasCsv(codUnidade, dataInicial, dataFinal, outputStream);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public Report getAderenciaPlacasReport(Long codUnidade, long dataInicial, long dataFinal) {
        try {
            return dao.getAderenciaPlacasReport(codUnidade, dataInicial, dataFinal);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Pneu> getPneusByFaixaSulco(double inicioFaixa, double fimFaixa, Long codEmpresa,
                                      String codUnidade, long limit, long offset) {
        try {
            return dao.getPneusByFaixaSulco(inicioFaixa, fimFaixa, codEmpresa, codUnidade, limit, offset);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void getDadosUltimaAfericaoCsv(Long codUnidade, OutputStream outputStream) {
        try {
            dao.getDadosUltimaAfericaoCsv(codUnidade, outputStream);
        }catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public Report getDadosUltimaAfericaoReport(Long codUnidade) {
        try {
            return dao.getDadosUltimaAfericaoReport(codUnidade);
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Report getEstratificacaoServicosFechadosReport(Long codUnidade, long dataInicial,
                                                          long dataFinal) {
        try {
            return dao.getEstratificacaoServicosFechadosReport(codUnidade, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getEstratificacaoServicosFechadosCsv(Long codUnidade, OutputStream outputStream, long dataInicial,
                                                        long dataFinal) {
        try {
            dao.getEstratificacaoServicosFechadosCsv(codUnidade, outputStream, new Date(dataInicial), new Date(dataFinal));
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

}
