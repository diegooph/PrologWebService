package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.time.LocalDate;

import br.com.zalf.prolog.models.indicador.IndicadorHolder;

/**
 * Contém os métodos para busca dos indicadores
 */
public interface IndicadorDao {
	
/**
 * Busca todos os indicadores de um colaborador, respeitando o período selecionado	
 * @param dataInicial uma Data
 * @param dataFinal uma Data
 * @param cpf ao qual será feita a busca
 * @param token para verificar se esta devidamente logado
 * @return IndicadorHolder contendo todos os indicadores
 * @see IndicadorHolder
 * @throws SQLException caso não seja possível realizar a busca
 */
IndicadorHolder getIndicadoresByPeriodo(LocalDate dataInicial, LocalDate dataFinal,
								Long cpf, String token) throws SQLException;

			
}
