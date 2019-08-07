package br.com.zalf.prolog.webservice.integracao.api.checklist;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 07/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiChecklistConverter {
    @NotNull
    public static ApiAlternativaModeloChecklist convert(@NotNull final ResultSet rSet) throws SQLException {
        return new ApiAlternativaModeloChecklist(
                rSet.getLong("COD_UNIDADE"),
                rSet.getString("NOME_UNIDADE"),
                rSet.getLong("COD_MODELO_CHECKLIST"),
                rSet.getString("NOME_MODELO"),
                rSet.getBoolean("STATUS_MODELO_CHECKLIST"),
                rSet.getLong("CODIGO_PERGUNTA"),
                rSet.getString("DESCRICAO_PERGUNTA"),
                rSet.getBoolean("SINGLE_CHOICE"),
                rSet.getBoolean("STATUS_PERGUNTA"),
                rSet.getLong("CODIGO_ALTERNATIVA"),
                rSet.getString("DESCRICAO_ALTERNATIVA"),
                rSet.getBoolean("ALTERNATIVA_TIPO_OUTROS"),
                ApiPrioridadeAlternativa.fromString(rSet.getString("PRIORIDADE")),
                rSet.getBoolean("DEVE_ABRIR_ORDEM_SERVICO"),
                rSet.getBoolean("STATUS_ALTERNATIVA"));
    }
}
