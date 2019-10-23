package br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.*;
import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PSQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

/**
 * Created on 13/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiCadastroPneuDaoImpl extends DatabaseConnection implements ApiCadastroPneuDao {

    @NotNull
    @Override
    @SuppressWarnings("Duplicates")
    public List<ApiPneuCargaInicialResponse> inserirCargaInicialPneu(
            @NotNull final String tokenIntegracao,
            @NotNull final List<ApiPneuCargaInicial> pneusCargaInicial) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_PNEU_CARGA_INICIAL_PNEU_PROLOG(" +
                    "F_COD_PNEU_SISTEMA_INTEGRADO := ?, " +
                    "F_CODIGO_PNEU_CLIENTE := ?, " +
                    "F_COD_UNIDADE_PNEU := ?, " +
                    "F_COD_MODELO_PNEU := ?, " +
                    "F_COD_DIMENSAO_PNEU := ?, " +
                    "F_PRESSAO_CORRETA_PNEU := ?, " +
                    "F_VIDA_ATUAL_PNEU := ?, " +
                    "F_VIDA_TOTAL_PNEU := ?, " +
                    "F_DOT_PNEU := ?, " +
                    "F_VALOR_PNEU := ?, " +
                    "F_PNEU_NOVO_NUNCA_RODADO := ?, " +
                    "F_COD_MODELO_BANDA_PNEU := ?, " +
                    "F_VALOR_BANDA_PNEU := ?, " +
                    "F_STATUS_PNEU := ?, " +
                    "F_PLACA_VEICULO_PNEU_APLICADO := ?, " +
                    "F_POSICAO_VEICULO_PNEU_APLICADO := ?, " +
                    "F_DATA_HORA_PNEU_CADASTRO := ?, " +
                    "F_TOKEN_INTEGRACAO := ?) AS COD_PNEU_PROLOG;");
            final List<ApiPneuCargaInicialResponse> pneuCargaInicialResponses = new ArrayList<>();
            final OffsetDateTime dataHoraAtual = Now.offsetDateTimeUtc();
            for (final ApiPneuCargaInicial pneuCargaInicial : pneusCargaInicial) {
                try {
                    stmt.setLong(1, pneuCargaInicial.getCodigoSistemaIntegrado());
                    stmt.setString(2, pneuCargaInicial.getCodigoCliente());
                    stmt.setLong(3, pneuCargaInicial.getCodUnidadePneu());
                    stmt.setLong(4, pneuCargaInicial.getCodModeloPneu());
                    stmt.setLong(5, pneuCargaInicial.getCodDimensaoPneu());
                    stmt.setDouble(6, pneuCargaInicial.getPressaoCorretaPneu());
                    stmt.setInt(7, pneuCargaInicial.getVidaAtualPneu());
                    stmt.setInt(8, pneuCargaInicial.getVidaTotalPneu());
                    bindValueOrNull(stmt, 9, pneuCargaInicial.getDotPneu(), SqlType.VARCHAR);
                    stmt.setBigDecimal(10, pneuCargaInicial.getValorPneu());
                    stmt.setBoolean(11, pneuCargaInicial.getPneuNovoNuncaRodado());
                    bindValueOrNull(stmt, 12, pneuCargaInicial.getCodModeloBanda(), SqlType.BIGINT);
                    bindValueOrNull(stmt, 13, pneuCargaInicial.getValorBandaPneu(), SqlType.NUMERIC);
                    stmt.setString(14, pneuCargaInicial.getStatusPneu().asString());
                    bindValueOrNull(stmt, 15,
                            pneuCargaInicial.getPlacaVeiculoPneuAplicado(), SqlType.VARCHAR);
                    bindValueOrNull(stmt, 16,
                            pneuCargaInicial.getPosicaoPneuAplicado(), SqlType.INTEGER);
                    stmt.setObject(17, dataHoraAtual);
                    stmt.setString(18, tokenIntegracao);
                    rSet = stmt.executeQuery();
                    final long codPneuProlog;
                    if (rSet.next() && (codPneuProlog = rSet.getLong("COD_PNEU_PROLOG")) > 0) {
                        // codPneuProlog > 0 significa que o pneu foi cadastrado com sucesso.
                        pneuCargaInicialResponses.add(ApiPneuCargaInicialResponse.ok(
                                pneuCargaInicial.getCodigoSistemaIntegrado(),
                                pneuCargaInicial.getCodigoCliente(),
                                codPneuProlog));
                    } else {
                        pneuCargaInicialResponses.add(ApiPneuCargaInicialResponse.error(
                                pneuCargaInicial.getCodigoSistemaIntegrado(),
                                pneuCargaInicial.getCodigoCliente()));
                    }
                } catch (final PSQLException sqlException) {
                    // Se ocorreu uma SQLException deveremos mapear a mensagem que está nela.
                    pneuCargaInicialResponses.add(ApiPneuCargaInicialResponse.error(
                            pneuCargaInicial.getCodigoSistemaIntegrado(),
                            pneuCargaInicial.getCodigoCliente(),
                            PostgresUtils.getPSQLErrorMessage(sqlException, ApiPneuCargaInicialResponse.ERROR_MESSAGE)));
                } catch (final Throwable t) {
                    pneuCargaInicialResponses.add(ApiPneuCargaInicialResponse.error(
                            pneuCargaInicial.getCodigoSistemaIntegrado(),
                            pneuCargaInicial.getCodigoCliente()));
                }
            }

            if (pneusCargaInicial.size() != pneuCargaInicialResponses.size()) {
                throw new IllegalStateException("Não foram processados todos os pneus da carga inicial:\n" +
                        "pneusCargaInicial.size: " + pneusCargaInicial.size() + "\n" +
                        "pneuCargaInicialResponses.size: " + pneuCargaInicialResponses.size());
            }

            return pneuCargaInicialResponses;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    @SuppressWarnings("Duplicates")
    public Long inserirPneuCadastro(@NotNull final String tokenIntegracao,
                                    @NotNull final ApiPneuCadastro pneuCadastro) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_PNEU_INSERE_PNEU_PROLOG(" +
                    "F_COD_PNEU_SISTEMA_INTEGRADO := ?, " +
                    "F_CODIGO_PNEU_CLIENTE := ?, " +
                    "F_COD_UNIDADE_PNEU := ?, " +
                    "F_COD_MODELO_PNEU := ?, " +
                    "F_COD_DIMENSAO_PNEU := ?, " +
                    "F_PRESSAO_CORRETA_PNEU := ?, " +
                    "F_VIDA_ATUAL_PNEU := ?, " +
                    "F_VIDA_TOTAL_PNEU := ?, " +
                    "F_DOT_PNEU := ?, " +
                    "F_VALOR_PNEU := ?, " +
                    "F_PNEU_NOVO_NUNCA_RODADO := ?, " +
                    "F_COD_MODELO_BANDA_PNEU := ?, " +
                    "F_VALOR_BANDA_PNEU := ?, " +
                    "F_DATA_HORA_PNEU_CADASTRO := ?, " +
                    "F_TOKEN_INTEGRACAO := ?) AS COD_PNEU_PROLOG;");
            stmt.setLong(1, pneuCadastro.getCodigoSistemaIntegrado());
            stmt.setString(2, pneuCadastro.getCodigoCliente());
            stmt.setLong(3, pneuCadastro.getCodUnidadePneu());
            stmt.setLong(4, pneuCadastro.getCodModeloPneu());
            stmt.setLong(5, pneuCadastro.getCodDimensaoPneu());
            stmt.setDouble(6, pneuCadastro.getPressaoCorretaPneu());
            stmt.setInt(7, pneuCadastro.getVidaAtualPneu());
            stmt.setInt(8, pneuCadastro.getVidaTotalPneu());
            bindValueOrNull(stmt, 9, pneuCadastro.getDotPneu(), SqlType.VARCHAR);
            stmt.setBigDecimal(10, pneuCadastro.getValorPneu());
            stmt.setBoolean(11, pneuCadastro.getPneuNovoNuncaRodado());
            bindValueOrNull(stmt, 12, pneuCadastro.getCodModeloBanda(), SqlType.BIGINT);
            bindValueOrNull(stmt, 13, pneuCadastro.getValorBandaPneu(), SqlType.REAL);
            stmt.setObject(14, Now.offsetDateTimeUtc());
            stmt.setString(15, tokenIntegracao);
            rSet = stmt.executeQuery();
            final long codPneuProlog;
            if (rSet.next() && (codPneuProlog = rSet.getLong("COD_PNEU_PROLOG")) > 0) {
                return codPneuProlog;
            } else {
                throw new SQLException("Erro ao inserir o pneu no Sistema ProLog");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Long atualizarPneuEdicao(@NotNull final String tokenIntegracao,
                                    @NotNull final ApiPneuEdicao pneuEdicao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_PNEU_ATUALIZA_PNEU_PROLOG(" +
                    "F_COD_PNEU_SISTEMA_INTEGRADO := ?, " +
                    "F_NOVO_CODIGO_PNEU_CLIENTE := ?, " +
                    "F_NOVO_COD_MODELO_PNEU := ?, " +
                    "F_NOVO_COD_DIMENSAO_PNEU := ?, " +
                    "F_NOVO_DOT_PNEU := ?, " +
                    "F_NOVO_VALOR_PNEU := ?, " +
                    "F_NOVO_COD_MODELO_BANDA_PNEU := ?, " +
                    "F_NOVO_VALOR_BANDA_PNEU := ?, " +
                    "F_DATA_HORA_EDICAO_PNEU := ?, " +
                    "F_TOKEN_INTEGRACAO := ?) AS COD_PNEU_PROLOG;");
            stmt.setLong(1, pneuEdicao.getCodigoSistemaIntegrado());
            stmt.setString(2, pneuEdicao.getNovoCodigoCliente());
            stmt.setLong(3, pneuEdicao.getNovoCodModeloPneu());
            stmt.setLong(4, pneuEdicao.getNovoCodDimensaoPneu());
            bindValueOrNull(stmt, 5, pneuEdicao.getNovoDotPneu(), SqlType.VARCHAR);
            stmt.setBigDecimal(6, pneuEdicao.getNovoValorPneu());
            bindValueOrNull(stmt, 7, pneuEdicao.getNovoCodModeloBanda(), SqlType.BIGINT);
            bindValueOrNull(stmt, 8, pneuEdicao.getNovoValorBandaPneu(), SqlType.REAL);
            stmt.setObject(9, Now.localDateTimeUtc());
            stmt.setString(10, tokenIntegracao);
            rSet = stmt.executeQuery();
            final long codPneuProlog;
            if (rSet.next() && (codPneuProlog = rSet.getLong("COD_PNEU_PROLOG")) > 0) {
                return codPneuProlog;
            } else {
                throw new SQLException("Erro ao atualizar o pneu no Sistema ProLog");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Long transferirPneu(@NotNull final String tokenIntegracao,
                               @NotNull final ApiPneuTransferencia pneuTransferencia) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            final List<Long> codPneusTransferidos =
                    Injection
                            .providePneuDao()
                            .getCodPneuByCodCliente(
                                    conn,
                                    getCodEmpresaByToken(conn, tokenIntegracao),
                                    pneuTransferencia.getCodPneusTransferidos());
            final Long codColaborador;
            try {
                codColaborador =
                        Injection
                                .provideColaboradorDao()
                                .getCodColaboradorByCpf(
                                        conn,
                                        pneuTransferencia.getCpfColaboradorRealizacaoTransferencia());
            } catch (final Throwable t) {
                throw new GenericException(
                        String.format(
                                "Cpf %s não está cadastrado na base de dados do ProLog",
                                pneuTransferencia.getCpfColaboradorRealizacaoTransferencia()));
            }
            final Long codProcessoTransferencia =
                    Injection
                            .providePneuTransferenciaDao()
                            .insertTransferencia(
                                    conn,
                                    ApiCadastroPneuConverter
                                            .convert(codColaborador, codPneusTransferidos, pneuTransferencia),
                                    Now.offsetDateTimeUtc(),
                                    false);
            conn.commit();
            return codProcessoTransferencia;
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn);
        }
    }

    @NotNull
    private Long getCodEmpresaByToken(@NotNull final Connection conn,
                                      @NotNull final String tokenIntegracao) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT TI.COD_EMPRESA " +
                    "FROM INTEGRACAO.TOKEN_INTEGRACAO TI WHERE TI.TOKEN_INTEGRACAO = ?;");
            stmt.setString(1, tokenIntegracao);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codEmpresa = rSet.getLong("COD_EMPRESA");
                if (codEmpresa <= 0) {
                    throw new SQLException("Não foi possível buscar o código da empresa para o token:\n" +
                            "tokenIntegracao: " + tokenIntegracao + "\n" +
                            "codEmpresa: " + codEmpresa);
                }
                return codEmpresa;
            } else {
                throw new SQLException("Não foi possível buscar o código da empresa para o token:\n" +
                        "tokenIntegracao: " + tokenIntegracao);
            }
        } finally {
            close(stmt, rSet);
        }
    }
}
