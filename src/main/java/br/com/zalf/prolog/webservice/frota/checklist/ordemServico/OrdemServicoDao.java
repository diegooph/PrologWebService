package br.com.zalf.prolog.webservice.frota.checklist.ordemServico;

import br.com.zalf.prolog.webservice.frota.checklist.Checklist;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by didi on 9/16/16.
 */
public interface OrdemServicoDao {

	/**
	 * busca OS - Orde de Serviço - na banco
	 * @param placa placa do veículo
	 * @param status status
	 * @param codUnidade código da unidade
	 * @param tipoVeiculo tipo de veículo
	 * @param limit limit de busca no banco
	 * @param offset offset de busca no banco
	 * @return uma lista de Ordens de serviços
	 * @throws SQLException caso ocorrer erro no banco
	 */
	List<OrdemServico> getOs(String placa, String status, Long codUnidade,
							 String tipoVeiculo, Integer limit, Long offset) throws SQLException;

	//TODO - adicionar javadoc
	List<ItemOrdemServico> getItensOsManutencaoHolder(String placa, String status,
													  int limit, long offset, String prioridade) throws SQLException;

	/**
	 * insere um item com problema na OS
	 * @param checklist um checklist
	 * @param conn conexão do banco
	 * @param codUnidade código da unidade
	 * @throws SQLException se ocorrer erro no banco
	 */
	void insertItemOs(Checklist checklist, Connection conn, Long codUnidade) throws SQLException;

	//TODO - adicionar javadoc
	List<ManutencaoHolder> getResumoManutencaoHolder(String placa, String codTipo, Long codUnidade, int limit,
													 long offset, String status) throws SQLException;

	/**
	 * marca um item como consertado
	 * @param item item consertado
	 * @param placa placa do veículo
	 * @return valor da operação
	 * @throws SQLException se ocorrer erro no banco
	 */
	boolean consertaItem (ItemOrdemServico item, String placa) throws SQLException;

}
