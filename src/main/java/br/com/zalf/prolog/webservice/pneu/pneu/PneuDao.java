package br.com.zalf.prolog.webservice.pneu.pneu;

import br.com.zalf.prolog.commons.veiculo.Marca;
import br.com.zalf.prolog.commons.veiculo.Modelo;
import br.com.zalf.prolog.commons.veiculo.Veiculo;
import br.com.zalf.prolog.frota.pneu.Pneu;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface PneuDao {

	/**
	 * retorna uma lista de pneus da placa requerida
	 * @param placa placa do veículo
	 * @return lista de pneus
	 * @throws SQLException
	 */
	List<Pneu> getPneusByPlaca(String placa) throws SQLException;

	/**
	 * insere um pneu
	 * @param pneu um pneu
	 * @param codUnidade código da unidade
	 * @return valor da operação
	 * @throws SQLException
	 */
	boolean insert(Pneu pneu, Long codUnidade) throws SQLException;

	/**
	 * atualiza medições do pneu no banco
	 * @param pneu um pneu
	 * @param codUnidade código da unidade
	 * @param conn conexão do banco
	 * @return valor da operação
	 * @throws SQLException
	 */
	boolean updateMedicoes (Pneu pneu, Long codUnidade, Connection conn) throws SQLException;

	/**
	 * atualiza valores do pneu
	 * @param pneu um pneu
	 * @param codUnidade código da unidade
	 * @param codOriginal código original do pneu
	 * @return valor da operação
	 * @throws SQLException
	 */
	boolean update (Pneu pneu, Long codUnidade, Long codOriginal) throws SQLException;

	/**
	 * atualiza a calibragem do pneu
	 * @param pneu um pneu
	 * @param codUnidade código da unidade
	 * @param conn conexão
	 * @throws SQLException
	 */
	void updateCalibragem (Pneu pneu, Long codUnidade, Connection conn) throws SQLException;

	/**
	 * atualiza status do pneu
	 * @param pneu um pneu
	 * @param codUnidade código da unidade
	 * @param status status do pneu
	 * @param conn conexão do banco
	 * @return valor da operação
	 * @throws SQLException
	 */
	boolean updateStatus (Pneu pneu, Long codUnidade, String status, Connection conn) throws SQLException;

	//TODO - comentar o javadoc
	boolean registraMovimentacaoHistorico (Pneu pneu, Long codUnidade, String statusDestino,
										   long kmVeiculo,String placaVeiculo, Connection conn, String token) throws SQLException;

	/**
	 * atualiza dados do veículo
	 * @param placa placa do véiculo
	 * @param pneu pneu
	 * @param pneuNovo novo valor a ser inserido em pneu
	 * @param conn conexão do banco
	 * @throws SQLException
	 */
	void updateVeiculoPneu (String placa, Pneu pneu, Pneu pneuNovo, Connection conn) throws SQLException;

	/**
	 * busca uma lista de pneus com base no código e status
	 * @param codUnidade código unidade
	 * @param status status do pneu
	 * @return uma lista de pneus
	 * @throws SQLException
	 */
	List<Pneu> getPneuByCodUnidadeByStatus(Long codUnidade, String status) throws SQLException;

	/**
	 * cria um novo pneu
	 * @param rSet conjunto de informações do banco
	 * @return um novo pneu
	 * @throws SQLException
	 */
	Pneu createPneu (ResultSet rSet) throws SQLException;

	/**
	 * retorna uma lista de marcas de pneus da empresa
	 * @param codEmpresa código da empresa
	 * @return uma lista de marcas
	 * @throws SQLException
	 */
	List<Marca> getMarcaModeloPneuByCodEmpresa(Long codEmpresa) throws SQLException;

	/**
	 * lista todas as dimensões
	 * @return uma lista com todas as dimensões
	 * @throws SQLException
	 */
	List<Pneu.Dimensao> getDimensoes() throws SQLException;

	/**
	 * insere um modelo de pneu
	 * @param modelo um modelo
	 * @param codEmpresa código da empresa
	 * @param codMarca código da marca
	 * @return valor da operação
	 * @throws SQLException
	 */
	boolean insertModeloPneu(Modelo modelo, long codEmpresa, long codMarca) throws SQLException;

	/**
	 * vinvula pneus a um veiculo
	 * @param veiculo um veículo
	 * @return valor da operação
	 * @throws SQLException
	 */
	boolean vinculaPneuVeiculo(Veiculo veiculo) throws SQLException;

}
