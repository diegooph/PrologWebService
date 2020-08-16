package br.com.zalf.prolog.webservice.entrega.produtividade.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.entrega.produtividade.relatorio._model.ProdutividadeColaboradorRelatorio;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Zart on 18/05/2017.
 */
public interface ProdutividadeRelatorioDao {

    /**
     * Busca os dados consolidados sobre produtivida e exporta em um arquivo csv.
     *
     * @param outputStream - arquivo binário onde o csv será escrito
     * @param codUnidade   - código da Unidade que deseja-se buscar os dados consolidados
     * @param dataInicial  - data inicial em milissegundos
     * @param dataFinal    - data final em milissegundos
     * @throws SQLException - caso ocorrer um erro ao buscar os dados no banco de dados
     * @throws IOException  - caso ocorra algum erro ao escrever o arquivo csv no arquivo binário
     */
    void getConsolidadoProdutividadeCsv(@NotNull final OutputStream outputStream,
                                        @NotNull final Long codUnidade,
                                        @NotNull final Date dataInicial,
                                        @NotNull final Date dataFinal) throws SQLException, IOException;

    /**
     * Busca os dados consolidados sobre produtivida encapsulando em um objeto {@link Report}
     * que será tratado pela aplicação requisitante.
     *
     * @param codUnidade  - código da unidade que deseja-se buscar os dados
     * @param dataInicial - data inicial em milissegundos
     * @param dataFinal   - data final em milissegundos
     * @return - um objeto {@link Report} que conterá os dados buscados
     * @throws SQLException - caso a operação não seja concluída
     */
    Report getConsolidadoProdutividadeReport(@NotNull final Long codUnidade,
                                             @NotNull final Date dataInicial,
                                             @NotNull final Date dataFinal) throws SQLException;

    /**
     * Busca os dados individuais sobre produtivida e exporta em um arquivo csv.
     *
     * @param outputStream - arquivo binário onde o csv será escrito
     * @param cpf          - cpf do colaborador
     * @param codUnidade   - código da Unidade que deseja-se buscar os dados individuais
     * @param dataInicial  - data inicial em milissegundos
     * @param dataFinal    - data final em milissegundos
     * @throws SQLException - caso ocorrer um erro ao buscar os dados no banco de dados
     * @throws IOException  - caso ocorra algum erro ao escrever o arquivo csv no arquiv
     */
    void getExtratoIndividualProdutividadeCsv(@NotNull final OutputStream outputStream,
                                              @NotNull final String cpf,
                                              @NotNull final Long codUnidade,
                                              @NotNull final Date dataInicial,
                                              @NotNull final Date dataFinal) throws SQLException, IOException;

    /**
     * Busca os dados individuais sobre produtivida do calaborador,
     * encapsulando em um objeto {@link Report} que será tratado pela aplicação requisitante.
     *
     * @param cpf         - cpf do colaborador
     * @param codUnidade  - código da unidade que deseja-se buscar os dados
     * @param dataInicial - data inicial em milissegundos
     * @param dataFinal   - data final em milissegundos
     * @return - um objeto {@link Report} que conterá os dados buscados
     * @throws SQLException - caso a operação não seja concluída
     */
    Report getExtratoIndividualProdutividadeReport(@NotNull final String cpf,
                                                   @NotNull final Long codUnidade,
                                                   @NotNull final Date dataInicial,
                                                   @NotNull final Date dataFinal) throws SQLException;

    /**
     * Busca dados sobre os acessos dos colaboradores à função de produtividade. Retorna esta informação
     * em um arquivo no formato CSV.
     *
     * @param outputStream - local onde os dados serão escritos.
     * @param cpf          - Cpf do colaborador que será buscado os dados.
     * @param codUnidade   - Códido da unidade deste colaborador.
     * @param dataInicial  - Data inicial do filtro de dados.
     * @param dataFinal    - Data final do filtro de dados.
     * @throws SQLException - Caso algum erro na busca aconteça.
     * @throws IOException  - Caso algum erro na escrita do arquivo aconteça.
     */
    void getAcessosProdutividadeCsv(@NotNull final OutputStream outputStream,
                                    @NotNull final String cpf,
                                    @NotNull final Long codUnidade,
                                    @NotNull final Date dataInicial,
                                    @NotNull final Date dataFinal) throws SQLException, IOException;

    /**
     * Busca dados sobre os acessos dos colaboradores à função de produtividade. Retorna esta informação
     * em um objeto {@link Report} para ser mostrado na tela do usuário.
     *
     * @param cpf         - Cpf do colaborador que será buscado os dados.
     * @param codUnidade  - Códido da unidade deste colaborador.
     * @param dataInicial - Data inicial do filtro de dados.
     * @param dataFinal   - Data final do filtro de dados.
     * @return - Objeto {@link Report} contendo os dados da busca.
     * @throws SQLException - Caso algum erro na busca aconteça.
     */
    Report getAcessosProdutividadeReport(@NotNull final String cpf,
                                         @NotNull final Long codUnidade,
                                         @NotNull final Date dataInicial,
                                         @NotNull final Date dataFinal) throws SQLException;

    /**
     * Busca a produtividade de cada colaborador baseado nos mapas que ele executou durante o período de filtragem.
     *
     * @param cpf              - um CPF.
     * @param codUnidade       - Códido da unidade deste colaborador.
     * @param dataInicial      - Data inicial do filtro de dados.
     * @param dataFinal        - Data final do filtro de dados.
     * @return - Uma lista de {@link ProdutividadeColaboradorRelatorio} contendo os dados buscados.
     * @throws SQLException - Caso algum erro na busca aconteça.
     */
    @NotNull
    List<ProdutividadeColaboradorRelatorio> getRelatorioProdutividadeColaborador(
            @NotNull final Long codUnidade,
            @NotNull final String cpf,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws SQLException;
}
