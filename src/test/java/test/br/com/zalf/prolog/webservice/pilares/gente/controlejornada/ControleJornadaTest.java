package test.br.com.zalf.prolog.webservice.pilares.gente.controlejornada;

import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.commons.util.datetime.PrologDateParser;
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
     * Este cen??rio contempla o caso mais cl??ssico, onde tudo s??o flores e tudo acontece como deve acontecer.
     * Simulamos uma marca????o de JORNADA com outras 3 marca????es dentro.
     * <p>
     * Todas as marca????es s??o no mesmo dia (2019-01-10), e nenhuma marca????o se sobrep??em a outra.
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
     * Para este cen??rio buscamos adicionar uma quantidade maior de marca????es dentro da jornada,
     * para for??ar uma quebra de linhas no relat??rio.
     * <p>
     * Simulamos uma marca????o de JORNADA com outras 3 marca????es dentro.
     * <p>
     * Todas as marca????es s??o no mesmo dia (2019-01-11), e nenhuma marca????o se sobrep??em a outra.
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
     * Neste cen??rio buscamos adicionar marca????es que foram realizadas fora de uma JORNADA,
     * para validar o comportamento da gera????o de relat??rio.
     * <p>
     * Simulamos uma marca????o de JORNADA com apenas uma marca????es dentro. Adicionamos outras
     * duas marca????es externas ?? JORNADA para validar o funcionamento.
     * <p>
     * Todas as marca????es s??o no mesmo dia (2019-01-12), e nenhuma marca????o se sobrep??em a outra.
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
     * O cen??rio 4 trata de um caso de esquecimento de finaliza????o de JORNADA. Assim, simulamos
     * a marca????o de uma marca????o de INICIO de JORNADA e outras duas marca????es que ficariam dentro
     * dessa JORNADA.
     * <p>
     * Todas as marca????es s??o no mesmo dia (2019-01-13), e nenhuma marca????o se sobrep??em a outra.
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
     * Neste cen??rio exploramos um caso em que a JORNADA ultrapassa um dia e ainda possui outras
     * marca????es internamente.
     * <p>
     * Obejtivo ?? validar o comportamento na gera????o do relat??rio.
     * <p>
     * As marca????es s??o referentes ao dia (2019-01-14) e (2019-01-15), mas nenhuma marca????o se sobrep??em a outra.
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
     * Este cen??rio tem por objetivo v??lidar a gera????o do relat??rio de folha de Ponto de Jornada
     * para o caso em que o colaborador tenha marcado mais de uma jornada no mesmo dia.
     * <p>
     * Simulamos ent??o neste dia duas JORNADAS cada uma com uma marca????o interna. Nenhuma das marca????es
     * deste dia sobrep??em outra.
     * <p>
     * Todas as marca????es s??o no mesmo dia (2019-01-16).
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
     * Atrav??s do cen??rio 7 tentaremos validar a gera????o do relat??rio para casos em que exista
     * mais de uma marca????o de JORNADA no mesmo dia, onde uma delas n??o ?? completa.
     * <p>
     * Simulamos neste caso uma marca????o de JORNADA sem fim e que possui uma marca????o internamente,
     * adicionamos neste mesmo dia outra marca????o de JORNADA mas esta com INICIO e FIM contendo
     * uma marca????o interna tamb??m
     * <p>
     * Todas as marca????es s??o no mesmo dia (2019-01-17).
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
     * Este cen??rio ?? simplesmente para validar o caso em que marca????es inciam ou terminam
     * fora de uma JORNADA, por??m t??m parte da sua dura????o dentro dela.
     * <p>
     * Para simular este caso criamos uma marca????o com INICIO fora e FIM dentro da JORNADA,
     * outra marca????o com INICIO dentro e FIM fora da JORNADA e ?? claro, a marca????o de JORNADA.
     * <p>
     * Todas as marca????es s??o no mesmo dia (2019-01-18).
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
     * O objetivo deste cen??rio ?? testar e validar a cria????o do relat??rio de folha de ponto de Jornada
     * em um caso especificamente complexo.
     *
     * Este cen??rio possui uma JORNADA que dura dois dias, por??m no dia de finaliza????o da primeira
     * JORNADA existe outra jornada marcada com IN??CIO e FIM. Al??m das marca????es de jornadas tamb??m
     * existem marca????es internas e externas ?? essas jornadas.
     *
     * As marca????es s??o referentes ao dia (2019-01-19) e (2019-01-02).
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
     * Com este cen??rio mapeamos o caso de marca????es que est??o dentro de uma jornada que dura mais de um dia.
     * Este cen??rio foi criado para facilitar o teste da busca da marca????es dentro de uma jornada,
     * por??m fora do range de filtragem. Por exemplo:
     *
     * Ao realizar a filtragem pelo dia 22, ser?? buscado a jornada do dia 22/01 at?? o dia 25/01,
     * juntamente com todas as marca????es com INICIO e FIM dentro do range da marca????o de JORNADA.
     * Mas as marca????es que possuem apenas in??cio ou apenas fim dentro do per??odo da jornada n??o
     * dever??o ser trazidas pelo relat??rio.
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

        // MARCACAO DE REFEI????O
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
     * Cen??rio criado para mapear o caso de um marca????o ter IN??CIO em uma JORNADA e FIM em outra JORNADA.
     *
     * O comportamento esperado para o m??todo neste cen??rio ?? que a marca????o que tem partes em uma
     * Jornada e partes noutra jornada, esteja vinculada sempre ?? Jornada cujo o in??cio est?? incluso.
     *
     * @throws ProLogException Caso algum erro aconte??a.
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
     * O cen??rio 12 trata de marca????es realizadas que n??o s??o completas, ou seja, n??o possui
     * INICIO ou n??o possui FIM.
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
     * Este cen??rio ?? semelhante ao {@code cenarioTeste12 cen??rio 12}, mas neste cen??rio a
     * JORNADA dura mais de um dia e cont??m marca????es apenas com apenas INICIO.
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
     * Cen??rio criado para testar o caso em que um dia n??o possui nenhuma JORNADA.
     * Para este dia o relat??rio dever?? mostrar apenas marca????es avulsas.
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
