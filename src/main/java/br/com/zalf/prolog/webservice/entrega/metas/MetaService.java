package br.com.zalf.prolog.webservice.entrega.metas;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * Classe MetaService responsavel por comunicar-se com a interface DAO
 */
public final class MetaService {
	private static final String TAG = MetaService.class.getSimpleName();
	@NotNull
	private final MetasDao dao = Injection.provideMetasDao();

	@NotNull
	public Response getByCodUnidade(@NotNull final Long codUnidade) {
		try {
			final Optional<Metas> optional = dao.getByCodUnidade(codUnidade);
			if (optional.isPresent()) {
				return Response.ok(optional.get()).build();
			} else {
				return Response.noContent().build();
			}
		} catch (final Throwable throwable) {
			Log.e(TAG, "Erro ao buscar as metas da unidade: " + codUnidade, throwable);
			throw Injection
					.provideProLogExceptionHandler()
					.map(throwable, "Erro ao buscar as metas, tente novamente");
		}
	}

	public void update(@NotNull final Metas metas, @NotNull final Long codUnidade) {
		try {
			dao.update(metas, codUnidade);
		} catch (final Throwable throwable) {
			Log.e(TAG, "Erro ao atualizar as metas da unidade: " + codUnidade, throwable);
			throw Injection
					.provideProLogExceptionHandler()
					.map(throwable, "Erro ao atualizar as metas, tente novamente");
		}
	}
}