package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.imagens.Galeria;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.checklist.model.PerguntaRespostaChecklist;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
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
            return new ArrayList<>();
        }
    }

    public ModeloChecklist getModeloChecklist(Long codModelo, Long codUnidade) {
        try {
            return dao.getModeloChecklist(codModelo, codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean setModeloChecklistInativo(Long codUnidade, Long codModelo) {
        try {
            return dao.setModeloChecklistInativo(codUnidade, codModelo);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertModeloChecklist(ModeloChecklist modeloChecklist) {
        try {
            return dao.insertModeloChecklist(modeloChecklist);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<PerguntaRespostaChecklist> getPerguntas(Long codUnidade, Long codModelo) {
        try {
            return dao.getPerguntas(codUnidade, codModelo);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Galeria getGaleriaImagensPublicas() {
        try {
            return dao.getGaleriaImagensPublicas();
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar galeria de imagens publicas", e);
            return null;
        }
    }

    public Galeria getGaleriaImagensEmpresa(@NotNull final Long codEmpresa) {
        try {
            return dao.getGaleriaImagensEmpresa(codEmpresa);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar galeria de imagens da empresa: " + codEmpresa, e);
            return null;
        }
    }
}
