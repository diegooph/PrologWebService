package test.br.com.zalf.prolog.webservice.pilares.frota.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.VeiculoTransferenciaService;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.listagem.ProcessoTransferenciaVeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.DetalhesVeiculoTransferido;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.visualizacao.ProcessoTransferenciaVeiculoVisualizacao;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

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
    private final VeiculoTransferenciaService service = new VeiculoTransferenciaService();
    @NotNull
    private final VeiculoDao veiculoDao = Injection.provideVeiculoDao();

    @Override
    public void initialize() throws Throwable {
        DatabaseManager.init();
    }

    @Override
    public void destroy() {
        DatabaseManager.finish();
    }

    @Test
    public void fluxoCompletoDeTransferenciaTest() throws Throwable {
        // Busca placas da Unidade.
        final List<String> placas = veiculoDao.getPlacasVeiculosByTipo(5L, COD_TIPO_TOCO);
        Collections.shuffle(placas);

        // Busca Veículos a serem transferidos.
        final List<Veiculo> veiculos = new ArrayList<>();
        veiculos.add(veiculoDao.getVeiculoByPlaca(placas.get(0), null, true));

        // Cria processo de Transferência com as placas selecionadas.
        final ProcessoTransferenciaVeiculoRealizacao processoRealizacao = convertTo(veiculos);

        assertThat(processoRealizacao).isNotNull();
        assertThat(processoRealizacao.getCodVeiculosTransferencia()).hasSize(veiculos.size());

        // Insere processo de Transferência.
        final ResponseWithCod response = service.insertProcessoTransferenciaVeiculo(USER_TOKEN, processoRealizacao);

        assertThat(response).isNotNull();
        assertThat(response.getCodigo()).isNotNull();
        assertThat(response.getCodigo()).isGreaterThan(0L);

        // Buscamos a listagem de processos.
        final List<ProcessoTransferenciaVeiculoListagem> listagemProcessos =
                service.getProcessosTransferenciaVeiculoListagem(
                        Collections.singletonList(5L),
                        Collections.singletonList(103L),
                        "2019-01-01",
                        "2019-05-30");

        assertThat(listagemProcessos).isNotNull();
        assertThat(listagemProcessos.size()).isAtLeast(1);

        ProcessoTransferenciaVeiculoListagem processoListagem = null;
        for (final ProcessoTransferenciaVeiculoListagem processo : listagemProcessos) {
            if (processo.getCodProcessoTransferencia().equals(response.getCodigo())) {
                processoListagem = processo;
            }
        }

        assertThat(processoListagem).isNotNull();
        assertThat(processoListagem.getCodProcessoTransferencia()).isEqualTo(response.getCodigo());
        assertThat(processoListagem.getQtdPlacasTransferidas()).isEqualTo(veiculos.size());

        // Buscamos o processo de transferência específico.
        final ProcessoTransferenciaVeiculoVisualizacao processoVisualizacao =
                service.getProcessoTransferenciaVeiculoVisualizacao(processoListagem.getCodProcessoTransferencia());

        assertThat(processoVisualizacao).isNotNull();
        assertThat(processoVisualizacao.getCodProcessoTransferencia()).isEqualTo(processoListagem.getCodProcessoTransferencia());
        assertThat(processoVisualizacao.getDataHoraRealizacao()).isEquivalentAccordingToCompareTo(processoListagem.getDataHoraRealizacao());
        assertThat(processoVisualizacao.getQtdVeiculosTransferidos()).isEqualTo(processoListagem.getQtdPlacasTransferidas());

        // Agora buscamos os detalhes da trasferencia
        final DetalhesVeiculoTransferido detalhesVeiculoTransferido =
                service.getDetalhesVeiculoTransferido(processoVisualizacao.getCodProcessoTransferencia(),
                                                      veiculos.get(0).getCodigo());

        assertThat(detalhesVeiculoTransferido).isNotNull();
        assertThat(detalhesVeiculoTransferido.getPlacaVeiculo()).isEqualTo(veiculos.get(0).getPlaca());
        assertThat(detalhesVeiculoTransferido.getPneusAplicadosMomentoTransferencia().size()).isEqualTo(veiculos.get(0)
                                                                                                                .getListPneus()
                                                                                                                .size());
    }

    @Test
    public void insertTransferenciaVeiculoTest() throws Throwable {
        final List<String> placas = veiculoDao.getPlacasVeiculosByTipo(5L, COD_TIPO_TOCO);
        Collections.shuffle(placas);

        final String placa1 = placas.get(0);
        //        final String placa2 = placas.get(1);

        final List<Veiculo> veiculos = new ArrayList<>();
        veiculos.add(veiculoDao.getVeiculoByPlaca(placa1, null, true));
        //        veiculos.add(veiculoDao.getVeiculoByPlaca(placa2, true));

        final ProcessoTransferenciaVeiculoRealizacao processo = convertTo(veiculos);

        //        System.out.println("Transferindo placas: " + veiculos.toString());
        //
        //        assertThat(processo).isNotNull();
        //        assertThat(processo.getVeiculosTransferencia()).hasSize(2);

        final ResponseWithCod response = service.insertProcessoTransferenciaVeiculo(USER_TOKEN, processo);

        assertThat(response).isNotNull();
        assertThat(response.getCodigo()).isNotNull();
        assertThat(response.getCodigo()).isGreaterThan(0L);
        System.out.println("Codigo processo inserido: " + response.getCodigo());
    }

    @Test
    public void getProcessosTransferenciaVeiculoListagem() throws ProLogException {
        final List<ProcessoTransferenciaVeiculoListagem> processos =
                service.getProcessosTransferenciaVeiculoListagem(
                        Collections.singletonList(5L),
                        Collections.singletonList(103L),
                        "2019-01-01",
                        "2019-05-30");
    }

    @Test
    public void getProcessoTransferenciaVeiculoVisualizacao() throws ProLogException {
        final ProcessoTransferenciaVeiculoVisualizacao processoTransferenciaVeiculoVisualizacao =
                service.getProcessoTransferenciaVeiculoVisualizacao(2L);

        System.out.println(processoTransferenciaVeiculoVisualizacao);
    }

    @Test
    public void getDetalhesVeiculoTransferido() throws ProLogException {
        final DetalhesVeiculoTransferido detalhesVeiculoTransferido =
                service.getDetalhesVeiculoTransferido(1L, 3032L);

        System.out.println(detalhesVeiculoTransferido);
    }

    @NotNull
    private ProcessoTransferenciaVeiculoRealizacao convertTo(@NotNull final List<Veiculo> veiculos) {
        final List<Long> veiculosTransferencia = new ArrayList<>();
        for (final Veiculo veiculo : veiculos) {
            veiculosTransferencia.add(veiculo.getCodigo());
        }

        return new ProcessoTransferenciaVeiculoRealizacao(
                3L,
                5L,
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
