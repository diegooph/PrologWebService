package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.TransicaoExistenteUnidade;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.TransicaoVisualizacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.insercao.MotivoMovimentoTransicaoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.insercao.TransicaoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.MotivoMovimentoUnidade;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.TransicaoUnidadeMotivos;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.UnidadeTransicoesMotivoMovimento;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao.MotivoMovimentoTransicaoConverter.createMotivoMovimentoUnidade;
import static br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao.MotivoMovimentoTransicaoConverter.createTransicaoUnidadeMotivos;

/**
 * Created on 2020-03-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class MotivoMovimentoTransicaoDaoImpl extends DatabaseConnection implements
        MotivoMovimentoTransicaoDao {

    @Override
    public void insert(@NotNull final List<MotivoMovimentoTransicaoInsercao> unidades,
                       @NotNull final Long codigoColaboradorInsercao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_MOVIMENTO_TRANSICAO_INSERE(" +
                    "F_COD_MOTIVO := ?," +
                    "F_COD_EMPRESA := ?," +
                    "F_COD_UNIDADE :=?," +
                    "F_ORIGEM := ?::MOVIMENTACAO_ORIGEM_DESTINO_TYPE," +
                    "F_DESTINO := ?::MOVIMENTACAO_ORIGEM_DESTINO_TYPE," +
                    "F_OBRIGATORIO := ?," +
                    "F_DATA_HORA_INSERCAO := ?," +
                    "F_COD_COLABORADOR_INSERCAO := ?)" +
                    "AS COD_MOTIVO_ORIGEM_DESTINO;");

            if (unidades.size() > 0) {
                delete(unidades.get(0).getCodEmpresa(), conn);
            }
            stmt.setObject(7, Now.offsetDateTimeUtc());
            stmt.setLong(8, codigoColaboradorInsercao);

            int totalInserts = 0;
            for (final MotivoMovimentoTransicaoInsercao unidade : unidades) {
                stmt.setLong(2, unidade.getCodEmpresa());
                stmt.setLong(3, unidade.getCodUnidade());

                for (final TransicaoInsercao origemDestino : unidade.getOrigensDestinos()) {
                    stmt.setString(4, origemDestino.getOrigem().asString());
                    stmt.setString(5, origemDestino.getDestino().asString());
                    stmt.setBoolean(6, origemDestino.isObrigatorio());

                    for (final Long codMotivo : origemDestino.getCodMotivos()) {
                        stmt.setLong(1, codMotivo);
                        stmt.addBatch();
                        totalInserts++;
                    }
                }
            }

            if (stmt.executeBatch().length != totalInserts) {
                throw new SQLException("Erro ao inserir relação de origem e destino com motivo");
            }
        } finally {
            close(conn, stmt);
        }
    }

    @NotNull
    @Override
    public void delete(@NotNull final Long codEmpresa, @NotNull final Connection conn) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("SELECT FUNC_MOTIVO_MOVIMENTO_TRANSICAO_DELETA(" +
                    "F_COD_EMPRESA => ?);");
            stmt.setLong(1, codEmpresa);

            stmt.executeQuery();
        } finally {
            close(stmt);
        }
    }

    @NotNull
    @Override
    public TransicaoVisualizacao getTransicaoVisualizacao(@NotNull final Long codTransicao,
                                                          @NotNull final ZoneId timeZone)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_MOVIMENTO_TRANSICAO_VISUALIZACAO(" +
                    "F_COD_MOTIVO_TRANSICAO := ?," +
                    "F_TIME_ZONE := ?);");
            stmt.setLong(1, codTransicao);
            stmt.setString(2, timeZone.toString());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return MotivoMovimentoTransicaoConverter.createTransicaoVisualizacao(rSet);
            } else {
                throw new IllegalStateException("Nenhuma relação motivo, origem e destino foi encontrada com o código: "
                        + codTransicao);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<UnidadeTransicoesMotivoMovimento> getUnidadesTransicoesMotivoMovimento(
            @NotNull final Long codColaborador) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_MOVIMENTO_TRANSICAO_LISTAGEM(" +
                    "F_COD_COLABORADOR := ?)");
            stmt.setLong(1, codColaborador);
            rSet = stmt.executeQuery();

            final List<UnidadeTransicoesMotivoMovimento> unidades = new ArrayList<>();
            long codUltimaUnidade = -1;
            while (rSet.next()) {
                if (codUltimaUnidade != rSet.getLong("codigo_unidade")) {
                    // Trocamos de unidade.
                    unidades.add(MotivoMovimentoTransicaoConverter.createUnidadeTransicoesMotivoMovimento(rSet));
                } else {
                    final UnidadeTransicoesMotivoMovimento ultimaUnidade = unidades.get(unidades.size() - 1);
                    final List<TransicaoUnidadeMotivos> transicoesUltimaUnidade = ultimaUnidade.getTransicoesUnidade();

                    // Verificamos se mudamos a transição comparando a atual com a última.
                    // Isso vai acontecer se origem ou destino tiverem mudado.
                    if (!transicoesUltimaUnidade
                            .get(transicoesUltimaUnidade.size() - 1)
                            .getOrigemMovimento()
                            .asString()
                            .equals(rSet.getString("origem_movimento"))
                            ||
                            !transicoesUltimaUnidade
                                    .get(transicoesUltimaUnidade.size() - 1)
                                    .getDestinoMovimento()
                                    .asString()
                                    .equals(rSet.getString("destino_movimento"))
                                    && rSet.getString("ORIGEM_MOVIMENTO") != null) {
                        // Trocamos de transição, se ela for diferente de null.
                        transicoesUltimaUnidade.add(createTransicaoUnidadeMotivos(rSet));
                    } else {
                        // Estamos na mesma transição, criamos apenas um novo motivo para ela.
                        final List<MotivoMovimentoUnidade> ultimaListaMotivosMovimento = transicoesUltimaUnidade
                                .get(transicoesUltimaUnidade.size() - 1)
                                .getMotivosMovimento();

                        if (rSet.getLong("CODIGO_MOTIVO") != 0) {
                            ultimaListaMotivosMovimento.add(createMotivoMovimentoUnidade(rSet));
                        }
                    }
                }

                codUltimaUnidade = rSet.getLong("codigo_unidade");
            }

            // Preenche a lista de transições da unidade com as possíveis transições que ela não tenha parametrizado.
            preencherTransicoesUnidades(unidades);

            // Ordena a lista de transições das unidades para que fiquem todas com a mesma ordenação.
            unidades.forEach(unidade -> unidade
                    .getTransicoesUnidade()
                    .sort(TransicaoUnidadeMotivos::compareTo));

            return unidades;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public TransicaoUnidadeMotivos getMotivosTransicaoUnidade(
            @NotNull final OrigemDestinoEnum origemMovimento,
            @NotNull final OrigemDestinoEnum destinoMovimento,
            @NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_MOVIMENTO_GET_BY_TRANSICAO(" +
                    "F_ORIGEM := ?::MOVIMENTACAO_ORIGEM_DESTINO_TYPE," +
                    "F_DESTINO := ?::MOVIMENTACAO_ORIGEM_DESTINO_TYPE," +
                    "F_COD_UNIDADE := ?);");
            stmt.setString(1, origemMovimento.asString());
            stmt.setString(2, destinoMovimento.asString());
            stmt.setLong(3, codUnidade);

            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<MotivoMovimentoUnidade> motivos = new ArrayList<>();
                boolean obrigatorioMotivoRetirada;
                do {
                    motivos.add(createMotivoMovimentoUnidade(rSet));
                    obrigatorioMotivoRetirada = rSet.getBoolean("OBRIGATORIO");
                } while (rSet.next());
                return new TransicaoUnidadeMotivos(
                        origemMovimento,
                        destinoMovimento,
                        motivos,
                        obrigatorioMotivoRetirada);
            }

            // Se a unidade não possuir relação para a origem e destino informados, retornará lista vazia com
            // obrigatório null.
            return new TransicaoUnidadeMotivos(
                    origemMovimento,
                    destinoMovimento,
                    Collections.emptyList(),
                    null);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<TransicaoExistenteUnidade> getTransicoesExistentesByUnidade(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_MOVIMENTO_GET_TRANSICAO_BY_UNIDADE(" +
                    "F_COD_UNIDADE := ?);");
            stmt.setLong(1, codUnidade);

            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<TransicaoExistenteUnidade> origensDestinos = new ArrayList<>();
                do {
                    origensDestinos.add(MotivoMovimentoTransicaoConverter.createTransicaoExistenteUnidade(rSet));
                } while (rSet.next());
                return origensDestinos;
            }
            return Collections.emptyList();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private void preencherTransicoesUnidades(@NotNull final List<UnidadeTransicoesMotivoMovimento> unidades) {
        final List<TransicaoUnidadeMotivos> transicoesPossiveisUnidade = TransicaoUtils.getListDeTransicoesPossiveis();

        transicoesPossiveisUnidade.forEach(t ->
                unidades.forEach(unidade ->
                        unidade.getTransicoesUnidade()
                                .stream()
                                .filter(transicao -> transicao.equals(t))
                                .findFirst()
                                .orElseGet(() -> {
                                    unidade.getTransicoesUnidade().add(t);
                                    return t;
                                })));
    }
}
