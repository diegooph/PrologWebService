package br.com.zalf.prolog.webservice.gente.treinamento.relatorios;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Created on 14/03/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface TreinamentoRelatorioDao {

    /**
     * Retorna um relatório estratificado por colaborador, listando os treinamentos que por ele foram visualizados
     * e também a data e hora de quando cada treinamento foi visualizados;
     *
     * @param outputStream - Referencia onde relatório será escrito
     * @param codUnidade   - Código da unidade que será gerado o relatório
     * @param dataInicial  - Data inicial da busca
     * @param dataFinal    - Data final da busca
     */
    void getRelatorioEstratificadoPorColaboradorCsv(@NotNull final OutputStream outputStream,
                                                    @NotNull final Long codUnidade,
                                                    @NotNull final LocalDate dataInicial,
                                                    @NotNull final LocalDate dataFinal) throws SQLException, IOException;
}
