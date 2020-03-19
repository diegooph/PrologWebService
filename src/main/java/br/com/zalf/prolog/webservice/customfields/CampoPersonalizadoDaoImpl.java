package br.com.zalf.prolog.webservice.customfields;

import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static br.com.zalf.prolog.webservice.commons.util.PostgresUtils.listToArray;

/**
 * Created on 2020-03-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class CampoPersonalizadoDaoImpl extends DatabaseConnection implements CampoPersonalizadoDao {

    @NotNull
    @Override
    public List<CampoPersonalizadoParaRealizacao> getCamposParaRealizacaoMovimentacao(@NotNull final Long codUnidade)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * " +
                    "from campo_personalizado_empresa cpe " +
                    "         join movimentacao_campo_personalizado_unidade mcpu " +
                    "              on cpe.codigo = mcpu.cod_campo " +
                    "where cpe.status_ativo = true " +
                    "  and mcpu.habilitado_para_uso = true " +
                    "  and mcpu.cod_unidade = ?;");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<CampoPersonalizadoParaRealizacao> campos = new ArrayList<>();
                do {
                    campos.add(new CampoPersonalizadoParaRealizacao(
                            rSet.getLong("COD_CAMPO"),
                            rSet.getLong("COD_EMPRESA"),
                            rSet.getShort("COD_FUNCAO_PROLOG"),
                            TipoCampoPersonalizado.fromCodigo(rSet.getInt("COD_TIPO_CAMPO")),
                            rSet.getString("NOME_CAMPO"),
                            rSet.getString("DESCRICAO_CAMPO"),
                            rSet.getString("TEXTO_AUXILIO_PREENCHIMENTO_CAMPO"),
                            rSet.getBoolean("PREENCHIMENTO_OBRIGATORIO"),
                            rSet.getString("MENSAGEM_CASO_CAMPO_NAO_PREENCHIDO"),
                            rSet.getBoolean("PERMITE_SELECAO_MULTIPLA"),
                            ((String[]) rSet.getArray("OPCOES_SELECAO").getArray())));
                } while (rSet.next());
                return campos;
            } else {
                return Collections.emptyList();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void salvaRespostasCamposPersonalizados(@NotNull final Connection conn,
                                                   @NotNull final CampoPersonalizadoFuncaoProlog funcaoProlog,
                                                   @NotNull final List<CampoPersonalizadoResposta> respostas,
                                                   @Nullable final List<ColunaTabelaResposta> colunasEspecificas)
            throws Throwable {

        String sql = "insert into %s (cod_tipo_campo, cod_campo, resposta, resposta_lista_selecao %s) " +
                "values (?, ?, ?, ? %s)";
        if (colunasEspecificas != null && !colunasEspecificas.isEmpty()) {
            final String nomesColunas = colunasEspecificas
                    .stream()
                    .map(ColunaTabelaResposta::getNomeColuna)
                    .collect(Collectors.joining(","));
            final String questionMarks = IntStream.of(colunasEspecificas.size())
                    .mapToObj(i -> ",?")
                    .collect(Collectors.joining());
            sql = String.format(sql, funcaoProlog.getTableNameRespostas(), nomesColunas, questionMarks);
        } else {
            sql = String.format(sql, funcaoProlog.getTableNameRespostas(), "", "");
        }

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (final CampoPersonalizadoResposta resposta : respostas) {
                stmt.setLong(1, resposta.getTipoCampo().getCodigoTipoCampo());
                stmt.setLong(2, resposta.getCodCampo());
                stmt.setString(3, resposta.getResposta());
                if (resposta.getRespostaListaSelecao() != null) {
                    stmt.setArray(4, listToArray(conn, SqlType.TEXT, resposta.getRespostaListaSelecao()));
                } else {
                    stmt.setNull(4, Types.NULL);
                }
                if (colunasEspecificas != null && !colunasEspecificas.isEmpty()) {
                    int nextParameterIndex = 5;
                    for (final ColunaTabelaResposta coluna : colunasEspecificas) {
                        stmt.setObject(nextParameterIndex, coluna.getValorColuna());
                        nextParameterIndex++;
                    }
                }
                stmt.addBatch();
            }

            final int[] batchResult = stmt.executeBatch();
            if (batchResult.length != respostas.size()) {
                throw new IllegalStateException(
                        String.format("Insert affected incorrect number of rows. Expected: %d - Actual: %d",
                                respostas.size(),
                                batchResult.length));
            }

            final boolean everyBatchAffectsOneRow = IntStream
                    .of(batchResult)
                    .allMatch(result -> result == 1);
            if (!everyBatchAffectsOneRow) {
                throw new IllegalStateException("Alguma das operações de insert em batch falhou!");
            }
        } finally {
            close(conn, stmt);
        }
    }
}
