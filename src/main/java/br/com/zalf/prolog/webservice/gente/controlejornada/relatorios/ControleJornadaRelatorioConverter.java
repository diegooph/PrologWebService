package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoMarcacao;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
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
                tipoIntervalo -> tiposIntervalosMap.put(tipoIntervalo.getCodigo(),tipoIntervalo));
        return tiposIntervalosMap;
    }
}