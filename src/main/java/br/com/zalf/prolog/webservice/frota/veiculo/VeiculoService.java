package br.com.zalf.prolog.webservice.frota.veiculo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Eixos;
import br.com.zalf.prolog.models.Marca;
import br.com.zalf.prolog.models.Modelo;
import br.com.zalf.prolog.models.TipoVeiculo;
import br.com.zalf.prolog.models.Veiculo;

public class VeiculoService {
	private VeiculoDaoImpl dao = new VeiculoDaoImpl();
	
	public List<Veiculo> getVeiculosAtivosByUnidade(Long codUnidade) {
		try {
			return dao.getVeiculosAtivosByUnidade(codUnidade);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Veiculo>();
		}
	}
	
	public List<TipoVeiculo> getTipoVeiculosByUnidade(Long codUnidade){
		try {
			return dao.getTipoVeiculosByUnidade(codUnidade);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public boolean insertTipoVeiculo(TipoVeiculo tipoVeiculo, Long codUnidade){
		try{
			return dao.insertTipoVeiculo(tipoVeiculo, codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public List<Eixos> getEixos(){
		try{
			return dao.getEixos();
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(Long cpf) {
		try {
			return dao.getVeiculosAtivosByUnidadeByColaborador(cpf);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Veiculo>();
		}
	}
	
	public boolean update(Veiculo veiculo, String placaOriginal) {
		try {
			return dao.update(veiculo, placaOriginal);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean delete(String placa){
		try{
			return dao.delete(placa);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean insert(Veiculo veiculo, Long codUnidade) {
		try{
			return dao.insert(veiculo, codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public List<Marca> getMarcaModeloVeiculoByCodEmpresa(Long codEmpresa){
		try{
			return dao.getMarcaModeloVeiculoByCodEmpresa(codEmpresa);
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public boolean insertModeloVeiculo(Modelo modelo, long codEmpresa, long codMarca){
		try{
			return dao.insertModeloVeiculo(modelo, codEmpresa, codMarca);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
}
