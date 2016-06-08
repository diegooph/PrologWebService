package br.com.zalf.prolog.webservice.veiculo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.TipoVeiculo;
import br.com.zalf.prolog.models.Veiculo;
import br.com.zalf.prolog.models.pneu.afericao.SelecaoPlacaAfericao;

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
	
	public SelecaoPlacaAfericao getSelecaoPlacaAfericao(LocalDate dataInicial, LocalDate dataFinal, Long codEmpresa, Long codUnidade){
		try{
			return dao.getSelecaoPlacaAfericao(dataInicial, dataFinal, codEmpresa, codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return new SelecaoPlacaAfericao();
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
	
	public List<Veiculo> getAll(Request<?> request) {
		try {
			return dao.getAll(request);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Veiculo>();
		}
	}
	
	public boolean update(String placa, String placaEditada, String modelo, boolean isAtivo) {
		try {
			return dao.update(placa, placaEditada, modelo, isAtivo);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean insert(Request<Veiculo> request) {
		try{
			return dao.insert(request);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
}
