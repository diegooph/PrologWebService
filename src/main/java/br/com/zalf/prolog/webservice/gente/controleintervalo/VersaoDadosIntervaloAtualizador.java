package br.com.zalf.prolog.webservice.gente.controleintervalo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class VersaoDadosIntervaloAtualizador implements DadosIntervaloChangedListener {

    @Override
    public void onTiposIntervaloChanged(Connection connection, Long codUnidade) throws Throwable {
        final PreparedStatement stmt = connection.prepareStatement("UPDATE INTERVALO_UNIDADE " +
                "SET VERSAO_DADOS = VERSAO_DADOS + 1 WHERE COD_UNIDADE = ?;");
        stmt.setLong(1, codUnidade);
        int count = stmt.executeUpdate();
        if (count == 0) {
            throw new SQLException("Erro ao incrementar versão dos dados para a unidade: " + codUnidade);
        }
    }

    @Override
    public void onCargoAtualizado() {

    }
}