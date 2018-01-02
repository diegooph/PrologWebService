package br.com.zalf.prolog.webservice.entrega.metas;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;

import java.sql.SQLException;

/**
 * Classe MetaService responsavel por comunicar-se com a interface DAO
 */
public class MetaService {
	private static final String TAG = MetaService.class.getSimpleName();
	private final MetasDao dao = Injection.provideMetasDao();

	public Metas getByCodUnidade(Long codUnidade) {
		try {
			return dao.getByCodUnidade(codUnidade);
		} catch (SQLException e) {
			Log.e(TAG, "Erro ao buscar as metas de uma unidade", e);
			return null;
		}
	}

	public boolean update(Metas metas, Long codUnidade) {
		try {
			return dao.update(metas, codUnidade);
		} catch (SQLException e) {
			Log.e(TAG, "Erro ao atualizar as metas de uma unidade", e);
			return false;
		}
	}
}