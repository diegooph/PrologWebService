package br.com.zalf.prolog.webservice.interno.suporte;

import br.com.zalf.prolog.webservice.AmazonConstants;
import br.com.zalf.prolog.webservice.commons.imagens.ImagemProlog;
import br.com.zalf.prolog.webservice.commons.imagens.UploadImageHelper;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.interno.autenticacao.AutenticacaoInternaService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public final class SuporteService {
    private static final String TAG = SuporteService.class.getSimpleName();
    @NotNull
    private final SuporteDaoImpl dao;

    @NotNull
    public Response alterarImagemLogoEmpresa(@NotNull final String authorization,
                                             @NotNull final Long codEmpresa,
                                             @NotNull final InputStream fileInputStream,
                                             @NotNull final FormDataContentDisposition fileDetail) {
        new AutenticacaoInternaService().authorize(authorization);

        try {
            final String imageType = FilenameUtils.getExtension(fileDetail.getFileName());
            final ImagemProlog imagemProlog = UploadImageHelper.uploadCompressedImagem(
                    fileInputStream,
                    AmazonConstants.BUCKET_NAME_LOGOS_EMPRESAS,
                    imageType);
            dao.alteraImagemLogoEmpresa(codEmpresa, imagemProlog.getUrlImagem());
            return Response.ok(imagemProlog.getUrlImagem());
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao alterar imagem da empresa %d", codEmpresa), throwable);
            throw new RuntimeException(throwable);
        }
    }
}
