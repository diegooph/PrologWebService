package test.br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.ApiMarcaModeloPneuService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiMarcaBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiModeloBanda;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 19/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ListagemMarcasModelosBandaTest extends BaseTest {
    private static final String TOKEN_INTEGRACAO = "kffdm2ba5ai3lsk79kqur9rb3mq7hv59qa8pr0sho4mcr56clck";
    private ApiMarcaModeloPneuService service;

    @Override
    public void initialize() throws Throwable {
        super.initialize();
        DatabaseManager.init();
        service = new ApiMarcaModeloPneuService();
    }

    @Override
    public void destroy() throws Throwable {
        DatabaseManager.finish();
        super.destroy();
    }

    @Test
    public void listagemMarcasBandaTest() throws Throwable {
        DatabaseManager.init();
        final List<ApiMarcaBanda> marcasBanda = new ApiMarcaModeloPneuService().getMarcasBanda(TOKEN_INTEGRACAO, false);
        assertThat(marcasBanda).isNotNull();
        assertThat(marcasBanda).isNotEmpty();
        DatabaseManager.finish();
    }

    @Test
    public void listagemModelosBandaTest() {
        DatabaseManager.init();
        final List<ApiMarcaBanda> marcasBanda =
                new ApiMarcaModeloPneuService().getMarcasBanda(TOKEN_INTEGRACAO, false);
        assertThat(marcasBanda).isNotNull();
        Collections.shuffle(marcasBanda);

        final ApiMarcaBanda marcaBanda = marcasBanda.get(0);

        final List<ApiModeloBanda> modelosBanda =
                new ApiMarcaModeloPneuService()
                        .getModelosBanda(TOKEN_INTEGRACAO, marcaBanda.getCodigo(), false);
        assertThat(modelosBanda).isNotNull();
        assertThat(modelosBanda.size()).isEqualTo(marcaBanda.getModelos().size());
        DatabaseManager.finish();
    }
}
