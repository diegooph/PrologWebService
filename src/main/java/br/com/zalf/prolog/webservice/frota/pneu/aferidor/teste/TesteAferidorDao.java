package br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste;

import br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste._model.ProcedimentoTesteAferidor;
import br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste._model.TesteAferidorExecutado;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-10-07
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface TesteAferidorDao {

    @NotNull
    ProcedimentoTesteAferidor getProcedimentoTeste() throws Throwable;

    @NotNull
    Long insereTeste(@NotNull final TesteAferidorExecutado teste) throws Throwable;
}