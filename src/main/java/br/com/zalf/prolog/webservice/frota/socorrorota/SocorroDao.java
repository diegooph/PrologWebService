package br.com.zalf.prolog.webservice.frota.socorrorota;

import br.com.zalf.prolog.webservice.frota.socorrorota._model.SocorroRotaAbertura;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 09/12/19.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface SocorroDao {

    /**
     * Cria uma nova solicitação de socorro no banco de dados.
     *
     * @param socorroRotaAbertura   Objeto contencod as informações do veículo a serem inseridas.
     * @return Código gerado pelo BD para a nova solicitação de socorro
     * @throws Throwable Se algum erro ocorrer ao salvar as informações.
     */
    @NotNull
    Long aberturaSocorro(@NotNull final SocorroRotaAbertura socorroRotaAbertura) throws Throwable;
}