package br.com.zalf.prolog.webservice.pneu.pneu;

import br.com.zalf.prolog.commons.veiculo.Marca;
import br.com.zalf.prolog.commons.veiculo.Modelo;
import br.com.zalf.prolog.frota.pneu.Pneu;

import java.sql.Connection;
import java.sql.ResultSet;
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
     * @return valor da operação
     * @throws SQLException caso ocorra erro no banco
     */
    boolean insert(Pneu pneu, Long codUnidade) throws SQLException;

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
     * @param codOriginal código original do pneu
     * @return valor da operação
     * @throws SQLException caso ocorra erro no banco
     */
    boolean update(Pneu pneu, Long codUnidade, Long codOriginal) throws SQLException;

    /**
     * atualiza a calibragem do pneu
     *
     * @param pneu       um pneu
     * @param codUnidade código da unidade
     * @param conn       conexão
     * @throws SQLException caso ocorra erro no banco
     */
    void updateCalibragem(Pneu pneu, Long codUnidade, Connection conn) throws SQLException;

    /**
     * atualiza status do pneu
     *
     * @param pneu       um pneu
     * @param codUnidade código da unidade
     * @param status     status do pneu
     * @param conn       conexão do banco
     * @return valor da operação
     * @throws SQLException caso ocorra erro no banco
     */
    boolean updateStatus(Pneu pneu, Long codUnidade, String status, Connection conn) throws SQLException;

    //TODO - comentar o javadoc
    boolean registraMovimentacaoHistorico(Pneu pneu, Long codUnidade, String statusDestino,
                                          long kmVeiculo, String placaVeiculo, Connection conn, String token) throws SQLException;

    /**
     * atualiza dados do veículo
     *
     * @param placa    placa do véiculo
     * @param pneu     pneu
     * @param pneuNovo novo valor a ser inserido em pneu
     * @param conn     conexão do banco caso ocorra erro no banco
     * @throws SQLException
     */
    void updateVeiculoPneu(String placa, Pneu pneu, Pneu pneuNovo, Connection conn) throws SQLException;

    /**
     * busca uma lista de pneus com base no código e status
     *
     * @param codUnidade código unidade
     * @param status     status do pneu
     * @return uma lista de pneus
     * @throws SQLException caso ocorra erro no banco
     */
    List<Pneu> getPneuByCodUnidadeByStatus(Long codUnidade, String status) throws SQLException;

    /**
     * cria um novo pneu
     *
     * @param rSet conjunto de informações do banco
     * @return um novo pneu
     * @throws SQLException caso ocorra erro no banco
     */
    Pneu createPneu(ResultSet rSet) throws SQLException;

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
     * @return valor da operação
     * @throws SQLException caso ocorra erro no banco
     */
    boolean insertModeloPneu(Modelo modelo, long codEmpresa, long codMarca) throws SQLException;

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
     * Incrementa a vida atual de um determinado pneu
     *
     * @param conn       uma connection aberta previamente
     * @param codPneu    código do pneu
     * @param codUnidade código da unidade
     * @return booleal com resultado da operação
     * @throws SQLException em caso de erro
     */
    boolean incrementaVida(Connection conn, int codPneu, Long codUnidade) throws SQLException;

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
    Long insertModeloBanda (Modelo modelo, Long codMarcaBanda, Long codEmpresa) throws SQLException;
}
