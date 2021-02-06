package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios;

import br.com.zalf.prolog.webservice.commons.util.FormatUtils;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.FolhaPontoDia;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.FolhaPontoIntervalo;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.FolhaPontoRelatorio;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.FolhaPontoTipoIntervalo;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada.FolhaPontoJornada;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada.FolhaPontoJornadaDia;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada.FolhaPontoJornadaRelatorio;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada.FolhaPontoMarcacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.FormulaCalculoJornada;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.*;

/**
 * Created on 25/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class ControleJornadaRelatorioConverter {
    private static final String TAG = ControleJornadaRelatorioConverter.class.getSimpleName();

    private ControleJornadaRelatorioConverter() {
        throw new IllegalStateException(ControleJornadaRelatorioConverter.class.getSimpleName()
                + " cannot be instantiated!");
    }

    @NotNull
    static List<FolhaPontoRelatorio> createFolhaPontoRelatorio(
            @NotNull final ResultSet rSet,
            @NotNull final List<TipoMarcacao> tiposIntervalos,
            @NotNull final ZoneId zoneIdUnidade) throws Throwable {
        final LocalDateTime dataHoraGeracaoRelatorioUtc = Now.getLocalDateTimeUtc();
        final LocalDateTime dataHoraGeracaoRelatorioZoned = dataHoraGeracaoRelatorioUtc
                .atZone(ZoneOffset.UTC)
                .withZoneSameInstant(zoneIdUnidade)
                .toLocalDateTime();
        final List<FolhaPontoRelatorio> relatorios = new ArrayList<>();
        final Map<Long, TipoMarcacao> tiposIntervalosUnidade = tiposMarcacoesToMap(tiposIntervalos);
        Long cpfAnterior = null;
        String nomeAnterior = null;
        LocalDate diaAnterior = null;
        List<FolhaPontoDia> dias = new ArrayList<>();
        List<FolhaPontoIntervalo> intervalosDia = new ArrayList<>();
        while (rSet.next()) {
            final Long cpfAtual = rSet.getLong("CPF_COLABORADOR");
            final LocalDateTime dataHoraInicio = rSet.getObject("DATA_HORA_INICIO", LocalDateTime.class);
            // A data/hora de início pode ser nula ou a de fim, mas nunca ambas. Para utilizar o dia atual, devemos
            // priorizar a data/hora de início.
            final LocalDate diaAtual = dataHoraInicio != null
                    ? dataHoraInicio.toLocalDate()
                    : rSet.getObject("DATA_HORA_FIM", LocalDateTime.class).toLocalDate();

            // Se for na primeira iteração, devemos deixar dia e cpf anterior como sendo igual aos atuais.
            if (cpfAnterior == null) {
                cpfAnterior = cpfAtual;
            }
            if (diaAnterior == null) {
                diaAnterior = diaAtual;
            }

            if (!cpfAnterior.equals(cpfAtual)) {
                // Trocou de colaborador.
                Log.d(TAG, "Colaborador alterado. Anterior: " + cpfAnterior + " - Atual: " + cpfAtual);
                final FolhaPontoRelatorio folhaPontoRelatorio = createFolhaPontoRelatorio(cpfAnterior,
                        nomeAnterior, diaAnterior, dias, intervalosDia, dataHoraGeracaoRelatorioUtc, dataHoraGeracaoRelatorioZoned);
                folhaPontoRelatorio.calculaTempoEmCadaTipoIntervalo(tiposIntervalosUnidade, zoneIdUnidade);
                relatorios.add(folhaPontoRelatorio);
                dias = new ArrayList<>();
                intervalosDia = new ArrayList<>();
            } else {
                // Mesmo colaborador.
                Log.d(TAG, "Mesmo colaborador. Anterior: " + cpfAnterior + " - Atual: " + cpfAtual);
                if (!diaAnterior.equals(diaAtual)) {
                    // Trocou o dia.
                    Log.d(TAG, "Dia alterado. Anterior: " + diaAnterior + " - Atual: " + diaAtual);
                    dias.add(new FolhaPontoDia(diaAnterior, intervalosDia));
                    intervalosDia = new ArrayList<>();
                } else {
                    // Mesmo dia.
                    Log.d(TAG, "Mesmo dia. Anterior: " + diaAnterior + " - Atual: " + diaAtual);
                }
            }

            final LocalDateTime dataHoraFim = rSet.getObject("DATA_HORA_FIM", LocalDateTime.class);
            final Long codTipoIntervaloLong = rSet.getLong("COD_TIPO_INTERVALO");

            if (dataHoraInicio != null && dataHoraFim != null && dataHoraFim.isBefore(dataHoraInicio)) {
                throw new GenericException("Erro!\nA marcação do colaborador <b>"
                        + rSet.getString("NOME_COLABORADOR")
                        + "</b> possui fim antes do início, impossibilitando a geração do relatório.\n"
                        + "<b>Início: " + FormatUtils.toUserFriendlyDateTime(dataHoraInicio) + "</b>\n"
                        + "<b>Fim: " + FormatUtils.toUserFriendlyDateTime(dataHoraFim) + "</b>\n\n"
                        + "<a href=\"https://prologapp.movidesk.com/kb/pt-br/article/74192/relatorios-controle-de-jornada#folha-ponto\" target=\"_blank\">Clique aqui para mais informações</a>");
            }

            final FolhaPontoIntervalo intervalo = new FolhaPontoIntervalo(
                    dataHoraInicio,
                    dataHoraFim,
                    rSet.getObject("DATA_HORA_INICIO_UTC", LocalDateTime.class),
                    rSet.getObject("DATA_HORA_FIM_UTC", LocalDateTime.class),
                    codTipoIntervaloLong,
                    rSet.getLong("COD_TIPO_INTERVALO_POR_UNIDADE"),
                    rSet.getBoolean("FOI_AJUSTADO_INICIO"),
                    rSet.getBoolean("FOI_AJUSTADO_FIM"),
                    rSet.getBoolean("TROCOU_DIA"));
            intervalosDia.add(intervalo);

            cpfAnterior = cpfAtual;
            diaAnterior = diaAtual;
            nomeAnterior = rSet.getString("NOME_COLABORADOR");
        }
        if (diaAnterior != null) {
            final FolhaPontoRelatorio folhaPontoRelatorio = createFolhaPontoRelatorio(cpfAnterior,
                    nomeAnterior, diaAnterior, dias, intervalosDia, dataHoraGeracaoRelatorioUtc, dataHoraGeracaoRelatorioZoned);
            folhaPontoRelatorio.calculaTempoEmCadaTipoIntervalo(tiposIntervalosUnidade, zoneIdUnidade);
            relatorios.add(folhaPontoRelatorio);
        }
        return relatorios;
    }

    @NotNull
    static List<FolhaPontoJornadaRelatorio> createFolhaPontoJornadaRelatorio(
            @NotNull final ResultSet rSet,
            @NotNull final List<TipoMarcacao> tiposIntervalos,
            @NotNull final FormulaCalculoJornada formulaCalculoJornada,
            @NotNull final ZoneId zoneIdUnidade) throws Throwable {
        final LocalDateTime dataHoraGeracaoRelatorioZoned = Now.getLocalDateTimeUtc()
                .atZone(ZoneOffset.UTC)
                .withZoneSameInstant(zoneIdUnidade)
                .toLocalDateTime();
        final List<FolhaPontoJornadaRelatorio> relatorios = new ArrayList<>();

        // Objetos utilizados para montar o relatório.
        final Map<Long, TipoMarcacao> tiposMarcacoesUnidade = tiposMarcacoesToMap(tiposIntervalos);
        final Map<Long, FolhaPontoTipoIntervalo> tiposMarcacoesMarcados = new HashMap<>();
        List<FolhaPontoJornadaDia> marcacoesDia = new ArrayList<>();
        List<FolhaPontoJornada> jornadasDia = new ArrayList<>();
        FolhaPontoJornada jornada = null;
        List<FolhaPontoMarcacao> marcacoesForaJornada = new ArrayList<>();
        // Variáveis de controle de fluxo do algorítmo.
        String nomeAnterior = null, cpfAtual, cpfAnterior = null;
        LocalDate diaAtual, diaAnterior = null;
        Long codMarcacaoJornadaAnterior = null;
        while (rSet.next()) {
            diaAtual = rSet.getObject("DIA_BASE", LocalDate.class);
            if (diaAnterior == null) {
                diaAnterior = diaAtual;
            }
            cpfAtual = rSet.getString("CPF_COLABORADOR");
            if (cpfAnterior == null) {
                cpfAnterior = cpfAtual;
            }

            if (!cpfAnterior.equals(cpfAtual)) {
                // Trocou de colaborador.
                jornadasDia.add(jornada);
                marcacoesDia.add(new FolhaPontoJornadaDia(diaAnterior, jornadasDia, marcacoesForaJornada));
                final FolhaPontoJornadaRelatorio folhaPontoJornadaRelatorio = new FolhaPontoJornadaRelatorio(
                        cpfAnterior,
                        nomeAnterior,
                        marcacoesDia,
                        dataHoraGeracaoRelatorioZoned,
                        formulaCalculoJornada);
                folhaPontoJornadaRelatorio.setTiposMarcacoesMarcadas(new HashSet<>(tiposMarcacoesMarcados.values()));
                folhaPontoJornadaRelatorio.calculaTotaisHorasJornadasLiquidaBruta();
                relatorios.add(folhaPontoJornadaRelatorio);
                // Resetamos os objetos para o novo colaborador.
                tiposMarcacoesMarcados.clear();
                marcacoesDia = new ArrayList<>();
                jornadasDia = new ArrayList<>();
                jornada = null;
                marcacoesForaJornada = new ArrayList<>();
                codMarcacaoJornadaAnterior = null;
            } else {
                // Mesmo colaborador.
                if (!diaAnterior.equals(diaAtual)) {
                    // Trocou de dia.
                    jornadasDia.add(jornada);
                    marcacoesDia.add(new FolhaPontoJornadaDia(diaAnterior, jornadasDia, marcacoesForaJornada));
                    jornadasDia = new ArrayList<>();
                    jornada = null;
                    marcacoesForaJornada = new ArrayList<>();
                    codMarcacaoJornadaAnterior = null;
                }
            }
            final boolean tipoJornada = rSet.getBoolean("TIPO_JORNADA");
            final Long codMarcacaoJornada = rSet.getLong("COD_MARCACAO_JORNADA");

            if (tipoJornada) {
                // Se o código da jornada mudou, temos que adicionar a jornada anterior processada.
                if (codMarcacaoJornadaAnterior != null
                        && !codMarcacaoJornada.equals(codMarcacaoJornadaAnterior)) {
                    jornadasDia.add(jornada);
                }
                // Criamos uma Jornada.
                jornada = createFolhaPontoJornada(rSet);
                codMarcacaoJornadaAnterior = codMarcacaoJornada;

                // Se tipoJornada for false e o codMarcacaoJornada for > 0, significa que essa marcação não é
                // de jornada, mas está dentro de alguma. Esse caso lida com marcações completas e avulsas!
            } else if (codMarcacaoJornada > 0) {
                // Garante que se uma marcação pertence a uma jornada, a jornada já estará instanciada.
                if (jornada == null) {
                    throw new IllegalStateException("O objeto jornada deve ser instanciado!");
                }
                jornada.addMarcacaoToJornada(createFolhaPontoMarcacao(rSet));
            } else {
                // Marcação deste dia não pertence à nenhuma Jornada.
                marcacoesForaJornada.add(createFolhaPontoMarcacao(rSet));
            }

            // Para cada Marcação processada, atualizamos os cálculos de tempo.
            calculaTempoPorTipoMarcacao(rSet, tiposMarcacoesMarcados, tiposMarcacoesUnidade, codMarcacaoJornada);

            diaAnterior = diaAtual;
            cpfAnterior = cpfAtual;
            nomeAnterior = rSet.getString("NOME_COLABORADOR");
        }
        if (cpfAnterior != null) {
            if (jornada != null) {
                jornadasDia.add(jornada);
            }
            marcacoesDia.add(new FolhaPontoJornadaDia(diaAnterior, jornadasDia, marcacoesForaJornada));
            final FolhaPontoJornadaRelatorio folhaPontoJornadaRelatorio = new FolhaPontoJornadaRelatorio(
                    cpfAnterior,
                    nomeAnterior,
                    marcacoesDia,
                    dataHoraGeracaoRelatorioZoned,
                    formulaCalculoJornada);
            folhaPontoJornadaRelatorio.setTiposMarcacoesMarcadas(new HashSet<>(tiposMarcacoesMarcados.values()));
            folhaPontoJornadaRelatorio.calculaTotaisHorasJornadasLiquidaBruta();
            relatorios.add(folhaPontoJornadaRelatorio);
        }

        return relatorios;
    }

    private static void calculaTempoPorTipoMarcacao(
            @NotNull final ResultSet rSet,
            @NotNull final Map<Long, FolhaPontoTipoIntervalo> tiposMarcacoesMarcadas,
            @NotNull final Map<Long, TipoMarcacao> tiposMarcacoesUnidade,
            @Nullable final Long codMarcacaoJornada) throws SQLException {

        // Antes de realizar a soma nos totais, é verificado se a marcação é fora de jornada e consequentemente
        // não deve compor os totais.
        if (codMarcacaoJornada != null && codMarcacaoJornada > 0) {
            // Precisamos atualizar a lista de tipos de marcações marcadas.
            final Long codTipoMarcacao = rSet.getLong("COD_TIPO_INTERVALO");
            if (tiposMarcacoesMarcadas.get(codTipoMarcacao) == null) {
                // Se ainda não estiver mapeado, precisamos criar o tipo de marcação e somar os tempos
                final TipoMarcacao tipoMarcacao = tiposMarcacoesUnidade.get(codTipoMarcacao);
                tiposMarcacoesMarcadas.put(
                        tipoMarcacao.getCodigo(),
                        FolhaPontoTipoIntervalo.createFromTipoIntervalo(
                                tipoMarcacao,
                                0L,
                                0L));
            }

            // Se o tipo já estiver mapeado, apenas somamos os tempos do intervalo.
            tiposMarcacoesMarcadas
                    .get(codTipoMarcacao)
                    .sumTempoTotalTipoIntervalo(rSet.getLong("DIFERENCA_MARCACOES_SEGUNDOS"));
            tiposMarcacoesMarcadas
                    .get(codTipoMarcacao)
                    .sumTempoTotalHorasNoturnas(rSet.getLong("TEMPO_NOTURNO_EM_SEGUNDOS"));
        }
    }

    @NotNull
    private static FolhaPontoJornada createFolhaPontoJornada(@NotNull final ResultSet rSet) throws SQLException {
        // Na inicialização a jornada bruta e líquida podem ser iniciadas iguais.
        final Duration jornadaBrutaLiquida = Duration.ofSeconds(rSet.getLong("DIFERENCA_MARCACOES_SEGUNDOS"));
        return new FolhaPontoJornada(
                rSet.getObject("DATA_HORA_INICIO", LocalDateTime.class),
                rSet.getObject("DATA_HORA_FIM", LocalDateTime.class),
                rSet.getLong("COD_TIPO_INTERVALO"),
                rSet.getLong("COD_TIPO_INTERVALO_POR_UNIDADE"),
                new ArrayList<>(),
                jornadaBrutaLiquida,
                jornadaBrutaLiquida,
                rSet.getBoolean("TROCOU_DIA"),
                rSet.getBoolean("MARCACAO_INICIO_AJUSTADA"),
                rSet.getBoolean("MARCACAO_FIM_AJUSTADA"));
    }

    @NotNull
    private static FolhaPontoMarcacao createFolhaPontoMarcacao(@NotNull final ResultSet rSet) throws SQLException {
        return new FolhaPontoMarcacao(
                rSet.getObject("DATA_HORA_INICIO", LocalDateTime.class),
                rSet.getObject("DATA_HORA_FIM", LocalDateTime.class),
                rSet.getLong("COD_TIPO_INTERVALO"),
                rSet.getLong("COD_TIPO_INTERVALO_POR_UNIDADE"),
                rSet.getBoolean("TROCOU_DIA"),
                rSet.getBoolean("MARCACAO_INICIO_AJUSTADA"),
                rSet.getBoolean("MARCACAO_FIM_AJUSTADA"),
                rSet.getBoolean("DESCONTA_JORNADA_BRUTA"),
                rSet.getBoolean("DESCONTA_JORNADA_LIQUIDA"),
                rSet.getLong("DIFERENCA_MARCACOES_SEGUNDOS"),
                rSet.getLong("TEMPO_NOTURNO_EM_SEGUNDOS"));
    }

    @NotNull
    private static FolhaPontoRelatorio createFolhaPontoRelatorio(
            @NotNull final Long cpfAnterior,
            @NotNull final String nomeAnterior,
            @NotNull final LocalDate diaAnterior,
            @NotNull final List<FolhaPontoDia> dias,
            @NotNull final List<FolhaPontoIntervalo> intervalosDia,
            @NotNull final LocalDateTime dataHoraGeracaoRelatorioUtc,
            @NotNull final LocalDateTime dataHoraGeracaoRelatorioZoned) {
        dias.add(new FolhaPontoDia(diaAnterior, intervalosDia));
        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(cpfAnterior);
        colaborador.setNome(nomeAnterior);
        return new FolhaPontoRelatorio(colaborador, dias, dataHoraGeracaoRelatorioUtc, dataHoraGeracaoRelatorioZoned);
    }

    @NotNull
    private static Map<Long, TipoMarcacao> tiposMarcacoesToMap(@NotNull final List<TipoMarcacao> tiposIntervalos) {
        final Map<Long, TipoMarcacao> tiposIntervalosMap = new HashMap<>();
        tiposIntervalos.forEach(
                tipoIntervalo -> tiposIntervalosMap.put(tipoIntervalo.getCodigo(), tipoIntervalo));
        return tiposIntervalosMap;
    }
}