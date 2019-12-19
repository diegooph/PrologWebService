package br.com.zalf.prolog.webservice.frota.socorrorota;

import br.com.zalf.prolog.webservice.frota.socorrorota._model.SocorroRotaAbertura;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 09/12/19.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface SocorroRotaDao {

    /**
     * Cria uma nova solicitação de socorro no banco de dados.
     *
     * @param socorroRotaAbertura Objeto contendo as informações para abertura de um socorro.
     * @return Código gerado pelo BD para a nova solicitação de socorro.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Long aberturaSocorro(@NotNull final SocorroRotaAbertura socorroRotaAbertura) throws Throwable;

    /**
     * Busca as unidades disponíveis para a abertura de socorro em rota por colaborador
     *
     * @param codColaborador Código do colaborador
     * @return Uma lista de {@link UnidadeAberturaSocorro unidades} que o colaborador tem acesso
     * @throws Throwable Se algo der errado na busca.
     */
    @NotNull
    List<UnidadeAberturaSocorro> getUnidadesDisponiveisAberturaSocorroByCodColaborador(
            @NotNull final Long codColaborador) throws Throwable;
}