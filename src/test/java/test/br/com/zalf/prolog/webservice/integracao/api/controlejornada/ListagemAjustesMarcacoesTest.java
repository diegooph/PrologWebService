package test.br.com.zalf.prolog.webservice.integracao.api.controlejornada;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes.ApiAjusteMarcacaoService;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes.model.ApiAjusteMarcacao;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 02/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ListagemAjustesMarcacoesTest extends BaseTest {
    private static final String TOKEN_INTEGRACAO = "A";
    private ApiAjusteMarcacaoService service;

    @Override
    public void initialize() throws Throwable {
        super.initialize();
        DatabaseManager.init();
        service = new ApiAjusteMarcacaoService();
    }

    @Override
    public void destroy() {
        DatabaseManager.finish();
        super.destroy();
    }

    @Test
    public void getAjustesMarcacaoRealizadosTest() {
        final List<ApiAjusteMarcacao> ajustesMarcacaoRealizados =
                service.getAjustesMarcacaoRealizados(TOKEN_INTEGRACAO, 0L);
        assertThat(ajustesMarcacaoRealizados).isNotNull();
        assertThat(ajustesMarcacaoRealizados).isNotEmpty();
        System.out.println(GsonUtils.getGson().toJson(ajustesMarcacaoRealizados));
    }
}
