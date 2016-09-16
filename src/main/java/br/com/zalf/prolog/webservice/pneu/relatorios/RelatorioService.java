package br.com.zalf.prolog.webservice.pneu.relatorios;

import br.com.zalf.prolog.frota.pneu.Pneu;
import br.com.zalf.prolog.frota.pneu.relatorio.Aderencia;
import br.com.zalf.prolog.frota.pneu.relatorio.Faixa;
import br.com.zalf.prolog.frota.pneu.relatorio.ResumoServicos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RelatorioService {
	
	private RelatorioDao dao = new RelatorioDaoImpl();
	
	public List<Faixa> getQtPneusByFaixaSulco(List<String> codUnidades, List<String> status){
		try{
			return dao.getQtPneusByFaixaSulco(codUnidades, status);
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public List<Faixa> getQtPneusByFaixaPressao(List<String> codUnidades, List<String> status){
		try{
			return dao.getQtPneusByFaixaPressao(codUnidades, status);
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public List<Aderencia> getAderenciaByUnidade(int ano, int mes, Long codUnidade){
		try{
			return dao.getAderenciaByUnidade(ano, mes, codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public List<ResumoServicos> getResumoServicosByUnidades(int ano, int mes, List<String> codUnidades){
		try{
			return dao.getResumoServicosByUnidades(ano, mes, codUnidades);
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public List<Pneu> getPneusByFaixa(double inicioFaixa, double fimFaixa, Long codEmpresa,
									  String codUnidade, long limit, long offset){
		try{
			return dao.getPneusByFaixa(inicioFaixa, fimFaixa, codEmpresa, codUnidade, limit, offset);
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

}
