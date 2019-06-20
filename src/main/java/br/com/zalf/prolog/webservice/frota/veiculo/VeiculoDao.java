package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Eixos;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Contém os métodos para manipular os veículos
 */
public interface VeiculoDao {

    /**
     * Insere um novo veículo no banco de dados.
     *
     * @param codUnidade               Código da Unidade a qual esse veículo será inserido.
     * @param veiculo                  Objeto contencod as informações do veículo a serem inseridas.
     * @param checklistOfflineListener Listener utilizado para notificar sobre atualizações de veículos
     *                                 no contexto de realização de checklist offline.
     * @return <code>TRUE</code> se operação for bem sucedida, <code>FALSE</code> caso contrário.
     * @throws Throwable Se algum erro ocorrer ao salvar as informações.
     */
    boolean insert(@NotNull final Long codUnidade,
                   @NotNull final Veiculo veiculo,
                   @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    /**
     * Método utilizado para atualizar as informações de um veículo.
     *
     * @param placaOriginal            Placa do veículo que será atualizado.
     * @param veiculo                  Objeto contendo as informações que serão inseridas.
     * @param checklistOfflineListener Listener utilizado para notificar a alteração em um veículo.
     * @return <code>TRUE</code> se a operação se bem sucedida, <code>FALSE</code> caso contrário.
     * @throws Throwable Se algum erro ocorrer durante a operação.
     */
    boolean update(@NotNull final String placaOriginal,
                   @NotNull final Veiculo veiculo,
                   @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    /**
     * Método utilizado para atualizar o status (ATIVO ou INATIVO) de um veículo.
     *
     * @param codUnidade               Código da Unidade a qual o veículo está alocado.
     * @param placa                    Placa do veículo, utilizada como identificador para este método.
     * @param veiculo                  Objeto contendo a informação de se o veículo deve ser ativado ou inativado.
     * @param checklistOfflineListener Listener utilizado para notificar sobre atualizações de veículos
     *                                 no contexto de realização de checklist offline.
     * @throws Throwable Caso algum erro ocorra na atualização do veículo.
     */
    void updateStatus(@NotNull final Long codUnidade,
                      @NotNull final String placa,
                      @NotNull final Veiculo veiculo,
                      @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    /**
     * Altera o status do veículo como <code>INATIVO</code> no banco de dados.
     *
     * @param placa                    Placa do veículo que será inativado, utilizado como identificador.
     * @param checklistOfflineListener Listener utilizado para notificar sobre atualizações de veículos
     *                                 no contexto de realização de checklist offline.
     * @return <code>TRUE</code> se operação realizada com sucesso, <code>FALSE</code> caso contrário.
     * @throws Throwable Se algum erro ocorrer no processo de inativação do veículo.
     */
    boolean delete(@NotNull final String placa,
                   @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    /**
     * Busca os veículos ativos de uma determinada unidade
     *
     * @param codUnidade um código
     * @param ativos     indica se queremos buscar os veículos ativos ou não.
     * @return lista de Veiculo
     * @throws SQLException caso não seja possível realizar a busca
     */
    List<Veiculo> getVeiculosAtivosByUnidade(Long codUnidade, @Nullable Boolean ativos) throws SQLException;

    /**
     * Busca os veículos ativos de uma determinada unidade
     *
     * @param cpf um cpf, ao qual será feita a busca da unidade
     * @return lista de Veiculo
     * @throws SQLException caso não seja possível realizar a busca
     */
    List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(Long cpf)
            throws SQLException;

    /**
     * Método para buscar um {@link Veiculo} através da {@code placa}.
     *
     * @param placa     Placa do {@link Veiculo}.
     * @param withPneus Retornar o {@link Veiculo} com seus pneus.
     * @return O {@link Veiculo} contendo as informações.
     * @throws SQLException Caso aconteça algum erro no banco.
     */
    @NotNull
    Veiculo getVeiculoByPlaca(@NotNull final String placa, final boolean withPneus) throws SQLException;

    /**
     * Método para buscar um {@link Veiculo} através da {@code placa}.
     *
     * @param placa     Placa do {@link Veiculo}.
     * @param withPneus Retornar o {@link Veiculo} com seus pneus.
     * @return O {@link Veiculo} contendo as informações.
     * @throws SQLException Caso aconteça algum erro no banco.
     */
    @NotNull
    Veiculo getVeiculoByPlaca(@NotNull final Connection conn,
                              @NotNull final String placa,
                              final boolean withPneus) throws Throwable;

    /**
     * busca os eixos
     *
     * @return uma lista de eixos
     * @throws SQLException se algo der errado no banco
     */
    List<Eixos> getEixos() throws SQLException;

    /**
     * atualiza a quilometragem atraves da placa do veículo
     *
     * @param placa placa do veículo
     * @param km    quilometragem
     * @param conn  conexão com o banco
     * @throws SQLException erro no banco
     */
    void updateKmByPlaca(String placa, long km, Connection conn) throws SQLException;

    /**
     * busca a marca do veículo atraves do código da empresa
     *
     * @param codEmpresa código da empresa
     * @return lista de marcas
     * @throws SQLException se ocorrer erro no banco
     */
    @Deprecated
    List<Marca> getMarcaModeloVeiculoByCodEmpresa(Long codEmpresa) throws SQLException;

    /**
     * As marcas de veículos são a nível ProLog. Esse método retorna uma lista com todas as marcas disponíveis.
     * Importante lembrar que os modelos para cada marca não serão setados, já que modelos de veículos são por empresa.
     *
     * @return uma lista de {@link Marca marcas}.
     * @throws Throwable caso qualquer erro aconteça.
     */
    @NotNull
    List<Marca> getMarcasVeiculosNivelProLog() throws Throwable;

    /**
     * As marcas de veículos são a nível ProLog, porém, os modelos são a nível de empresa. Esse método retorna uma
     * lista com todas as marcas disponíveis, cada marca contém uma lista de modelos com os modelos criados pela
     * empresa para qual as informações foram solicitadas.
     * Caso a empresa não tenha modelos para uma marca qualquer, essa marca irá possuir uma lista vazia de modelos,
     * não nula.
     *
     * @return uma lista de {@link Marca marcas}.
     * @throws Throwable caso qualquer erro aconteça.
     */
    @NotNull
    List<Marca> getMarcasModelosVeiculosByEmpresa(@NotNull final Long codEmpresa) throws Throwable;

    /**
     * Insere um modelo de veiculo
     *
     * @param modelo     descrição do modelo.
     * @param codEmpresa código da empresa.
     * @param codMarca   códiga da marca.
     * @return código do novo modelo inserido.
     * @throws Throwable caso ocorrer erro.
     */
    @NotNull
    Long insertModeloVeiculo(@NotNull final Modelo modelo,
                             @NotNull final Long codEmpresa,
                             @NotNull final Long codMarca) throws Throwable;

    /**
     * busca o total de vaículos de uma unidade
     *
     * @param codUnidade código da unidade
     * @param conn       conexão com o banco
     * @return o numero de veículos
     * @throws SQLException caso ocorrer erro no banco
     */
    int getTotalVeiculosByUnidade(Long codUnidade, Connection conn) throws SQLException;

    /**
     * busca os veículo por tipo
     *
     * @param codUnidade código da unidade
     * @param codTipo    codígo do tipo
     * @return lista de placas de veículos
     * @throws SQLException se acontecer erro no banco
     */
    List<String> getPlacasVeiculosByTipo(Long codUnidade, String codTipo) throws SQLException;

    Optional<DiagramaVeiculo> getDiagramaVeiculoByPlaca(@NotNull final String placa) throws SQLException;

    /**
     * Método utilizado para buscar o {@link DiagramaVeiculo} com base na {@code placa}.
     *
     * @param conn  {@link Connection} que será utilizada para realizar a operação.
     * @param placa Placa do {@link Veiculo}.
     * @return Caso exista, retornará o {@link DiagramaVeiculo} caso contrário NULL.
     * @throws SQLException Se qualquer erro ocorrer na busca.
     */
    Optional<DiagramaVeiculo> getDiagramaVeiculoByPlaca(@NotNull final Connection conn,
                                                        @NotNull final String placa) throws SQLException;

    Optional<DiagramaVeiculo> getDiagramaVeiculoByCod(@NotNull final Short codDiagrama) throws SQLException;

    Set<DiagramaVeiculo> getDiagramasVeiculos() throws SQLException;

    /**
     * busca um modelo de veículo a partir de sua chave
     *
     * @param codUnidade
     * @param codModelo
     * @return
     * @throws SQLException
     */
    Modelo getModeloVeiculo(Long codUnidade, Long codModelo) throws SQLException;

    /**
     * Atualiza nome de um modelo de veículo
     *
     * @param modelo
     * @param codUnidade
     * @return
     * @throws SQLException
     */
    boolean updateModelo(Modelo modelo, Long codUnidade, Long codMarca) throws SQLException;

    /**
     * deleta um modelo de veiculo do banco, só funciona quando o modelo não está vínculado a nenhum veículo
     *
     * @param codModelo
     * @param codUnidade
     * @return
     * @throws SQLException
     */
    boolean deleteModelo(Long codModelo, Long codUnidade) throws SQLException;

    /**
     * Aplica um pneu à uma posição específico do veículo.
     *
     * @param conn               - {@link Connection} que será utilizada para conectar ao banco de dados.
     * @param codUnidade         - Código da {@link Unidade} em que veículo e pneu estão alocados.
     * @param placa              - A placa do veículo no qual o pneu será aplicado.
     * @param codPneu            - O código do pneu que deve ser aplicado.
     * @param posicaoPneuVeiculo - A posição na qual o pneu será aplicado no {@link Veiculo}.
     * @throws Throwable - Se algum erro ocorrer.
     */
    void adicionaPneuVeiculo(@NotNull final Connection conn,
                             @NotNull final Long codUnidade,
                             @NotNull final String placa,
                             @NotNull final Long codPneu,
                             final int posicaoPneuVeiculo) throws Throwable;

    /**
     * Remove um pneu do veículo no qual ele está aplicado.
     *
     * @param conn       - {@link Connection} que será utilizada para conectar ao banco de dados.
     * @param codUnidade - Código da {@link Unidade} em que veículo e pneu estão alocados.
     * @param placa      - A placa do veículo no qual o pneu que deve ser removido se encontra.
     * @param codPneu    - O código do pneu que deve ser removido.
     * @throws Throwable - Se algum erro ocorrer.
     */
    void removePneuVeiculo(@NotNull final Connection conn,
                           @NotNull final Long codUnidade,
                           @NotNull final String placa,
                           @NotNull final Long codPneu) throws Throwable;

    /**
     * Busca todos os códigos dos pneus que estão aplicados ao veículo informado.
     *
     * @param conn       {@link Connection} que será utilizada para conectar ao banco de dados.
     * @param codVeiculo Código do {@link Veiculo veículo} do qual serão buscados os pneus aplicados.
     * @return Um {@link Optional optional} contendo a lista de códigos de pneus aplicados ao veículo,
     * caso exista algum aplicado; Um {@link Optional optional} vazio caso não exista nenhum.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Optional<List<Long>> getCodPneusAplicadosVeiculo(@NotNull final Connection conn,
                                                     @NotNull final Long codVeiculo) throws Throwable;

    Long getCodUnidadeByPlaca(@NotNull final Connection conn, @NotNull final String placaVeiculo) throws Throwable;
}