package br.com.zalf.prolog.webservice.gente.controlejornada;

import br.com.zalf.prolog.webservice.autenticacao._model.token.TokenGenerator;
import br.com.zalf.prolog.webservice.gente.colaborador.ColaboradorDao;
import br.com.zalf.prolog.webservice.gente.colaborador.model.ColaboradorEdicao;
import br.com.zalf.prolog.webservice.gente.colaborador.model.ColaboradorInsercao;
import br.com.zalf.prolog.webservice.gente.empresa.EmpresaDao;
import br.com.zalf.prolog.webservice.permissao.Visao;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.permissao.pilares.Pilares.Gente.Intervalo.MARCAR_INTERVALO;

public final class VersaoDadosIntervaloAtualizador implements DadosIntervaloChangedListener {

    public VersaoDadosIntervaloAtualizador() {

    }

    @Override
    public void onTiposMarcacaoChanged(@NotNull final Connection connection,
                                       @NotNull final Long codUnidade) throws Throwable {
        incrementaVersaoDadosUnidade(connection, codUnidade);
    }

    @Override
    public void onCargoAtualizado(@NotNull final Connection connection,
                                  @NotNull final EmpresaDao empresaDao,
                                  @NotNull final Visao visaoNova,
                                  @NotNull final Long codCargo,
                                  @NotNull final Long codUnidade) throws Throwable {
        final Visao visaoAtual = empresaDao.getVisaoCargo(codUnidade, codCargo);

        if (visaoAtual == null) {
            throw new IllegalStateException();
        }

        if (permissaoMarcacaoIntervaloRemovidaOuAdicionada(visaoAtual, visaoNova)) {
            incrementaVersaoDadosUnidade(connection, codUnidade);
        }
    }

    @Override
    public void onColaboradorInativado(@NotNull final Connection connection,
                                       @NotNull final ColaboradorDao colaboradorDao,
                                       @NotNull final Long cpf) throws Throwable {
        final boolean colaboradorTemAcessoMarcacaoIntervalo =
                colaboradorDao.colaboradorTemAcessoFuncao(cpf, Pilares.GENTE, MARCAR_INTERVALO);

        // Se ele tinha acesso à marcação precisamos atualizar a versão dos dados para garantir
        // que caso esse colaborador esteja no BD de algum aplicativo, ele será removido.
        if (colaboradorTemAcessoMarcacaoIntervalo) {
            incrementaVersaoDadosUnidade(connection, colaboradorDao.getCodUnidadeByCpf(cpf));
        }
    }

    @Override
    public void onColaboradorInserido(@NotNull final Connection connection,
                                      @NotNull final EmpresaDao empresaDao,
                                      @NotNull final ColaboradorInsercao colaborador) throws Throwable {
        final Visao visaoCargoColaborador = empresaDao.getVisaoCargo(
                colaborador.getCodUnidade(),
                colaborador.getCodFuncao());

        if (visaoCargoColaborador == null) {
            throw new IllegalStateException();
        }

        // Se o cargo no qual esse colaborador foi adicionado tem permissão para marcar intervalo, precisamos
        // incrementar a versão dos dados para invalidar o BD dos aplicativos.
        if (visaoCargoColaborador.hasAccessToFunction(Pilares.GENTE, MARCAR_INTERVALO)) {
            incrementaVersaoDadosUnidade(connection, colaborador.getCodUnidade());
        }
    }

    @Override
    public void onColaboradorAtualizado(@NotNull final Connection connection,
                                        @NotNull final EmpresaDao empresaDao,
                                        @NotNull final ColaboradorDao colaboradorDao,
                                        @NotNull final ColaboradorEdicao colaborador,
                                        @NotNull final Long cpfAntigo) throws Throwable {
        // Como atualização ainda não foi concretizada, essa busca retorna se o cargo antigo do colaborador lhe dava
        // ou não acesso a marcação de intervalo.
        final boolean colaboradorTemAcessoMarcacaoIntervalo =
                colaboradorDao.colaboradorTemAcessoFuncao(cpfAntigo, Pilares.GENTE, MARCAR_INTERVALO);

        // O colaborador pode ter tido seu cargo alterado nesse update. Através do código do cargo, buscamos a sua visão.
        final Visao visaoCargoColaborador = empresaDao.getVisaoCargo(
                colaborador.getCodUnidade(),
                colaborador.getCodFuncao());

        if (visaoCargoColaborador == null) {
            throw new IllegalStateException();
        }

        // Se o colaborador tinha permissão de marcação e parou de ter ou vice-versa, precisamos incrementar a versão
        // dos dados para invalidar o BD dos aplicativos.
        if (colaboradorTemAcessoMarcacaoIntervalo
                && !visaoCargoColaborador.hasAccessToFunction(Pilares.GENTE, MARCAR_INTERVALO)
                || !colaboradorTemAcessoMarcacaoIntervalo
                && visaoCargoColaborador.hasAccessToFunction(Pilares.GENTE, MARCAR_INTERVALO)) {
            incrementaVersaoDadosUnidade(connection, colaborador.getCodUnidade());
        }
    }

    private void incrementaVersaoDadosUnidade(@NotNull final Connection connection,
                                              @NotNull final Long codUnidade) throws Throwable {
        if (!updateVersaoDadosUnidade(connection, codUnidade)) {
            if (!insertVersaoDadosUnidade(connection, codUnidade)) {
                throw new SQLException("Erro ao incrementar versão dos dados para a unidade: " + codUnidade);
            }
        }
    }

    private boolean updateVersaoDadosUnidade(@NotNull final Connection connection,
                                             @NotNull final Long codUnidade) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("UPDATE INTERVALO_UNIDADE " +
                    "SET VERSAO_DADOS = VERSAO_DADOS + 1 WHERE COD_UNIDADE = ?;");
            stmt.setLong(1, codUnidade);
            // retorna false caso nenhuma linha tenha sido afetada, ou seja, unidade não possui dados na tabela
            return stmt.executeUpdate() > 0;
        } finally {
            close(stmt);
        }
    }

    private boolean insertVersaoDadosUnidade(@NotNull final Connection connection,
                                             @NotNull final Long codUnidade) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(
                    "INSERT INTO INTERVALO_UNIDADE(COD_UNIDADE, VERSAO_DADOS, TOKEN_SINCRONIZACAO_MARCACAO) "
                            + "VALUES (?, 1, ?)");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, getValidTokenMarcacaoJornada(connection).toUpperCase());
            // retorna false caso nenhuma linha tenha sido afetada, ou seja, unidade não possui dados na tabela
            return stmt.executeUpdate() > 0;
        } finally {
            close(stmt);
        }
    }

    @NotNull
    private String getValidTokenMarcacaoJornada(@NotNull final Connection connection) throws SQLException {
        final String tokenMarcacao = new TokenGenerator().getNextToken();
        if (tokenExiste(connection, tokenMarcacao)) {
            getValidTokenMarcacaoJornada(connection);
        }
        return tokenMarcacao;
    }

    private boolean tokenExiste(@NotNull final Connection connection,
                                @NotNull final String tokenMarcacao) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = connection.prepareStatement("SELECT EXISTS(SELECT TOKEN_SINCRONIZACAO_MARCACAO " +
                    " FROM INTERVALO_UNIDADE WHERE TOKEN_SINCRONIZACAO_MARCACAO = ?) AS EXISTE_TOKEN;");
            stmt.setString(1, tokenMarcacao);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("EXISTE_TOKEN");
            } else {
                throw new SQLException(
                        "Não foi possível verifica a existencia do token de sincronia de marcação: " + tokenMarcacao);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private boolean permissaoMarcacaoIntervaloRemovidaOuAdicionada(@NotNull final Visao visaoAtual,
                                                                   @NotNull final Visao visaoNova) {
        return (visaoAtual.hasAccessToFunction(Pilares.GENTE, MARCAR_INTERVALO)
                && !visaoNova.hasAccessToFunction(Pilares.GENTE, MARCAR_INTERVALO))
                || (!visaoAtual.hasAccessToFunction(Pilares.GENTE, MARCAR_INTERVALO)
                && visaoNova.hasAccessToFunction(Pilares.GENTE, MARCAR_INTERVALO));
    }
}