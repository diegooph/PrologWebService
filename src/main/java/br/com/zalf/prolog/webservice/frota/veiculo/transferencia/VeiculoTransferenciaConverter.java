package br.com.zalf.prolog.webservice.frota.veiculo.transferencia;

import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao.VeiculoEnvioTransferencia;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 09/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class VeiculoTransferenciaConverter {

    private VeiculoTransferenciaConverter() {
        throw new IllegalStateException(VeiculoTransferenciaConverter.class.getSimpleName()
                + "cannot be instantiated!");
    }

    @NotNull
    static PneuTransferenciaRealizacao toPneuTransferenciaRealizacao(
            final long codUnidadeOrigem,
            final long codUnidadeDestino,
            final long codColaboradorRealizacaoTransferencia,
            @NotNull final VeiculoEnvioTransferencia veiculoEnvioTransferencia) {
        return new PneuTransferenciaRealizacao(
                codUnidadeOrigem,
                codUnidadeDestino,
                codColaboradorRealizacaoTransferencia,
                veiculoEnvioTransferencia.getCodPneusAplicadosVeiculo(),
                // TODO - Vamos setar algum texto aqui?
                "Transferido junto a Placa");
    }

    @NotNull
    static TipoVeiculoDiagrama createVeiculoSemDiagrama(@NotNull final ResultSet rSet) throws SQLException {
        return new TipoVeiculoDiagrama(
                rSet.getLong("COD_VEICULO"),
                rSet.getString("PLACA_VEICULO"),
                rSet.getLong("COD_TIPO_VEICULO"),
                rSet.getString("NOME_TIPO_VEICULO"),
                rSet.getBoolean("POSSUI_DIAGAMA"));
    }
}
