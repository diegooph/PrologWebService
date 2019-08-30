package br.com.zalf.prolog.webservice.frota.pneu.pneu;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface PneuDao {

    /**
     * retorna uma lista de pneus da placa requerida
     *
     * @param placa placa do veículo
     * @return lista de pneus
     * @throws SQLException caso ocorra erro no banco
     */
    List<Pneu> getPneusByPlaca(String placa) throws SQLException;

    /**
     * insere um pneu
     *
     * @param pneu       um pneu
     * @param codUnidade código da unidade
     * @return código do pneu recém cadastrado
     * @throws Throwable caso ocorra erro no banco
     */
    @NotNull
    Long insert(Pneu pneu, Long codUnidade) throws Throwable;

    /**
     * Insere vários pneus em lote, caso um falhe, aborta a inserção de todos.
     *
     * @param pneus Pneus para inserir.
     * @return Códigos dos pneus recém cadastrados.
     * @throws Throwable Caso ocorra erro no banco.
     */
    @NotNull
    List<Long> insert(@NotNull final List<Pneu> pneus) throws Throwable;

    /**
     * Atualiza medições do pneu no banco.
     *
     * @param conn        Instância da conexão com o banco de dados.
     * @param codPneu     Código do {@link Pneu} que será atualizado.
     * @param novosSulcos Valores de {@link Sulcos} a serem inseridos.
     * @param novaPressao Valor da pressão coletada.
     * @return Valor booleano indicando se a operação foi sucesso ou não.
     * @throws Throwable Se algum erro ocorrer na atualização.
     */
    @SuppressWarnings("UnusedReturnValue")
    @CanIgnoreReturnValue
    boolean updateMedicoes(@NotNull final Connection conn,
                           @NotNull final Long codPneu,
                           @NotNull final Sulcos novosSulcos,
                           final double novaPressao) throws Throwable;

    /**
     * atualiza valores do pneu
     *
     * @param pneu            um pneu
     * @param codUnidade      código da unidade
     * @param codOriginalPneu código original do pneu
     * @throws SQLException caso ocorra erro no banco
     */
    void update(@NotNull final Pneu pneu,
                @NotNull final Long codUnidade,
                @NotNull final Long codOriginalPneu) throws Throwable;

    /**
     * Atualiza a pressão do pneu.
     *
     * @param conn    Instância da conexão com o banco de dados.
     * @param codPneu Código do {@link Pneu} que será atualizado.
     * @param pressao Nova pressão a ser inserida no pneu.
     * @return Valor booleano indicando se a operação foi sucesso ou não.
     * @throws Throwable Se algum erro ocorrer na atualização.
     */
    @SuppressWarnings("UnusedReturnValue")
    @CanIgnoreReturnValue
    boolean updatePressao(@NotNull final Connection conn,
                          @NotNull final Long codPneu,
                          final double pressao) throws Throwable;

    /**
     * Atualiza a pressão do pneu.
     *
     * @param conn        Instância da conexão com o banco de dados.
     * @param codPneu     Código do {@link Pneu} que será atualizado.
     * @param novosSulcos Novos {@link Sulcos} a serem inseridos no pneu.
     * @throws Throwable Se algum erro ocorrer na atualização.
     */
    void updateSulcos(@NotNull final Connection conn,
                      @NotNull final Long codPneu,
                      @NotNull final Sulcos novosSulcos) throws Throwable;

    /**
     * atualiza status do pneu
     *
     * @param conn   conexão do banco
     * @param pneu   um pneu
     * @param status status do pneu
     * @throws SQLException caso ocorra erro no banco
     */
    void updateStatus(@NotNull final Connection conn,
                      @NotNull final Pneu pneu,
                      @NotNull final StatusPneu status) throws SQLException;

    /**
     * Altera a vida atual de um determinado {@link Pneu}. Sempre que um {@link Pneu} tiver sua vida alterada,
     * é necessário aterar também seu código de {@link Banda}, pois significa que o mesmo foi recapado.
     *
     * @param conn           - {@link Connection} pela qual a troca de vida do pneu será realizada.
     * @param codPneu        - Código do {@link Pneu} que sofreu a troca de vida.
     * @param codModeloBanda - Código do {@link ModeloBanda} que será aplicado no {@link Pneu}.
     * @throws SQLException - Se algum erro ocorrer na execução da troca de vida.
     */
    void incrementaVidaPneu(@NotNull final Connection conn,
                            @NotNull final Long codPneu,
                            @NotNull final Long codModeloBanda) throws Throwable;

    /**
     * Busca uma lista de pneus com base no código e status.
     *
     * @param codUnidade código unidade
     * @param status     status do pneu
     * @return uma lista de pneus
     * @throws Throwable caso aconteça um erro
     */
    @NotNull
    List<Pneu> getPneusByCodUnidadeByStatus(@NotNull final Long codUnidade, @NotNull final StatusPneu status) throws Throwable;

    @NotNull
    List<Pneu> getTodosPneus(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    List<Pneu> getPneusAnalise(@NotNull Long codUnidade) throws Throwable;

    /**
     * retorna uma lista de marcas de pneus da empresa
     *
     * @param codEmpresa código da empresa
     * @return uma lista de marcas
     * @throws SQLException caso ocorra erro no banco
     */
    List<Marca> getMarcaModeloPneuByCodEmpresa(Long codEmpresa) throws SQLException;

    /**
     * lista todas as dimensões
     *
     * @return uma lista com todas as dimensões
     * @throws SQLException caso ocorra erro no banco
     */
    List<PneuComum.Dimensao> getDimensoes() throws SQLException;

    /**
     * insere um modelo de pneu
     *
     * @param modelo     um modelo
     * @param codEmpresa código da empresa
     * @param codMarca   código da marca
     * @return codigo de inserção do modelo
     * @throws SQLException caso ocorra erro no banco
     */
    Long insertModeloPneu(ModeloPneu modelo, Long codEmpresa, Long codMarca) throws SQLException;

    /**
     * Vincula pneus a um veículo
     *
     * @param placaVeiculo a placa de um veículo
     * @param pneus        os pneus que se deseja vincular
     * @return true se deu certo; caso contrário false
     * @throws SQLException caso ocorra erro no banco
     */
    boolean vinculaPneuVeiculo(String placaVeiculo, List<PneuComum> pneus) throws SQLException;

    /**
     * Busca as marcas e modelos de bandas de uma empresa
     *
     * @param codEmpresa código da empresa
     * @return uma lista de marcas contendo os modelos de cada uma
     * @throws SQLException caso não seja possivel realizar a busca
     */
    List<Marca> getMarcaModeloBanda(Long codEmpresa) throws SQLException;

    /**
     * Insere uma nova marca de banda
     *
     * @param marca      marca a ser inserida
     * @param codEmpresa código da empresa a ser vinculada a marca
     * @return código gerado pelo BD para a nova banda inserida
     * @throws SQLException
     */
    Long insertMarcaBanda(Marca marca, Long codEmpresa) throws SQLException;

    /**
     * Insere um nomo modelo de banda
     *
     * @param modelo        modelo a ser inserido
     * @param codMarcaBanda código da marca da banda que esse modelo pertence
     * @param codEmpresa    código da empresa que esse modelo pertence
     * @return código gerado pelo BD para o novo modelo inserido
     * @throws SQLException
     */
    Long insertModeloBanda(ModeloBanda modelo, Long codMarcaBanda, Long codEmpresa) throws SQLException;

    /**
     * Atualiza o nome de uma marca
     *
     * @param marca      marca com o nome atualizado
     * @param codEmpresa código da empresa na qual a marca pertence
     * @return
     * @throws SQLException
     */
    boolean updateMarcaBanda(Marca marca, Long codEmpresa) throws SQLException;

    /**
     * Atualiza o nome de um modelo de banda
     *
     * @param modelo modelo da banda a ser atualizada
     */
    boolean updateModeloBanda(@NotNull final Modelo modelo) throws SQLException;

    /**
     * Busca um pneu através de seu código e código da sua unidade.
     */
    @NotNull
    Pneu getPneuByCod(@NotNull final Long codPneu, @NotNull final Long codUnidade) throws Throwable;

    /**
     * Busca um pneu através de seu código e código da sua unidade reaproveitando uma connection já aberta.
     */
    @NotNull
    Pneu getPneuByCod(@NotNull final Connection conn,
                      @NotNull final Long codUnidade,
                      @NotNull final Long codPneu) throws Throwable;

    /**
     * Busca um modelo de pneu a partir de seu código único
     */
    @NotNull
    Modelo getModeloPneu(@NotNull final Long codModelo) throws SQLException;

    void marcarFotoComoSincronizada(@NotNull final Long codPneu,
                                    @NotNull final String urlFotoPneu) throws SQLException;


    /**
     * Insere ou atualiza as informações de uma {@link PneuNomenclaturaItem pneuNomenclaturaItem}.
     *
     * @param pneuNomenclaturaItem    Lista de objetos contendo as informações para a pneuNomenclaturaItem.
     * @param userToken o token do usuário que fez a requisição.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    void insertOrUpdateNomenclatura(@NotNull final List <PneuNomenclaturaItem> pneuNomenclaturaItem,
                                    @NotNull final String userToken) throws Throwable;
}