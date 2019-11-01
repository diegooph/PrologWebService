package test.br.com.zalf.prolog.webservice.integracao.api.controlejornada;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.model.ApiMarcacao;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.ApiMarcacaoService;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 02/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ListagemMarcacoesTest extends BaseTest {
    private static final String TOKEN_INTEGRACAO = "A";
    private ApiMarcacaoService service;

    @Override
    public void initialize() throws Throwable {
        super.initialize();
        DatabaseManager.init();
        service = new ApiMarcacaoService();
    }

    @Override
    public void destroy() {
        DatabaseManager.finish();
        super.destroy();
    }

    @Test
    public void getMarcacoesRealizadasTest() {
        final List<ApiMarcacao> marcacoesRealizadas =
                service.getMarcacoesRealizadas(TOKEN_INTEGRACAO, 0L);
        assertThat(marcacoesRealizadas).isNotNull();
        assertThat(marcacoesRealizadas).isNotEmpty();
        System.out.println(GsonUtils.getGson().toJson(marcacoesRealizadas));
    }
}
