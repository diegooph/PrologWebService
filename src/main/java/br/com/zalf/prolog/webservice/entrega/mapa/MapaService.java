package br.com.zalf.prolog.webservice.entrega.mapa;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.entrega.mapa._model.CelulaPlanilhaMapaErro;
import br.com.zalf.prolog.webservice.entrega.mapa._model.ResponseErrorUploadMapa;
import br.com.zalf.prolog.webservice.entrega.mapa.validator.PlanilhaMapaValidator;
import br.com.zalf.prolog.webservice.entrega.mapa.validator.RegrasValidacaoPlanilhaMapa;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Classe MapaService responsavel por comunicar-se com a interface DAO
 */
public final class MapaService {
    private static final String TAG = MapaService.class.getSimpleName();
    private final MapaDao dao = Injection.provideMapaDao();

    @NotNull
    public AbstractResponse insertOrUpdateMapa(@NotNull final InputStream inputStream,
                                               @NotNull final Long codUnidade) {
        try {
            final List<String[]> planilhaMapa = PlanilhaMapaReader.readFromCsv(inputStream);

            // Verifica se a planilha está com algum erro.
            final Optional<List<CelulaPlanilhaMapaErro>> errors = new PlanilhaMapaValidator().findErrors(
                    planilhaMapa,
                    RegrasValidacaoPlanilhaMapa.getInstance());
            if (errors.isPresent()) {
                return new ResponseErrorUploadMapa(errors.get());
            }

            dao.insertOrUpdateMapa(codUnidade, planilhaMapa);
            return Response.ok("Arquivo do mapa inserido com sucesso");
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro não identificado ao realizar o import da planilha de mapa.\n" +
                    "codUnidade: %d", codUnidade), throwable);
            return Response.error("Erro ao realizar o import da planilha, tente novamente");
        }
    }

}
