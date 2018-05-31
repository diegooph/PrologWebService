package br.com.zalf.prolog.webservice.frota.pneu.pneu;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Modelo;
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
     * @throws SQLException caso ocorra erro no banco
     */
    @NotNull
    Long insert(Pneu pneu, Long codUnidade) throws SQLException;

    /**
     * atualiza medições do pneu no banco
     *
     * @param pneu       um pneu
     * @param codUnidade código da unidade
     * @param conn       conexão do banco
     * @return valor da operação
     * @throws SQLException caso ocorra erro no banco
     */
    boolean updateMedicoes(Pneu pneu, Long codUnidade, Connection conn) throws SQLException;

    /**
     * atualiza valores do pneu
     *
     * @param pneu        um pneu
     * @param codUnidade  código da unidade
     * @param codOriginalPneu código original do pneu
     * @return valor da operação
     * @throws SQLException caso ocorra erro no banco
     */
    boolean update(Pneu pneu, Long codUnidade, Long codOriginalPneu) throws SQLException;

    /**
     * Atualiza a pressão do pneu.
     */
    boolean updatePressao(Long codPneu, double pressao, Long codUnidade, Connection conn) throws SQLException;

    /**
     * atualiza status do pneu
     *
     * @param pneu       um pneu
     * @param codUnidade código da unidade
     * @param status     status do pneu
     * @param conn       conexão do banco
     * @throws SQLException caso ocorra erro no banco
     */
    void updateStatus(Pneu pneu, Long codUnidade, StatusPneu status, Connection conn) throws SQLException;

    /**
     * Altera a vida atual de um determinado {@link Pneu}. Sempre que um {@link Pneu} tiver sua vida alterada,
     * é necessário aterar também seu código de {@link Banda}, pois significa que o mesmo foi recapado.
     */
    void trocarVida(Pneu pneu, Long codUnidade, Connection conn) throws SQLException;

    void updateSulcos(Long codPneu, Sulcos novosSulcos, Long codUnidade, Connection conn) throws SQLException;

    /**
     * busca uma lista de pneus com base no código e status
     *
     * @param codUnidade código unidade
     * @param status     status do pneu
     * @return uma lista de pneus
     * @throws SQLException caso ocorra erro no banco
     */
    @NotNull
    List<Pneu> getPneusByCodUnidadeByStatus(Long codUnidade, StatusPneu status) throws Throwable;

    @NotNull
    List<Pneu> getTodosPneus(@NotNull Long codUnidade) throws Throwable;

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
    List<Pneu.Dimensao> getDimensoes() throws SQLException;

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
    boolean vinculaPneuVeiculo(String placaVeiculo, List<Pneu> pneus) throws SQLException;

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
     * @param marca marca a ser inserida
     * @param codEmpresa código da empresa a ser vinculada a marca
     * @return código gerado pelo BD para a nova banda inserida
     * @throws SQLException
     */
    Long insertMarcaBanda (Marca marca, Long codEmpresa) throws SQLException;

    /**
     * Insere um nomo modelo de banda
     * @param modelo modelo a ser inserido
     * @param codMarcaBanda código da marca da banda que esse modelo pertence
     * @param codEmpresa código da empresa que esse modelo pertence
     * @return código gerado pelo BD para o novo modelo inserido
     * @throws SQLException
     */
    Long insertModeloBanda (ModeloBanda modelo, Long codMarcaBanda, Long codEmpresa) throws SQLException;

    /**
     * Atualiza o nome de uma marca
     * @param marca marca com o nome atualizado
     * @param codEmpresa código da empresa na qual a marca pertence
     * @return
     * @throws SQLException
     */
    boolean updateMarcaBanda(Marca marca, Long codEmpresa) throws SQLException;

    /**
     * Atualiza o nome de um modelo de banda
     * @param modelo modelo da banda a ser atualizada
     * @return
     * @throws SQLException
     */
    boolean updateModeloBanda(Modelo modelo) throws SQLException;

    /**
     * Busca um pneu através de seu código
     * @param codPneu
     * @param codUnidade
     * @return
     * @throws SQLException
     */
    Pneu getPneuByCod(Long codPneu, Long codUnidade) throws SQLException;

    /**
     * Busca um modelo de pneu a partir de seu código único
     * @param codModelo
     * @return
     * @throws SQLException
     */
    Modelo getModeloPneu(Long codModelo) throws SQLException;

    void marcarFotoComoSincronizada(@NotNull final Long codUnidade,
                                    @NotNull final Long codPneu,
                                    @NotNull final String urlFotoPneu) throws SQLException;
}