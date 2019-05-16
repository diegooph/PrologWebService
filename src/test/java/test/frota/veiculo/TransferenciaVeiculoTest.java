package test.frota.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.VeiculoTransferenciaService;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao.VeiculoEnvioTransferencia;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import test.BaseTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 09/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TransferenciaVeiculoTest extends BaseTest {

    private static final String COD_TIPO_TOCO = "63";
    private static final Long COD_UNIDADE_DESTINO = 103L;
    private static final Long COD_COLABORADOR = 2272L;
    @NotNull
    private VeiculoTransferenciaService service = new VeiculoTransferenciaService();
    @NotNull
    private VeiculoDao veiculoDao = Injection.provideVeiculoDao();

    @Override
    public void initialize() throws Throwable {
        DatabaseManager.init();
    }

    @Override
    public void destroy() {
        DatabaseManager.finish();
    }

    @Test
    public void insertTransferenciaVeiculoTest() throws Throwable {
        List<String> placas = veiculoDao.getPlacasVeiculosByTipo(COD_UNIDADE, COD_TIPO_TOCO);
        Collections.shuffle(placas);

        final String placa1 = placas.get(0);
        final String placa2 = placas.get(1);

        final Veiculo veiculo1 = veiculoDao.getVeiculoByPlaca(placa1, true);
        final Veiculo veiculo2 = veiculoDao.getVeiculoByPlaca(placa2, true);

        final ProcessoTransferenciaVeiculoRealizacao processo = convertTo(veiculo1, veiculo2);

        System.out.println("Transferindo placas: " + veiculo1.getPlaca() + " e " + veiculo2.getPlaca());

        assertThat(processo).isNotNull();
        assertThat(processo.getVeiculosTransferencia()).hasSize(2);

        final ResponseWithCod response = service.insertProcessoTransferenciaVeiculo(processo);

        assertThat(response).isNotNull();
        assertThat(response.getCodigo()).isNotNull();
        assertThat(response.getCodigo()).isGreaterThan(0L);
        System.out.println("Codigo processo inserido: " + response.getCodigo());
    }

    @NotNull
    private ProcessoTransferenciaVeiculoRealizacao convertTo(@NotNull final Veiculo veiculo1,
                                                             @NotNull final Veiculo veiculo2) {
        final List<VeiculoEnvioTransferencia> veiculosTransferencia = new ArrayList<>();
        veiculosTransferencia.add(new VeiculoEnvioTransferencia(veiculo1.getCodigo(), getCodPneus(veiculo1)));
        veiculosTransferencia.add(new VeiculoEnvioTransferencia(veiculo2.getCodigo(), getCodPneus(veiculo2)));

        return new ProcessoTransferenciaVeiculoRealizacao(
                COD_UNIDADE,
                COD_UNIDADE_DESTINO,
                COD_COLABORADOR,
                veiculosTransferencia,
                "Movimentação de teste");
    }

    @NotNull
    private List<Long> getCodPneus(@NotNull final Veiculo veiculo) {
        final List<Long> codigos = new ArrayList<>();
        for (final Pneu pneu : veiculo.getListPneus()) {
            codigos.add(pneu.getCodigo());
        }
        return codigos;
    }
}
