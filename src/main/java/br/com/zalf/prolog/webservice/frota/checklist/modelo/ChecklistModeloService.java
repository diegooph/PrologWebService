package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.AmazonConstants;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.autenticacao._model.token.TokenCleaner;
import br.com.zalf.prolog.webservice.commons.imagens.FileFormatNotSupportException;
import br.com.zalf.prolog.webservice.commons.imagens.Galeria;
import br.com.zalf.prolog.webservice.commons.imagens.ImagemProlog;
import br.com.zalf.prolog.webservice.commons.imagens.UploadImageHelper;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.ModeloChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.ResponseImagemChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistRealizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistSelecao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.ModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.integracao.router.RouterModeloChecklist;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.List;

/**
 * Classe ChecklistModeloService responsavel por comunicar-se com a interface DAO
 */
public final class ChecklistModeloService {
    private static final String TAG = ChecklistModeloService.class.getSimpleName();
    @NotNull
    private final ChecklistModeloDao dao = Injection.provideChecklistModeloDao();

    @NotNull
    public ResultInsertModeloChecklist insertModeloChecklist(@NotNull final ModeloChecklistInsercao modeloChecklist,
                                                             @NotNull final String userToken) throws ProLogException {
        try {
            ChecklistModeloValidator.validaModelo(modeloChecklist);

            return RouterModeloChecklist
                    .create(dao, userToken)
                    .insertModeloChecklist(
                            modeloChecklist,
                            Injection.provideDadosChecklistOfflineChangedListener(),
                            true,
                            TokenCleaner.getOnlyToken(userToken));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir modelo de checklist", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir modelo de checklist, tente novamente");
        }
    }

    @NotNull
    public Response updateModeloChecklist(final Long codUnidade,
                                          final Long codModelo,
                                          final ModeloChecklistEdicao modeloChecklist,
                                          final String userToken) throws ProLogException {
        try {
            ChecklistModeloValidator.validaModelo(modeloChecklist);

            RouterModeloChecklist
                    .create(dao, userToken)
                    .updateModeloChecklist(
                            codUnidade,
                            codModelo,
                            modeloChecklist,
                            Injection.provideDadosChecklistOfflineChangedListener(),
                            true,
                            TokenCleaner.getOnlyToken(userToken));
            return Response.ok("Modelo de checklist atualizado com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao atualizar modelo de checklist", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar modelo de checklist, tente novamente");
        }
    }

    @NotNull
    public ModeloChecklistVisualizacao getModeloChecklist(@NotNull final Long codUnidade,
                                                          @NotNull final Long codModelo) throws ProLogException {
        try {
            return dao.getModeloChecklist(codUnidade, codModelo);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar o modelo de checklist " + codModelo, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar modelo de checklist, tente novamente");
        }
    }

    @NotNull
    public List<String> getUrlImagensPerguntas(@NotNull final Long codUnidade,
                                               @NotNull final Long codFuncao) throws ProLogException {
        try {
            return dao.getUrlImagensPerguntas(codUnidade, codFuncao);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar as URLs das perguntas", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar as URLs das perguntas, tente novamente");
        }
    }

    @NotNull
    public List<ModeloChecklistSelecao> getModelosSelecaoRealizacao(@NotNull final Long codUnidade,
                                                                    @NotNull final Long codCargo,
                                                                    @NotNull final String userToken) {
        try {
            return RouterModeloChecklist
                    .create(dao, userToken)
                    .getModelosSelecaoRealizacao(codUnidade, codCargo);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar os modelos de checklist para seleção." +
                    "\ncodUnidade: " + codUnidade +
                    "\ncodCargo: " + codCargo, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar os modelos de checklist para seleção, tente novamente");
        }
    }

    @NotNull
    public ModeloChecklistRealizacao getModeloChecklistRealizacao(@NotNull final Long codModeloChecklist,
                                                                  @NotNull final Long codVeiculo,
                                                                  @NotNull final String placaVeiculo,
                                                                  @NotNull final String tipoChecklist,
                                                                  @NotNull final String userToken) {
        try {
            return RouterModeloChecklist
                    .create(dao, userToken)
                    .getModeloChecklistRealizacao(
                            codModeloChecklist,
                            codVeiculo,
                            placaVeiculo,
                            TipoChecklist.fromString(tipoChecklist));
        } catch (final Throwable throwable) {
            final String errorMessage = String.format("Erro ao buscar modelo de checklist para realização.\n" +
                    "codModeloChecklist: %d\n" +
                    "codVeiculo: %d\n" +
                    "placaVeiculo: %s\n" +
                    "tipoChecklist: %s\n", codModeloChecklist, codVeiculo, placaVeiculo, tipoChecklist);
            Log.e(TAG, errorMessage, throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao iniciar checklist, tente novamente");
        }
    }

    @NotNull
    List<ModeloChecklistListagem> getModelosChecklistListagemByCodUnidade(@NotNull final Long codUnidade)
            throws ProLogException {
        try {
            return dao.getModelosChecklistListagemByCodUnidade(codUnidade);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar os modelos de checklist para a unidade " + codUnidade, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar modelos de checklist, tente novamente");
        }
    }

    @NotNull
    Response updateStatusAtivo(final Long codUnidade,
                               final Long codModelo,
                               final ModeloChecklistEdicao modeloChecklist) throws ProLogException {
        try {
            dao.updateStatusAtivo(
                    codUnidade,
                    codModelo,
                    modeloChecklist.isAtivo(),
                    Injection.provideDadosChecklistOfflineChangedListener());
            return Response.ok("Modelo de checklist " + (modeloChecklist.isAtivo() ? "ativado" : "inativado"));
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao ativar/inativar o modelo de checklist: " + codModelo, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao ativar/inativar o modelo de checklist, tente novamente");
        }
    }

    @NotNull
    List<ModeloChecklistVisualizacao> getModelosChecklistProLog() throws ProLogException {
        try {
            return dao.getModelosChecklistProLog();
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar modelos de checklist do ProLog", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar modelos de checklist do ProLog, tente novamente");
        }
    }

    @NotNull
    Galeria getGaleriaImagensPublicas() throws ProLogException {
        try {
            return dao.getGaleriaImagensPublicas();
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar galeria de imagens publicas", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar galeria de imagens publicas, tente novamente");
        }
    }

    @NotNull
    Galeria getGaleriaImagensEmpresa(@NotNull final Long codEmpresa) throws ProLogException {
        try {
            return dao.getGaleriaImagensEmpresa(codEmpresa);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar galeria de imagens da empresa: " + codEmpresa, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar galeria de imagens da empresa, tente novamente");
        }
    }

    @NotNull
    AbstractResponse insertImagem(@NotNull final Long codEmpresa,
                                  @NotNull final InputStream fileInputStream,
                                  @NotNull final FormDataContentDisposition fileDetail) throws ProLogException {
        try {
            final String imageType = FilenameUtils.getExtension(fileDetail.getFileName());
            final ImagemProlog imagemProLog = UploadImageHelper.uploadCompressedImagem(
                    fileInputStream,
                    AmazonConstants.BUCKET_CHECKLIST_GALERIA_IMAGENS,
                    imageType);
            final Long codImagem = dao.insertImagem(codEmpresa, imagemProLog);
            return ResponseImagemChecklist.ok(
                    "Imagem inserida com sucesso",
                    codImagem,
                    imagemProLog.getUrlImagem());
        } catch (final FileFormatNotSupportException e) {
            Log.e(TAG, "Arquivo recebido não é uma imagem", e);
            return Response.error(e.getMessage());
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir a imagem", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir a imagem, tente novamente");
        }
    }
}