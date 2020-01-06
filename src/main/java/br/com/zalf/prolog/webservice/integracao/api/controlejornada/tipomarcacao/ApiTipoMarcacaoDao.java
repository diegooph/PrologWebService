package br.com.zalf.prolog.webservice.integracao.api.controlejornada.tipomarcacao;

import br.com.zalf.prolog.webservice.integracao.api.controlejornada.tipomarcacao._model.ApiTipoMarcacao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 29/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ApiTipoMarcacaoDao {
    /**
     * Método utilizado para listar os tipos de marcações disponíveis para a empresa.
     * <p>
     * Esse método irá retornar todos os tipos de marcações da empresa a qual o {@code tokenIntegracao} pertence.
     * <p>
     * Para buscar apenas tipos de marcações ativos, <code>apenasTiposMarcacoesAtivos = true</code>;
     *
     * @param tokenIntegracao            Token da empresa que está buscando os tipos de marcações.
     * @param apenasTiposMarcacoesAtivos Booleano indicando que será filtrado apenas tipos de marcações ativos.
     * @return Uma lista de {@link ApiTipoMarcacao tipos de marcações} contendo todas as informações.
     * @throws Throwable Caso algum erro ocorrer.
     */
    @NotNull
    List<ApiTipoMarcacao> getTiposMarcacoes(@NotNull final String tokenIntegracao,
                                            final boolean apenasTiposMarcacoesAtivos) throws Throwable;
}
