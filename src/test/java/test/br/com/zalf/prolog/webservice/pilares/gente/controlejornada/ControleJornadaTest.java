package test.br.com.zalf.prolog.webservice.pilares.gente.controlejornada;

import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.commons.util.date.PrologDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.gente.controlejornada.ControleJornadaService;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.IntervaloMarcacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.ResponseIntervalo;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoInicioFim;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created on 15/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ControleJornadaTest extends BaseTest {

    private final String TOKEN_MARCACAO = "R43VIDK2G3CJ9DU68454G8SG0A";
    private final Long VERSAO_DADOS = 27L;
    private final Long TIPO_INTERVALO_REFEICAO = 15L;
    private final Long TIPO_INTERVALO_HOSPEDAGEM = 18L;
    private final Long TIPO_INTERVALO_DESCANSO = 19L;
    private final Long TIPO_INTERVALO_JORNADA = 85L;
    private final Long TIPO_INTERVALO_INTERVALO = 86L;
    private final Long CPF_COLABORADOR_ZALF = 3383283194L;
    private Date DATA_NASCIMENTO_COLABORADOR_ZALF;
    private ControleJornadaService service;

    @Override
    public void initialize() throws Throwable {
        service = new ControleJornadaService();
        DATA_NASCIMENTO_COLABORADOR_ZALF = new SimpleDateFormat("yyyy-MM-dd").parse("2018-02-26");
    }

    @Test
    public void testFolhaPontoJornadaRelatorio() throws Throwable {
        cenarioTeste1();
        cenarioTeste2();
        cenarioTeste3();
        cenarioTeste4();
        cenarioTeste5();
        cenarioTeste6();
        cenarioTeste7();
        cenarioTeste8();
        cenarioTeste9();
        cenarioTeste10();
        cenarioTeste11();
        cenarioTeste12();
        cenarioTeste13();
        cenarioTeste14();
    }

    /**
     * Este cenário contempla o caso mais clássico, onde tudo são flores e tudo acontece como deve acontecer.
     * Simulamos uma marcação de JORNADA com outras 3 marcações dentro.
     * <p>
     * Todas as marcações são no mesmo dia (2019-01-10), e nenhuma marcação se sobrepõem a outra.
     *
     * @throws ProLogException Em caso de erro
     */
    private void cenarioTeste1() throws ProLogException {
        // MARCACAO DE JORNADA
        IntervaloMarcacao marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-10T06:15:00"));
        Long codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-10T18:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-10T09:15:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-10T09:45:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE ALMOCO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-10T12:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-10T13:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE INTERVALO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_INTERVALO,
                PrologDateParser.toLocalDateTime("2019-01-10T15:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_INTERVALO,
                PrologDateParser.toLocalDateTime("2019-01-10T15:45:00"));
        insertMarcacaoIntervalo(marcacao);
    }

    /**
     * Para este cenário buscamos adicionar uma quantidade maior de marcações dentro da jornada,
     * para forçar uma quebra de linhas no relatório.
     * <p>
     * Simulamos uma marcação de JORNADA com outras 3 marcações dentro.
     * <p>
     * Todas as marcações são no mesmo dia (2019-01-11), e nenhuma marcação se sobrepõem a outra.
     *
     * @throws ProLogException Em caso de erro
     */
    private void cenarioTeste2() throws ProLogException {
        // MARCACAO DE JORNADA
        IntervaloMarcacao marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-11T06:15:00"));
        Long codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-11T18:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-11T09:15:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-11T09:45:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE ALMOCO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-11T12:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-11T13:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE INTERVALO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_INTERVALO,
                PrologDateParser.toLocalDateTime("2019-01-11T15:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_INTERVALO,
                PrologDateParser.toLocalDateTime("2019-01-11T15:45:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE INTERVALO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_INTERVALO,
                PrologDateParser.toLocalDateTime("2019-01-11T14:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_INTERVALO,
                PrologDateParser.toLocalDateTime("2019-01-11T14:45:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-11T16:15:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-11T16:45:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-11T17:15:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-11T17:45:00"));
        insertMarcacaoIntervalo(marcacao);
    }

    /**
     * Neste cenário buscamos adicionar marcações que foram realizadas fora de uma JORNADA,
     * para validar o comportamento da geração de relatório.
     * <p>
     * Simulamos uma marcação de JORNADA com apenas uma marcações dentro. Adicionamos outras
     * duas marcações externas à JORNADA para validar o funcionamento.
     * <p>
     * Todas as marcações são no mesmo dia (2019-01-12), e nenhuma marcação se sobrepõem a outra.
     *
     * @throws ProLogException Casos de erro
     */
    private void cenarioTeste3() throws ProLogException {
        // MARCACAO DE JORNADA
        IntervaloMarcacao marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-12T06:15:00"));
        Long codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-12T14:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE ALMOCO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-12T12:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-12T13:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE HOSPEDAGEM
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_HOSPEDAGEM,
                PrologDateParser.toLocalDateTime("2019-01-12T19:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_HOSPEDAGEM,
                PrologDateParser.toLocalDateTime("2019-01-12T20:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-12T15:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-12T17:00:00"));
        insertMarcacaoIntervalo(marcacao);
    }

    /**
     * O cenário 4 trata de um caso de esquecimento de finalização de JORNADA. Assim, simulamos
     * a marcação de uma marcação de INICIO de JORNADA e outras duas marcações que ficariam dentro
     * dessa JORNADA.
     * <p>
     * Todas as marcações são no mesmo dia (2019-01-13), e nenhuma marcação se sobrepõem a outra.
     *
     * @throws ProLogException Em caso de erro
     */
    private void cenarioTeste4() throws ProLogException {
        // MARCACAO DE JORNADA
        IntervaloMarcacao marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-13T06:15:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE ALMOCO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-13T12:00:00"));
        Long codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-13T13:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-13T15:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-13T17:00:00"));
        insertMarcacaoIntervalo(marcacao);
    }

    /**
     * Neste cenário exploramos um caso em que a JORNADA ultrapassa um dia e ainda possui outras
     * marcações internamente.
     * <p>
     * Obejtivo é validar o comportamento na geração do relatório.
     * <p>
     * As marcações são referentes ao dia (2019-01-14) e (2019-01-15), mas nenhuma marcação se sobrepõem a outra.
     *
     * @throws ProLogException Se algum erro acontecer
     */
    private void cenarioTeste5() throws ProLogException {
        // MARCACAO DE JORNADA
        IntervaloMarcacao marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-14T06:15:00"));
        Long codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-15T18:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-14T09:15:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-14T09:45:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE ALMOCO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-15T12:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-15T13:00:00"));
        insertMarcacaoIntervalo(marcacao);
    }

    /**
     * Este cenário tem por objetivo válidar a geração do relatório de folha de Ponto de Jornada
     * para o caso em que o colaborador tenha marcado mais de uma jornada no mesmo dia.
     * <p>
     * Simulamos então neste dia duas JORNADAS cada uma com uma marcação interna. Nenhuma das marcações
     * deste dia sobrepõem outra.
     * <p>
     * Todas as marcações são no mesmo dia (2019-01-16).
     *
     * @throws ProLogException Em caso de erro
     */
    private void cenarioTeste6() throws ProLogException {
        // MARCACAO DE JORNADA
        IntervaloMarcacao marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-16T06:15:00"));
        Long codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-16T11:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-16T09:15:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-16T09:45:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE JORNADA
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-16T12:30:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-16T17:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-16T14:15:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-16T14:45:00"));
        insertMarcacaoIntervalo(marcacao);
    }

    /**
     * Através do cenário 7 tentaremos validar a geração do relatório para casos em que exista
     * mais de uma marcação de JORNADA no mesmo dia, onde uma delas não é completa.
     * <p>
     * Simulamos neste caso uma marcação de JORNADA sem fim e que possui uma marcação internamente,
     * adicionamos neste mesmo dia outra marcação de JORNADA mas esta com INICIO e FIM contendo
     * uma marcação interna também
     * <p>
     * Todas as marcações são no mesmo dia (2019-01-17).
     *
     * @throws ProLogException Se algum erro acontecer
     */
    private void cenarioTeste7() throws ProLogException {
        // MARCACAO DE JORNADA
        IntervaloMarcacao marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-17T06:15:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-17T09:15:00"));
        Long codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-17T09:45:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE JORNADA
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-17T12:30:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-17T17:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-17T14:15:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-17T14:45:00"));
        insertMarcacaoIntervalo(marcacao);
    }

    /**
     * Este cenário é simplesmente para validar o caso em que marcações inciam ou terminam
     * fora de uma JORNADA, porém têm parte da sua duração dentro dela.
     * <p>
     * Para simular este caso criamos uma marcação com INICIO fora e FIM dentro da JORNADA,
     * outra marcação com INICIO dentro e FIM fora da JORNADA e é claro, a marcação de JORNADA.
     * <p>
     * Todas as marcações são no mesmo dia (2019-01-18).
     *
     * @throws ProLogException Em caso de erro.
     */
    private void cenarioTeste8() throws ProLogException {
        // MARCACAO DE JORNADA
        IntervaloMarcacao marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-18T12:30:00"));
        Long codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-18T17:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-18T11:15:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-18T12:45:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-18T16:15:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-18T17:45:00"));
        insertMarcacaoIntervalo(marcacao);
    }

    /**
     * O objetivo deste cenário é testar e validar a criação do relatório de folha de ponto de Jornada
     * em um caso especificamente complexo.
     *
     * Este cenário possui uma JORNADA que dura dois dias, porém no dia de finalização da primeira
     * JORNADA existe outra jornada marcada com INÍCIO e FIM. Além das marcações de jornadas também
     * existem marcações internas e externas à essas jornadas.
     *
     * As marcações são referentes ao dia (2019-01-19) e (2019-01-02).
     *
     * @throws ProLogException Em caso de erro.
     */
    private void cenarioTeste9() throws ProLogException {
        // MARCACAO DE JORNADA
        IntervaloMarcacao marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-19T06:15:00"));
        Long codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-20T13:15:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE REFEICAO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-19T12:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-19T13:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE HOSPEDAGEM
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_HOSPEDAGEM,
                PrologDateParser.toLocalDateTime("2019-01-19T22:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_HOSPEDAGEM,
                PrologDateParser.toLocalDateTime("2019-01-20T05:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE REFEICAO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-20T12:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-20T13:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE JORNADA
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-20T14:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-20T18:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-20T15:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-20T16:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE INTERVALO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_INTERVALO,
                PrologDateParser.toLocalDateTime("2019-01-20T18:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_INTERVALO,
                PrologDateParser.toLocalDateTime("2019-01-20T20:00:00"));
        insertMarcacaoIntervalo(marcacao);
    }

    /**
     * Com este cenário mapeamos o caso de marcações que estão dentro de uma jornada que dura mais de um dia.
     * Este cenário foi criado para facilitar o teste da busca da marcações dentro de uma jornada,
     * porém fora do range de filtragem. Por exemplo:
     *
     * Ao realizar a filtragem pelo dia 22, será buscado a jornada do dia 22/01 até o dia 25/01,
     * juntamente com todas as marcações com INICIO e FIM dentro do range da marcação de JORNADA.
     * Mas as marcações que possuem apenas início ou apenas fim dentro do período da jornada não
     * deverão ser trazidas pelo relatório.
     *
     * @throws ProLogException Em caso de erro.
     */
    private void cenarioTeste10() throws ProLogException {
        // MARCACAO DE JORNADA
        IntervaloMarcacao marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-22T06:15:00"));
        Long codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-24T18:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-21T22:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-22T07:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE REFEIÇÃO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-22T12:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-22T13:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE HOSPEDAGEM
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_HOSPEDAGEM,
                PrologDateParser.toLocalDateTime("2019-01-22T20:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_HOSPEDAGEM,
                PrologDateParser.toLocalDateTime("2019-01-23T05:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-24T22:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-25T07:00:00"));
        insertMarcacaoIntervalo(marcacao);
    }


    /**
     * Cenário criado para mapear o caso de um marcação ter INÍCIO em uma JORNADA e FIM em outra JORNADA.
     *
     * O comportamento esperado para o método neste cenário é que a marcação que tem partes em uma
     * Jornada e partes noutra jornada, esteja vinculada sempre à Jornada cujo o início está incluso.
     *
     * @throws ProLogException Caso algum erro aconteça.
     */
    private void cenarioTeste11() throws ProLogException {
        // MARCACAO DE JORNADA
        IntervaloMarcacao marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-25T06:15:00"));
        Long codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-26T07:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-25T22:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-26T14:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE JORNADA
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-26T12:00:00"));
        codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-26T18:00:00"));
        insertMarcacaoIntervalo(marcacao);
    }

    /**
     * O cenário 12 trata de marcações realizadas que não são completas, ou seja, não possui
     * INICIO ou não possui FIM.
     *
     * @throws ProLogException Em caso de erro.
     */
    private void cenarioTeste12() throws ProLogException {
        // MARCACAO DE JORNADA
        IntervaloMarcacao marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-27T06:15:00"));
        final Long codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-27T18:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE INICIO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-27T09:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE INICIO DE REFEICAO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-27T12:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE INICIO DE INTERVALO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_INTERVALO,
                PrologDateParser.toLocalDateTime("2019-01-27T19:00:00"));
        insertMarcacaoIntervalo(marcacao);
    }

    /**
     * Este cenário é semelhante ao {@code cenarioTeste12 cenário 12}, mas neste cenário a
     * JORNADA dura mais de um dia e contém marcações apenas com apenas INICIO.
     *
     * @see ControleJornadaTest#cenarioTeste12()
     * @throws ProLogException Em caso de erro.
     */
    private void cenarioTeste13() throws ProLogException {
        // MARCACAO DE JORNADA
        IntervaloMarcacao marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-28T06:15:00"));
        final Long codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_JORNADA,
                PrologDateParser.toLocalDateTime("2019-01-30T18:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE INICIO DE DESCANSO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-28T09:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE INICIO DE REFEICAO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-29T12:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE INICIO DE INTERVALO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_INTERVALO,
                PrologDateParser.toLocalDateTime("2019-01-30T15:00:00"));
        insertMarcacaoIntervalo(marcacao);
    }

    /**
     * Cenário criado para testar o caso em que um dia não possui nenhuma JORNADA.
     * Para este dia o relatório deverá mostrar apenas marcações avulsas.
     *
     * @throws ProLogException Em caso de erros.
     */
    private void cenarioTeste14() throws ProLogException {
        // MARCACAO DE INICIO DE DESCANSO
        IntervaloMarcacao marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-31T09:00:00"));
        final Long codMarcacaoInserida = insertMarcacaoIntervalo(marcacao);

        marcacao = getIntervaloMarcacaoFim(
                codMarcacaoInserida,
                TIPO_INTERVALO_DESCANSO,
                PrologDateParser.toLocalDateTime("2019-01-31T10:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE INICIO DE REFEICAO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_REFEICAO,
                PrologDateParser.toLocalDateTime("2019-01-31T12:00:00"));
        insertMarcacaoIntervalo(marcacao);

        // MARCACAO DE INICIO DE INTERVALO
        marcacao = getIntervaloMarcacaoInicio(
                TIPO_INTERVALO_INTERVALO,
                PrologDateParser.toLocalDateTime("2019-01-31T15:00:00"));
        insertMarcacaoIntervalo(marcacao);
    }

    @NotNull
    private Long insertMarcacaoIntervalo(final IntervaloMarcacao marcacao) throws ProLogException {
        final ResponseIntervalo responseIntervalo =
                service.insertMarcacaoIntervalo(TOKEN_MARCACAO, VERSAO_DADOS, marcacao, null);

        Assert.assertNotNull(responseIntervalo);
        Assert.assertEquals(responseIntervalo.getStatus(), ResponseIntervalo.OK);
        Assert.assertNotNull(responseIntervalo.getCodigo());
        Assert.assertTrue(responseIntervalo.getCodigo() > 0);

        return responseIntervalo.getCodigo();
    }

    @NotNull
    private IntervaloMarcacao getIntervaloMarcacaoInicio(@NotNull final Long codTipoIntervalo,
                                                         @NotNull final LocalDateTime dataHoraMarcacao) {
        final IntervaloMarcacao marcacao = new IntervaloMarcacao();
        marcacao.setCodTipoIntervalo(codTipoIntervalo);
        marcacao.setCodUnidade(5L);
        marcacao.setCpfColaborador(CPF_COLABORADOR_ZALF);
        marcacao.setDataNascimentoColaborador(DATA_NASCIMENTO_COLABORADOR_ZALF);
        marcacao.setDataHoraMaracao(dataHoraMarcacao);
        marcacao.setFonteDataHora(FonteDataHora.SERVIDOR);
        marcacao.setTipoMarcacaoIntervalo(TipoInicioFim.MARCACAO_INICIO);
        return marcacao;
    }

    @NotNull
    private IntervaloMarcacao getIntervaloMarcacaoFim(@NotNull final Long codMarcacaoInserida,
                                                      @NotNull final Long codTipoIntervalo,
                                                      @NotNull final LocalDateTime dataHoraMarcacao) {
        final IntervaloMarcacao marcacao = new IntervaloMarcacao();
        marcacao.setCodTipoIntervalo(codTipoIntervalo);
        marcacao.setCodUnidade(5L);
        marcacao.setCpfColaborador(CPF_COLABORADOR_ZALF);
        marcacao.setDataNascimentoColaborador(DATA_NASCIMENTO_COLABORADOR_ZALF);
        marcacao.setDataHoraMaracao(dataHoraMarcacao);
        marcacao.setFonteDataHora(FonteDataHora.SERVIDOR);
        marcacao.setTipoMarcacaoIntervalo(TipoInicioFim.MARCACAO_FIM);
        marcacao.setCodMarcacaoVinculada(codMarcacaoInserida);
        return marcacao;
    }
}
