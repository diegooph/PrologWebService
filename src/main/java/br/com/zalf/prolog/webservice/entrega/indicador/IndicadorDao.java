package br.com.zalf.prolog.webservice.entrega.indicador;

import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.IndicadorAcumulado;
import br.com.zalf.prolog.webservice.entrega.indicador.item.IndicadorItem;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by didi on 04/07/17.
 */
public interface IndicadorDao {

    @NotNull
    List<IndicadorAcumulado> getAcumuladoIndicadoresIndividual(@NotNull final Long cpf,
                                                               @NotNull final LocalDate dataInicial,
                                                               @NotNull final LocalDate dataFinal) throws SQLException;

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