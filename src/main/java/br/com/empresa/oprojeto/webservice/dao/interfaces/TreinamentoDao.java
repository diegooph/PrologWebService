package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.util.List;

import br.com.empresa.oprojeto.webservice.domain.treinamento.Treinamento;

public interface TreinamentoDao {
	
	List<Treinamento> getNaoVistosColaborador(long cpf);
	List<Treinamento> getVistosColaborador(long cpf);
	
}
