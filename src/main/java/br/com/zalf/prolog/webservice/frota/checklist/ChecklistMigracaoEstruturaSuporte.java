package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2019-08-15
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistMigracaoEstruturaSuporte {
    private static final long VERSION_CODE_APP_NOVA_ESTRUTURA = 83L;

    @NotNull
    public Long encontraCodVersaoModeloChecklist(@NotNull final Connection conn,
                                                 @NotNull final Checklist checklist) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CHECKLIST_ENCONTRA_VERSAO_MODELO(" +
                    "F_COD_MODELO_CHECKLIST   := ?, " +
                    "F_PERGUNTAS_ALTERNATIVAS := ?) AS COD_VERSAO;");
            stmt.setLong(1, checklist.getCodModelo());
            stmt.setString(1, GsonUtils.getGson().toJson(createChecklistJson(checklist)));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("COD_VERSAO");
            } else {
                throw new SQLException("Erro ao buscar versão do modelo de checklist para o modelo: "
                        + checklist.getCodModelo());
            }
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    @NotNull
    private List<ChecklistJson> createChecklistJson(@NotNull final Checklist checklist) {
        final List<ChecklistJson> jsons = new ArrayList<>();
        checklist
                .getListRespostas()
                .forEach(pergunta -> {
                    final List<Long> alternativasPergunta = pergunta
                            .getAlternativasResposta()
                            .stream()
                            .map(Alternativa::getCodigo)
                            .collect(Collectors.toList());
                    jsons.add(new ChecklistJson(checklist.getCodModelo(), pergunta.getCodigo(), alternativasPergunta));

                });
        return jsons;
    }

    public static boolean isAppNovaEstruturaChecklist(@NotNull final Checklist checklist) {
        return checklist.getCodVersaoModeloChecklist() != null;
    }
}
