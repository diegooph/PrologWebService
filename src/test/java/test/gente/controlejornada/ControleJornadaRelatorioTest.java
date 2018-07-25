package test.gente.controlejornada;

import br.com.zalf.prolog.webservice.gente.controleintervalo.ControleIntervaloService;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.FonteDataHora;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.IntervaloMarcacao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoIntervalo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoMarcacaoIntervalo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios.ControleIntervaloRelatorioService;
import br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios.FolhaPontoRelatorio;
import br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios.FolhaPontoTipoIntervalo;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import test.BaseTest;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoMarcacaoIntervalo.MARCACAO_FIM;
import static br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoMarcacaoIntervalo.MARCACAO_INICIO;
import static org.junit.Assert.*;

/**
 * Created on 24/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class ControleJornadaRelatorioTest extends BaseTest {
    private ControleIntervaloRelatorioService service;
    private static final Long COD_UNIDADE = 5L;
    private static final Long CPF_COLABORADOR = 3383283194L;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String DATA_NASCIMENTO_COLABORADOR = "2018-02-26";
    private static final String TODOS_TIPOS_INTERVALOS = "%";
    private static final String TODOS_COLABORADORES = "%";
    private static final String DATA_HORA_INICIO = "2018-01-01T10:00:00";
    private static final String DATA_HORA_FIM = "2018-01-06T10:00:00";

    @Override
    public void initialize() {
        service = new ControleIntervaloRelatorioService();
    }

    @Test
    public void testFolhaPontoRelatorio() throws Throwable {
        final List<FolhaPontoRelatorio> folhaPontoRelatorio = service.getFolhaPontoRelatorio(
                COD_UNIDADE,
                TODOS_TIPOS_INTERVALOS,
                TODOS_COLABORADORES,
                DATA_HORA_INICIO,
                DATA_HORA_FIM);
        assertNotNull(folhaPontoRelatorio);
        assertFalse(folhaPontoRelatorio.isEmpty());
    }

    @Test
    public void testCalculoHorasPorTipoMarcacao() throws Throwable {
        final ControleIntervaloService intervaloService = new ControleIntervaloService();

        // Escolhemos o tipo de intervalo que iremos realizar.
        final List<TipoIntervalo> tiposIntervalos = intervaloService.getTiposIntervalos(COD_UNIDADE, false);
        assertNotNull(tiposIntervalos);
        assertFalse(tiposIntervalos.isEmpty());
        final TipoIntervalo tipoIntervalo = tiposIntervalos.get(0);
        assertNotNull(tipoIntervalo);

        final String inicioFiltro = "2018-01-05T00:00:00";
        final String fimFiltro = "2018-01-10T23:59:00";
        // Precisamos inserir marcações que cubram 4 diferentes casos:
        // 1 - Uma marcação com início ANTES do período do filtro e fim DENTRO do período.
        // 2 - Uma marcação com início DENTRO do período do filtro e fim DENTRO do período.
        // 3 - Uma marcação com início DENTRO do período do filtro e fim DEPOIS do período.
        // 4 - Uma marcação com início ANTES do período do filtro e fim DEPOIS do período.

        IntervaloMarcacao marcacaoInicio, marcacaoFim;
        // Caso 1) ANTES -> DENTRO
        // Total: 34 horas ou 2040 minutos (fim marcação - inicio filtro)
        marcacaoInicio = createIntervaloMarcacao(tipoIntervalo.getCodigo(), LocalDateTime.of(2018, 1, 4, 10, 0, 0), MARCACAO_INICIO);
        marcacaoFim = createIntervaloMarcacao(tipoIntervalo.getCodigo(), LocalDateTime.of(2018, 1, 6, 10, 0, 0), MARCACAO_FIM);
        intervaloService.insertMarcacaoIntervalo(1, marcacaoInicio);
        intervaloService.insertMarcacaoIntervalo(1, marcacaoFim);

        // Caso 2) DENTRO -> DENTRO
        // Total: 702 minutos (fim marcação - início marcação)
        marcacaoInicio = createIntervaloMarcacao(tipoIntervalo.getCodigo(), LocalDateTime.of(2018, 1, 6, 10, 35, 0), MARCACAO_INICIO);
        marcacaoFim = createIntervaloMarcacao(tipoIntervalo.getCodigo(), LocalDateTime.of(2018, 1, 6, 22, 17, 0), MARCACAO_FIM);
        intervaloService.insertMarcacaoIntervalo(1, marcacaoInicio);
        intervaloService.insertMarcacaoIntervalo(1, marcacaoFim);

        // Caso 3) DENTRO -> DEPOIS
        // Total: 4699 minutos (fim filtro - início marcação)
        marcacaoInicio = createIntervaloMarcacao(tipoIntervalo.getCodigo(), LocalDateTime.of(2018, 1, 7, 17, 40, 0), MARCACAO_INICIO);
        marcacaoFim = createIntervaloMarcacao(tipoIntervalo.getCodigo(), LocalDateTime.of(2018, 1, 11, 18, 0, 0), MARCACAO_FIM);
        intervaloService.insertMarcacaoIntervalo(1, marcacaoInicio);
        intervaloService.insertMarcacaoIntervalo(1, marcacaoFim);

        // Caso 4) ANTES -> DEPOIS
        // Total: 8639 minutos (fim filtro - início filtro)
        marcacaoInicio = createIntervaloMarcacao(tipoIntervalo.getCodigo(), LocalDateTime.of(2018, 1, 5, 9, 0, 0), MARCACAO_INICIO);
        marcacaoFim = createIntervaloMarcacao(tipoIntervalo.getCodigo(), LocalDateTime.of(2018, 1, 10, 23, 59, 1), MARCACAO_FIM);
        intervaloService.insertMarcacaoIntervalo(1, marcacaoInicio);
        intervaloService.insertMarcacaoIntervalo(1, marcacaoFim);

        final List<FolhaPontoRelatorio> relatorios = service.getFolhaPontoRelatorio(
                COD_UNIDADE,
                TODOS_TIPOS_INTERVALOS,
                String.valueOf(CPF_COLABORADOR),
                inicioFiltro,
                fimFiltro);
        assertNotNull(relatorios);
        assertEquals(1, relatorios.size());
        final FolhaPontoRelatorio folhaPontoRelatorio = relatorios.get(0);
        assertNotNull(folhaPontoRelatorio);
        assertEquals(1, folhaPontoRelatorio.getTiposIntervalosMarcados().size());
        final List<FolhaPontoTipoIntervalo> tiposMarcados = new ArrayList<>(folhaPontoRelatorio.getTiposIntervalosMarcados());
        final FolhaPontoTipoIntervalo tipoMarcado = tiposMarcados.get(0);
        assertNotNull(tipoMarcado);
        assertEquals(tipoIntervalo.getCodigo(), tipoMarcado.getCodigo());

        // Tempo total: 2040 + 702 + 4699 = 7441.
        assertEquals(7441, tipoMarcado.getTempoTotalTipoIntervalo().toMinutes());
    }

    @Test
    public void testCalculoHorasPorTipoMarcacao_InicoAntesFiltroFimDepoisFiltro() throws Throwable {
        final ControleIntervaloService intervaloService = new ControleIntervaloService();

        // Escolhemos o tipo de intervalo que iremos realizar.
        final List<TipoIntervalo> tiposIntervalos = intervaloService.getTiposIntervalos(COD_UNIDADE, false);
        assertNotNull(tiposIntervalos);
        assertFalse(tiposIntervalos.isEmpty());
        final TipoIntervalo tipoIntervalo = tiposIntervalos.get(0);
        assertNotNull(tipoIntervalo);

        final String inicioFiltro = "2018-01-10T00:00:00";
        final String fimFiltro = "2018-01-11T23:59:00";
        // Esse método cobre um caso específico:
        // 4 - Uma marcação com início ANTES do período do filtro e fim DEPOIS do período.

        IntervaloMarcacao marcacaoInicio, marcacaoFim;
        // Caso 4) ANTES -> DEPOIS
        // Total: 2879 minutos (fim filtro - início filtro)
        marcacaoInicio = createIntervaloMarcacao(tipoIntervalo.getCodigo(), LocalDateTime.of(2018, 1, 5, 21, 30, 30), MARCACAO_INICIO);
        marcacaoFim = createIntervaloMarcacao(tipoIntervalo.getCodigo(), LocalDateTime.of(2018, 1, 12, 23, 59, 1), MARCACAO_FIM);
        intervaloService.insertMarcacaoIntervalo(1, marcacaoInicio);
        intervaloService.insertMarcacaoIntervalo(1, marcacaoFim);

        final List<FolhaPontoRelatorio> relatorios = service.getFolhaPontoRelatorio(
                COD_UNIDADE,
                TODOS_TIPOS_INTERVALOS,
                String.valueOf(CPF_COLABORADOR),
                inicioFiltro,
                fimFiltro);
        assertNotNull(relatorios);
        assertEquals(1, relatorios.size());
        final FolhaPontoRelatorio folhaPontoRelatorio = relatorios.get(0);
        assertNotNull(folhaPontoRelatorio);
        assertEquals(1, folhaPontoRelatorio.getTiposIntervalosMarcados().size());
        final List<FolhaPontoTipoIntervalo> tiposMarcados = new ArrayList<>(folhaPontoRelatorio.getTiposIntervalosMarcados());
        final FolhaPontoTipoIntervalo tipoMarcado = tiposMarcados.get(0);
        assertNotNull(tipoMarcado);
        assertEquals(tipoIntervalo.getCodigo(), tipoMarcado.getCodigo());

        // Tempo total: 2879.
        assertEquals(2879, tipoMarcado.getTempoTotalTipoIntervalo().toMinutes());
    }

    @Test
    public void testCalculoHorasNoturnasPorTipoMarcacao() throws Throwable {
        final List<FolhaPontoRelatorio> folhaPontoRelatorio = service.getFolhaPontoRelatorio(
                COD_UNIDADE,
                TODOS_TIPOS_INTERVALOS,
                TODOS_COLABORADORES,
                DATA_HORA_INICIO,
                DATA_HORA_FIM);
        assertNotNull(folhaPontoRelatorio);
        assertFalse(folhaPontoRelatorio.isEmpty());
    }

    private IntervaloMarcacao createIntervaloMarcacao(@NotNull final Long codTipoIntervalo,
                                                      @NotNull final LocalDateTime dataHoraMarcacao,
                                                      @NotNull final TipoMarcacaoIntervalo tipoMarcacaoIntervalo)
            throws Throwable {
        final IntervaloMarcacao intervaloMarcacao = new IntervaloMarcacao();
        intervaloMarcacao.setCodUnidade(COD_UNIDADE);
        intervaloMarcacao.setCpfColaborador(CPF_COLABORADOR);
        intervaloMarcacao.setDataNascimentoColaborador(DATE_FORMAT.parse(DATA_NASCIMENTO_COLABORADOR));
        intervaloMarcacao.setCodTipoIntervalo(codTipoIntervalo);
        intervaloMarcacao.setDataHoraMaracao(dataHoraMarcacao);
        intervaloMarcacao.setFonteDataHora(FonteDataHora.REDE_CELULAR);
        intervaloMarcacao.setTipoMarcacaoIntervalo(tipoMarcacaoIntervalo);
        return intervaloMarcacao;
    }
}