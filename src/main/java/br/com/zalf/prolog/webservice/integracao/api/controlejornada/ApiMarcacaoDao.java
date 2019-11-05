package br.com.zalf.prolog.webservice.integracao.api.controlejornada;

import br.com.zalf.prolog.webservice.integracao.api.controlejornada._model.ApiMarcacao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 30/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ApiMarcacaoDao {
    /**
     * Método utilizado para listar as marcações realizadas pelos colaboradores da empresa representada pelo
     * {@code tokenIntegracao}.
     * <p>
     * O método irá listar todas as marcações a partir do código {@code codUltimaMarcacaoSincronizada} recebido por
     * parâmetro.
     * <p>
     * A lista conterá todas as informações referentes às marcações realizadas.
     *
     * @param tokenIntegracao               Token da empresa que está buscando as marcações realizadas.
     * @param codUltimaMarcacaoSincronizada Código da última marcação sincronizada. Se este código for ZERO então
     *                                      listará todas as marcações da empresa.
     * @return Uma lista de {@link ApiMarcacao marcações} realizadas pelos colaboradores da empresa.
     * @throws Throwable Se algum erro acontecer.
     */
    @NotNull
    List<ApiMarcacao> getMarcacoesRealizadas(@NotNull final String tokenIntegracao,
                                             @NotNull final Long codUltimaMarcacaoSincronizada) throws Throwable;
}
