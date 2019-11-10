package test.br.com.zalf.prolog.webservice.pilares.frota.pneu;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.PneuTransferenciaService;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.listagem.PneuTransferenciaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.visualizacao.PneuTransferenciaProcessoVisualizacao;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 15/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TransferenciaPneuTest extends BaseTest {
    @NotNull
    private static final String DATA_INICIAL_BUSCA = "2019-01-01";
    @NotNull
    private static final String DATA_FINAL_BUSCA = "2019-05-31";
    @NotNull
    private final PneuTransferenciaService service = new PneuTransferenciaService();

    @Override
    public void initialize() throws Throwable {
        DatabaseManager.init();
    }

    @Override
    public void destroy() {
        DatabaseManager.finish();
    }

    @Test
    public void getListagemTransferencia() throws ProLogException {
        final List<Long> codUnidadesOrigem = new ArrayList<>();
        codUnidadesOrigem.add(5L);
        final List<Long> codUnidadesDestino = new ArrayList<>();
        codUnidadesDestino.add(103L);
        final List<PneuTransferenciaListagem> listagem =
                service.getListagem(codUnidadesOrigem, codUnidadesDestino, DATA_INICIAL_BUSCA, DATA_FINAL_BUSCA);

        assertThat(listagem).isNotNull();
        assertThat(listagem).isNotEmpty();

        final PneuTransferenciaListagem pneuTransferenciaListagem = listagem.get(0);

        assertThat(pneuTransferenciaListagem).isNotNull();
        assertThat(pneuTransferenciaListagem.getLinkTransferenciaVeiculo()).isNotNull();
    }

    @Test
    public void getTransferenciaVisualizacao() throws ProLogException {
        final List<Long> codUnidadesOrigem = new ArrayList<>();
        codUnidadesOrigem.add(5L);
        final List<Long> codUnidadesDestino = new ArrayList<>();
        codUnidadesDestino.add(103L);
        final List<PneuTransferenciaListagem> listagem =
                service.getListagem(codUnidadesOrigem, codUnidadesDestino, DATA_INICIAL_BUSCA, DATA_FINAL_BUSCA);

        assertThat(listagem).isNotNull();
        assertThat(listagem).isNotEmpty();

        Collections.shuffle(listagem);

        final PneuTransferenciaProcessoVisualizacao processo =
                service.getTransferenciaVisualizacao(listagem.get(0).getCodTransferenciaProcesso());

        assertThat(processo).isNotNull();
        assertThat(processo.getPneusTransferidos()).isNotEmpty();
    }
}
