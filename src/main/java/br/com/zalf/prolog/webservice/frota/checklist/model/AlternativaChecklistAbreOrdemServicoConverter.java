package br.com.zalf.prolog.webservice.frota.checklist.model;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 11/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class AlternativaChecklistAbreOrdemServicoConverter extends DatabaseConnection {

    private AlternativaChecklistAbreOrdemServicoConverter() {
        throw new IllegalStateException(AlternativaChecklistAbreOrdemServicoConverter.class.getSimpleName()
                + " cannot be instantiated!");
    }

    @NotNull
    public static AlternativaChecklistAbreOrdemServico createAlternativaChecklistAbreOrdemServico(
            @NotNull final ResultSet rSet) throws SQLException {
        final AlternativaChecklistAbreOrdemServico alternativa = new AlternativaChecklistAbreOrdemServico();
        alternativa.setCodAlteranativa(rSet.getLong("COD_ALTERNATIVA"));
        alternativa.setDescricao(rSet.getString("DESCRICAO_ALERNATIVA"));
        alternativa.setTipoOutros(rSet.getBoolean("ALTERNATIVA_TIPO_OUTROS"));
        alternativa.setPossuiItemPendente(rSet.getBoolean("POSSUI_ITEM_PENDENTE"));
        alternativa.setCodOrdemServico(rSet.getLong("COD_ORDEM_SERVICO"));
        alternativa.setQuantidadeApontamentos(rSet.getInt("QTD_APONTAMENTOS"));
        return alternativa;
    }
}
