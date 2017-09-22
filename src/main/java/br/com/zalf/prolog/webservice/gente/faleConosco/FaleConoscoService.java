package br.com.zalf.prolog.webservice.gente.faleConosco;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;

import java.sql.SQLException;
import java.util.List;

/**
 * Classe FaleConoscoService responsavel por comunicar-se com a interface DAO
 */
public class FaleConoscoService {

	private FaleConoscoDao dao = new FaleConoscoDaoImpl();

	public AbstractResponse insert(FaleConosco faleConosco, Long codUnidade) {
		try {
			return ResponseWithCod.ok("Fale conosco inserido com sucesso.", dao.insert(faleConosco, codUnidade));
		} catch (Exception e) {
			e.printStackTrace();
			return Response.error("Erro ao inserir fale conosco.");
		}
	}

	public boolean insertFeedback(FaleConosco faleConosco, Long codUnidade){
		try{
			return dao.insertFeedback(faleConosco, codUnidade);
		}catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}

	public List<FaleConosco> getAll(long dataInicial, long dataFinal, int limit, int offset,
									String cpf, String equipe, Long codUnidade, String status, String categoria){

		try{
			return dao.getAll(dataInicial, dataFinal, limit, offset, cpf, equipe, codUnidade, status, categoria);
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public List<FaleConosco> getByColaborador(Long cpf, String status) {
		try {
			return dao.getByColaborador(cpf, status);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
