package br.com.zalf.prolog.webservice.entrega.relatorio;

import br.com.zalf.prolog.entrega.indicador.indicadores.ConsolidadoDia;
import br.com.zalf.prolog.entrega.indicador.indicadores.Indicador;
import br.com.zalf.prolog.entrega.indicador.indicadores.acumulado.IndicadorAcumulado;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zalf on 14/09/16.
 */
public class RelatorioService {

    RelatorioDaoImpl dao = new RelatorioDaoImpl();

    public List<IndicadorAcumulado> getAcumuladoIndicadores(Long dataInicial, Long dataFinal, Long codEmpresa,
                                                            String codRegional, String codUnidade, String equipe){
        try{
            return dao.getAcumuladoIndicadores(dataInicial, dataFinal, codEmpresa, codRegional, codUnidade, equipe);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public List<Indicador> getExtratoIndicador(Long dataInicial, Long dataFinal, String codRegional, Long codEmpresa,
                                               String codUnidade, String equipe, String cpf, String indicador){
        try{
            return dao.getExtratoIndicador(dataInicial, dataFinal, codRegional, codEmpresa, codUnidade, equipe, cpf, indicador);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public List<ConsolidadoDia> getConsolidadoDia(Long dataInicial, Long dataFinal, Long codEmpresa,
                                                  String codRegional, String codUnidade, String equipe) {
        try {
            return dao.getConsolidadoDia(dataInicial, dataFinal, codEmpresa, codRegional, codUnidade, equipe);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
