package br.com.zalf.prolog.webservice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.imports.Tracking;

public class TrackingDaoImpl extends DatabaseConnection {

	public boolean insertOrUpdateTracking (List<Tracking> listTracking, Colaborador colaborador) throws SQLException {
		System.out.println("Entrou no insertOrUpdateTracking");
		//System.out.println(listMapas.get(0));
		for(Tracking tracking : listTracking){

			if(updateTracking(tracking, colaborador)){
				// Mapa ja existia e foi atualizado
				System.out.println("update tracking");
			}else{
				System.out.println("insert tracking");
				// Mapa não existia e foi inserido na base
				insertTracking(tracking, colaborador);
			}
		}
		return true;
	}



	public boolean insertTracking (Tracking tracking, Colaborador colaborador) throws SQLException{

		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO TRACKING VALUES("
					+ " ?,  ?,  ?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,"
					+ "	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,"
					+ "	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,"
					+ "	?,  ?)");

			stmt.setInt(1, tracking.classe);
			stmt.setDate(2, DateUtils.toSqlDate(tracking.data));
			stmt.setInt(3, tracking.mapa);
			stmt.setString(4, tracking.placa);
			stmt.setInt(5, tracking.codCliente);
			stmt.setInt(6, tracking.seqReal);
			stmt.setInt(7, tracking.seqPlan);
			stmt.setTime(8, tracking.inicioRota);
			stmt.setTime(9, tracking.horarioMatinal);
			stmt.setTime(10, tracking.saidaCDD);
			stmt.setTime(11, tracking.chegadaPDV);
			stmt.setTime(12, tracking.tempoPrevRetorno);
			stmt.setTime(13, tracking.tempoRetorno);
			stmt.setDouble(14, tracking.distPrevRetorno);
			stmt.setDouble(15, tracking.distPercRetorno);
			stmt.setTime(16, tracking.inicioEntrega);
			stmt.setTime(17, tracking.fimEntrega);
			stmt.setTime(18, tracking.fimRota);
			stmt.setTime(19, tracking.entradaCDD);
			stmt.setDouble(20, tracking.caixasCarregadas);
			stmt.setDouble(21, tracking.caixasDevolvidas);
			stmt.setDouble(22, tracking.repasse);
			stmt.setTime(23, tracking.tempoEntrega);
			stmt.setTime(24, tracking.tempoDescarga);
			stmt.setTime(25, tracking.tempoEspera);
			stmt.setTime(26, tracking.tempoAlmoco);
			stmt.setTime(27, tracking.tempoTotalRota);
			System.out.println(tracking.tempoTotalRota);
			stmt.setDouble(28, tracking.dispApontCadastrado);
			stmt.setString(29, tracking.latEntrega);
			stmt.setString(30, tracking.lonEntrega);
			stmt.setInt(31, tracking.unidadeNegocio);
			stmt.setString(32, tracking.transportadora);
			stmt.setString(33, tracking.latClienteApontamento);
			stmt.setString(34, tracking.lonClienteApontamento);
			stmt.setString(35, tracking.latAtualCliente);
			stmt.setString(36, tracking.lonAtualCliente);
			stmt.setDouble(37, tracking.distanciaPrev);
			stmt.setTime(38, tracking.tempoDeslocamento);
			stmt.setDouble(39, tracking.velMedia);
			stmt.setDouble(40, tracking.distanciaPercApontamento);
			stmt.setString(41, tracking.aderenciaSequenciaEntrega);
			stmt.setString(42, tracking.aderenciaJanelaEntrega);
			stmt.setString(43, tracking.pdvLacrado);
			stmt.setLong(44, colaborador.getCodUnidade());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao inserir a tabela");
			}
		}
		finally {
			closeConnection(conn, stmt, null);
		}
		return true;
	}

	private boolean updateTracking(Tracking tracking, Colaborador colaborador) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE TRACKING "
					+ "SET "
					+" Classe = ?, "
					+" Data = ?, "
					+" Mapa = ?, "
					+" Placa = ?, "
					+" Cod_Cliente = ?, "
					+" Seq_Real = ?, "
					+" Seq_Plan = ?, "
					+" Inicio_Rota = ?, "
					+" Horário_Matinal = ?, "
					+" Saída_CDD = ?, "
					+" Chegada_ao_PDV = ?, "
					+" Tempo_Prev_Retorno = ?, "
					+" Tempo_Retorno = ?, "
					+" Dist_Prev_Retorno = ?, "
					+" Dist_Perc_Retorno = ?, "
					+" Inicio_Entrega = ?, "
					+" Fim_Entrega = ?, "
					+" Fim_Rota = ?, "
					+" Entrada_CDD = ?, "
					+" Caixas_carregadas = ?, "
					+" Caixas_devolvidas = ?, "
					+" Repasse = ?, "
					+" Tempo_de_entrega = ?, "
					+" Tempo_Descarga = ?, "
					+" Tempo_Espera = ?, "
					+" Tempo_Almoço = ?, "
					+" Tempo_total_de_rota = ?, "
					+" Disp_Apont_Cadastrado = ?, "
					+" Lat_Entrega = ?, "
					+" Lon_Entrega = ?, "
					+" Unidade_Negócio = ?, "
					+" Transportadora = ?, "
					+" Lat_Cliente_Apontamento = ?, "
					+" Lon_Cliente_Apontamento = ?, "
					+" Lat_Atual_Cliente = ?, "
					+" Lon_Atual_Cliente = ?, "
					+" Distância_Prev = ?, "
					+" Tempo_Deslocamento = ?, "
					+" Vel_Média_km_h = ?, "
					+" Distância_Perc_Apontamento = ?, "
					+" Aderência_Sequencia_Entrega = ?, "
					+" Aderência_Janela_Entrega = ?, "
					+" PDV_Lacrado = ?, "
					+" Código_Transportadora = ?, "
					+ "data_hora_import = CURRENT_TIMESTAMP "
					+" WHERE Mapa = ? AND data = ? AND placa = ? AND cod_cliente =?;");

			stmt.setInt(1, tracking.classe);
			stmt.setDate(2, DateUtils.toSqlDate(tracking.data));
			stmt.setInt(3, tracking.mapa);
			stmt.setString(4, tracking.placa);
			stmt.setInt(5, tracking.codCliente);
			stmt.setInt(6, tracking.seqReal);
			stmt.setInt(7, tracking.seqPlan);
			stmt.setTime(8, tracking.inicioRota);
			stmt.setTime(9, tracking.horarioMatinal);
			stmt.setTime(10, tracking.saidaCDD);
			stmt.setTime(11, tracking.chegadaPDV);
			stmt.setTime(12, tracking.tempoPrevRetorno);
			stmt.setTime(13, tracking.tempoRetorno);
			stmt.setDouble(14, tracking.distPrevRetorno);
			stmt.setDouble(15, tracking.distPercRetorno);
			stmt.setTime(16, tracking.inicioEntrega);
			stmt.setTime(17, tracking.fimEntrega);
			stmt.setTime(18, tracking.fimRota);
			stmt.setTime(19, tracking.entradaCDD);
			stmt.setDouble(20, tracking.caixasCarregadas);
			stmt.setDouble(21, tracking.caixasDevolvidas);
			stmt.setDouble(22, tracking.repasse);
			stmt.setTime(23, tracking.tempoEntrega);
			stmt.setTime(24, tracking.tempoDescarga);
			stmt.setTime(25, tracking.tempoEspera);
			stmt.setTime(26, tracking.tempoAlmoco);
			stmt.setTime(27, tracking.tempoTotalRota);
			stmt.setDouble(28, tracking.dispApontCadastrado);
			stmt.setString(29, tracking.latEntrega);
			stmt.setString(30, tracking.lonEntrega);
			stmt.setInt(31, tracking.unidadeNegocio);
			stmt.setString(32, tracking.transportadora);
			stmt.setString(33, tracking.latClienteApontamento);
			stmt.setString(34, tracking.lonClienteApontamento);
			stmt.setString(35, tracking.latAtualCliente);
			stmt.setString(36, tracking.lonAtualCliente);
			stmt.setDouble(37, tracking.distanciaPrev);
			stmt.setTime(38, tracking.tempoDeslocamento);
			stmt.setDouble(39, tracking.velMedia);
			stmt.setDouble(40, tracking.distanciaPercApontamento);
			stmt.setString(41, tracking.aderenciaSequenciaEntrega);
			stmt.setString(42, tracking.aderenciaJanelaEntrega);
			stmt.setString(43, tracking.pdvLacrado);
			stmt.setLong(44, colaborador.getCodUnidade());
			stmt.setInt(45, tracking.mapa);
			stmt.setDate(46, DateUtils.toSqlDate(tracking.data));
			stmt.setString(47, tracking.placa);
			stmt.setInt(48, tracking.codCliente);

			int count = stmt.executeUpdate();
			if(count == 0){
				return false;				
			}	
		} finally {
			closeConnection(conn, stmt, null);
		}
		return true;
	}


}
