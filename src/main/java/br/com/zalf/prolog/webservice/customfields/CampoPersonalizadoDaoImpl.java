package br.com.zalf.prolog.webservice.customfields;

import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoFuncaoProlog;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoParaRealizacao;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoResposta;
import br.com.zalf.prolog.webservice.customfields._model.ColunaTabelaResposta;
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
            stmt = conn.prepareStatement("SELECT * FROM FUNC_CAMPO_GET_DISPONIVEIS_MOVIMENTACAO(" +
                    "F_COD_UNIDADE => ?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<CampoPersonalizadoParaRealizacao> campos = new ArrayList<>();
                do {
                    campos.add(CampoPersonalizadoConverter.createCampoPersonalizadoParaRealizacao(rSet));
                } while (rSet.next());
                return campos;
            } else {
                return Collections.emptyList();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @SuppressWarnings("SqlResolve")
    @Override
    public void salvaRespostasCamposPersonalizados(@NotNull final Connection conn,
                                                   @NotNull final CampoPersonalizadoFuncaoProlog funcaoProlog,
                                                   @NotNull final List<CampoPersonalizadoResposta> respostas,
                                                   @Nullable final List<ColunaTabelaResposta> colunasEspecificas)
            throws Throwable {

        // SQL base, com nome da tabela, colunas e valores pendentes para serem setados dinamicamente.
        String sql = "insert into %s (cod_tipo_campo, cod_campo, resposta, resposta_lista_selecao, %s) " +
                "values (?, ?, ?, ? %s)";
        if (colunasEspecificas != null && !colunasEspecificas.isEmpty()) {
            // Como a tabela de respostas possui colunas específicas, precisamos formatar o SQL base para que contenha
            // o nome e o ponto de interrogação (?) para cada uma dessas colunas.
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
                    // Se tiver colunas específicas, setamos os valores para cada uma delas. Utilizamos setObject(...)
                    // e deixamos o driver inferir o tipo.
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
            close(stmt);
        }
    }
}
