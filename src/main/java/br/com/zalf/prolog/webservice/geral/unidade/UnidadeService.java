package br.com.zalf.prolog.webservice.geral.unidade;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEdicao;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacaoListagem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class UnidadeService {
    private static final String TAG = UnidadeService.class.getSimpleName();
    @NotNull
    private final UnidadeDao dao = Injection.provideUnidadeDao();

    public void updateUnidade(@NotNull final UnidadeEdicao unidadeEdicao) {
        try {
            dao.update(unidadeEdicao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao atualizar a unidade %d", unidadeEdicao.getCodUnidade()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar unidade, tente novamente.");
        }
    }

    @NotNull
    public UnidadeVisualizacaoListagem getUnidadeByCodigo(@NotNull final Long codUnidade) {
        try {
            return dao.getUnidadeByCodigo(codUnidade);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar unidade.\n" +
                    "Código da Unidade: %d", codUnidade), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar unidade, tente novamente.");
        }
    }

    @NotNull
    public List<UnidadeVisualizacaoListagem> getUnidadesListagem(
            @NotNull final Long codEmpresa,
            @Nullable final Long codRegional) {
        try {
            return dao.getUnidadesListagem(codEmpresa, codRegional);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar lista de unidades da empresa.\n" +
                    "Código da Empresa: %d\n" +
                    "Código da Regional: %d", codEmpresa, codRegional), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar unidades, tente novamente.");
        }
    }

}
