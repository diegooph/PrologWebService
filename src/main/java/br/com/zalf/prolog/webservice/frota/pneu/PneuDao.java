package br.com.zalf.prolog.webservice.frota.pneu;

import br.com.zalf.prolog.webservice.frota.pneu._model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
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

    void marcarFotoComoSincronizada(@NotNull final Long codPneu,
                                    @NotNull final String urlFotoPneu) throws SQLException;

    /**
     * Método utilizado para buscar os códigos internos dos pneus dados os respectivos códigos de clientes.
     *
     * @param conn               Conexão que será utilizada para buscar os dados.
     * @param codEmpresa         Código da empresa onde esses códigos serão buscados.
     * @param codigoClientePneus Códigos dos clientes, para utilizar na busca.
     * @return Uma lista dos códigos internos que representam os códigos do cliente.
     * @throws Throwable Caso algum erro ocorrer.
     */
    @NotNull
    List<Long> getCodPneuByCodCliente(@NotNull final Connection conn,
                                      @NotNull final Long codEmpresa,
                                      @NotNull final List<String> codigoClientePneus) throws Throwable;
}