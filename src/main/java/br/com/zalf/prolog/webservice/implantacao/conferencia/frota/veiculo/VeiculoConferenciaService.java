package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo.model.VeiculoPlanilha;
import br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo.model.insert.VeiculoPlanilhaReader;
import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class VeiculoConferenciaService  {
    private static final String TAG = VeiculoConferenciaService.class.getSimpleName();
    @NotNull
    private final VeiculoConferenciaDao dao = Injection.provideVeiculoConferenciaDao();

    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

    @NotNull
    public List<Long> insert(@NotNull final Long codUnidade,
                             @NotNull final InputStream fileInputStream) throws ProLogException {
        try {
            final File file = createFileFromImport(codUnidade, fileInputStream);
            final List<VeiculoPlanilha> veiculoPlanilhaItens = VeiculoPlanilhaReader
                    .readListFromCsvFilePath(file);

            //Montar JSON para verificar no banco

//            for (VeiculoPlanilha item : veiculoPlanilhaItens) {
//                RaizenProdutividadeValidator.validacaoAtributosRaizenProdutividade(item);
//                // O código da unidade vem no path pois os itens são importados através de arquivo.
//                item.setCodUnidade(codUnidade);
//            }

//            dao.insertOrUpdateProdutividadeRaizen(
//                    TokenCleaner.getOnlyToken(token),
//                    raizenProdutividadeItens);
//            return Response.ok("Upload realizado com sucesso!");
        } catch (final Throwable e) {
            final String errorMessage = "Não foi possível inserir os dados no banco de dados, tente novamente!";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
        }

        return null;
    }

    @NotNull
    @SuppressWarnings("Duplicates")
    private File createFileFromImport(@NotNull final Long codUnidade,
                                      @NotNull final InputStream fileInputStream) throws Throwable {
        final String fileName = String.valueOf(Now.utcMillis()) + "_" + codUnidade
                + "_";
        // Pasta temporária
        final File tmpDir = Files.createTempDir();
        final File file = new File(tmpDir, fileName);
        final FileOutputStream out = new FileOutputStream(file);
        IOUtils.copy(fileInputStream, out);
        IOUtils.closeQuietly(out);
        return file;
    }
}
