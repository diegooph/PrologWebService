package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.AmazonConstants;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.imagens.FileFormatNotSupportException;
import br.com.zalf.prolog.webservice.commons.imagens.Galeria;
import br.com.zalf.prolog.webservice.commons.imagens.ImagemProLog;
import br.com.zalf.prolog.webservice.commons.imagens.UploadImageHelper;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.S3FileSender;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.insercao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.ResponseImagemChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.visualizacao.ModeloChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.visualizacao.ModeloChecklistVisualizacao;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * Classe ChecklistModeloService responsavel por comunicar-se com a interface DAO
 */
public class ChecklistModeloService {
    private static final String TAG = ChecklistModeloService.class.getSimpleName();
    @NotNull
    private final ChecklistModeloDao dao = Injection.provideChecklistModeloDao();
    @NotNull
    private final ProLogExceptionHandler handler = Injection.provideProLogExceptionHandler();

    @NotNull
    Response insertModeloChecklist(@NotNull final ModeloChecklistInsercao modeloChecklist) throws ProLogException {
        try {
            dao.insertModeloChecklist(modeloChecklist);
            return Response.ok("Modelo de checklist inserido com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir modelo de checklist", t);
            throw handler.map(t, "Erro ao inserir modelo de checklist, tente novamente");
        }
    }

    @NotNull
    List<ModeloChecklistListagem> getModelosChecklistListagemByCodUnidadeByCodFuncao(
            @NotNull final Long codUnidade,
            @NotNull final String codFuncao) throws ProLogException {
        try {
            return dao.getModelosChecklistListagemByCodUnidadeByCodFuncao(codUnidade, codFuncao);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar os modelos de checklist para o cargo " + codFuncao, t);
            throw handler.map(t, "Erro ao buscar modelos de checklist, tente novamente");
        }
    }

    @NotNull
    ModeloChecklistVisualizacao getModeloChecklist(@NotNull final Long codUnidade,
                                                   @NotNull final Long codModelo) throws ProLogException {
        try {
            return dao.getModeloChecklist(codUnidade, codModelo);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar o modelo de checklist " + codModelo, t);
            throw handler.map(t, "Erro ao buscar modelo de checklist, tente novamente");
        }
    }

    @NotNull
    Response updateModeloChecklist(@NotNull final String token,
                                   @NotNull final Long codUnidade,
                                   @NotNull final Long codModelo,
                                   @NotNull final ModeloChecklistEdicao modeloChecklist) throws ProLogException {
        try {
            dao.updateModeloChecklist(TokenCleaner.getOnlyToken(token), codUnidade, codModelo, modeloChecklist);
            return Response.ok("Modelo de checklist atualizado com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao atualizar modelo de checklist", t);
            throw handler.map(t, "Erro ao atualizar modelo de checklist, tente novamente");
        }
    }

    @NotNull
    public List<PerguntaRespostaChecklist> getPerguntas(@NotNull final Long codUnidade,
                                                        @NotNull final Long codModelo) throws ProLogException {
        try {
            return dao.getPerguntas(codUnidade, codModelo);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar perguntas do modelo de checklist " + codModelo, t);
            throw handler.map(t, "Erro ao buscar perguntas do modelo de checklist, tente novamente");
        }
    }

    @NotNull
    Response updateStatusAtivo(@NotNull final Long codUnidade,
                               @NotNull final Long codModelo,
                               @NotNull final ModeloChecklistEdicao modeloChecklist) throws ProLogException {
        try {
            dao.updateStatusAtivo(codUnidade, codModelo, modeloChecklist.isAtivo());
            return Response.ok("Modelo de checklist " + (modeloChecklist.isAtivo() ? "ativado" : "inativado"));
        } catch (Throwable t) {
            Log.e(TAG, "Erro ao ativar/inativar o modelo de checklist: " + codModelo, t);
            throw handler.map(t, "Erro ao ativar/inativar o modelo de checklist, tente novamente");
        }
    }

    @NotNull
    List<ModeloChecklistVisualizacao> getModelosChecklistProLog() throws ProLogException {
        try {
            return dao.getModelosChecklistProLog();
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar modelos de checklist do ProLog", t);
            throw handler.map(t, "Erro ao buscar modelos de checklist do ProLog, tente novamente");
        }
    }

    @NotNull
    public List<String> getUrlImagensPerguntas(@NotNull final Long codUnidade,
                                               @NotNull final Long codFuncao) throws ProLogException {
        try {
            return dao.getUrlImagensPerguntas(codUnidade, codFuncao);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar as URLs das perguntas", t);
            throw handler.map(t, "Erro ao buscar as URLs das perguntas, tente novamente");
        }
    }

    @NotNull
    Galeria getGaleriaImagensPublicas() throws ProLogException {
        try {
            return dao.getGaleriaImagensPublicas();
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar galeria de imagens publicas", t);
            throw handler.map(t, "Erro ao buscar galeria de imagens publicas, tente novamente");
        }
    }

    @NotNull
    Galeria getGaleriaImagensEmpresa(@NotNull final Long codEmpresa) throws ProLogException {
        try {
            return dao.getGaleriaImagensEmpresa(codEmpresa);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar galeria de imagens da empresa: " + codEmpresa, t);
            throw handler.map(t, "Erro ao buscar galeria de imagens da empresa, tente novamente");
        }
    }

    @NotNull
    AbstractResponse insertImagem(@NotNull final Long codEmpresa,
                                  @NotNull final InputStream fileInputStream,
                                  @NotNull final FormDataContentDisposition fileDetail) {
        try {
            final String imageType = FilenameUtils.getExtension(fileDetail.getFileName());
            final ImagemProLog imagemProLog = UploadImageHelper.uploadCompressedImagem(
                    fileInputStream,
                    AmazonConstants.BUCKET_CHECKLIST_GALERIA_IMAGENS,
                    imageType);
            final Long codImagem = dao.insertImagem(codEmpresa, imagemProLog);
            return ResponseImagemChecklist.ok(
                    "Imagem inserida com sucesso",
                    codImagem,
                    imagemProLog.getUrlImagem());
        } catch (FileFormatNotSupportException e) {
            Log.e(TAG, "Arquivo recebido não é uma imagem", e);
            return Response.error(e.getMessage());
        } catch (SQLException | IOException | S3FileSender.S3FileSenderException e) {
            Log.e(TAG, "Erro ao inserir o imagem", e);
            throw new RuntimeException(e);
        }
    }
}