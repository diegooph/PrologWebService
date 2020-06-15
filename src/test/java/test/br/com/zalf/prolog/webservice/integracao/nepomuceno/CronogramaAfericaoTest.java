package test.br.com.zalf.prolog.webservice.integracao.nepomuceno;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoService;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.CronogramaAfericao;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 2020-06-15
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class CronogramaAfericaoTest extends BaseTest {
    @NotNull
    private static final String CPF_COLABORADOR = "03383283194";
    @NotNull
    private static final List<Long> COD_UNIDADES_PROLOG = Arrays.asList(5L, 103L, 179L, 215L);

    @BeforeAll
    public void initialize() {
        DatabaseManager.init();
    }

    @AfterAll
    public void destroy() {
        DatabaseManager.finish();
    }

    @Test
    void testBuscaCronogramaAfericao() throws Throwable {
        final AfericaoService service = new AfericaoService();
        final CronogramaAfericao cronogramaAfericao =
                service.getCronogramaAfericao(getValidToken(CPF_COLABORADOR), COD_UNIDADES_PROLOG);

        assertThat(cronogramaAfericao).isNotNull();
        assertThat(cronogramaAfericao.getModelosPlacasAfericao()).isNotEmpty();
    }
}
