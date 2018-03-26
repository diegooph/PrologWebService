package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.AmazonConstants;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.imagens.Galeria;
import br.com.zalf.prolog.webservice.commons.imagens.ImagemProLog;
import br.com.zalf.prolog.webservice.commons.imagens.UploadImageHelper;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.S3FileSender;
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

    public List<ModeloChecklist> getModelosChecklistByCodUnidadeByCodFuncao(Long codUnidade, String codFuncao) {
        try {
            return dao.getModelosChecklistByCodUnidadeByCodFuncao(codUnidade, codFuncao);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public ModeloChecklist getModeloChecklist(Long codModelo, Long codUnidade) {
        try {
            return dao.getModeloChecklist(codModelo, codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean setModeloChecklistInativo(Long codUnidade, Long codModelo) {
        try {
            return dao.setModeloChecklistInativo(codUnidade, codModelo);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean insertModeloChecklist(ModeloChecklist modeloChecklist) {
        try {
            return dao.insertModeloChecklist(modeloChecklist);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<PerguntaRespostaChecklist> getPerguntas(Long codUnidade, Long codModelo) {
        try {
            return dao.getPerguntas(codUnidade, codModelo);
        } catch (SQLException e) {
            e.printStackTrace();
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

    public Long insertImagem(@NotNull final Long codEmpresa,
                             @NotNull final InputStream fileInputStream,
                             @NotNull final ImagemProLog imagemProLog) {
        try {
            return dao.insertImagem(codEmpresa, UploadImageHelper.uploadImagem(
                    imagemProLog,
                    fileInputStream,
                    AmazonConstants.BUCKET_CHECKLIST_GALERIA_IMAGENS));
        } catch (SQLException | IOException | S3FileSender.S3FileSenderException e) {
            Log.e(TAG, "Erro ao inserir o imagem.", e);
            throw new RuntimeException(e);
        }
    }
}