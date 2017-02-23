package br.com.zalf.prolog.webservice.metas;

import br.com.zalf.prolog.entrega.indicador.Meta;

import java.sql.SQLException;

/**
 * Classe MetaService responsavel por comunicar-se com a interface DAO
 */
public class MetaService {

	private MetasDao dao = new MetasDaoImpl();
	
	public Meta getByCodUnidade(Long codUnidade) {
		try {
			return dao.getByCodUnidade(codUnidade);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	public boolean update(Meta meta, Long codUnidade) {
		try {
			return dao.update(meta, codUnidade);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
