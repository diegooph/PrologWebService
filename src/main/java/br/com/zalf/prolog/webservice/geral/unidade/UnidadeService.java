package br.com.zalf.prolog.webservice.geral.unidade;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEdicao;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
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
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao atualizar a unidade %d", unidadeEdicao.getCodUnidade()), e);
        }
    }

    @NotNull
    public UnidadeVisualizacao getUnidadeByCodigo(@NotNull final Long codUnidade) throws Throwable {
        try {
            return dao.getUnidadeByCodigo(codUnidade);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar unidade. \n" +
                    "Código da Unidade: %s", codUnidade), e);
            return null;
        }
    }

    @NotNull
    public List<UnidadeVisualizacao> getUnidadesListagem(
            @NotNull final Long codEmpresa,
            @Nullable final Long codRegional) throws Throwable {
        try {
            return dao.getUnidadesListagem(codEmpresa, codRegional);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar lista de unidades da empresa. \n" +
                    "Código da Empresa: %s\n" +
                    "Código da Regional: %s", codEmpresa, codRegional), e);
            return null;
        }
    }

}
