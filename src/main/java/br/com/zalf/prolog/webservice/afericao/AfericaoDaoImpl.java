package br.com.zalf.prolog.webservice.afericao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.pneu.Pneu;
import br.com.zalf.prolog.models.pneu.Restricao;
import br.com.zalf.prolog.models.pneu.afericao.NovaAfericao;
import br.com.zalf.prolog.models.servico.Afericao;
import br.com.zalf.prolog.models.servico.Servico;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.pneu.PneuDaoImpl;
import br.com.zalf.prolog.webservice.veiculo.VeiculoDaoImpl;

public class AfericaoDaoImpl extends DatabaseConnection implements AfericaoDao{

	@Override
	public boolean insert (Afericao afericao, Long codUnidade) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		VeiculoDaoImpl veiculoDaoImpl = new VeiculoDaoImpl();
		try{
			conn = getConnection();
			conn.setAutoCommit(false);
			stmt = conn.prepareStatement("INSERT INTO AFERICAO(DATA_HORA, PLACA_VEICULO, CPF_AFERIDOR, KM_VEICULO) "
					+ "VALUES (?, ?, ?, ?) RETURNING CODIGO");
			stmt.setTimestamp(1, DateUtils.toTimestamp(afericao.getDataHora()));
			stmt.setString(2, afericao.getVeiculo().getPlaca());
			stmt.setLong(3, afericao.getCpfAferidor());
			stmt.setLong(4, afericao.getKmMomentoAfericao());
			rSet = stmt.executeQuery();
			if(rSet.next()){
				afericao.setCodigo(rSet.getLong("CODIGO"));
				insertValores(afericao, codUnidade, conn);
				veiculoDaoImpl.updateKilometragem(afericao.getVeiculo().getPlaca(), afericao.getKmMomentoAfericao());
			}			
		}catch(SQLException e){
			e.printStackTrace();
			conn.rollback();
			return false;
		}finally {
			closeConnection(conn, stmt, rSet);
		}
		return true;
	}

	private void insertValores (Afericao afericao, Long codUnidade, Connection conn) throws SQLException{
		
		PreparedStatement stmt = null;
		PneuDaoImpl pneuDaoImpl = new PneuDaoImpl();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO AFERICAO_VALORES "
					+ "(COD_AFERICAO, COD_PNEU, COD_UNIDADE, PSI, ALTURA_SULCO_CENTRAL,ALTURA_SULCO_EXTERNO, ALTURA_SULCO_INTERNO) VALUES "
					+ "(?, ?, ?, ?, ?, ?, ?)");
			for(Pneu pneu : afericao.getVeiculo().getListPneus()){
				stmt.setLong(1, afericao.getCodigo());
				stmt.setLong(2, pneu.getCodigo());
				stmt.setLong(3, codUnidade);
				stmt.setDouble(4, pneu.getPressaoAtual());
				stmt.setDouble(5, pneu.getSulcoAtual().getCentral());
				stmt.setDouble(6, pneu.getSulcoAtual().getExterno());
				stmt.setDouble(7, pneu.getSulcoAtual().getInterno());
				//Atualiza as informações de Sulco atual e calibragem atual na tabela Pneu do BD
				pneuDaoImpl.updateSulcos(pneu, codUnidade, conn);
				stmt.executeUpdate();
				if(verificaAptoServico(pneu, codUnidade)){// verifica se o pneu tem alguma anomalia e deve ser inserido na base de serviços
					insertOrUpdateServico(pneu, afericao.getCodigo(), codUnidade);
				}
			}
		}finally {
			closeConnection(conn, stmt, null);
		}
	}

	private boolean verificaAptoServico(Pneu pneu, Long codUnidade) throws SQLException{
		Restricao restricao = getRestricoesByCodUnidade(codUnidade);
		if (pneu.getVidaAtual() == pneu.getVidasTotal()) {
			if(verificaPressao(pneu, restricao) || pneu.getSulcoAtual().getCentral() < restricao.getSulcoMinimoDescarte()){
				return true;
			}else{
				return false;
			}
		}else if(verificaPressao(pneu, restricao) || pneu.getSulcoAtual().getCentral() < restricao.getSulcoMinimoRecape()){
			return true;
		}else{
			return false;
		}
	}

	private boolean verificaPressao(Pneu pneu, Restricao restricao){
		double psiMinimo = 1 - restricao.getToleranciaCalibragem();
		double psiMaximo = 1 + restricao.getToleranciaCalibragem();
		if(pneu.getPressaoAtual() < pneu.getPressaoCorreta()* psiMinimo || pneu.getPressaoAtual() > pneu.getPressaoCorreta() * psiMaximo){
			return true;
		}else{
			return false;
		}
	}

	private void insertOrUpdateServico (Pneu pneu, long codAfericao, Long codUnidade) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<String> listServico = new ArrayList<>();

		try{
			conn = getConnection();
			stmt = conn.prepareStatement(("SELECT TIPO_SERVICO, COUNT(TIPO_SERVICO) "
					+ "FROM AFERICAO_MANUTENCAO WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND DATA_HORA_RESOLUCAO IS NULL "
					+ "GROUP BY TIPO_SERVICO "
					+ "ORDER BY TIPO_SERVICO"),ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			stmt.setLong(1, pneu.getCodigo());
			stmt.setLong(2, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				listServico.add(rSet.getString("TIPO_SERVICO"));
			}
			String tipoServico = verificaTipoServico(pneu, codUnidade);
			if(listServico.size() > 0){
				int i = 0;
				while(i < listServico.size()){
					if(tipoServico.equals(Servico.TIPO_AMBOS)){// se o pneu tem ambos os serviços para serem resolvidos, verifico quais já tem apontamento no banco
						if(listServico.get(i).equals(Servico.TIPO_CALIBRAGEM) && i != listServico.size()-1){ // caso ja tenha uma calibragem apontada e o rSet não está na ultima posição significa que tbm tem uma movimentação
							incrementaQtApontamentos(pneu,  codUnidade, Servico.TIPO_MOVIMENTACAO, conn);// incremento a calibragem
							incrementaQtApontamentos(pneu, codUnidade, Servico.TIPO_CALIBRAGEM, conn); // incremento a movimentação
							i++;
						}else if(listServico.get(i).equals(Servico.TIPO_CALIBRAGEM) && i == listServico.size()-1){ // caso ja tenha no banco uma calibragem e ja esta na ultima linha do rset, ou seja, não tem movimentação cadastrada
							incrementaQtApontamentos(pneu,  codUnidade, Servico.TIPO_CALIBRAGEM, conn); // incremento a calibragem
							insertServico(pneu, codAfericao, Servico.TIPO_MOVIMENTACAO, codUnidade, conn); // insiro a movimentação

						}else if(listServico.get(i).equals(Servico.TIPO_MOVIMENTACAO) && i == 0){ // se o unico item do rSet for uma movimentação, significa que não tenho uma calibragem
							incrementaQtApontamentos(pneu, codUnidade, Servico.TIPO_MOVIMENTACAO, conn); // incremento a movimentação
							insertServico(pneu, codAfericao, Servico.TIPO_CALIBRAGEM, codUnidade, conn); // insiro a calibragem
						}

					}else if(tipoServico.equals(Servico.TIPO_CALIBRAGEM)){ // caso o pneu tenha apenas problema de calibragem
						if(listServico.get(i).equals(Servico.TIPO_CALIBRAGEM)){ // caso ja tenha uma calibragem cadastrada
							incrementaQtApontamentos(pneu, codUnidade, Servico.TIPO_CALIBRAGEM, conn); // incrementa a qt apontamentos
						}else if(listServico.get(i).equals(Servico.TIPO_MOVIMENTACAO) &&  i == 0){ // caso só tenha uma movimentação para aquele pneu
							insertServico(pneu, codAfericao, Servico.TIPO_CALIBRAGEM, codUnidade, conn); // insere uma nova calibragem
						}

					}else if(tipoServico.equals(Servico.TIPO_MOVIMENTACAO)){ // pneu só tem problema de movimentação
						if(listServico.get(i).equals(Servico.TIPO_CALIBRAGEM) &&  i == listServico.size()-1){ // caso o pneu só tenha uma calibragem no sistema
							insertServico(pneu, codAfericao, Servico.TIPO_MOVIMENTACAO, codUnidade, conn); // insere uma nova movimentação
						}else{
							incrementaQtApontamentos(pneu, codUnidade, Servico.TIPO_MOVIMENTACAO, conn); // quando ja tem uma movimentação no bd, incrementa.
							i++;
						}				
					}
					i ++;}
			}else{
				if(tipoServico.equals(Servico.TIPO_AMBOS)){
					insertServico(pneu, codAfericao, Servico.TIPO_CALIBRAGEM, codUnidade, conn);
					insertServico(pneu, codAfericao, Servico.TIPO_MOVIMENTACAO, codUnidade, conn);
				}else if(tipoServico.equals(Servico.TIPO_CALIBRAGEM)){
					insertServico(pneu, codAfericao, Servico.TIPO_CALIBRAGEM, codUnidade, conn);
				}else if(tipoServico.equals(Servico.TIPO_MOVIMENTACAO)){
					insertServico(pneu, codAfericao, Servico.TIPO_MOVIMENTACAO, codUnidade, conn);
				}
			}
		}finally {
			closeConnection(conn, stmt, rSet);
		}
	}

	private void insertServico (Pneu pneu, long codAfericao, String tipoServico, Long codUnidade, Connection conn) throws SQLException{
		PreparedStatement stmt = null;
		stmt = conn.prepareStatement("INSERT INTO AFERICAO_MANUTENCAO(COD_AFERICAO, COD_PNEU, COD_UNIDADE, TIPO_SERVICO)"
				+ " VALUES(?,?,?,?)");
		stmt.setLong(1, codAfericao);
		stmt.setLong(2, pneu.getCodigo());
		stmt.setLong(3, codUnidade);
		stmt.setString(4, tipoServico);
		int count = stmt.executeUpdate();
		if(count == 0){
			throw new SQLException("Erro ao inserir o serviço");
		}
		closeConnection(null, stmt, null);
	}

	private String verificaTipoServico(Pneu pneu, Long codUnidade) throws SQLException{

		Restricao restricao = getRestricoesByCodUnidade(codUnidade);
		if (pneu.getVidaAtual() == pneu.getVidasTotal()) {
			if(pneu.getSulcoAtual().getCentral() < restricao.getSulcoMinimoDescarte() && verificaPressao(pneu, restricao)){
				return Servico.TIPO_AMBOS;
			}else if(pneu.getSulcoAtual().getCentral() < restricao.getSulcoMinimoDescarte()){
				return Servico.TIPO_MOVIMENTACAO;
			}else{
				return Servico.TIPO_CALIBRAGEM;
			}
		}else{
			if(pneu.getSulcoAtual().getCentral() < restricao.getSulcoMinimoRecape() && verificaPressao(pneu, restricao)){
				return Servico.TIPO_AMBOS;
			}else if(pneu.getSulcoAtual().getCentral() < restricao.getSulcoMinimoRecape()){
				return Servico.TIPO_MOVIMENTACAO;
			}else{
				return Servico.TIPO_CALIBRAGEM;
			}
		}
	}

	private void incrementaQtApontamentos (Pneu pneu, Long codUnidade,String tipoServico, Connection conn) throws SQLException{
		Connection connection = conn;
		PreparedStatement stmt = null;
		stmt = conn.prepareStatement(" UPDATE AFERICAO_MANUTENCAO SET QT_APONTAMENTOS = "
				+ "(SELECT QT_APONTAMENTOS FROM AFERICAO_MANUTENCAO WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL) + 1 "
				+ "WHERE COD_PNEU = ? AND COD_UNIDADE = ? AND TIPO_SERVICO = ? AND DATA_HORA_RESOLUCAO IS NULL");
		stmt.setLong(1, pneu.getCodigo());
		stmt.setLong(2, codUnidade);
		stmt.setString(3, tipoServico);
		stmt.setLong(4, pneu.getCodigo());
		stmt.setLong(5, codUnidade);
		stmt.setString(6, tipoServico);
		int count = stmt.executeUpdate();
		if(count == 0){
			throw new SQLException("Erro ao atualizar a quantidade de apontamentos do serviço");
		}
		closeConnection(null, stmt, null);
	}


	public Restricao getRestricoesByCodUnidade(Long codUnidade) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		Restricao restricao = new Restricao();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT ER.SULCO_MINIMO_DESCARTE, ER.SULCO_MINIMO_RECAPAGEM, ER.TOLERANCIA_CALIBRAGEM "
					+ "FROM UNIDADE U JOIN "
					+ "EMPRESA E ON E.CODIGO = U.COD_EMPRESA "
					+ "JOIN EMPRESA_RESTRICAO_PNEU ER ON ER.COD_EMPRESA = E.CODIGO "
					+ "WHERE U.CODIGO = ?");
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			if(rSet.next()){
				restricao.setSulcoMinimoDescarte(rSet.getDouble("SULCO_MINIMO_DESCARTE"));
				restricao.setSulcoMinimoRecape(rSet.getDouble("SULCO_MINIMO_RECAPE"));
				restricao.setToleranciaCalibragem(rSet.getDouble("TOLERANCIA_CALIBRAGEM"));
			}else{
				new SQLException("Erro ao buscar os dados de restrição");
			}
		}finally {
			closeConnection(conn, stmt, rSet);
		}
		return restricao;
	}

	public Restricao getRestricoesByPlaca(String placa) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		Restricao restricao = new Restricao();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT ER.SULCO_MINIMO, ER.TOLERANCIA_CALIBRAGEM "
					+ "FROM VEICULO V JOIN UNIDADE U ON U.CODIGO = V.COD_UNIDADE "
					+ "JOIN EMPRESA E ON E.CODIGO = U.COD_EMPRESA "
					+ "JOIN EMPRESA_RESTRICAO_PNEU ER ON ER.COD_EMPRESA = E.CODIGO "
					+ "WHERE V.PLACA = ?");
			stmt.setString(1, placa);
			rSet = stmt.executeQuery();
			if(rSet.next()){
				restricao.setSulcoMinimoDescarte(rSet.getDouble("SULCO_MINIMO_DESCARTE"));
				restricao.setSulcoMinimoRecape(rSet.getDouble("SULCO_MINIMO_RECAPE"));
				restricao.setToleranciaCalibragem(rSet.getDouble("TOLERANCIA_CALIBRAGEM"));
			}else{
				new SQLException("Erro ao buscar os dados de restrição");
			}
		}finally {
			closeConnection(conn, stmt, rSet);
		}
		return restricao;
	}

	@Override
	public NovaAfericao getNovaAfericao(String placa) throws SQLException{
		VeiculoDaoImpl veiculoDaoImpl = new VeiculoDaoImpl();
		NovaAfericao afericaoHolder = new NovaAfericao();
		afericaoHolder.setVeiculo(veiculoDaoImpl.getVeiculoByPlaca(placa));
		if(afericaoHolder.getVeiculo().getPlaca() != null){
			afericaoHolder.setRestricao(getRestricoesByPlaca(placa));
			return afericaoHolder;
		}
		return new NovaAfericao();
	}
}


