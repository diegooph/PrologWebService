package br.com.zalf.prolog.webservice.gente.colaborador;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import org.jetbrains.annotations.NotNull;

public class ColaboradorBackwardHelper {
    @NotNull
    private static final String TAG = ColaboradorBackwardHelper.class.getSimpleName();

    @NotNull
    public static Long getCodColaboradorByCpf(@NotNull final Long codColaboradorResponsavelRequest,
                                              @NotNull final String cpf) {
        try {
            final ColaboradorDao colaboradorDao = Injection.provideColaboradorDao();
            return colaboradorDao.getCodColaboradorByCpfAndCodColaboradorBase(codColaboradorResponsavelRequest, cpf);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar código do colaborador para o cpf: " + cpf, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar o código do colaborador.");
        }
    }
}
