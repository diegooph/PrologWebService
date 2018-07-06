package br.com.zalf.prolog.webservice.raizen.produtividade;


import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.errorhandling.exception.RaizenProdutividadeException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jetbrains.annotations.NotNull;
import software.amazon.ion.IonException;

import java.io.File;
import java.io.InputStream;

/**
 * Created on 04/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeService {

    private static final RaizenProdutividadeDao dao = Injection.provideRaizenProdutividadeDao();

    public Response uploadRaizenProdutividade (@NotNull final String token,
                                               @NotNull final Long codEmpresa,
                                               @NotNull final InputStream fileInputStrem,
                                               @NotNull final FormDataContentDisposition fileDetail)
            throws RaizenProdutividadeException {
        final File file = createFileFromImport(codEmpresa, fileInputStrem, fileDetail);
        readAndInsertImport(token, codEmpresa, file);
        return Response.ok("Upload realizado com sucesso!");
    }

    private File createFileFromImport(@NotNull final Long codEmpresa,
                                      @NotNull final InputStream fileInputStrem,
                                      @NotNull final FormDataContentDisposition fileDetail) {
        try {

        }catch (IonException e){

        }

        return null;
    }

    private void readAndInsertImport(String token, Long codEmpresa, File file) {
    }
}
