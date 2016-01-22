import java.sql.SQLException;
import java.time.LocalDate;

import br.com.zalf.prolog.models.indicador.IndicadorHolder;
import br.com.zalf.prolog.webservice.DataBaseConnection;
import br.com.zalf.prolog.webservice.dao.interfaces.RelatorioDao;

public class RelatorioDaoImpl extends DataBaseConnection implements RelatorioDao {

	public static final String BUSCA_INDICADORES_EQUIPE = "TESTE";
	
	
	@Override
	public IndicadorHolder getIndicadoresEquipeByPeriodo(LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long cpf, String token) throws SQLException {
		
		
		
		
		
		
		
		
		return null;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public IndicadorHolder getIndicadoresUnidadeByPeriodo(LocalDate dataInicial, LocalDate dataFinal, int codUnidade,
			Long cpf, String token) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
