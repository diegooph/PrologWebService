package br.com.zalf.prolog.webservice.pneu;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Marca;
import br.com.zalf.prolog.models.pneu.Pneu;
import br.com.zalf.prolog.models.pneu.Pneu.Dimensao;

public class PneuService {
	private PneuDaoImpl dao = new PneuDaoImpl();
	
	
	public boolean insert(Pneu pneu, Long codUnidade){
		try{
			return dao.insert(pneu, codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public List<Pneu> getPneuByCodUnidadeByStatus(Long codUnidade, String status){
		try{
			return dao.getPneuByCodUnidadeByStatus(codUnidade, status);
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public List<Marca> getMarcaModeloPneuByCodEmpresa(Long codEmpresa){
		try{
			return dao.getMarcaModeloPneuByCodEmpresa(codEmpresa);
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public List<Dimensao> getDimensoes(){
		try{
			return dao.getDimensoes();
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
}