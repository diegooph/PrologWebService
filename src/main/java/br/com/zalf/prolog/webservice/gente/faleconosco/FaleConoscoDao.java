package br.com.zalf.prolog.webservice.gente.faleconosco;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Contém os métodos para manipular os fale conosco
 */
public interface FaleConoscoDao {

    Long insert(FaleConosco faleConosco, Long codUnidade) throws SQLException;

    FaleConosco getByCod(Long codigo, Long codUnidade) throws Exception;

    List<FaleConosco> getAll(final long dataInicial,
                             final long dataFinal,
                             final int limit,
                             final int offset,
                             final Long codColaborador,
                             final String equipe,
                             final Long codUnidade,
                             final String status,
                             final String categoria) throws Exception;

    @NotNull
    List<FaleConosco> getByColaborador(@NotNull final Long codColaborador,
                                       @NotNull final String status) throws Exception;

    boolean insertFeedback(FaleConosco faleConosco, Long codUnidade) throws SQLException;
}