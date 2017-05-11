package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDaoImpl;
import br.com.zalf.prolog.webservice.commons.util.L;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServicoDaoImpl extends DatabaseConnection implements ServicoDao {

	private PneuDao pneuDao;
	private VeiculoDao veiculoDao;
	private Long codUnidade;

	@Override
	public PlacaServicoHolder getPlacasServico(Long codUnidade) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		PlacaServicoHolder placaServicoHolder = new PlacaServicoHolder();
		List<PlacaServicoHolder.PlacaServico> listaServicos = new ArrayList<>();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT V.PLACA, MOV.TOTAL_MOVIMENTACAO, CAL.TOTAL_CALIBRAGEM, INSP.TOTAL_INSPECAO\n" +
					"FROM VEICULO V  JOIN\n" +
					"    (SELECT VP.PLACA AS PLACA_TOT, COUNT(VP.PLACA) AS TOTAL_SERVICOS\n" +
					"    FROM AFERICAO A\n" +
					"    JOIN AFERICAO_MANUTENCAO ITS ON ITS.cod_afericao = A.codigo\n" +
					"    JOIN veiculo_pneu VP ON VP.placa = A.placa_veiculo AND ITS.cod_pneu = VP.cod_pneu AND ITS.cod_unidade = VP.cod_unidade\n" +
					"    WHERE ITS.TIPO_SERVICO LIKE '%' AND ITS.DATA_HORA_RESOLUCAO IS NULL\n" +
					"    GROUP BY VP.PLACA) AS TOT ON PLACA_TOT = V.PLACA\n" +
					"FULL OUTER JOIN\n" +
					"    (SELECT VP.PLACA AS PLACA_MOV, COUNT(ITS.TIPO_SERVICO) AS TOTAL_MOVIMENTACAO\n" +
					"    FROM AFERICAO A\n" +
					"    JOIN AFERICAO_MANUTENCAO ITS ON ITS.cod_afericao = A.codigo\n" +
					"    JOIN VEICULO_PNEU VP ON A.placa_veiculo = VP.placa AND VP.cod_pneu = ITS.cod_pneu AND ITS.cod_unidade = VP.cod_unidade\n" +
					"    WHERE ITS.TIPO_SERVICO = ? AND ITS.DATA_HORA_RESOLUCAO IS NULL\n" +
					"    GROUP BY 1,ITS.TIPO_SERVICO) AS MOV ON PLACA_MOV = V.PLACA\n" +
					"FULL OUTER JOIN\n" +
					"    (SELECT VP.PLACA AS PLACA_CAL, COUNT(ITS.TIPO_SERVICO) AS TOTAL_CALIBRAGEM\n" +
					"    FROM AFERICAO A\n" +
					"    JOIN AFERICAO_MANUTENCAO ITS ON ITS.cod_afericao = A.codigo\n" +
					"    JOIN VEICULO_PNEU VP ON A.placa_veiculo = VP.placa AND VP.cod_pneu = ITS.cod_pneu AND ITS.cod_unidade = VP.cod_unidade\n" +
					"    WHERE ITS.TIPO_SERVICO = ?  AND ITS.DATA_HORA_RESOLUCAO IS NULL\n" +
					"    GROUP BY 1,ITS.TIPO_SERVICO) AS CAL ON PLACA_CAL = V.PLACA\n" +
					"FULL OUTER JOIN\n" +
					"    (SELECT VP.PLACA AS PLACA_INSP, COUNT(ITS.TIPO_SERVICO) AS TOTAL_INSPECAO\n" +
					"    FROM AFERICAO A\n" +
					"    JOIN AFERICAO_MANUTENCAO ITS ON ITS.cod_afericao = A.codigo\n" +
					"    JOIN VEICULO_PNEU VP ON A.placa_veiculo = VP.placa AND VP.cod_pneu = ITS.cod_pneu AND ITS.cod_unidade = VP.cod_unidade\n" +
					"    WHERE ITS.TIPO_SERVICO = ? AND ITS.DATA_HORA_RESOLUCAO IS NULL\n" +
					"    GROUP BY 1,ITS.TIPO_SERVICO) AS INSP ON PLACA_INSP = V.PLACA\n" +
					"WHERE V.COD_UNIDADE = ?");
			stmt.setString(1, Servico.TIPO_MOVIMENTACAO);
			stmt.setString(2, Servico.TIPO_CALIBRAGEM);
			stmt.setString(3, Servico.TIPO_INSPECAO);
			stmt.setLong(4, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				PlacaServicoHolder.PlacaServico item = new PlacaServicoHolder.PlacaServico();
				item.placa = rSet.getString("PLACA");
				item.qtCalibragem = rSet.getInt("TOTAL_CALIBRAGEM");
				item.qtMovimentacao = rSet.getInt("TOTAL_MOVIMENTACAO");
				item.qtInspecao = rSet.getInt("TOTAL_INSPECAO");

				listaServicos.add(item);
				placaServicoHolder.setQtCalibragemTotal(placaServicoHolder.getQtCalibragemTotal() + item.qtCalibragem);
				placaServicoHolder.setQtMovimentacaoTotal(placaServicoHolder.getQtMovimentacaoTotal() + item.qtMovimentacao);
				placaServicoHolder.setQtInspecaoTotal(placaServicoHolder.getQtInspecaoTotal() + item.qtInspecao);
			}
		}finally {
			closeConnection(conn, stmt, null);
		}
		placaServicoHolder.setListPlacas(listaServicos);
		return placaServicoHolder;
	}

	@Override
	public ServicoHolder getServicosByPlaca (String placa, Long codUnidade) throws SQLException {
		ServicoHolder holder = new ServicoHolder();
		veiculoDao = new VeiculoDaoImpl();
		holder.setVeiculo(veiculoDao.getVeiculoByPlaca(placa, true));
		holder.setListServicos(getServicosAbertosByPlaca(placa, "%"));
		if(containInspecao(holder.getListServicos())){
			holder.setListAlternativaInspecao(getListAlternativasInspecao());
		}
		if(containMovimentacao(holder.getListServicos())){
			L.d("teste", "contem movimentacao");
			holder.setPneusDisponiveis(pneuDao.getPneuByCodUnidadeByStatus(codUnidade, Pneu.ESTOQUE));;
		}
		AfericaoDao afericaoDao = new AfericaoDaoImpl();
		holder.setRestricao(afericaoDao.getRestricoesByPlaca(placa));

		return holder;
	}

	@Override
	public List<Servico> getServicosAbertosByPlaca(String placa, String tipoServico) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Servico> listServicos = new ArrayList<>();
		pneuDao = new PneuDaoImpl();

		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT V.PLACA, V.KM,V.COD_UNIDADE AS COD_UNIDADE, "
					+ "A.CODIGO AS COD_AFERICAO, ITS.TIPO_SERVICO, ITS.QT_APONTAMENTOS, P.CODIGO, VP.POSICAO, MAP.NOME AS MARCA, MAP.CODIGO AS COD_MARCA, "
					+ "MP.NOME AS MODELO, MP.CODIGO AS COD_MODELO, DP.*, P.*, "
					+ "MB.codigo AS COD_MODELO_BANDA, MB.nome AS NOME_MODELO_BANDA, MAB.codigo AS COD_MARCA_BANDA, MAB.nome AS NOME_MARCA_BANDA\n "
					+ "FROM AFERICAO_MANUTENCAO ITS "
					+ "JOIN PNEU P ON ITS.COD_PNEU = P.CODIGO "
					+ "JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO "
					+ "JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA "
					+ "JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO "
					+ "JOIN AFERICAO A ON A.CODIGO = ITS.COD_AFERICAO "
					+ "JOIN VEICULO_PNEU VP ON VP.COD_PNEU = P.CODIGO AND VP.COD_UNIDADE = P.COD_UNIDADE AND A.PLACA_VEICULO = VP.PLACA "
					+ "JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO "
					+ "JOIN UNIDADE U ON U.CODIGO = P.cod_unidade\n "
					+ "LEFT JOIN modelo_banda MB ON MB.codigo = P.cod_modelo_banda AND MB.cod_empresa = U.cod_empresa\n "
					+ "LEFT JOIN marca_banda MAB ON MAB.codigo = MB.cod_marca AND MAB.cod_empresa = MB.cod_empresa\n "
					+ "WHERE A.PLACA_VEICULO = ? AND ITS.DATA_HORA_RESOLUCAO IS NULL AND ITS.TIPO_SERVICO LIKE ? "
					+ "ORDER BY ITS.TIPO_SERVICO");
			stmt.setString(1, placa);
			stmt.setString(2, tipoServico);
			rSet = stmt.executeQuery();
			while(rSet.next()){

				String tipo = rSet.getString("TIPO_SERVICO");

				switch (tipo) {
				case Servico.TIPO_CALIBRAGEM:
					listServicos.add(createCalibragem(rSet));
					break;
				case Servico.TIPO_MOVIMENTACAO:
					listServicos.add(createMovimentacao(rSet));
					break;
				case Servico.TIPO_INSPECAO:
					listServicos.add(createInspecao(rSet));
					break;
				}
			}
		}finally {
			closeConnection(conn, stmt, null);
		}
		return listServicos;
	}

	@Override
	public boolean insertManutencao(Servico servico, Long codUnidade, String token) throws SQLException {

		this.codUnidade = codUnidade;
		Connection conn = getConnection();
		conn.setAutoCommit(false);
		pneuDao = new PneuDaoImpl();

		try{
			switch (servico.getTipo()) {
				case Servico.TIPO_CALIBRAGEM:
					insertCalibragem((Calibragem) servico, conn);
					break;
				case Servico.TIPO_INSPECAO:
					insertInspecao((Inspecao) servico, conn);
					break;
				case Servico.TIPO_MOVIMENTACAO:
//					 chamar movimentacão da DAO específica
//					insertMovimentacao((Movimentacao) servico, conn, token);
					break;
			}
			conn.commit();
		}catch(SQLException e){
			e.printStackTrace();
			conn.rollback();
			return false;
		}finally{
			closeConnection(conn, null, null);
		}
		return true;
	}

	private boolean containInspecao(List<Servico> listServicos){

		for (Servico servico : listServicos) {
			if (servico instanceof Inspecao) {
				return true;
			}
		}
		return false;
	}

	private boolean containMovimentacao(List<Servico> listServicos){

		for (Servico servico : listServicos) {
			if (servico instanceof Movimentacao) {
				return true;
			}
		}
		return false;
	}

	private List<Alternativa> getListAlternativasInspecao() throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Alternativa> listAlternativas = new ArrayList<>();

		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM AFERICAO_ALTERNATIVA_MANUTENCAO_INSPECAO A "
					+ "WHERE A.STATUS_ATIVO = TRUE");
			rSet = stmt.executeQuery();
			while(rSet.next()){
				AlternativaChecklist alternativa = new AlternativaChecklist();
				alternativa.codigo = rSet.getLong("CODIGO");
				alternativa.alternativa = rSet.getString("ALTERNATIVA");
				listAlternativas.add(alternativa);
			}
		}finally {
			closeConnection(conn, stmt, null);
		}
		return listAlternativas;
	}

	private Calibragem createCalibragem(ResultSet rSet) throws SQLException{
		Calibragem calibragem = new Calibragem();
		calibragem.setPneu(pneuDao.createPneu(rSet));
		calibragem.setCodAfericao(rSet.getLong("COD_AFERICAO"));
		calibragem.setTipo(rSet.getString("TIPO_SERVICO"));
		calibragem.setQtApontamentos(rSet.getInt("QT_APONTAMENTOS"));
		return calibragem;
	}

	private Inspecao createInspecao(ResultSet rSet) throws SQLException{
		Inspecao inspecao = new Inspecao();
		inspecao.setPneu(pneuDao.createPneu(rSet));
		inspecao.setCodAfericao(rSet.getLong("COD_AFERICAO"));
		inspecao.setTipo(rSet.getString("TIPO_SERVICO"));
		inspecao.setQtApontamentos(rSet.getInt("QT_APONTAMENTOS"));
		return inspecao;
	}

	private Movimentacao createMovimentacao(ResultSet rSet) throws SQLException{
		Movimentacao movimentacao = new Movimentacao();
		movimentacao.setPneu(pneuDao.createPneu(rSet));
		movimentacao.setCodAfericao(rSet.getLong("COD_AFERICAO"));
		movimentacao.setTipo(rSet.getString("TIPO_SERVICO"));
		movimentacao.setQtApontamentos(rSet.getInt("QT_APONTAMENTOS"));
		return movimentacao;
	}

	private boolean insertCalibragem(Calibragem servico, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		try{
			stmt = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
					+ "DATA_HORA_RESOLUCAO = ?, "
					+ "CPF_MECANICO = ?, "
					+ "PSI_APOS_CONSERTO = ?, "
					+ "KM_MOMENTO_CONSERTO = ? "
					+ "WHERE COD_AFERICAO = ? AND "
					+ "DATA_HORA_RESOLUCAO IS NULL AND "
					+ "COD_PNEU = ? "
					+ "AND TIPO_SERVICO = ?");
			stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
			stmt.setLong(2, servico.getCpfMecanico());
			stmt.setDouble(3, servico.getPneu().getPressaoAtual());
			stmt.setLong(4, servico.getKmVeiculo());
			stmt.setLong(5, servico.getCodAfericao());
			stmt.setLong(6, servico.getPneu().getCodigo());
			stmt.setString(7, servico.getTipo());
			int count = stmt.executeUpdate();
			if (count == 0) {
				throw new SQLException("Erro ao inserir o item consertado");
			}
			pneuDao.updateCalibragem(servico.getPneu(), codUnidade, conn);
		}finally {
			closeConnection(null, stmt, null);
		}
		return true;
	}

	private boolean insertInspecao(Inspecao servico, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		try{
			stmt = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
					+ "DATA_HORA_RESOLUCAO = ?, "
					+ "CPF_MECANICO = ?, "
					+ "PSI_APOS_CONSERTO = ?, "
					+ "KM_MOMENTO_CONSERTO = ?, "
					+ "COD_ALTERNATIVA = ? "
					+ "WHERE COD_AFERICAO = ? AND "
					+ "COD_PNEU = ? AND "
					+ "DATA_HORA_RESOLUCAO IS NULL "
					+ "AND TIPO_SERVICO = ?");
			stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
			stmt.setLong(2, servico.getCpfMecanico());
			stmt.setDouble(3, servico.getPneu().getPressaoAtual());
			stmt.setLong(4, servico.getKmVeiculo());
			stmt.setLong(5, servico.getAlternativaSelecionada().codigo);
			stmt.setLong(6, servico.getCodAfericao());
			stmt.setLong(7, servico.getPneu().getCodigo());
			stmt.setString(8, servico.getTipo());
			int count = stmt.executeUpdate();
			if (count == 0) {
				throw new SQLException("Erro ao inserir o item consertado");
			}
			pneuDao.updateCalibragem(servico.getPneu(), codUnidade, conn);
		}finally {
			closeConnection(null, stmt, null);
		}
		return true;
	}

//	Método será substituido por uma movimentação padrão, com origem e destino.
//	private boolean insertMovimentacao(Movimentacao servico, Connection conn, String token) throws SQLException{
//		PreparedStatement stmt = null;
//		try{
//			stmt = conn.prepareStatement("UPDATE AFERICAO_MANUTENCAO SET "
//					+ "DATA_HORA_RESOLUCAO = ?, "
//					+ "CPF_MECANICO = ?, "
//					+ "PSI_APOS_CONSERTO = ?, "
//					+ "KM_MOMENTO_CONSERTO = ?, "
//					+ "COD_PNEU_INSERIDO = ? "
//					+ "WHERE COD_AFERICAO = ? AND "
//					+ "COD_PNEU = ? AND "
//					+ "DATA_HORA_RESOLUCAO IS NULL AND "
//					+ "TIPO_SERVICO = ?");
//			stmt.setTimestamp(1, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
//			stmt.setLong(2, servico.getCpfMecanico());
//			stmt.setDouble(3, servico.getPneuNovo().getPressaoAtual());
//			stmt.setLong(4, servico.getKmVeiculo());
//			stmt.setLong(5, servico.getPneuNovo().getCodigo());
//			stmt.setLong(6, servico.getCodAfericao());
//			stmt.setLong(7, servico.getPneu().getCodigo());
//			stmt.setString(8, servico.getTipo());
//			int count = stmt.executeUpdate();
//			if (count == 0) {
//				throw new SQLException("Erro ao inserir o item consertado");
//			}
//			pneuDao.updateCalibragem(servico.getPneuNovo(), codUnidade, conn);
//
//			if (servico.getPneu().getVidaAtual() == servico.getPneu().getVidasTotal()) {
//				pneuDao.updateStatus(servico.getPneu(), codUnidade, Pneu.DESCARTE, conn);
//				pneuDao.registraMovimentacaoHistorico(servico.getPneu(), codUnidade, Pneu.DESCARTE, servico.getKmVeiculo(), servico.getPlaca(), conn, token);
//			}else{
//				pneuDao.updateStatus(servico.getPneu(), codUnidade, Pneu.ANALISE, conn);
//				pneuDao.registraMovimentacaoHistorico(servico.getPneu(), codUnidade, Pneu.ANALISE, servico.getKmVeiculo(), servico.getPlaca(), conn, token);
//			}
//
//			pneuDao.updateStatus(servico.getPneuNovo(), codUnidade, Pneu.EM_USO, conn);
//			pneuDao.registraMovimentacaoHistorico(servico.getPneuNovo(), codUnidade, Pneu.EM_USO, servico.getKmVeiculo(), servico.getPlaca(), conn, token);
//			pneuDao.updateVeiculoPneu(servico.getPlaca(), servico.getPneu(), servico.getPneuNovo(), conn);
//
//		}finally {
//			closeConnection(null, stmt, null);
//		}
//		return true;
//	}

}
