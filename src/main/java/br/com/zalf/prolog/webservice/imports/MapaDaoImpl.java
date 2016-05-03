package br.com.zalf.prolog.webservice.imports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;

public class MapaDaoImpl extends DatabaseConnection implements MapaDao{

	public boolean insertOrUpdateMapa (List<MapaImport> listMapas, Colaborador colaborador) throws SQLException {
		//System.out.println("Entrou no insertOrUpdate");
		//System.out.println(listMapas.get(0));
		for(MapaImport mapa : listMapas){

			if(updateMapa(mapa, colaborador)){
				// Mapa ja existia e foi atualizado
				System.out.println("update mapa");
			}else{
				System.out.println("insert mapa");
				// Mapa não existia e foi inserido na base
				insertMapa(mapa, colaborador);
			}
			insertOrUpdateMapaColaborador(mapa.mapa, colaborador.getCodUnidade(), mapa.matricMotorista);
			insertOrUpdateMapaColaborador(mapa.mapa, colaborador.getCodUnidade(), mapa.matricAjud1);
			insertOrUpdateMapaColaborador(mapa.mapa, colaborador.getCodUnidade(), mapa.matricAjud2);
		}
		return true;
	}

	public boolean insertOrUpdateMapaColaborador (int mapa, long codUnidade, int matricula) throws SQLException {
		//System.out.println("Entrou no insertOrUpdateMapaColaborador");
		//System.out.println(listMapas.get(0));

		if(matricula > 0){
		if(updateMapaColaborador(mapa, codUnidade, matricula)){
			System.out.println("update mapa");
		}else{
			System.out.println("insert mapa");
			insertMapaColaborador(mapa, codUnidade, matricula);
		}}
		return true;
	}

	public boolean insertMapa (MapaImport mapa, Colaborador colaborador) throws SQLException{

		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO MAPA VALUES(?, ?,	?,	?,	"
					+ " ?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?, "
					+ "	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?, "
					+ "	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?, "
					+ "	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?, "
					+ "	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?,	?, "
					+ "	?,	?,	?,	?,	?,	?,	?,	?,	?,	?, ?)");

			stmt.setDate(1, DateUtils.toSqlDate(mapa.data));
			stmt.setInt(2, mapa.transp);
			stmt.setString(3, mapa.entrega);
			stmt.setString(4, mapa.cargaAtual);
			stmt.setString(5, mapa.frota);
			stmt.setDouble(6, mapa.custoSpot);
			stmt.setInt(7, mapa.regiao);
			stmt.setInt(8, mapa.veiculo);
			stmt.setString(9, mapa.placa);
			stmt.setDouble(10, mapa.veiculoIndisp);
			stmt.setDouble(11, mapa.placaIndisp);
			stmt.setDouble(12, mapa.frotaIndisp);
			stmt.setInt(13, mapa.tipoIndisp);
			stmt.setInt(14, mapa.mapa);
			stmt.setInt(15, mapa.entregas);
			stmt.setDouble(16, mapa.cxCarreg);
			stmt.setDouble(17, mapa.cxEntreg);
			stmt.setDouble(18, mapa.ocupacao);
			stmt.setDouble(19, mapa.cxRota);
			stmt.setDouble(20, mapa.cxAs);
			stmt.setDouble(21, mapa.veicBM);
			stmt.setInt(22, mapa.rShow);
			stmt.setString(23, mapa.entrVol);
			stmt.setTimestamp(24, DateUtils.toTimestamp(mapa.hrSai));
			stmt.setTimestamp(25, DateUtils.toTimestamp(mapa.hrEntr));
			stmt.setInt(26, mapa.kmSai);
			stmt.setInt(27, mapa.kmEntr);
			stmt.setDouble(28, mapa.custoVariavel);
			stmt.setDouble(29, mapa.lucro);
			stmt.setDouble(30, mapa.lucroUnit);
			stmt.setDouble(31, mapa.valorFrete);
			stmt.setString(32, mapa.tipoImposto);
			stmt.setDouble(33, mapa.percImposto);
			stmt.setDouble(34, mapa.valorImposto);
			stmt.setDouble(35, mapa.valorFaturado);
			stmt.setDouble(36, mapa.valorUnitCxEntregue);
			stmt.setDouble(37, mapa.valorPgCxEntregSemImp);
			stmt.setDouble(38, mapa.valorPgCxEntregComImp);
			stmt.setTime(39, mapa.tempoPrevistoRoad);
			stmt.setDouble(40, mapa.kmPrevistoRoad);
			stmt.setDouble(41, mapa.valorUnitPontoMot);
			stmt.setDouble(42, mapa.valorUnitPontoAjd);
			stmt.setDouble(43, mapa.valorEquipeEntrMot);
			stmt.setDouble(44, mapa.valorEquipeEntrAjd);
			stmt.setDouble(45, mapa.custoVLC);
			stmt.setDouble(46, mapa.lucroUnitCEDBZ);
			stmt.setDouble(47,	mapa.CustoVlcCxEntr);
			stmt.setTime(48,mapa.tempoInterno);
			stmt.setDouble(49, mapa.valorDropDown);
			stmt.setString(50, mapa.veicCadDD);
			stmt.setDouble(51, mapa.kmLaco);
			stmt.setDouble(52, mapa.kmDeslocamento);
			stmt.setTime(53, mapa.tempoLaco);
			stmt.setTime(54, mapa.tempoDeslocamento);
			stmt.setDouble(55, mapa.sitMultiCDD);
			stmt.setInt(56, mapa.unbOrigem);
			stmt.setInt(57, mapa.matricMotorista);
			stmt.setInt(58, mapa.matricAjud1);
			stmt.setInt(59, mapa.matricAjud2);
			stmt.setString(60, mapa.valorCTEDifere);
			stmt.setInt(61, mapa.qtNfCarregadas);
			stmt.setInt(62, mapa.qtNfEntregues);
			stmt.setDouble(63, mapa.indDevCx);
			stmt.setDouble(64, mapa.indDevNf);
			stmt.setDouble(65, mapa.fator);
			stmt.setString(66, mapa.recarga);
			stmt.setTime(67, mapa.hrMatinal);
			stmt.setTime(68, mapa.hrJornadaLiq);
			stmt.setTime(69, mapa.hrMetaJornada);
			stmt.setDouble(70, mapa.vlBateuJornMot);
			stmt.setDouble(71, mapa.vlNaoBateuJornMot);
			stmt.setDouble(72, mapa.vlRecargaMot);
			stmt.setDouble(73, mapa.vlBateuJornAju);
			stmt.setDouble(74, mapa.vlNaoBateuJornAju);
			stmt.setDouble(75, mapa.vlRecargaAju);
			stmt.setDouble(76, mapa.vlTotalMapa);
			stmt.setDouble(77, mapa.qtHlCarregados);
			stmt.setDouble(78, mapa.qtHlEntregues);
			stmt.setDouble(79, mapa.indiceDevHl);
			stmt.setString(80, mapa.regiao2);
			stmt.setInt(81, mapa.qtNfCarregGeral);
			stmt.setInt(82, mapa.qtNfEntregGeral);
			stmt.setDouble(83, mapa.capacidadeVeiculoKg);
			stmt.setDouble(84, mapa.pesoCargaKg);
			stmt.setLong(85, colaborador.getCodUnidade());
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

	public boolean insertMapaColaborador (int mapa,long codUnidade,  int matricula) throws SQLException{

			Connection conn = null;
			PreparedStatement stmt = null;
			try {
				conn = getConnection();
				stmt = conn.prepareStatement("INSERT INTO MAPA_COLABORADOR VALUES(?, ?,	?);");
				stmt.setInt(1, mapa);
				stmt.setLong(2, codUnidade);
				stmt.setInt(3, matricula);
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

	public boolean updateMapaColaborador (int mapa,long codUnidade,  int matricula) throws SQLException{

			Connection conn = null;
			PreparedStatement stmt = null;
			ResultSet rSet = null;
			try {
				conn = getConnection();
				stmt = conn.prepareStatement("SELECT EXISTS(SELECT MC.MAPA FROM "
					+ "MAPA_COLABORADOR MC WHERE MC.MAPA = ? AND MC.COD_AMBEV = ? AND MC.COD_UNIDADE = ?);");
				stmt.setInt(1, mapa);
				stmt.setInt(2, matricula);
				stmt.setLong(3, codUnidade);
				rSet = stmt.executeQuery();
				if (rSet.next()) {
					return rSet.getBoolean("EXISTS");
				}
			}
			finally{
				closeConnection(conn, stmt, rSet);
			}
		return true;
	}

	private boolean updateMapa(MapaImport mapa, Colaborador colaborador) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE MAPA "
					+ "SET "
					+"Data= ?, "
					+"Transp= ?, "
					+"Entrega= ?, "
					+"CargaAtual= ?, "
					+"Frota= ?, "
					+"CustoSpot= ?, "
					+"Regiao= ?, "
					+"Veiculo= ?, "
					+"Placa= ?, "
					+"VeiculoIndisp= ?, "
					+"PlacaIndisp= ?, "
					+"FrotaIndisp= ?, "
					+"TipoIndisp= ?, "
					+"Entregas= ?, "
					+"CxCarreg= ?, "
					+"CxEntreg= ?, "
					+"Ocupacao= ?, "
					+"CxRota= ?, "
					+"CxAS= ?, "
					+"VeicBM= ?, "
					+"RShow= ?, "
					+"EntrVol= ?, "
					+"HrSai= ?, "
					+"HrEntr= ?, "
					+"KmSai= ?, "
					+"KmEntr= ?, "
					+"CustoVariavel= ?, "
					+"Lucro= ?, "
					+"LucroUnit= ?, "
					+"ValorFrete= ?, "
					+"TipoImposto= ?, "
					+"PercImposto= ?, "
					+"ValorImposto= ?, "
					+"ValorFaturado= ?, "
					+"ValorUnitCxEntregue= ?, "
					+"ValorPgCxEntregSemImp= ?, "
					+"ValorPgCxEntregComImp= ?, "
					+"TempoPrevistoRoad= ?, "
					+"KmPrevistoRoad= ?, "
					+"ValorUnitPontoMot= ?, "
					+"ValorUnitPontoAjd= ?, "
					+"ValorEquipeEntrMot= ?, "
					+"ValorEquipeEntrAjd= ?, "
					+"CustoVariavelCEDBZ= ?, "
					+"LucroUnitCEDBZ= ?, "
					+"LucroVariavelCxEntregueFFCEDBZ= ?, "
					+"TempoInterno= ?, "
					+"ValorDropDown= ?, "
					+"VeicCadDD= ?, "
					+"KmLaco= ?, "
					+"KmDeslocamento= ?, "
					+"TempoLaco= ?, "
					+"TempoDeslocamento= ?, "
					+"SitMultiCDD= ?, "
					+"UnbOrigem= ?, "
					+"MatricMotorista= ?, "
					+"MatricAjud1= ?, "
					+"MatricAjud2= ?, "
					+"ValorCTEDifere= ?, "
					+"QtNfCarregadas= ?, "
					+"QtNfEntregues= ?, "
					+"IndDevCx= ?, "
					+"IndDevNf= ?, "
					+"Fator= ?, "
					+"Recarga= ?, "
					+"HrMatinal= ?, "
					+"HrJornadaLiq= ?, "
					+"HrMetaJornada= ?, "
					+"VlBateuJornMot= ?, "
					+"VlNaoBateuJornMot= ?, "
					+"VlRecargaMot= ?, "
					+"VlBateuJornAju= ?, "
					+"VlNaoBateuJornAju= ?, "
					+"VlRecargaAju= ?, "
					+"VlTotalMapa= ?, "
					+"QtHlCarregados= ?, "
					+"QtHlEntregues= ?, "
					+"IndiceDevHl= ?, "
					+"Regiao2= ?, "
					+"QtNfCarregGeral= ?, "
					+"QtNfEntregGeral= ?, "
					+"CapacidadeVeiculoKG= ?, "
					+"PesoCargaKG= ?, "
					+"cod_unidade= ?, "
					+ "data_hora_import = CURRENT_TIMESTAMP "
					+" WHERE Mapa = ? AND cod_unidade = ?;");

			stmt.setDate(1, DateUtils.toSqlDate(mapa.data));
			stmt.setInt(2, mapa.transp);
			stmt.setString(3, mapa.entrega);
			stmt.setString(4, mapa.cargaAtual);
			stmt.setString(5, mapa.frota);
			stmt.setDouble(6, mapa.custoSpot);
			stmt.setInt(7, mapa.regiao);
			stmt.setInt(8, mapa.veiculo);
			stmt.setString(9, mapa.placa);
			stmt.setDouble(10, mapa.veiculoIndisp);
			stmt.setDouble(11, mapa.placaIndisp);
			stmt.setDouble(12, mapa.frotaIndisp);
			stmt.setInt(13, mapa.tipoIndisp);
			stmt.setInt(14, mapa.entregas);
			stmt.setDouble(15, mapa.cxCarreg);
			stmt.setDouble(16, mapa.cxEntreg);
			stmt.setDouble(17, mapa.ocupacao);
			stmt.setDouble(18, mapa.cxRota);
			stmt.setDouble(19, mapa.cxAs);
			stmt.setDouble(20, mapa.veicBM);
			stmt.setInt(21, mapa.rShow);
			stmt.setString(22, mapa.entrVol);
			stmt.setTimestamp(23, DateUtils.toTimestamp(mapa.hrSai));
			stmt.setTimestamp(24, DateUtils.toTimestamp(mapa.hrEntr));
			stmt.setInt(25, mapa.kmSai);
			stmt.setInt(26, mapa.kmEntr);
			stmt.setDouble(27, mapa.custoVariavel);
			stmt.setDouble(28, mapa.lucro);
			stmt.setDouble(29, mapa.lucroUnit);
			stmt.setDouble(30, mapa.valorFrete);
			stmt.setString(31, mapa.tipoImposto);
			stmt.setDouble(32, mapa.percImposto);
			stmt.setDouble(33, mapa.valorImposto);
			stmt.setDouble(34, mapa.valorFaturado);
			stmt.setDouble(35, mapa.valorUnitCxEntregue);
			stmt.setDouble(36, mapa.valorPgCxEntregSemImp);
			stmt.setDouble(37, mapa.valorPgCxEntregComImp);
			stmt.setTime(38,mapa.tempoPrevistoRoad);
			stmt.setDouble(39, mapa.kmPrevistoRoad);
			stmt.setDouble(40, mapa.valorUnitPontoMot);
			stmt.setDouble(41, mapa.valorUnitPontoAjd);
			stmt.setDouble(42, mapa.valorEquipeEntrMot);
			stmt.setDouble(43, mapa.valorEquipeEntrAjd);
			stmt.setDouble(44, mapa.custoVLC);
			stmt.setDouble(45, mapa.lucroUnitCEDBZ);
			stmt.setDouble(46,	mapa.CustoVlcCxEntr);
			stmt.setTime(47,mapa.tempoInterno);
			stmt.setDouble(48, mapa.valorDropDown);
			stmt.setString(49, mapa.veicCadDD);
			stmt.setDouble(50, mapa.kmLaco);
			stmt.setDouble(51, mapa.kmDeslocamento);
			stmt.setTime(52, mapa.tempoLaco);
			stmt.setTime(53, mapa.tempoDeslocamento);
			stmt.setDouble(54, mapa.sitMultiCDD);
			stmt.setInt(55, mapa.unbOrigem);
			stmt.setInt(56, mapa.matricMotorista);
			stmt.setInt(57, mapa.matricAjud1);
			stmt.setInt(58, mapa.matricAjud2);
			stmt.setString(59, mapa.valorCTEDifere);
			stmt.setInt(60, mapa.qtNfCarregadas);
			stmt.setInt(61, mapa.qtNfEntregues);
			stmt.setDouble(62, mapa.indDevCx);
			stmt.setDouble(63, mapa.indDevNf);
			stmt.setDouble(64, mapa.fator);
			stmt.setString(65, mapa.recarga);
			stmt.setTime(66, mapa.hrMatinal);
			stmt.setTime(67, mapa.hrJornadaLiq);
			stmt.setTime(68, mapa.hrMetaJornada);
			stmt.setDouble(69, mapa.vlBateuJornMot);
			stmt.setDouble(70, mapa.vlNaoBateuJornMot);
			stmt.setDouble(71, mapa.vlRecargaMot);
			stmt.setDouble(72, mapa.vlBateuJornAju);
			stmt.setDouble(73, mapa.vlNaoBateuJornAju);
			stmt.setDouble(74, mapa.vlRecargaAju);
			stmt.setDouble(75, mapa.vlTotalMapa);
			stmt.setDouble(76, mapa.qtHlCarregados);
			stmt.setDouble(77, mapa.qtHlEntregues);
			stmt.setDouble(78, mapa.indiceDevHl);
			stmt.setString(79, mapa.regiao2);
			stmt.setInt(80, mapa.qtNfCarregGeral);
			stmt.setInt(81, mapa.qtNfEntregGeral);
			stmt.setDouble(82, mapa.capacidadeVeiculoKg);
			stmt.setDouble(83, mapa.pesoCargaKg);
			stmt.setLong(84, colaborador.getCodUnidade());
			stmt.setInt(85, mapa.mapa);
			stmt.setLong(86, colaborador.getCodUnidade());

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
