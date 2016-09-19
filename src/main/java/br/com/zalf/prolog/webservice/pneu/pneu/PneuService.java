package br.com.zalf.prolog.webservice.pneu.pneu;

import br.com.zalf.prolog.commons.veiculo.Marca;
import br.com.zalf.prolog.commons.veiculo.Modelo;
import br.com.zalf.prolog.commons.veiculo.Veiculo;
import br.com.zalf.prolog.frota.pneu.Pneu;
import br.com.zalf.prolog.frota.pneu.Pneu.Dimensao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe PneuService responsavel por comunicar-se com a interface DAO
 */
public class PneuService {

	private PneuDao dao = new PneuDaoImpl();
	
	
	public boolean insert(Pneu pneu, Long codUnidade){
		try{
			return dao.insert(pneu, codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean update (Pneu pneu, Long codUnidade, Long codOriginal){
		try{
			return dao.update(pneu, codUnidade, codOriginal);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean insertModeloPneu(Modelo modelo, long codEmpresa, long codMarca){
		try{
			return dao.insertModeloPneu(modelo, codEmpresa, codMarca);
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
	
	public boolean vinculaPneuVeiculo(Veiculo veiculo){
		try{
			return dao.vinculaPneuVeiculo(veiculo);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
}