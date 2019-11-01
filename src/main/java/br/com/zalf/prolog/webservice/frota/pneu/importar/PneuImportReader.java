package br.com.zalf.prolog.webservice.frota.pneu.importar;

import br.com.zalf.prolog.webservice.frota.pneu._model.*;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 15/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuImportReader {

    private PneuImportReader() {
        throw new IllegalStateException(PneuImportReader.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static List<Pneu> readFromCsv(@NotNull final InputStream inputStream) {
        final BeanListProcessor<PneuImport> rowProcessor = new BeanListProcessor<>(PneuImport.class);
        final CsvParserSettings settings = new CsvParserSettings();
        settings.setDelimiterDetectionEnabled(true, ',', ';');
        settings.setProcessor(rowProcessor);
        settings.setHeaderExtractionEnabled(true);
        final CsvParser parser = new CsvParser(settings);
        parser.parse(inputStream);
        return toPneus(rowProcessor.getBeans());
    }

    @NotNull
    private static List<Pneu> toPneus(@NotNull final List<PneuImport> pneusImport) {
        final List<Pneu> pneus = new ArrayList<>(pneusImport.size());
        for (final PneuImport pneuImport : pneusImport) {
            final Pneu pneu = new PneuComum();
            pneu.setCodigoCliente(pneuImport.getCodigoCliente());
            pneu.setDot(pneuImport.getDot());
            pneu.setValor(pneuImport.getValor());
            pneu.setCodUnidadeAlocado(pneuImport.getCodUnidadeAlocado());
            pneu.setPneuNovoNuncaRodado(pneuImport.isPneuNovoNuncaRodado());

            final ModeloPneu modeloPneu = new ModeloPneu();
            modeloPneu.setCodigo(pneuImport.getCodModeloPneu());
            pneu.setModelo(modeloPneu);

            pneu.setBanda(createBanda(pneuImport));

            final PneuComum.Dimensao dimensao = new PneuComum.Dimensao();
            dimensao.setCodigo(pneuImport.getCodDimensao());
            pneu.setDimensao(dimensao);

            pneu.setPressaoCorreta(pneuImport.getPressaoRecomendada());
            pneu.setStatus(pneuImport.getStatusPneu());
            pneu.setVidaAtual(pneuImport.getVidaAtual());
            pneu.setVidasTotal(pneuImport.getVidasTotal());
            pneus.add(pneu);
        }
        return pneus;
    }

    @Nullable
    private static Banda createBanda(@NotNull final PneuImport pneuImport) {
        if (pneuImport.getVidaAtual() > 1 && pneuImport.getCodModeloBanda() == null) {
            throw new IllegalStateException("O pneu " + pneuImport.getCodigoCliente() + " está acima da primeira vida e " +
                    "não tem banda setada");
        }
        if (pneuImport.getVidaAtual() > pneuImport.getVidasTotal()) {
            throw new IllegalStateException("A vida atual não pode ser maior do que o total de vidas: "
                    + pneuImport.getCodigoCliente());
        }

        if (pneuImport.getCodModeloBanda() != null) {
            final Banda banda = new Banda();
            final ModeloBanda modeloBanda = new ModeloBanda();
            modeloBanda.setCodigo(pneuImport.getCodModeloBanda());
            banda.setModelo(modeloBanda);
            banda.setValor(pneuImport.getValorBanda());
            return banda;
        }

        return null;
    }
}