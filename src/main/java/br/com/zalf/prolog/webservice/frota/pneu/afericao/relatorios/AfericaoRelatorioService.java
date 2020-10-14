package br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios._model.AfericaoExportacaoProtheus;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.util.List;

/**
 * Created on 30/08/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class AfericaoRelatorioService {
    private static final String TAG = AfericaoRelatorioService.class.getSimpleName();
    @NotNull
    private final AfericaoRelatorioDao dao = Injection.provideAfericaoRelatorioDao();

    public void getCronogramaAfericoesPlacasCsv(@NotNull final OutputStream out,
                                                @NotNull final List<Long> codUnidades,
                                                @NotNull final String userToken) {
        try {
            dao.getCronogramaAfericoesPlacasCsv(
                    out,
                    codUnidades,
                    userToken);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório do cronograma de aferições (CSV)", throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    public Report getCronogramaAfericoesPlacasReport(@NotNull final List<Long> codUnidades,
                                                     @NotNull final String userToken) throws ProLogException {
        try {
            return dao.getCronogramaAfericoesPlacasReport(
                    codUnidades, userToken);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório do cronograma de aferições  (REPORT)", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }

    public void getDadosGeraisAfericoesCsv(@NotNull final OutputStream out,
                                           @NotNull final List<Long> codUnidades,
                                           @NotNull final String dataInicial,
                                           @NotNull final String dataFinal) {
        try {
            dao.getDadosGeraisAfericoesCsv(
                    out,
                    codUnidades,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de dados gerais das aferições (CSV)", throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    public Report getDadosGeraisAfericoesReport(@NotNull final List<Long> codUnidades,
                                                @NotNull final String dataInicial,
                                                @NotNull final String dataFinal) throws ProLogException {
        try {
            return dao.getDadosGeraisAfericoesReport(
                    codUnidades,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de dados gerais das aferições (REPORT)", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }

    @NotNull
    public List<AfericaoExportacaoProtheus> getExportacaoAfericoesProtheus(
            @NotNull final List<Long> codUnidades,
            @NotNull final List<Long> codVeiculos,
            @NotNull final String dataInicial,
            @NotNull final String dataFinal) throws ProLogException {
        try {
            return dao.getExportacaoAfericoesProtheus(
                    codUnidades,
                    codVeiculos,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de exportação de aferições no padrão Protheus", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }
}