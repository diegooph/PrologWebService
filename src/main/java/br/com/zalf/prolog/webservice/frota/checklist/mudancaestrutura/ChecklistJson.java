package br.com.zalf.prolog.webservice.frota.checklist.mudancaestrutura;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 2019-08-14
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistJson {
    @NotNull
    private final Long codModeloChecklist;
    @NotNull
    private final Long codPergunta;
    @NotNull
    private final List<Long> codAlternativas;

    public ChecklistJson(@NotNull final Long codModeloChecklist,
                         @NotNull final Long codPergunta,
                         @NotNull final List<Long> codAlternativas) {
        this.codModeloChecklist = codModeloChecklist;
        this.codPergunta = codPergunta;
        this.codAlternativas = codAlternativas;
    }

    @NotNull
    public Long getCodModeloChecklist() {
        return codModeloChecklist;
    }

    @NotNull
    public Long getCodPergunta() {
        return codPergunta;
    }

    @NotNull
    public List<Long> getCodAlternativas() {
        return codAlternativas;
    }

    @NotNull
    public List<ChecklistJson> geraDummy() {
        final List<ChecklistJson> jsons = new ArrayList<>();
        jsons.add(new ChecklistJson(1L, 1L, Arrays.asList(1L, 2L, 3L, 4L)));
        jsons.add(new ChecklistJson(1L, 1L, Arrays.asList(1L, 2L, 3L, 4L)));
        jsons.add(new ChecklistJson(1L, 1L, Arrays.asList(1L, 2L, 3L, 4L)));
        jsons.add(new ChecklistJson(1L, 1L, Arrays.asList(1L, 2L, 3L, 4L)));
        return jsons;
    }

    public static class GeraJsonModeloBd {

        public GeraJsonModeloBd() {

        }

        @NotNull
        public List<ChecklistJson> geraJson(@NotNull final Long codModeloChecklist) throws Throwable {
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rSet = null;
            try {
                conn = DatabaseConnection.getConnection();
                stmt = conn.prepareStatement("select ? as cod_checklist_modelo, " +
                        "       cp.codigo  as cod_pergunta, " +
                        "       array_agg(cap.codigo) as cod_alternativas " +
                        "from checklist_perguntas cp " +
                        "         join checklist_alternativa_pergunta cap on cp.codigo = cap.cod_pergunta " +
                        "where cp.cod_checklist_modelo = ? " +
                        "  and cp.cod_versao_checklist_modelo = (select cm.cod_versao_atual from checklist_modelo_data cm where cm.codigo = ?) " +
                        "group by 2 " +
                        "order by 2");
                stmt.setLong(1, codModeloChecklist);
                stmt.setLong(2, codModeloChecklist);
                stmt.setLong(3, codModeloChecklist);
                rSet = stmt.executeQuery();
                final List<ChecklistJson> jsons = new ArrayList<>();
                while (rSet.next()) {
                    final Long[] codAlternativas = (Long[]) rSet.getArray("COD_ALTERNATIVAS").getArray();
                    jsons.add(new ChecklistJson(
                            rSet.getLong("COD_CHECKLIST_MODELO"),
                            rSet.getLong("COD_PERGUNTA"),
                            Arrays.asList(codAlternativas)));
                }
                return jsons;
            } finally {
                DatabaseConnection.close(conn, stmt, rSet);
            }
        }
    }
}