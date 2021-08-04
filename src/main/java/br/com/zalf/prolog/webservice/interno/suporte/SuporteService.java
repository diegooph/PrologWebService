package br.com.zalf.prolog.webservice.interno.suporte;

import br.com.zalf.prolog.webservice.AmazonConstants;
import br.com.zalf.prolog.webservice.commons.imagens.ImagemProlog;
import br.com.zalf.prolog.webservice.commons.imagens.UploadImageHelper;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.interno.autenticacao.AutenticacaoInternaService;
import br.com.zalf.prolog.webservice.interno.suporte._model.InternalEmpresa;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public final class SuporteService {
    private static final String TAG = SuporteService.class.getSimpleName();
    @NotNull
    private final SuporteDaoImpl dao;

    @NotNull
    public Response insertEmpresa(@NotNull final String authorization,
                                  @NotNull final String nomeEmpresa,
                                  @NotNull final InputStream fileInputStream,
                                  @NotNull final FormDataContentDisposition fileDetail) {
        validate(authorization);
        try {
            String urlImagem = null;
            if (!StringUtils.isNullOrEmpty(fileDetail.getFileName())) {
                urlImagem = uploadLogoEmpresa(fileInputStream, fileDetail).getUrlImagem();
            }
            dao.insertEmpresa(nomeEmpresa, urlImagem);
            return Response.ok("Empresa cadastrada com sucesso!");
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao cadastrar empresa %s", nomeEmpresa), throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    public List<InternalEmpresa> getTodasEmpresas(@NotNull final String authorization) {
        validate(authorization);
        return dao.getTodasEmpresas();
    }

    @NotNull
    public InternalEmpresa getEmpresa(@NotNull final String authorization,
                                      @NotNull final Long codEmpresa) {
        validate(authorization);
        return dao.getEmpresa(codEmpresa);
    }

    @NotNull
    public Response alterarImagemLogoEmpresa(@NotNull final String authorization,
                                             @NotNull final Long codEmpresa,
                                             @NotNull final InputStream fileInputStream,
                                             @NotNull final FormDataContentDisposition fileDetail) {
        validate(authorization);
        try {
            final ImagemProlog imagemProlog = uploadLogoEmpresa(fileInputStream, fileDetail);
            dao.alteraImagemLogoEmpresa(codEmpresa, imagemProlog.getUrlImagem());
            return Response.ok(imagemProlog.getUrlImagem());
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao alterar imagem da empresa %d", codEmpresa), throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    private ImagemProlog uploadLogoEmpresa(@NotNull final InputStream fileInputStream,
                                           @NotNull final FormDataContentDisposition fileDetail) throws Throwable {
        final String imageType = FilenameUtils.getExtension(fileDetail.getFileName());
        return UploadImageHelper.uploadCompressedImagem(
                fileInputStream,
                AmazonConstants.BUCKET_NAME_LOGOS_EMPRESAS,
                imageType);
    }

    private void validate(@NotNull final String authorization) {
        new AutenticacaoInternaService().authorize(authorization);
    }
}
