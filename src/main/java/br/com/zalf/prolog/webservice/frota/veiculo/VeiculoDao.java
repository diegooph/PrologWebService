package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.veiculo.model.*;
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
     * Insere um novo veículo
     *
     * @param veiculo    veículo a ser inserido
     * @param codUnidade código da unidade
     * @return resultado da requisição
     * @throws SQLException caso não seja possível realizar o insert
     */
    boolean insert(Veiculo veiculo, Long codUnidade) throws Throwable;

    /**
     * Atualiza os dados de um veículo
     *
     * @param veiculo       veículo
     * @param placaOriginal placa original do veículo
     * @return resultado da requisição
     * @throws SQLException caso não seja possível realizar o update
     */
    boolean update(Veiculo veiculo, String placaOriginal) throws SQLException;

    void updateStatus(@NotNull final Long codUnidade, @NotNull final String placa, @NotNull final Veiculo veiculo)
            throws SQLException;

    /**
     * Seta o veiculo como inativo no banco de dados
     *
     * @param placa placa do veículo a ser deletado
     * @return valor da operação
     * @throws SQLException caso não for possivel deletar
     */
    boolean delete(String placa) throws SQLException;

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
     * busca o tipo de veículo pela empresa
     *
     * @param codEmpresa código da unidade
     * @return uma lista de tipos de veículos
     * @throws Throwable caso ocorrer erro no banco
     */
    List<TipoVeiculo> getTiposVeiculosByEmpresa(@NotNull final Long codEmpresa) throws Throwable;

    /**
     * insere um tipo de veículo
     *
     * @param tipoVeiculo descrição do tipo do veículo
     * @param codEmpresa  código da empresa
     * @return valor referente a operação
     * @throws SQLException se ocorrer erro no banco
     */
    boolean insertTipoVeiculoPorEmpresa(TipoVeiculo tipoVeiculo, Long codEmpresa) throws Throwable;

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
    List<Marca> getMarcaModeloVeiculoByCodEmpresa(Long codEmpresa) throws SQLException;

    /**
     * insere um modelo de veiculo
     *
     * @param modelo     descrição do modelo
     * @param codEmpresa código da empresa
     * @param codMarca   códiga da marca
     * @return resultado da operação
     * @throws SQLException caso ocorrer erro
     */
    boolean insertModeloVeiculo(Modelo modelo, long codEmpresa, long codMarca) throws SQLException;

    /**
     * busca o total de vaículos de uma unidade
     *
     * @param codUnidade código da unidade
     * @param conn       conexão com o banco
     * @return o numero de véiculos
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
     * @param tipo
     * @param codUnidade
     * @return
     * @throws SQLException
     * @deprecated at 2019-01-17.
     * Método depreciado pois não será mais utilizado o código da unidade.
     * Utilize {@link #updateTipoVeiculo(TipoVeiculo)}.
     * <p>
     * atualizava um tipo de veículo
     */
    boolean updateTipoVeiculo(TipoVeiculo tipo, Long codUnidade) throws SQLException;

    /**
     * atualiza um tipo de veículo
     *
     * @param tipo
     * @return
     * @throws SQLException
     */
    void updateTipoVeiculo(@NotNull final TipoVeiculo tipo) throws Throwable;

    /**
     * deleta um tipo de veículo, apenas se não tiver nenhuma placa vinculada
     *
     * @param codTipo
     * @param codEmpresa
     * @return
     * @throws SQLException
     */
    void deleteTipoVeiculoByEmpresa(@NotNull final Long codTipo,
                                    @NotNull final Long codEmpresa) throws Throwable;

    TipoVeiculo getTipoVeiculo(Long codTipo) throws Throwable;

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
     * @param tipoVeiculo descrição do tipo do veículo
     * @param codUnidade  código da unidade
     * @return valor referente a operação
     * @throws SQLException se ocorrer erro no banco
     * @deprecated at 2019-01-10.
     * Método depreciado pois não será mais utilizado o código da unidade.
     * Em seu lugar será utilizado o código da empresa.
     * Utilize {@link #insertTipoVeiculoPorEmpresa(TipoVeiculo, Long)}.
     * <p>
     * inseria um tipo de veículo por unidade
     */
    @Deprecated
    boolean insertTipoVeiculo(TipoVeiculo tipoVeiculo, Long codUnidade) throws SQLException;

    /**
     * @param codUnidade código da unidade
     * @return uma lista de tipos de veículos
     * @throws SQLException caso ocorrer erro no banco
     * @deprecated at 2019-01-10.
     * Método depreciado pois não será mais utilizado o código da unidade.
     * Em seu lugar será utilizado o código da empresa.
     * Utilize {@link #getTiposVeiculosByEmpresa(Long)}.
     * <p>
     * buscava o tipo de veículo pela unidade
     */
    @Deprecated
    List<TipoVeiculo> getTipoVeiculosByUnidade(Long codUnidade) throws SQLException;

    /**
     * @param codTipo
     * @param codUnidade
     * @return
     * @throws SQLException
     * @deprecated at 2019-01-18.
     * Método depreciado pois não será mais utilizado o código da unidade.
     * Utilize {@link #deleteTipoVeiculoByEmpresa(Long, Long)}.
     * <p>
     * deletava um tipo de veículo, apenas se não tiver nenhuma placa vinculada
     */
    boolean deleteTipoVeiculo(Long codTipo, Long codUnidade) throws SQLException;

    /**
     * @param codTipo
     * @param codUnidade
     * @return
     * @throws SQLException
     * @deprecated at 2019-01-22.
     * Método depreciado pois não será mais utilizado o código da unidade.
     * Utilize {@link #getTipoVeiculo(Long)}.
     * <p>
     * buscava um tipo de veículo.
     */
    @Deprecated
    TipoVeiculo getTipoVeiculo(Long codTipo, Long codUnidade) throws SQLException;
}