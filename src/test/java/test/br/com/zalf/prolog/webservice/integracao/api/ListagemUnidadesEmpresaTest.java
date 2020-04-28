package test.br.com.zalf.prolog.webservice.integracao.api;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.integracao.api.unidade.ApiUnidade;
import br.com.zalf.prolog.webservice.integracao.api.unidade.ApiUnidadeService;
import com.google.common.truth.Truth;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.util.List;

/**
 * Created on 19/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ListagemUnidadesEmpresaTest extends BaseTest {
    private static final String TOKEN_INTEGRACAO = "kffdm2ba5ai3lsk79kqur9rb3mq7hv59qa8pr0sho4mcr56clck";
    private ApiUnidadeService service;

    @Override
    public void initialize() throws Throwable {
        super.initialize();
        DatabaseManager.init();
        service = new ApiUnidadeService();
    }

    @Override
    public void destroy() throws Throwable {
        DatabaseManager.finish();
        super.destroy();
    }

    @Test
    public void listagemUnidadesEmpresaTest() {
        final List<ApiUnidade> unidades = service.getUnidades(TOKEN_INTEGRACAO, false);
        Truth.assertThat(unidades).isNotNull();
        Truth.assertThat(unidades).isNotEmpty();
    }
}
