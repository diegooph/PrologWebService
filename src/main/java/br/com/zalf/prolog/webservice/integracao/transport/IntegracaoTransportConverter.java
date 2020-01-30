package br.com.zalf.prolog.webservice.integracao.transport;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Created on 03/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class IntegracaoTransportConverter {

    public IntegracaoTransportConverter() {
        throw new IllegalStateException(IntegracaoTransportConverter.class.getSimpleName()
                + " cannot be instatiated!");
    }

    @NotNull
    static ItemPendenteIntegracaoTransport convert(@NotNull final ResultSet rSet) throws SQLException {
        return new ItemPendenteIntegracaoTransport(
                rSet.getString("PLACA_VEICULO"),
                rSet.getLong("KM_ABERTURA_SERVICO"),
                rSet.getLong("COD_ORDEM_SERVICO"),
                rSet.getLong("COD_UNIDADE_ORDEM_SERVICO"),
                rSet.getObject("DATA_HORA_ABERTURA_SERVICO", LocalDateTime.class),
                rSet.getLong("COD_ITEM_ORDEM_SERVICO"),
                rSet.getLong("COD_UNIDADE_ITEM_ORDEM_SERVICO"),
                rSet.getObject("DATA_HORA_PRIMEIRO_APONTAMENTO", LocalDateTime.class),
                rSet.getInt("PRAZO_RESOLUCAO_ITEM_HORAS"),
                rSet.getInt("QTD_APONTAMENTOS"),
                rSet.getLong("COD_CHECKLIST_PRIMEIRO_APONTAMENTO"),
                rSet.getLong("COD_CONTEXTO_PERGUNTA"),
                rSet.getString("DESCRICAO_PERGUNTA"),
                rSet.getLong("COD_CONTEXTO_ALTERNATIVA"),
                rSet.getString("DESCRICAO_ALTERNATIVA"),
                rSet.getBoolean("IS_TIPO_OUTROS"),
                rSet.getString("DESCRICAO_TIPO_OUTROS"),
                PrioridadeAlternativa.fromString(rSet.getString("PRIORIDADE_ALTERNATIVA")));
    }
}