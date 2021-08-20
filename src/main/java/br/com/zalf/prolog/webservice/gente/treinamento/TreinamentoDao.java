package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.webservice.gente.treinamento.model.Treinamento;
import br.com.zalf.prolog.webservice.gente.treinamento.model.TreinamentoColaborador;

import java.sql.SQLException;
import java.util.List;

/**
 * Contém os métodos para manipular os treinamentos
 */
public interface TreinamentoDao {

    List<Treinamento> getAll(Long dataInicial,
                             Long dataFinal,
                             String codFuncao,
                             Long codUnidade,
                             Boolean comCargosLiberados,
                             boolean apenasTreinamentosLiberados,
                             long limit,
                             long offset) throws SQLException;

    List<Treinamento> getNaoVistosColaborador(Long codColaborador) throws SQLException;

    List<Treinamento> getVistosColaborador(Long codColaborador) throws SQLException;

    boolean marcarTreinamentoComoVisto(Long codTreinamento, Long codColaborador) throws SQLException;

    Long insert(Treinamento treinamento) throws SQLException;

    List<TreinamentoColaborador> getVisualizacoesByTreinamento(Long codTreinamento, Long codUnidade) throws SQLException;

    Treinamento getTreinamentoByCod(Long codTreinamento, Long codUnidade, boolean comCargosLiberados) throws SQLException;

    boolean updateTreinamento(Treinamento treinamento) throws SQLException;

    boolean updateUrlImagensTreinamento(List<String> urls, Long codTreinamento) throws SQLException;

    boolean deleteTreinamento(Long codTreinamento) throws SQLException;
}