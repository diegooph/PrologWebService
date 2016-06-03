package br.com.zalf.prolog.webservice.servico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.PlacaServicoHolder;
import br.com.zalf.prolog.models.servico.Calibragem;
import br.com.zalf.prolog.models.servico.Movimentacao;
import br.com.zalf.prolog.models.servico.Servico;
import br.com.zalf.prolog.models.servico.ServicoHolder;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.afericao.AfericaoDaoImpl;
import br.com.zalf.prolog.webservice.pneu.PneuDaoImpl;
import br.com.zalf.prolog.webservice.veiculo.VeiculoDaoImpl;

public class ServicoDaoImpl extends DatabaseConnection implements ServicoDao{

	PneuDaoImpl pneuDao;
	VeiculoDaoImpl veiculoDao;
	Long codUnidade;

	@Override
	public PlacaServicoHolder getPlacasServico(Long codUnidade) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		PlacaServicoHolder placaServicoHolder = new PlacaServicoHolder();
		List<PlacaServicoHolder.PlacaServico> listaServicos = new ArrayList<>();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT V.PLACA, MOV.TOTAL_MOVIMENTACAO, CAL.TOTAL_CALIBRAGEM "
					+ "FROM VEICULO V join "
					+ "(SELECT VP.PLACA AS PLACA_MOV, COUNT(ITS.TIPO_SERVICO) AS TOTAL_MOVIMENTACAO "
					+ "FROM VEICULO_PNEU VP "
					+ "JOIN ITEM_SERVICO ITS ON ITS.COD_PNEU = VP.COD_PNEU "
					+ "WHERE ITS.TIPO_SERVICO = ? "
					+ "GROUP BY 1,ITS.TIPO_SERVICO) AS MOV ON PLACA_MOV = V.PLACA "
					+ "JOIN (SELECT VP.PLACA AS PLACA_CAL, COUNT(ITS.TIPO_SERVICO) AS TOTAL_CALIBRAGEM "
					+ "FROM VEICULO_PNEU VP "
					+ "JOIN ITEM_SERVICO ITS ON ITS.COD_PNEU = VP.COD_PNEU WHERE ITS.TIPO_SERVICO = ? "
					+ "GROUP BY 1,ITS.TIPO_SERVICO) AS CAL ON PLACA_CAL = V.PLACA "
					+ "WHERE V.COD_UNIDADE = ?");
			stmt.setString(1, Servico.TIPO_MOVIMENTACAO);
			stmt.setString(2, Servico.TIPO_CALIBRAGEM);
			stmt.setLong(3, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				PlacaServicoHolder.PlacaServico item = new PlacaServicoHolder.PlacaServico();
				item.placa = rSet.getString("PLACA");
				item.qtCalibragem = rSet.getInt("TOTAL_CALIBRAGEM");
				item.qtMovimentacao = rSet.getInt("TOTAL_MOVIMENTACAO");
				listaServicos.add(item);
				placaServicoHolder.setQtCalibragemTotal(placaServicoHolder.getQtCalibragemTotal() + item.qtCalibragem);
				placaServicoHolder.setQtMovimentacaoTotal(placaServicoHolder.getQtMovimentacaoTotal() + item.qtMovimentacao);
			}
		}finally {
			closeConnection(conn, stmt, null);
		}
		placaServicoHolder.setListPlacas(listaServicos);
		return placaServicoHolder;
	}

	public ServicoHolder getServicosByPlaca (String placa) throws SQLException{
		ServicoHolder holder = new ServicoHolder();
		veiculoDao = new VeiculoDaoImpl();
		holder.setVeiculo(veiculoDao.getVeiculoByPlaca(placa));
		setServicos(holder);
		AfericaoDaoImpl afericaoDaoImpl = new AfericaoDaoImpl();
		afericaoDaoImpl.getRestricoes(codUnidade);
		holder.setToleranciaCalibragem(afericaoDaoImpl.toleranciaCalibragem);
		holder.setSulcoMinimoAceitavel(afericaoDaoImpl.sulcoMinimo);

		return holder;
	}

	private void setServicos(ServicoHolder holder) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Calibragem> listCalibragem = new ArrayList<>();
		List<Movimentacao> listMovimentacao = new ArrayList<>();
		pneuDao = new PneuDaoImpl();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT V.PLACA, V.KILOMETRAGEM,V.COD_UNIDADE AS COD_UNIDADE, "
					+ "A.CODIGO AS COD_AFERICAO, ITS.TIPO_SERVICO, ITS.QT_APONTAMENTOS, P.CODIGO, VP.POSICAO, MAP.NOME AS MARCA, "
					+ "MP.NOME AS MODELO, DP.*, P.* "
					+ "FROM ITEM_SERVICO ITS "
					+ "JOIN PNEU P ON ITS.COD_PNEU = P.CODIGO "
					+ "JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO "
					+ "JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA "
					+ "JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO "
					+ "JOIN AFERICAO A ON A.CODIGO = ITS.COD_AFERICAO "
					+ "JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO "
					+ "JOIN VEICULO_PNEU VP ON VP.COD_PNEU = ITS.COD_PNEU "
					+ "WHERE A.PLACA_VEICULO = ? AND ITS.DATA_HORA_RESOLUCAO IS NULL "
					+ "ORDER BY ITS.TIPO_SERVICO");
			stmt.setString(1, holder.getVeiculo().getPlaca());
			rSet = stmt.executeQuery();
			while(rSet.next()){
				codUnidade = rSet.getLong("COD_UNIDADE");
				if(rSet.getString("TIPO_SERVICO").equals(Servico.TIPO_CALIBRAGEM)){
					listCalibragem.add(createCalibragem(rSet));
				}else if(rSet.getString("TIPO_SERVICO").equals(Servico.TIPO_MOVIMENTACAO)){
					listMovimentacao.add(createMovimentacao(rSet));
				}
			}
		}finally {
			closeConnection(conn, stmt, null);
		}
		holder.setListCalibragem(listCalibragem);
		holder.setListMovimentacao(listMovimentacao);
	}

	private Calibragem createCalibragem(ResultSet rSet) throws SQLException{
		Calibragem calibragem = new Calibragem();
		calibragem.setPneu(pneuDao.createPneu(rSet));
		calibragem.setCodAfericao(rSet.getLong("COD_AFERICAO"));
		calibragem.setTipo(rSet.getString("TIPO_SERVICO"));
		calibragem.setQtApontamentos(rSet.getInt("QT_APONTAMENTOS"));
		return calibragem;
	}

	private Movimentacao createMovimentacao(ResultSet rSet) throws SQLException{
		Movimentacao movimentacao = new Movimentacao();
		movimentacao.setPneu(pneuDao.createPneu(rSet));
		movimentacao.setCodAfericao(rSet.getLong("COD_AFERICAO"));
		movimentacao.setTipo(rSet.getString("TIPO_SERVICO"));
		movimentacao.setQtApontamentos(rSet.getInt("QT_APONTAMENTOS"));
		return movimentacao;
	}


}
