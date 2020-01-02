package test.br.com.zalf.prolog.webservice.integracao.api.controlejornada;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.tipomarcacao.ApiTipoMarcacaoService;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.tipomarcacao._model.ApiTipoMarcacao;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 30/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ListagemTiposMarcacaoTest extends BaseTest {
    private static final String TOKEN_INTEGRACAO = "SDADSAS";
    private ApiTipoMarcacaoService service;

    @Override
    public void initialize() throws Throwable {
        super.initialize();
        DatabaseManager.init();
        service = new ApiTipoMarcacaoService();
    }

    @Override
    public void destroy() {
        DatabaseManager.finish();
        super.destroy();
    }

    @Test
    public void getTiposMarcacoesTest() {
        final List<ApiTipoMarcacao> tipoMarcacoes =
                service.getTiposMarcacoes(TOKEN_INTEGRACAO, false);
        assertThat(tipoMarcacoes).isNotNull();
        assertThat(tipoMarcacoes).isNotEmpty();
        System.out.println(GsonUtils.getGson().toJson(tipoMarcacoes));
    }
}
