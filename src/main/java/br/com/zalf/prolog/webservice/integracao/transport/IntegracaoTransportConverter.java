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
        final ItemPendenteIntegracaoTransport item = new ItemPendenteIntegracaoTransport();
        item.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
        item.setKmAberturaServico(rSet.getLong("KM_ABERTURA_SERVICO"));
        item.setCodOrdemServico(rSet.getLong("COD_ORDEM_SERVICO"));
        item.setCodUnidadeOrdemServico(rSet.getLong("COD_UNIDADE_ORDEM_SERVICO"));
        item.setDataHoraAberturaServico(rSet.getObject("DATA_HORA_ABERTURA_SERVICO", LocalDateTime.class));
        item.setCodItemOrdemServico(rSet.getLong("COD_ITEM_ORDEM_SERVICO"));
        item.setCodUnidadeItemOrdemServico(rSet.getLong("COD_UNIDADE_ITEM_ORDEM_SERVICO"));
        item.setDataHoraPrimeiroApontamento(
                rSet.getObject("DATA_HORA_PRIMEIRO_APONTAMENTO", LocalDateTime.class));
        item.setPrazoResolucaoItemHoras(rSet.getInt("PRAZO_RESOLUCAO_ITEM_HORAS"));
        item.setQtdApontamentos(rSet.getInt("QTD_APONTAMENTOS"));
        item.setCodChecklistPrimeiroApontamento(rSet.getLong("COD_CHECKLIST_PRIMEIRO_APONTAMENTO"));
        item.setCodPergunta(rSet.getLong("COD_PERGUNTA"));
        item.setDescricaoPergunta(rSet.getString("DESCRICAO_PERGUNTA"));
        item.setCodAlternativaPergunta(rSet.getLong("COD_ALTERNATIVA_PERGUNTA"));
        item.setDescricaoAlternativa(rSet.getString("DESCRICAO_ALTERNATIVA"));
        item.setTipoOutros(rSet.getBoolean("IS_TIPO_OUTROS"));
        item.setDescricaoTipoOutros(rSet.getString("DESCRICAO_TIPO_OUTROS"));
        item.setPrioridadeAlternativa(
                PrioridadeAlternativa.fromString(rSet.getString("PRIORIDADE_ALTERNATIVA")));
        return item;
    }
}