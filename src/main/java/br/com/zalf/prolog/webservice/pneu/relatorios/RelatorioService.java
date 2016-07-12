package br.com.zalf.prolog.webservice.pneu.relatorios;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.pneu.Pneu;
import br.com.zalf.prolog.models.pneu.relatorios.Aderencia;
import br.com.zalf.prolog.models.pneu.relatorios.Faixa;

public class RelatorioService {
	
	RelatorioDaoImpl dao = new RelatorioDaoImpl();
	
	public List<Pneu> getPneusByFaixa(double inicioFaixa, double fimFaixa, Long codEmpresa, String codUnidade, long limit, long offset){
		try{
			return dao.getPneusByFaixa(inicioFaixa, fimFaixa, codEmpresa, codUnidade, limit, offset);
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
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

}
