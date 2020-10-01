package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.veiculo.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.InfosVeiculoEditado;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoVisualizacao;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
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
     * @param veiculo                  Objeto contencod as informações do veículo a serem inseridas.
     * @param checklistOfflineListener Listener utilizado para notificar sobre atualizações de veículos
     *                                 no contexto de realização de checklist offline.
     * @throws Throwable Se algum erro ocorrer ao salvar as informações.
     */
    void insert(@NotNull final VeiculoCadastro veiculo,
                @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable;

    /**
     * Método utilizado para atualizar as informações de um veículo.
     *
     * @param codColaboradorResponsavelEdicao Código do colaborador responsável por realizar esta edição.
     * @param veiculo                         Objeto contendo as informações que serão atualizadas.
     * @param checklistOfflineListener        Listener utilizado para notificar a alteração em um veículo.
     * @return Dados sobre a edição realizada.
     * @throws Throwable Se algum erro ocorrer durante a operação.
     */
    @NotNull
    InfosVeiculoEditado update(@NotNull final Long codColaboradorResponsavelEdicao,
                               @NotNull final VeiculoEdicao veiculo,
                               @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener)
            throws Throwable;

    /**
     * Busca os veículos que pertencem as unidades informadas.
     *
     * @param codUnidades    Array com os códigos de unidade
     * @param apenasAtivos   indica se queremos buscar somente por veículos ativos.
     * @param codTipoVeiculo codigo de tipo de veiculo para filtragem - opcional
     * @return lista de {@link VeiculoListagem}
     * @throws Throwable caso não seja possível realizar a busca
     */
    List<VeiculoListagem> buscaVeiculosByUnidades(@NotNull final List<Long> codUnidades,
                                                  final boolean apenasAtivos,
                                                  @Nullable final Long codTipoVeiculo) throws Throwable;

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
     * Método para buscar um {@link VeiculoVisualizacao} através do {@code codVeiculo}.
     *
     * @param codVeiculo código do {@link VeiculoVisualizacao}.
     * @return O {@link VeiculoVisualizacao} contendo as informações.
     * @throws Throwable Caso aconteça algum erro no banco.
     */
    @NotNull
    VeiculoVisualizacao getVeiculoByCodigo(@NotNull final Long codVeiculo) throws Throwable;

    /**
     * Método para buscar os {@code codVeiculos} através das {@code placas}.
     *
     * @param codColaborador código do colaborador que realizou a requisição.
     * @param placas         lista de uma ou mais placas, para cada placa retornará um código, do contrário dará erro.
     * @return Uma lista de códigos de veículos.
     * @throws Throwable Caso aconteça algum erro no banco.
     */
    @NotNull
    List<Long> getCodVeiculosByPlacas(@NotNull final Long codColaborador,
                                      @NotNull final List<String> placas) throws Throwable;

    /**
     * Método para buscar um {@link Veiculo} através da {@code placa}.
     *
     * @param placa     Placa do {@link Veiculo}.
     * @param withPneus Retornar o {@link Veiculo} com seus pneus.
     * @return O {@link Veiculo} contendo as informações.
     * @throws SQLException Caso aconteça algum erro no banco.
     */
    @Deprecated
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
    @Deprecated
    @NotNull
    Veiculo getVeiculoByPlaca(@NotNull final Connection conn,
                              @NotNull final String placa,
                              final boolean withPneus) throws Throwable;

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

    /**
     * Método utilizado para buscar o código da unidade a qual a placa está associada.
     *
     * @param conn         Conexão utilizada para realizar busca no bando de dados.
     * @param placaVeiculo Placa do veículo para buscar a unidade.
     * @return O código da unidade onde o veículo está situado.
     * @throws Throwable Se algum erro ocorrer.
     */
    Long getCodUnidadeByPlaca(@NotNull final Connection conn, @NotNull final String placaVeiculo) throws Throwable;

    /**
     * Método utilizado para buscar o código de um veículo utilizando como base a {@code placaVeiculo}.
     *
     * @param conn         Conexão que será utilizada para buscar o código do veículo.
     * @param placaVeiculo Placa que será utilizada para identificar o código.
     * @return O código único de identificação do veículo no sistema.
     * @throws Throwable Se algum erro acontecer.
     */
    @NotNull
    Long getCodVeiculoByPlaca(@NotNull final Connection conn,
                              @NotNull final String placaVeiculo) throws Throwable;

    /**
     * Busca a evolução de kms de um veículo pela placa, código da empresa e em um determinado intervalo de data.
     *
     * @param codEmpresa  Código da empresa para a qual as informações serão filtradas.
     * @param codVeiculo  Código do veículo para o qual as informações serão filtradas.
     * @param dataInicial Data inicial para a qual as informações serão filtradas.
     * @param dataFinal   Data final para a qual as informações serão filtradas.
     * @throws Throwable  Se algum erro ocorrer.
     */
    @NotNull
    List<VeiculoEvolucaoKm> getVeiculoEvolucaoKm(@NotNull final Long codEmpresa,
                                                 @NotNull final Long codVeiculo,
                                                 @NotNull final LocalDate dataInicial,
                                                 @NotNull final LocalDate dataFinal) throws Throwable;

    @Deprecated
    /**
     * Busca os veículos ativos de uma determinada unidade
     *
     * @param codUnidade um código
     * @param ativos     indica se queremos buscar os veículos ativos ou não.
     * @return lista de Veiculo
     * @throws SQLException caso não seja possível realizar a busca
     */
    List<Veiculo> getVeiculosAtivosByUnidade(Long codUnidade, @Nullable Boolean ativos) throws SQLException;
}