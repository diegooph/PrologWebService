package br.com.zalf.prolog.webservice.entrega.mapa;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

/**
 * Classe MapaService responsavel por comunicar-se com a interface DAO
 */
public final class MapaService {
    private static final String TAG = MapaService.class.getSimpleName();
    private final MapaDao dao = Injection.provideMapaDao();

    @NotNull
    public Response insertOrUpdateMapa(@NotNull final InputStream inputStream,
                                       @NotNull final Long codUnidade) {
        try {
            final List<String[]> planilhaMapa = PlanilhaMapaReader.readFromCsv(inputStream);

            if (dao.insertOrUpdateMapa("", codUnidade)) {
                return Response.ok("Arquivo do mapa inserido com sucesso");
            } else {
                return Response.error("Problema ao inserir o arquivo");
            }
        } catch (final SQLException e) {
            Log.e(TAG, String.format("Erro relacionado ao banco de dados ao inserir o arquivo.\n" +
                    "codUnidade: %d", codUnidade), e);
            return Response.error("Erro relacionado ao banco de dados ao inserir o arquivo");
        } catch (final IOException e) {
            Log.e(TAG, String.format("Erro relacionado ao processamento do arquivo.\n" +
                    "codUnidade: %d", codUnidade), e);
            return Response.error("Erro relacionado ao processamento do arquivo");
        } catch (final ParseException | NumberFormatException e) {
            Log.e(TAG, String.format("Erro relacionado aos dados da planilha.\n" +
                    "codUnidade: %d", codUnidade), e);
            return Response.error("Erro nos dados da planilha");
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro não identificado ao realizar o import da planilha de mapa.\n" +
                    "codUnidade: %d", codUnidade), throwable);
            return Response.error("Erro ao realizar o import da planilha");
        }
    }

}
