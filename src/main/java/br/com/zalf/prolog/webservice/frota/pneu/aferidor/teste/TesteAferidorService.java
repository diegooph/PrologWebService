package br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste._model.ProcedimentoTesteAferidor;
import br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste._model.TesteAferidorExecutado;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-10-07
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class TesteAferidorService {
    @NotNull
    private static final String TAG = TesteAferidorService.class.getSimpleName();
    @NotNull
    private final TesteAferidorDao dao = Injection.provideTesteAferidorDao();

    @NotNull
    public ProcedimentoTesteAferidor getProcedimentoTeste() {
        try {
            return dao.getProcedimentoTeste();
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao buscar procedimento de teste do aferidor", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar procedimento de teste, tente novamente");
        }
    }

    @NotNull
    public ResponseWithCod insereTeste(@NotNull final TesteAferidorExecutado teste) {
        try {
            return ResponseWithCod.ok(
                    "Teste salvo com sucesso",
                    dao.insereTeste(teste));
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao salvar testes executados do aferidor", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao salvar testes executados, tente novamente");
        }
    }
}