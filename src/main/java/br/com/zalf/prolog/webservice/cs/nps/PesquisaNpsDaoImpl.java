package br.com.zalf.prolog.webservice.cs.nps;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.cs.nps.model.PesquisaNpsBloqueio;
import br.com.zalf.prolog.webservice.cs.nps.model.PesquisaNpsDisponivel;
import br.com.zalf.prolog.webservice.cs.nps.model.PesquisaNpsRealizada;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

/**
 * Created on 2019-10-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PesquisaNpsDaoImpl extends DatabaseConnection implements PesquisaNpsDao {

    @NotNull
    @Override
    public Optional<PesquisaNpsDisponivel> getPesquisaNpsColaborador(@NotNull final Long codColaborador)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareCall("{CALL CS.FUNC_NPS_BUSCA_PESQUISA_DISPONIVEL(" +
                    "F_COD_COLABORADOR := ?," +
                    "F_DATA_ATUAL      := ?)}");
            stmt.setLong(1, codColaborador);
            stmt.setObject(2, Now.getLocalDateUtc());
            rSet = stmt.executeQuery();
            if (rSet.next() && rSet.getLong("COD_PESQUISA_NPS") > 0) {
                return Optional.of(
                        new PesquisaNpsDisponivel(
                                rSet.getLong("COD_PESQUISA_NPS"),
                                rSet.getString("TITULO_PESQUISA"),
                                rSet.getString("BREVE_DESCRICAO_PESQUISA"),
                                rSet.getString("TITULO_PERGUNTA_ESCALA"),
                                rSet.getString("LEGENDA_ESCALA_BAIXA"),
                                rSet.getString("LEGENDA_ESCALA_ALTA"),
                                rSet.getString("TITULO_PERGUNTA_DESCRITIVA")));
            } else {
                return Optional.empty();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Long insereRespostasPesquisaNps(@NotNull final OrigemAcaoEnum origemResposta,
                                           @NotNull final PesquisaNpsRealizada pesquisaRealizada)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareCall("{call cs.func_nps_insere_respostas_pesquisa(" +
                                            "f_cod_pesquisa_nps              := ?," +
                                            "f_cod_colaborador_realizacao    := ?," +
                                            "f_data_hora_realizacao_pesquisa := ?," +
                                            "f_resposta_pergunta_escala      := ?," +
                                            "f_resposta_pergunta_descritiva  := ?," +
                                            "f_origem_resposta               := ?)}");
            stmt.setLong(1, pesquisaRealizada.getCodPesquisaNps());
            stmt.setLong(2, pesquisaRealizada.getCodColaboradorRealizacao());
            stmt.setObject(3, Now.getOffsetDateTimeUtc());
            stmt.setShort(4, pesquisaRealizada.getRespostaPerguntaEscala());
            stmt.setString(5, pesquisaRealizada.getRespostaPerguntaDescritiva());
            stmt.setString(6, origemResposta.asString());
            rSet = stmt.executeQuery();
            if (rSet.next() && rSet.getLong(1) != 0) {
                return rSet.getLong(1);
            } else {
                throw new IllegalStateException("Erro ao inserir pesquisa de NPS respondida");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void bloqueiaPesquisaNpsColaborador(@NotNull final OrigemAcaoEnum origemBloqueio,
                                               @NotNull final PesquisaNpsBloqueio pesquisaBloqueio) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareCall("{call cs.func_nps_bloqueia_pesquisa(" +
                                            "f_cod_pesquisa_nps            := ?," +
                                            "f_cod_colaborador_bloqueio    := ?," +
                                            "f_data_hora_bloqueio_pesquisa := ?," +
                                            "f_origem_bloqueio_pesquisa    := ?)}");
            stmt.setLong(1, pesquisaBloqueio.getCodPesquisaNps());
            stmt.setLong(2, pesquisaBloqueio.getCodColaboradorBloqueio());
            stmt.setObject(3, Now.getOffsetDateTimeUtc());
            stmt.setString(4, origemBloqueio.asString());
            stmt.execute();
        } finally {
            close(conn, stmt);
        }
    }
}