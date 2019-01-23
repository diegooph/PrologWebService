package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoMarcacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.FolhaPontoDia;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.FolhaPontoIntervalo;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.FolhaPontoRelatorio;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada.FolhaPontoJornada;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada.FolhaPontoJornadaDia;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada.FolhaPontoJornadaRelatorio;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada.FolhaPontoMarcacao;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final LocalDateTime dataHoraGeracaoRelatorioUtc = Now.localDateTimeUtc();
        final LocalDateTime dataHoraGeracaoRelatorioZoned = dataHoraGeracaoRelatorioUtc
                .atZone(ZoneOffset.UTC)
                .withZoneSameInstant(zoneIdUnidade)
                .toLocalDateTime();
        final List<FolhaPontoJornadaRelatorio> folhasPontoJornada = new ArrayList<>();

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
                // TODO - Aqui setar mais algum atributo?
                jornadasDia.add(jornada);
                marcacoesDia.add(new FolhaPontoJornadaDia(diaAnterior, jornadasDia, marcacoesForaJornada));
                //noinspection ConstantConditions
                folhasPontoJornada.add(
                        new FolhaPontoJornadaRelatorio(
                                cpfAnterior,
                                nomeAnterior,
                                marcacoesDia,
                                dataHoraGeracaoRelatorioZoned));
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
                    marcacoesForaJornada = new ArrayList<>();
                    codMarcacaoJornadaAnterior = null;
                }
            }
            final boolean tipoJornada = rSet.getBoolean("TIPO_JORNADA");
            final Long codMarcacaoJornada = rSet.getLong("COD_MARCACAO_JORNADA");

            if (tipoJornada) {
                // Criamos uma Jornada.
                if (codMarcacaoJornadaAnterior != null &&!codMarcacaoJornada.equals(codMarcacaoJornadaAnterior)) {
                    jornadasDia.add(jornada);
                }
                jornada = createFolhaPontoJornada(rSet);
                codMarcacaoJornadaAnterior = codMarcacaoJornada;
            } else if (codMarcacaoJornada > 0) {
                if (jornada == null) {
                    throw new IllegalStateException("O objeto jornada deve ser instanciado!");
                }
                // Marcação não é Jornada mas está vinculada à uma.
                jornada.addMarcacaoToJornada(createFolhaPontoMarcacao(rSet));
            } else {
                // Marcação deste dia não pertence à nenhuma Jornada.
                marcacoesForaJornada.add(createFolhaPontoMarcacao(rSet));
            }

            diaAnterior = diaAtual;
            cpfAnterior = cpfAtual;
            nomeAnterior = rSet.getString("NOME_COLABORADOR");
        }
        if (cpfAnterior != null) {
            jornadasDia.add(jornada);
            marcacoesDia.add(new FolhaPontoJornadaDia(diaAnterior, jornadasDia, marcacoesForaJornada));
            folhasPontoJornada.add(
                    new FolhaPontoJornadaRelatorio(
                            cpfAnterior,
                            nomeAnterior,
                            marcacoesDia,
                            dataHoraGeracaoRelatorioZoned));
        }

        return folhasPontoJornada;
    }

    @NotNull
    private static FolhaPontoJornada createFolhaPontoJornada(@NotNull final ResultSet rSet) throws SQLException {
        return new FolhaPontoJornada(
                rSet.getObject("DATA_HORA_INICIO", LocalDateTime.class),
                rSet.getObject("DATA_HORA_FIM", LocalDateTime.class),
                rSet.getLong("COD_TIPO_INTERVALO"),
                rSet.getLong("COD_TIPO_INTERVALO_POR_UNIDADE"),
                new ArrayList<>(),
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
                rSet.getBoolean("MARCACAO_FIM_AJUSTADA"));
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
    private static Map<Long, TipoMarcacao> tiposIntervalosToMap(@NotNull final List<TipoMarcacao> tiposIntervalos) {
        final Map<Long, TipoMarcacao> tiposIntervalosMap = new HashMap<>();
        tiposIntervalos.forEach(
                tipoIntervalo -> tiposIntervalosMap.put(tipoIntervalo.getCodigo(), tipoIntervalo));
        return tiposIntervalosMap;
    }
}