package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.empresa.EmpresaDao;
import br.com.zalf.prolog.webservice.permissao.Visao;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class VersaoDadosIntervaloAtualizador implements DadosIntervaloChangedListener {

    @Override
    public void onTiposIntervaloChanged(@NotNull final Connection connection,
                                        @NotNull final Long codUnidade) throws Throwable {
        incrementaVersaoDadosUnidade(connection, codUnidade);
    }

    @Override
    public void onPreUpdateCargo(@NotNull final Connection connection,
                                 @NotNull final EmpresaDao empresaDao,
                                 @NotNull final Visao visaoNova,
                                 @NotNull final Long codCargo,
                                 @NotNull final Long codUnidade) throws Throwable {
        // TODO: Apenas cargos que tem pessoas associadas e o que retorna caso não tenha cargo nenhum? Optional?
        final Visao visaoAtual = empresaDao.getVisaoCargo(codUnidade, codCargo);

        if (visaoAtual == null || visaoNova == null)
            throw new IllegalStateException();

        if (permissaoMarcacaoIntervaloRemovidaOuAdicionada(visaoAtual, visaoNova)) {
            incrementaVersaoDadosUnidade(connection, codUnidade);
        }
    }

    private void incrementaVersaoDadosUnidade(Connection connection, Long codUnidade) throws Throwable {
        final PreparedStatement stmt = connection.prepareStatement("UPDATE INTERVALO_UNIDADE " +
                "SET VERSAO_DADOS = VERSAO_DADOS + 1 WHERE COD_UNIDADE = ?;");
        stmt.setLong(1, codUnidade);
        int count = stmt.executeUpdate();
        if (count == 0) {
            throw new SQLException("Erro ao incrementar versão dos dados para a unidade: " + codUnidade);
        }
    }

    private boolean permissaoMarcacaoIntervaloRemovidaOuAdicionada(Visao visaoAtual, Visao visaoNova) throws Throwable {
        return (visaoAtual.hasAccessToFunction(Pilares.GENTE, Pilares.Gente.Intervalo.MARCAR_INTERVALO)
                && !visaoNova.hasAccessToFunction(Pilares.GENTE, Pilares.Gente.Intervalo.MARCAR_INTERVALO))
                ||
                (!visaoAtual.hasAccessToFunction(Pilares.GENTE, Pilares.Gente.Intervalo.MARCAR_INTERVALO)
                        && visaoNova.hasAccessToFunction(Pilares.GENTE, Pilares.Gente.Intervalo.MARCAR_INTERVALO));
    }
}