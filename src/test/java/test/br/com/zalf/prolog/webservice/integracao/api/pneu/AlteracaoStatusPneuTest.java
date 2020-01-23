package test.br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.integracao.api.pneu.ApiPneuService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiPneuAlteracaoStatus;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiPneuAlteracaoStatusVeiculo;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import com.google.common.truth.Truth;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 23/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class AlteracaoStatusPneuTest extends BaseTest {
    private static final String TOKEN_INTEGRACAO = "kffdm2ba5ai3lsk79kqur9rb3mq7hv59qa8pr0sho4mcr56clck";
    private ApiPneuService service;

    @Override
    public void initialize() throws Throwable {
        super.initialize();
        DatabaseManager.init();
        service = new ApiPneuService();
    }

    @Override
    public void destroy() {
        DatabaseManager.finish();
        super.destroy();
    }

    @Test
    public void alteracaoStatusPneuTest() {
        final List<ApiPneuAlteracaoStatus> pneusAlteracaoStatus = new ArrayList<>();
        pneusAlteracaoStatus.add(createPneuStatusVeiculo());
        final SuccessResponseIntegracao response = service.atualizaStatusPneus(TOKEN_INTEGRACAO, pneusAlteracaoStatus);
        Truth.assertThat(response).isNotNull();
    }

    @NotNull
    private ApiPneuAlteracaoStatusVeiculo createPneuStatusVeiculo() {
        return new ApiPneuAlteracaoStatusVeiculo(
                55555L,
                "PN005",
                96L,
                "03383283194",
                Now.localDateTimeUtc(),
                "FYN8859",
                222,
                true,
                153L,
                new BigDecimal(300));
    }
}
