package br.com.zalf.prolog.webservice.entrega.indicador;

import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.IndicadorAcumulado;
import br.com.zalf.prolog.webservice.entrega.indicador.item.IndicadorItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by didi on 04/07/17.
 */
public interface IndicadorDao {

    List<IndicadorAcumulado> getAcumuladoIndicadoresIndividual(Long dataInicial,
                                                               Long dataFinal,
                                                               Long cpf) throws SQLException;

    List<Indicador> getExtratoIndicador(Long dataInicial,
                                        Long dataFinal,
                                        String codRegional,
                                        String codEmpresa,
                                        String codUnidade,
                                        String equipe,
                                        String cpf,
                                        String indicador) throws SQLException;

    List<IndicadorItem> createExtratoDia(ResultSet rSet) throws SQLException;

    List<IndicadorAcumulado> createAcumulados(ResultSet rSet) throws SQLException;

    IndicadorAcumulado createAcumuladoIndicador(ResultSet rSet, String indicador) throws SQLException;
}