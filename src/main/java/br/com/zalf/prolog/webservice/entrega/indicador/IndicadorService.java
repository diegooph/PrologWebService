package br.com.zalf.prolog.webservice.entrega.indicador;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.IndicadorAcumulado;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zalf on 14/09/16.
 */
public class IndicadorService {

    private IndicadorDaoImpl dao = new IndicadorDaoImpl();
    private static final String TAG = IndicadorService.class.getSimpleName();

    public List<IndicadorAcumulado> getAcumuladoIndicadoresIndividual(Long dataInicial, Long dataFinal, Long cpf){
        try{
            return dao.getAcumuladoIndicadoresIndividual(dataInicial, dataFinal, cpf);
        }catch (SQLException e){
            Log.e(TAG, "Erro ao buscar os indicadores acumulados de um colaborador", e);
            return null;
        }
    }

    public List<Indicador> getExtratoIndicador(Long dataInicial, Long dataFinal, String codRegional, String codEmpresa,
                                               String codUnidade, String equipe, String cpf, String indicador){
        try{
            return dao.getExtratoIndicador(dataInicial, dataFinal, codRegional, codEmpresa, codUnidade, equipe, cpf,
                    indicador);
        }catch (SQLException e){
            Log.e(TAG, "Erro ao buscar o extrato de um indicadores de um colaborador", e);
            return null;
        }
    }

}
