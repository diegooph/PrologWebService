package br.com.zalf.prolog.webservice.frota.pneu.pneu.importar;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.*;
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
        for (final PneuImport i : pneusImport) {
            final Pneu pneu = new PneuComum();
            pneu.setCodigoCliente(i.getCodigoCliente());
            pneu.setDot(i.getDot());
            pneu.setValor(i.getValor());
            pneu.setCodUnidadeAlocado(i.getCodUnidadeAlocado());
            pneu.setPneuNovoNuncaRodado(i.isPneuNovoNuncaRodado());

            final ModeloPneu modeloPneu = new ModeloPneu();
            modeloPneu.setCodigo(i.getCodModeloPneu());
            pneu.setModelo(modeloPneu);

            pneu.setBanda(createBanda(i));

            final PneuComum.Dimensao dimensao = new PneuComum.Dimensao();
            dimensao.setCodigo(i.getCodDimensao());
            pneu.setDimensao(dimensao);

            final Sulcos sulcoAtual = new Sulcos();
            sulcoAtual.setCentralInterno(i.getAlturaSulcoCentralInterno());
            sulcoAtual.setCentralExterno(i.getAlturaSulcoCentralExterno());
            sulcoAtual.setExterno(i.getAlturaSulcoExterno());
            sulcoAtual.setInterno(i.getAlturaSulcoInterno());
            pneu.setSulcosAtuais(sulcoAtual);

            pneu.setPressaoCorreta(i.getPressaoRecomendada());
            pneu.setStatus(i.getStatusPneu());
            pneu.setVidaAtual(i.getVidaAtual());
            pneu.setVidasTotal(i.getVidasTotal());
            pneus.add(pneu);
        }
        return pneus;
    }

    @Nullable
    private static Banda createBanda(@NotNull final PneuImport i) {
        if (i.getCodModeloBanda() != null) {
            final Banda banda = new Banda();
            final ModeloBanda modeloBanda = new ModeloBanda();
            modeloBanda.setCodigo(i.getCodModeloBanda());
            banda.setModelo(modeloBanda);
            banda.setValor(i.getValorBanda());
            return banda;
        }

        return null;
    }
}