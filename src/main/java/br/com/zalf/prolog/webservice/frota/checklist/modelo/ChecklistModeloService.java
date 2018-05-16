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
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.checklist.model.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.ModeloChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.model.PerguntaRespostaChecklist;
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
    private final ChecklistModeloDao dao = Injection.provideChecklistModeloDao();

    public void insertModeloChecklist(@NotNull final ModeloChecklist modeloChecklist) {
        try {
            dao.insertModeloChecklist(modeloChecklist);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao inserir modelo de checklist", e);
            throw new RuntimeException(e);
        }
    }

    public List<ModeloChecklistListagem> getModelosChecklistListagemByCodUnidadeByCodFuncao(
            @NotNull final Long codUnidade,
            @NotNull final String codFuncao) {
        try {
            return dao.getModelosChecklistListagemByCodUnidadeByCodFuncao(codUnidade, codFuncao);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar os modelos de checklist para o cargo " + codFuncao, e);
            throw new RuntimeException(e);
        }
    }

    public ModeloChecklist getModeloChecklist(@NotNull final Long codUnidade, @NotNull final Long codModelo) {
        try {
            return dao.getModeloChecklist(codUnidade, codModelo);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar o modelo de checklist " + codModelo, e);
            throw new RuntimeException(e);
        }
    }

    public Response updateModeloChecklist(@NotNull final String token,
                                          @NotNull final Long codUnidade,
                                          @NotNull final Long codModelo,
                                          @NotNull final ModeloChecklist modeloChecklist) throws Exception {
        try {
            dao.updateModeloChecklist(token, codUnidade, codModelo, modeloChecklist);
            return Response.ok("Modelo de checklist atualizado com sucesso");
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao atualizar modelo de checklist", e);
            throw new GenericException("Não foi possível atualizar o modelo do checklist",
                    "Erro ao atualizar o modelo de checklist código: " + codModelo,
                    e);
        }
    }

    public List<PerguntaRespostaChecklist> getPerguntas(@NotNull final Long codUnidade, @NotNull final Long codModelo) {
        try {
            return dao.getPerguntas(codUnidade, codModelo);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar perguntas do modelo de checklist " + codModelo, e);
            throw new RuntimeException(e);
        }
    }

    public boolean setModeloChecklistInativo(@NotNull final Long codUnidade, @NotNull final Long codModelo) {
        try {
            return dao.setModeloChecklistInativo(codUnidade, codModelo);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao inativar o modelo de checklist " + codModelo, e);
            throw new RuntimeException(e);
        }
    }

    public List<ModeloChecklist> getModelosChecklistProLog() {
        try {
            return dao.getModelosChecklistProLog();
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar modelos de checklist do ProLog", e);
            throw new RuntimeException(e);
        }
    }

    public List<String> getUrlImagensPerguntas(@NotNull final Long codUnidade, @NotNull final Long codFuncao) {
        try {
            return dao.getUrlImagensPerguntas(codUnidade, codFuncao);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar as URL das perguntas", e);
            return null;
        }
    }

    public Galeria getGaleriaImagensPublicas() {
        try {
            return dao.getGaleriaImagensPublicas();
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar galeria de imagens publicas", e);
            throw new RuntimeException(e);
        }
    }

    public Galeria getGaleriaImagensEmpresa(@NotNull final Long codEmpresa) {
        try {
            return dao.getGaleriaImagensEmpresa(codEmpresa);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar galeria de imagens da empresa: " + codEmpresa, e);
            throw new RuntimeException(e);
        }
    }

    public AbstractResponse insertImagem(@NotNull final Long codEmpresa,
                                         @NotNull final InputStream fileInputStream) {
        try {
            final ImagemProLog imagemProLog = UploadImageHelper.uploadImagem(
                    fileInputStream,
                    AmazonConstants.BUCKET_CHECKLIST_GALERIA_IMAGENS);
            final Long codImagem = dao.insertImagem(codEmpresa, imagemProLog);
            return ResponseImagemChecklist.ok("Imagem inserida com sucesso!", codImagem, imagemProLog.getUrlImagem());
        } catch (FileFormatNotSupportException e) {
            Log.e(TAG, "Arquivo recebido não é uma imagem", e);
            return Response.error(e.getMessage());
        } catch (SQLException | IOException | S3FileSender.S3FileSenderException e) {
            Log.e(TAG, "Erro ao inserir o imagem", e);
            throw new RuntimeException(e);
        }
    }
}