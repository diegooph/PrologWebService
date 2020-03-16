package br.com.zalf.prolog.webservice.gente.unidade;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.gente.unidade._model.UnidadeEdicao;
import br.com.zalf.prolog.webservice.gente.unidade._model.UnidadeVisualizacao;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.QueryParam;
import java.sql.SQLException;
import java.util.List;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public class UnidadeService {

    private static final String TAG = UnidadeService.class.getSimpleName();
    private final UnidadeDao dao = Injection.provideUnidadeDao();

    public boolean updateUnidade(@NotNull final UnidadeEdicao unidadeEdicao) {
        try {
            dao.update(unidadeEdicao);
            return true;
        } catch (final Throwable e) {
            Log.e(TAG, String.format("Erro ao atualizar a unidade %d", unidadeEdicao.getCodUnidade()), e);
            return false;
        }
    }

    @NotNull
    public UnidadeVisualizacao getUnidadeByCodUnidade(final Long codUnidade) throws Throwable {
        try {
            return dao.getUnidadeByCodUnidade(codUnidade);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar unidade. \n" +
                    "Código da Unidade: %s", codUnidade), e);
            return null;
        }
    }

    @NotNull
    public List<UnidadeVisualizacao> getAllUnidadeByCodEmpresaAndCodRegional(
            @QueryParam("codEmpresa") final Long codEmpresa,
            @QueryParam("codRegional") final Long codRegional) throws Throwable {
        try {
            return dao.getAllUnidadeByCodEmpresaAndCodRegional(codEmpresa, codRegional);
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar lista de unidades da empresa. \n" +
                    "Código da Empresa: %s\n" +
                    "Código da Regional: %s", codEmpresa, codRegional), e);
            return null;
        }
    }

}
