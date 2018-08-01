package br.com.zalf.prolog.webservice.raizen.produtividade.relatorios;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.time.LocalDate;
import java.sql.SQLException;
import java.io.IOException;

/**
 * Created on 31/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface RaizenProdutividadeRelatorioServiceDao {

    /**
     * Método para gerar um relatório contendo todos os dados da produtividade em arquivo CSV para fins de exportação.
     * O único filtro aplicado será por data inicial e final.
     *
     * @param out              - Arquivo onde os dados serão armazenados para retornar.
     * @param dataInicial      - Data inicial do período de filtro.
     * @param dataFinal        - Data final do período de filtro.
     * @throws SQLException - Se algum erro na busca dos dados ocorrer.
     * @throws IOException  - Se algum erro na escrita dos dados ocorrer.
     */
    void getDadosGeraisProdutividadeCsv(@NotNull final OutputStream out,
                                         @NotNull final LocalDate dataInicial,
                                         @NotNull final LocalDate dataFinal) throws Throwable;

}
