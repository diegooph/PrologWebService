package br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes;

import br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes.model.ApiAjusteMarcacao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 02/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ApiAjusteMarcacaoDao {
    /**
     * Método utilizado para listar todos os ajustes de marcações realizados pelos colaboradores da empresa
     * representada pelo {@code tokenIntegracao}.
     * <p>
     * O método irá listar todos os ajustes de marcações a partir do código {@code codUltimoAjusteMarcacaoSincronizado}
     * recebido por parâmetro.
     * <p>
     * A lista conterá todas as informações referentes aos ajustes realizados, bem como a marcação que foi ajustada.
     *
     * @param tokenIntegracao                     Token da empresa que está buscando os ajustes de marcações realizados.
     * @param codUltimoAjusteMarcacaoSincronizado Código do último ajuste sincronizado. Se este código for ZERO então
     *                                            listará todos os ajustes de marcações realizados na empresa.
     * @return Uma lista de {@link ApiAjusteMarcacao ajustes de marcações} realizadas pelos colaboradores da empresa.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    List<ApiAjusteMarcacao> getAjustesMarcacaoRealizados(
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUltimoAjusteMarcacaoSincronizado) throws Throwable;
}
