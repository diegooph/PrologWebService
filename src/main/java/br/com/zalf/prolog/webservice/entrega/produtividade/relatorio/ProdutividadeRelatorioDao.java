package br.com.zalf.prolog.webservice.entrega.produtividade.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;

/**
 * Created by Zart on 18/05/2017.
 */
public interface ProdutividadeRelatorioDao {

    /**
     * Busca os dados consolidados sobre produtivida e exporta em um arquivo csv.
     *
     * @param outputStream arquivo binário onde o csv será escrito
     * @param codUnidade   código da Unidade que deseja-se buscar os dados consolidados
     * @param dataInicial  data inicial em milissegundos
     * @param dataFinal    data final em milissegundos
     * @throws SQLException caso ocorrer um erro ao buscar os dados no banco de dados
     * @throws IOException  caso ocorra algum erro ao escrever o arquivo csv no arquivo binário
     */
    void getConsolidadoProdutividadeCsv(@NotNull OutputStream outputStream,
                                        @NotNull Long codUnidade,
                                        @NotNull Date dataInicial,
                                        @NotNull Date dataFinal) throws SQLException, IOException;

    /**
     * Busca os dados consolidados sobre produtivida encapsulando em um objeto {@link Report}
     * que será tratado pela aplicação requisitante.
     *
     * @param codUnidade  código da unidade que deseja-se buscar os dados
     * @param dataInicial data inicial em milissegundos
     * @param dataFinal   data final em milissegundos
     * @return um objeto {@link Report} que conterá os dados buscados
     * @throws SQLException caso a operação não seja concluída
     */
    Report getConsolidadoProdutividadeReport(@NotNull Long codUnidade,
                                             @NotNull Date dataInicial,
                                             @NotNull Date dataFinal) throws SQLException;

    /**
     * Busca os dados individuais sobre produtivida e exporta em um arquivo csv.
     *
     * @param outputStream arquivo binário onde o csv será escrito
     * @param cpf          cpf do colaborador
     * @param codUnidade   código da Unidade que deseja-se buscar os dados individuais
     * @param dataInicial  data inicial em milissegundos
     * @param dataFinal    data final em milissegundos
     * @throws SQLException caso ocorrer um erro ao buscar os dados no banco de dados
     * @throws IOException  caso ocorra algum erro ao escrever o arquivo csv no arquiv
     */
    void getExtratoIndividualProdutividadeCsv(@NotNull OutputStream outputStream,
                                              @NotNull String cpf,
                                              @NotNull Long codUnidade,
                                              @NotNull Date dataInicial,
                                              @NotNull Date dataFinal) throws SQLException, IOException;

    /**
     * Busca os dados individuais sobre produtivida do calaborador,
     * encapsulando em um objeto {@link Report} que será tratado pela aplicação requisitante.
     *
     * @param cpf         cpf do colaborador
     * @param codUnidade  código da unidade que deseja-se buscar os dados
     * @param dataInicial data inicial em milissegundos
     * @param dataFinal   data final em milissegundos
     * @return um objeto {@link Report} que conterá os dados buscados
     * @throws SQLException caso a operação não seja concluída
     */
    Report getExtratoIndividualProdutividadeReport(@NotNull String cpf,
                                                   @NotNull Long codUnidade,
                                                   @NotNull Date dataInicial,
                                                   @NotNull Date dataFinal) throws SQLException;

    void getAcessosProdutividadeCsv(@NotNull OutputStream outputStream,
                                    @NotNull String cpf,
                                    @NotNull Long codUnidade,
                                    @NotNull Date dataInicial,
                                    @NotNull Date dataFinal) throws SQLException, IOException;

    Report getAcessosProdutividadeReport(@NotNull String cpf,
                                         @NotNull Long codUnidade,
                                         @NotNull Date dataInicial,
                                         @NotNull Date dataFinal) throws SQLException;

}
