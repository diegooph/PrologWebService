package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.integracao.praxio.afericao.AfericaoIntegracaoPraxioConverter;
import br.com.zalf.prolog.webservice.integracao.praxio.afericao.MedicaoIntegracaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.ItemOSAbertaGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.ItemResolvidoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.OrdemServicoAbertaGlobus;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class IntegracaoPraxioDaoImpl extends DatabaseConnection implements IntegracaoPraxioDao {

    @NotNull
    @Override
    public List<MedicaoIntegracaoPraxio> getAfericoesRealizadas(
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUltimaAfericao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_INTEGRACAO_BUSCA_AFERICOES_EMPRESA(?, ?);");
            stmt.setString(1, tokenIntegracao);
            stmt.setLong(2, codUltimaAfericao);
            rSet = stmt.executeQuery();
            final List<MedicaoIntegracaoPraxio> medicoes = new ArrayList<>();
            while (rSet.next()) {
                medicoes.add(AfericaoIntegracaoPraxioConverter.convert(rSet));
            }
            return medicoes;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void inserirOrdensServicoGlobus(
            @NotNull final String tokenIntegracao,
            @NotNull final List<OrdemServicoAbertaGlobus> ordensServicoAbertas) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(
                    "SELECT * FROM TP_TRANSPORTES.FUNC_CHECK_OS_INSERE_ITEM_OS_ABERTA( " +
                            "F_COD_OS_GLOBUS                     := ?, " +
                            "F_COD_UNIDADE_OS                    := ?, " +
                            "F_COD_CHECKLIST                     := ?, " +
                            "F_COD_ITEM_OS_GLOBUS                := ?, " +
                            "F_COD_PERGUNTA_CHECKLIST            := ?, " +
                            "F_COD_ALTERNATIVA_CHECKLIST         := ?, " +
                            "F_DATA_HORA_SINCRONIZACAO_PENDENCIA := ?, " +
                            "F_TOKEN_INTEGRACAO                  := ?);");
            final OffsetDateTime dataHoraAtualUtc = Now.offsetDateTimeUtc();
            int totalItensNoBatch = 0;
            for (final OrdemServicoAbertaGlobus ordemServicoAberta : ordensServicoAbertas) {
                // Primeiro setamos as informações de cada O.S..
                stmt.setLong(1, ordemServicoAberta.getCodOsGlobus());
                stmt.setLong(2, ordemServicoAberta.getCodUnidadeOs());
                stmt.setLong(3, ordemServicoAberta.getCodChecklistProLog());
                final List<ItemOSAbertaGlobus> itensOSAbertaGlobus = ordemServicoAberta.getItensOSAbertaGlobus();
                if (itensOSAbertaGlobus.size() <= 0) {
                    throw new GenericException(
                            String.format(
                                    "[ERRO DE ESTRUTURA] A O.S %d não possui nenhum item",
                                    ordemServicoAberta.getCodOsGlobus()));
                }
                // Depois inserimos as informações de cada item da O.S..
                for (final ItemOSAbertaGlobus itemOSAbertaGlobus : itensOSAbertaGlobus) {
                    stmt.setLong(4, itemOSAbertaGlobus.getCodItemGlobus());
                    stmt.setLong(5, itemOSAbertaGlobus.getCodPerguntaItemOs());
                    stmt.setLong(6, itemOSAbertaGlobus.getCodAlternativaItemOs());
                    stmt.setObject(7, dataHoraAtualUtc);
                    stmt.setString(8, tokenIntegracao);
                    stmt.addBatch();
                    totalItensNoBatch++;
                }
            }
            final int[] batch = stmt.executeBatch();
            if (batch.length != totalItensNoBatch) {
                throw new IllegalStateException(
                        String.format("[INTEGRACAO - TP TRANSPORTES] Não foi possível inserir todos os itens:\n" +
                                "totalItensNoBatch: %d\n" +
                                "batchLength: %d", totalItensNoBatch, batch.length));
            }
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn, stmt);
        }
    }

    @Override
    public void resolverMultiplosItens(@NotNull final String tokenIntegracao,
                                       @NotNull final List<ItemResolvidoGlobus> itensResolvidos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM TP_TRANSPORTES.FUNC_CHECK_OS_RESOLVE_ITEM_PENDENTE(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            final LocalDateTime dataHoraAtualUtc = Now.localDateTimeUtc();
            int totalItensNoBatch = 0;
            for (final ItemResolvidoGlobus itemResolvido : itensResolvidos) {
                stmt.setLong(1, itemResolvido.getCodUnidadeItemOs());
                stmt.setLong(2, itemResolvido.getCodOsGlobus());
                stmt.setLong(3, itemResolvido.getCodItemResolvidoGlobus());
                stmt.setLong(4, Long.valueOf(itemResolvido.getCpfColaboradorResolucao()));
                stmt.setString(5, itemResolvido.getPlacaVeiculoItemOs());
                stmt.setLong(6, itemResolvido.getKmColetadoResolucao());
                stmt.setLong(7, itemResolvido.getDuracaoResolucaoItemOsMillis());
                stmt.setString(8, itemResolvido.getFeedbackResolucaoItemOs());
                stmt.setObject(9, itemResolvido.getDataHoraResolucaoItemOsUtc());
                stmt.setObject(10, itemResolvido.getDataHoraInicioResolucaoItemOsUtc());
                stmt.setObject(11, itemResolvido.getDataHoraFimResolucaoItemOsUtc());
                stmt.setString(12, tokenIntegracao);
                stmt.setObject(13, dataHoraAtualUtc);
                stmt.addBatch();
                totalItensNoBatch++;
            }
            final int[] batch = stmt.executeBatch();
            if (batch.length != totalItensNoBatch) {
                throw new IllegalStateException(
                        String.format("[INTEGRACAO - TP TRANSPORTES] Não foi possível inserir todos os itens:\n" +
                                "totalItensNoBatch: %d\n" +
                                "batchLength: %d", totalItensNoBatch, batch.length));
            }
            conn.commit();
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn, stmt);
        }
    }
}