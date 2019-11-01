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
    /**
     * Busca o procedimento de teste que será executado no equipamento de aferição através do aplicativo.
     *
     * @return O procedimento de teste que deverá ser executado.
     * @throws Throwable Caso algum erro ocorrer.
     */
    @NotNull
    ProcedimentoTesteAferidor getProcedimentoTeste() throws Throwable;

    /**
     * Salva o teste do equipamento de aferição que foi executado no aplicativo.
     *
     * @param teste O teste executado.
     * @return O código do teste executado inserido.
     * @throws Throwable Caso algum erro ocorrer.
     */
    @NotNull
    Long insereTeste(@NotNull final TesteAferidorExecutado teste) throws Throwable;
}