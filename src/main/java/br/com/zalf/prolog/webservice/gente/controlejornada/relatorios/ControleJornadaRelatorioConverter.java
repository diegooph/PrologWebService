package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoMarcacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.*;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada.*;
import org.jetbrains.annotations.NotNull;

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
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal,
            @NotNull final ZoneId zoneIdUnidade) throws Throwable {
        final LocalDateTime dataHoraGeracaoRelatorioUtc = Now.localDateTimeUtc();
        final LocalDateTime dataHoraGeracaoRelatorioZoned = dataHoraGeracaoRelatorioUtc
                .atZone(ZoneOffset.UTC)
                .withZoneSameInstant(zoneIdUnidade)
                .toLocalDateTime();
        final List<FolhaPontoRelatorio> relatorios = new ArrayList<>();
        final Map<Long, TipoMarcacao> tiposIntervalosUnidade = tiposIntervalosToMap(tiposIntervalos);
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
                //noinspection ConstantConditions
                final FolhaPontoRelatorio folhaPontoRelatorio = createFolhaPontoRelatorio(cpfAnterior,
                        nomeAnterior, diaAnterior, dias, intervalosDia, dataHoraGeracaoRelatorioUtc, dataHoraGeracaoRelatorioZoned);
                folhaPontoRelatorio.calculaTempoEmCadaTipoIntervalo(dataInicial, dataFinal, tiposIntervalosUnidade, zoneIdUnidade);
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
            final FolhaPontoIntervalo intervalo = new FolhaPontoIntervalo(
                    dataHoraInicio,
                    dataHoraFim,
                    rSet.getObject("DATA_HORA_INICIO_UTC", LocalDateTime.class),
                    rSet.getObject("DATA_HORA_FIM_UTC", LocalDateTime.class),
                    codTipoIntervaloLong,
                    rSet.getLong("COD_TIPO_INTERVALO_POR_UNIDADE"),
                    rSet.getBoolean("TROCOU_DIA"));
            intervalosDia.add(intervalo);

            cpfAnterior = cpfAtual;
            diaAnterior = diaAtual;
            nomeAnterior = rSet.getString("NOME_COLABORADOR");
        }
        if (diaAnterior != null) {
            final FolhaPontoRelatorio folhaPontoRelatorio = createFolhaPontoRelatorio(cpfAnterior,
                    nomeAnterior, diaAnterior, dias, intervalosDia, dataHoraGeracaoRelatorioUtc, dataHoraGeracaoRelatorioZoned);
            folhaPontoRelatorio.calculaTempoEmCadaTipoIntervalo(dataInicial, dataFinal, tiposIntervalosUnidade, zoneIdUnidade);
            relatorios.add(folhaPontoRelatorio);
        }
        return relatorios;
    }

    @NotNull
    static List<FolhaPontoJornadaRelatorio> createFolhaPontoJornadaRelatorio(
            @NotNull final ResultSet rSet,
            @NotNull final List<TipoMarcacao> tiposIntervalos,
            @NotNull final ZoneId zoneIdUnidade) throws Throwable {
        final List<FolhaPontoJornadaRelatorio> folhasPontoJornada = new ArrayList<>();

        // Estrutura de dados = Map<colaborador, Map<dia, List<marcacao>>>
        // Assim temos para um colaborador uma estrutura de dias e marcações naquele dia.
        final Map<ColaboradorFolhaPontoDb, Map<LocalDate, List<FolhaPontoMarcacaoDb>>> marcacoesDb =
                createMarcacoesDb(rSet);
        // Se não tiver dados para processar, retornamos o relatório vazio
        if (marcacoesDb.isEmpty()) {
            return folhasPontoJornada;
        }

        final Map<Long, FolhaPontoTipoIntervalo> tiposIntervalosUnidade = new HashMap<>();

        // Precisamos, de alguma forma, montrar o relatório :chan:.
        marcacoesDb.forEach((colaboradorFolhaPontoDb, marcacoesDia) -> {
            // Para cada colaborador.

            final List<FolhaPontoJornadaDia> marcacoesDiaColaborador = new ArrayList<>();
            marcacoesDia.forEach((dia, marcacoes) -> {
                // Para cada dia.

                final List<FolhaPontoJornadas> jornadasDia = new ArrayList<>();
                final List<FolhaPontoMarcacoes> marcacoesForaJornada = new ArrayList<>();
                marcacoes.forEach(folhaPontoMarcacaoDb -> {
                    // Para cada marcação.

                    // Precisamos realizar os cálculos e gerar os objetos corretos
                    final FolhaPontoMarcacoes folhaPontoMarcacao =
                            convertoToFolhaPontoMarcacao(folhaPontoMarcacaoDb);

                    // precisamos descobrir se ela vai pra lista de jornada ou fora.
                    if (folhaPontoMarcacaoDb.isTipoJornada()) {
                        // Se é uma marcação tipo Jornada, sabemos que ela será adicionada nas jornadas
                        final FolhaPontoJornadas jornada = new FolhaPontoJornadas(
                                folhaPontoMarcacaoDb.getDataHoraMarcacaoInicio(),
                                folhaPontoMarcacaoDb.getDataHoraMarcacaoFim(),
                                folhaPontoMarcacaoDb.getCodTipoMarcacao(),
                                folhaPontoMarcacaoDb.getCodTipoMarcacaoPorUnidade(),
                                new ArrayList<>(),
                                folhaPontoMarcacaoDb.isTrocouDia(),
                                folhaPontoMarcacaoDb.isMarcacaoInicioAjustada(),
                                folhaPontoMarcacaoDb.isMarcacaoFimAjustada());
                        jornada.setJornadaBruta(Duration.ofSeconds(folhaPontoMarcacaoDb.getTempoNoturnoEmSegundos()));
                        jornadasDia.add(jornada);
                    } else if (jornadasDia.isEmpty()) {
                        // Se não tem nenhuma jornada criada ainda, não tem como a marcação pertencer à uma.
                        marcacoesForaJornada.add(folhaPontoMarcacao);
                    } else {
                        // Se não é uma jornada, e já temos jornadas adicionadas. Então precisamos
                        // descobrir se essa marcação está dentro de uma jornada.
                        boolean foiAdicionadaEmJornada = false;
                        for (final FolhaPontoJornadas jornada : jornadasDia) {
                            if (jornada.hasInicioFim() && folhaPontoMarcacao.fitIn(jornada)) {
                                jornada.addMarcacaoToJornada(folhaPontoMarcacao);
                                jornada.calculaJornadaLiquida(folhaPontoMarcacaoDb.getDiferencaoInicioFimEmSegundos());
                                foiAdicionadaEmJornada = true;
                                break;
                            }
                        }
                        if (!foiAdicionadaEmJornada) {
                            // Se a marcação não se encaixa em nenhuma jornada, então adicionamos fora das jornadas.
                            marcacoesForaJornada.add(folhaPontoMarcacao);
                        }
                    }

                    // Precisamos atualizar a lista de tipos de marcações marcadas.
                    final Long codTipoMarcacao = folhaPontoMarcacaoDb.getCodTipoMarcacao();
                    if (tiposIntervalosUnidade.get(codTipoMarcacao) == null) {
                        // Se ainda não estiver mapeado, precisamos criar o tipo de intervalo e somar os tempos
                        for (final TipoMarcacao tipoMarcacao : tiposIntervalos) {
                            if (tipoMarcacao.getCodigo().equals(codTipoMarcacao)) {
                                tiposIntervalosUnidade.put(
                                        tipoMarcacao.getCodigo(),
                                        FolhaPontoTipoIntervalo.createFromTipoIntervalo(
                                                tipoMarcacao,
                                                0L,
                                                0L));
                            }
                        }
                    }

                    // Se o tipo já estiver mapeado, apenas somamos os tempos do intervalo
                    tiposIntervalosUnidade
                            .get(codTipoMarcacao)
                            .sumTempoTotalTipoIntervalo(folhaPontoMarcacaoDb.getDiferencaoInicioFimEmSegundos());
                    tiposIntervalosUnidade
                            .get(codTipoMarcacao)
                            .sumTempoTotalHorasNoturnas(folhaPontoMarcacaoDb.getTempoNoturnoEmSegundos());
                });
                marcacoesDiaColaborador.add(new FolhaPontoJornadaDia(dia, jornadasDia, marcacoesForaJornada));
            });

            final FolhaPontoJornadaRelatorio relatorioColaborador =
                    new FolhaPontoJornadaRelatorio(
                            colaboradorFolhaPontoDb.getCpfColaborador(),
                            colaboradorFolhaPontoDb.getNomeColaborador(),
                            marcacoesDiaColaborador,
                            LocalDateTime.now().atZone(zoneIdUnidade).toLocalDateTime());
            relatorioColaborador.setTiposMarcacoesMarcadas(new HashSet<>(tiposIntervalosUnidade.values()));
            relatorioColaborador.calculaTotaisHorasJornadasLiquidaBruta();
            folhasPontoJornada.add(relatorioColaborador);
        });

        return folhasPontoJornada;
    }

    @NotNull
    private static Map<ColaboradorFolhaPontoDb, Map<LocalDate, List<FolhaPontoMarcacaoDb>>> createMarcacoesDb(
            @NotNull final ResultSet rSet) throws SQLException {
        final Map<ColaboradorFolhaPontoDb, Map<LocalDate, List<FolhaPontoMarcacaoDb>>> maperson = new HashMap<>();
        Map<LocalDate, List<FolhaPontoMarcacaoDb>> internalMap = new HashMap<>();
        List<FolhaPontoMarcacaoDb> marcacoesDb = new ArrayList<>();

        String cpfAnterior = null;
        String nomeAnterior = null;
        LocalDate diaAnterior = null;
        while (rSet.next()) {
            final String cpfAtual = rSet.getString("CPF_COLABORADOR");
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
                //noinspection ConstantConditions
                maperson.put(new ColaboradorFolhaPontoDb(cpfAnterior, nomeAnterior), internalMap);
                internalMap = new HashMap<>();
                marcacoesDb = new ArrayList<>();
            } else {
                // Mesmo colaborador.
                Log.d(TAG, "Mesmo colaborador. Anterior: " + cpfAnterior + " - Atual: " + cpfAtual);
                if (!diaAnterior.equals(diaAtual)) {
                    // Trocou o dia.
                    internalMap.put(diaAnterior, marcacoesDb);
                    marcacoesDb = new ArrayList<>();
                }

                marcacoesDb.add(
                        new FolhaPontoMarcacaoDb(
                                rSet.getString("CPF_COLABORADOR"),
                                rSet.getString("NOME_COLABORADOR"),
                                rSet.getLong("COD_TIPO_INTERVALO"),
                                rSet.getLong("COD_TIPO_INTERVALO_POR_UNIDADE"),
                                rSet.getLong("COD_MARCACAO_INICIO"),
                                rSet.getLong("COD_MARCACAO_FIM"),
                                rSet.getObject("DATA_HORA_INICIO", LocalDateTime.class),
                                rSet.getObject("DATA_HORA_FIM", LocalDateTime.class),
                                rSet.getObject("DATA_HORA_INICIO_UTC", LocalDateTime.class),
                                rSet.getObject("DATA_HORA_FIM_UTC", LocalDateTime.class),
                                rSet.getLong("DIFERENCA_MARCACOES_SEGUNDOS"),
                                rSet.getLong("TEMPO_NOTURNO_EM_SEGUNDOS"),
                                rSet.getBoolean("MARCACAO_INICIO_AJUSTADA"),
                                rSet.getBoolean("MARCACAO_FIM_AJUSTADA"),
                                rSet.getBoolean("TROCOU_DIA"),
                                rSet.getBoolean("TIPO_JORNADA")));
            }
            cpfAnterior = cpfAtual;
            nomeAnterior = rSet.getString("NOME_COLABORADOR");
            diaAnterior = diaAtual;
        }

        if (diaAnterior != null) {
            internalMap.put(diaAnterior, marcacoesDb);
            maperson.put(new ColaboradorFolhaPontoDb(cpfAnterior, nomeAnterior), internalMap);
        }

        return maperson;
    }

    @NotNull
    private static FolhaPontoMarcacoes convertoToFolhaPontoMarcacao(
            @NotNull final FolhaPontoMarcacaoDb folhaPontoMarcacaoDb) {
        return new FolhaPontoMarcacoes(
                folhaPontoMarcacaoDb.getDataHoraMarcacaoInicio(),
                folhaPontoMarcacaoDb.getDataHoraMarcacaoFim(),
                folhaPontoMarcacaoDb.getCodTipoMarcacao(),
                folhaPontoMarcacaoDb.getCodTipoMarcacaoPorUnidade(),
                folhaPontoMarcacaoDb.isTrocouDia(),
                folhaPontoMarcacaoDb.isMarcacaoInicioAjustada(),
                folhaPontoMarcacaoDb.isMarcacaoFimAjustada());
    }

    @NotNull
    private static FolhaPontoRelatorio createFolhaPontoRelatorio(@NotNull final Long cpfAnterior,
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
    private static Map<Long, TipoMarcacao> tiposIntervalosToMap(@NotNull final List<TipoMarcacao> tiposIntervalos) {
        final Map<Long, TipoMarcacao> tiposIntervalosMap = new HashMap<>();
        tiposIntervalos.forEach(
                tipoIntervalo -> tiposIntervalosMap.put(tipoIntervalo.getCodigo(), tipoIntervalo));
        return tiposIntervalosMap;
    }
}