package test.br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.ApiMarcaModeloPneuService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiMarcaPneu;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiModeloPneu;
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
public class ListagemMarcasModelosPneuTest extends BaseTest {
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
    public void listagemMarcasPneuTest() {
        DatabaseManager.init();
        final List<ApiMarcaPneu> marcasPneu =
                new ApiMarcaModeloPneuService().getMarcasPneu(TOKEN_INTEGRACAO, false);
        assertThat(marcasPneu).isNotNull();
        assertThat(marcasPneu).isNotEmpty();
        DatabaseManager.finish();
    }

    @Test
    public void litagemModeloPneuTest() {
        DatabaseManager.init();
        List<ApiMarcaPneu> marcasPneu =
                new ApiMarcaModeloPneuService().getMarcasPneu(TOKEN_INTEGRACAO, false);
        assertThat(marcasPneu).isNotNull();
        assertThat(marcasPneu).isNotEmpty();
        Collections.shuffle(marcasPneu);

        final ApiMarcaPneu marcaPneu = marcasPneu.get(0);

        final List<ApiModeloPneu> modelosPneu =
                new ApiMarcaModeloPneuService()
                        .getModelosPneu(TOKEN_INTEGRACAO, marcaPneu.getCodigo(), false);
        assertThat(modelosPneu).isNotNull();
        assertThat(modelosPneu.size()).isEqualTo(marcaPneu.getModelos().size());
        DatabaseManager.finish();
    }
}
