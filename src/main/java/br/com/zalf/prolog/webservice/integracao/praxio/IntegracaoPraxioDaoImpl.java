package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.integracao.praxio.afericao.AfericaoIntegracaoPraxioConverter;
import br.com.zalf.prolog.webservice.integracao.praxio.afericao.MedicaoIntegracaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoCadastroPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoEdicaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoTransferenciaPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ChecklistParaSincronizar;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ItemOSAbertaGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ItemResolvidoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.OrdemServicoAbertaGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.error.GlobusPiccoloturException;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class IntegracaoPraxioDaoImpl extends DatabaseConnection implements IntegracaoPraxioDao {

    @Override
    public void inserirVeiculoCadastroPraxio(
            @NotNull final String tokenIntegracao,
            @NotNull final VeiculoCadastroPraxio veiculoCadastroPraxio) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_VEICULO_INSERE_VEICULO_PROLOG(" +
                    " F_COD_UNIDADE_VEICULO_ALOCADO   := ?," +
                    " F_PLACA_VEICULO_CADASTRADO      := ?," +
                    " F_KM_ATUAL_VEICULO_CADASTRADO   := ?," +
                    " F_COD_MODELO_VEICULO_CADASTRADO := ?," +
                    " F_COD_TIPO_VEICULO_CADASTRADO   := ?," +
                    " F_DATA_HORA_VEICULO_CADASTRO    := ?," +
                    " F_TOKEN_INTEGRACAO              := ?) AS COD_VEICULO_PROLOG;");
            stmt.setLong(1, veiculoCadastroPraxio.getCodUnidadeAlocado());
            stmt.setString(2, veiculoCadastroPraxio.getPlacaVeiculo());
            stmt.setLong(3, veiculoCadastroPraxio.getKmAtualVeiculo());
            stmt.setLong(4, veiculoCadastroPraxio.getCodModeloVeiculo());
            stmt.setLong(5, veiculoCadastroPraxio.getCodTipoVeiculo());
            stmt.setObject(6, Now.localDateTimeUtc());
            stmt.setString(7, tokenIntegracao);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codVeiculoProlog = rSet.getLong("COD_VEICULO_PROLOG");
                if (codVeiculoProlog <= 0) {
                    throw new SQLException("Erro na function de inserir veículo, não atualizou as tabelas");
                }
            } else {
                throw new SQLException("Erro ao inserir um veículo do Globus no ProLog");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void atualizarVeiculoPraxio(@NotNull final String tokenIntegracao,
                                       @NotNull final Long codUnidadeVeiculoAntesEdicao,
                                       @NotNull final String placaVeiculoAntesEdicao,
                                       @NotNull final VeiculoEdicaoPraxio veiculoEdicaoPraxio) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_VEICULO_ATUALIZA_VEICULO_PROLOG(" +
                    " F_COD_UNIDADE_ORIGINAL_ALOCADO := ?," +
                    " F_PLACA_ORIGINAL_VEICULO       := ?," +
                    " F_NOVO_COD_UNIDADE_ALOCADO     := ?," +
                    " F_NOVA_PLACA_VEICULO           := ?," +
                    " F_NOVO_KM_VEICULO              := ?," +
                    " F_NOVO_COD_MODELO_VEICULO      := ?," +
                    " F_NOVO_COD_TIPO_VEICULO        := ?," +
                    " F_DATA_HORA_EDICAO_VEICULO     := ?," +
                    " F_TOKEN_INTEGRACAO             := ?) AS COD_VEICULO_PROLOG;");
            stmt.setLong(1, codUnidadeVeiculoAntesEdicao);
            stmt.setString(2, placaVeiculoAntesEdicao);
            stmt.setLong(3, veiculoEdicaoPraxio.getCodUnidadeAlocado());
            stmt.setString(4, veiculoEdicaoPraxio.getPlacaVeiculo());
            stmt.setLong(5, veiculoEdicaoPraxio.getNovoKmVeiculo());
            stmt.setLong(6, veiculoEdicaoPraxio.getNovoCodModeloVeiculo());
            stmt.setLong(7, veiculoEdicaoPraxio.getNovoCodTipoVeiculo());
            stmt.setObject(8, Now.localDateTimeUtc());
            stmt.setString(9, tokenIntegracao);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codVeiculoProlog = rSet.getLong("COD_VEICULO_PROLOG");
                if (codVeiculoProlog <= 0) {
                    throw new SQLException("Erro na function de atualizar veículo, não atualizou as tabelas");
                }
            } else {
                throw new SQLException("Erro ao atualizar um veículo do Globus no ProLog");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void transferirVeiculoPraxio(
            @NotNull final String tokenIntegracao,
            @NotNull final VeiculoTransferenciaPraxio veiculoTransferenciaPraxio) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_VEICULO_TRANSFERE_VEICULO(" +
                    "F_COD_UNIDADE_ORIGEM := ?," +
                    "F_COD_UNIDADE_DESTINO := ?," +
                    "F_CPF_COLABORADOR_TRANSFERENCIA := ?," +
                    "F_PLACA := ?," +
                    "F_OBSERVACAO := ?," +
                    "F_TOKEN_INTEGRACAO := ?, " +
                    "F_DATA_HORA := ?)");
            stmt.setLong(1, veiculoTransferenciaPraxio.getCodUnidadeOrigem());
            stmt.setLong(2, veiculoTransferenciaPraxio.getCodUnidadeDestino());
            stmt.setLong(3,
                    Colaborador.formatCpf(veiculoTransferenciaPraxio.getCpfColaboradorRealizacaoTransferencia()));
            stmt.setString(4, veiculoTransferenciaPraxio.getPlacaTransferida());
            bindValueOrNull(stmt, 5, veiculoTransferenciaPraxio.getObservacao(), SqlType.TEXT);
            stmt.setString(6, tokenIntegracao);
            stmt.setObject(7, Now.offsetDateTimeUtc());
            stmt.executeQuery();
        } finally {
            close(conn, stmt);
        }
    }

    @Override
    public void ativarDesativarVeiculoPraxio(@NotNull final String tokenIntegracao,
                                             @NotNull final String placaVeiculo,
                                             @NotNull final Boolean veiculoAtivo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT * FROM INTEGRACAO.FUNC_VEICULO_ATIVA_DESATIVA_VEICULO_PROLOG(" +
                            " F_PLACA_VEICULO             := ?," +
                            " F_ATIVAR_DESATIVAR_VEICULO  := ?," +
                            " F_DATA_HORA_EDICAO_VEICULO  := ?," +
                            " F_TOKEN_INTEGRACAO          := ?) AS COD_VEICULO_PROLOG;");
            stmt.setString(1, placaVeiculo);
            stmt.setBoolean(2, veiculoAtivo);
            stmt.setObject(3, Now.localDateTimeUtc());
            stmt.setString(4, tokenIntegracao);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codVeiculoProlog = rSet.getLong("COD_VEICULO_PROLOG");
                if (codVeiculoProlog <= 0) {
                    throw new SQLException("Erro na function de ativar/desativar veículo, não atualizou as tabelas");
                }
            } else {
                throw new SQLException("Erro ao ativar/desativar um veículo do Globus no ProLog");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

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
                    "SELECT * FROM PICCOLOTUR.FUNC_CHECK_OS_INSERE_ITEM_OS_ABERTA( " +
                            "F_COD_OS_GLOBUS                      := ?, " +
                            "F_COD_UNIDADE_OS                     := ?, " +
                            "F_COD_CHECKLIST                      := ?, " +
                            "F_COD_ITEM_OS_GLOBUS                 := ?, " +
                            "F_COD_CONTEXTO_PERGUNTA_CHECKLIST    := ?, " +
                            "F_COD_CONTEXTO_ALTERNATIVA_CHECKLIST := ?, " +
                            "F_DATA_HORA_SINCRONIZACAO_PENDENCIA  := ?, " +
                            "F_TOKEN_INTEGRACAO                   := ?);");
            final OffsetDateTime dataHoraAtualUtc = Now.offsetDateTimeUtc();
            int totalItensNoBatch = 0;
            for (final OrdemServicoAbertaGlobus ordemServicoAberta : ordensServicoAbertas) {
                // Primeiro setamos as informações de cada O.S..
                stmt.setLong(1, ordemServicoAberta.getCodOsGlobus());
                stmt.setLong(2, ordemServicoAberta.getCodUnidadeOs());
                stmt.setLong(3, ordemServicoAberta.getCodChecklistProLog());
                final List<ItemOSAbertaGlobus> itensOSAbertaGlobus = ordemServicoAberta.getItensOSAbertaGlobus();
                if (itensOSAbertaGlobus.size() <= 0) {
                    throw new GlobusPiccoloturException(
                            String.format(
                                    "[ERRO DE ESTRUTURA] A O.S %d não possui nenhum item",
                                    ordemServicoAberta.getCodOsGlobus()));
                }
                // Depois inserimos as informações de cada item da O.S..
                for (final ItemOSAbertaGlobus itemOSAbertaGlobus : itensOSAbertaGlobus) {
                    stmt.setLong(4, itemOSAbertaGlobus.getCodItemGlobus());
                    stmt.setLong(5, itemOSAbertaGlobus.getCodContextoPerguntaItemOs());
                    stmt.setLong(6, itemOSAbertaGlobus.getCodContextoAlternativaItemOs());
                    stmt.setObject(7, dataHoraAtualUtc);
                    stmt.setString(8, tokenIntegracao);
                    stmt.addBatch();
                    totalItensNoBatch++;
                }
            }
            final int[] batch = stmt.executeBatch();
            if (batch.length != totalItensNoBatch) {
                throw new IllegalStateException(
                        String.format("[INTEGRACAO - PICCOLOTUR] Não foi possível inserir todos os itens:\n" +
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
                    "FROM PICCOLOTUR.FUNC_CHECK_OS_RESOLVE_ITEM_PENDENTE( " +
                    "F_COD_UNIDADE_ITEM_OS           := ?, " +
                    "F_COD_OS_GLOBUS                 := ?, " +
                    "F_COD_ITEM_RESOLVIDO_GLOBUS     := ?, " +
                    "F_CPF_COLABORADOR_RESOLUCAO     := ?, " +
                    "F_PLACA_VEICULO_ITEM_OS         := ?, " +
                    "F_KM_COLETADO_RESOLUCAO         := ?, " +
                    "F_DURACAO_RESOLUCAO_MS          := ?, " +
                    "F_FEEDBACK_RESOLUCAO            := ?, " +
                    "F_DATA_HORA_RESOLVIDO_PROLOG    := ?, " +
                    "F_DATA_HORA_INICIO_RESOLUCAO    := ?, " +
                    "F_DATA_HORA_FIM_RESOLUCAO       := ?, " +
                    "F_TOKEN_INTEGRACAO              := ?, " +
                    "F_DATA_HORA_SINCRONIA_RESOLUCAO := ?);");
            final OffsetDateTime dataHoraAtualUtc = Now.offsetDateTimeUtc();
            int totalItensNoBatch = 0;
            for (final ItemResolvidoGlobus itemResolvido : itensResolvidos) {
                stmt.setLong(1, itemResolvido.getCodUnidadeItemOs());
                stmt.setLong(2, itemResolvido.getCodOsGlobus());
                stmt.setLong(3, itemResolvido.getCodItemResolvidoGlobus());
                stmt.setLong(4, Long.parseLong(itemResolvido.getCpfColaboradorResolucao()));
                stmt.setString(5, itemResolvido.getPlacaVeiculoItemOs());
                stmt.setLong(6, itemResolvido.getKmColetadoResolucao());
                stmt.setLong(7, itemResolvido.getDuracaoResolucaoItemOsMillis());
                stmt.setString(8, itemResolvido.getFeedbackResolucaoItemOs());
                stmt.setObject(
                        9, itemResolvido.getDataHoraResolucaoItemOsUtc().atOffset(ZoneOffset.UTC));
                stmt.setObject(
                        10, itemResolvido.getDataHoraInicioResolucaoItemOsUtc().atOffset(ZoneOffset.UTC));
                stmt.setObject(
                        11, itemResolvido.getDataHoraFimResolucaoItemOsUtc().atOffset(ZoneOffset.UTC));
                stmt.setString(12, tokenIntegracao);
                stmt.setObject(13, dataHoraAtualUtc);
                stmt.addBatch();
                totalItensNoBatch++;
            }
            final int[] batch = stmt.executeBatch();
            if (batch.length != totalItensNoBatch) {
                throw new IllegalStateException(
                        String.format("[INTEGRACAO - PICCOLOTUR] Não foi possível resolver todos os itens:\n" +
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

    @NotNull
    @Override
    public ChecklistParaSincronizar getCodChecklistParaSincronizar() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM PICCOLOTUR.FUNC_CHECK_OS_GET_NEXT_COD_CHECKLIST_PARA_SINCRONIZAR();");
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return new ChecklistParaSincronizar(
                        rSet.getLong("COD_CHECKLIST"),
                        rSet.getBoolean("IS_LAST_COD"));
            } else {
                throw new SQLException("Não foi possível buscar o código do checklist que será sincronizado");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}