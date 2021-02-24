package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.commons.util.NullIf;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.AcomplamentoValidacaoHolder;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.VeiculoEstadoAcoplamento;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class VeiculoAcoplamentoConverter {

    public VeiculoAcoplamentoConverter() {
        throw new IllegalStateException(VeiculoAcoplamentoConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static AcomplamentoValidacaoHolder createHolderAcomplamentoValidacao(
            @NotNull final ResultSet rSet) throws SQLException {
        if (rSet.next()) {
            final Map<Long, VeiculoEstadoAcoplamento> veiculosEstadoAcoplamento = new HashMap<>();
            do {
                final Long codVeiculo = rSet.getLong("cod_veiculo");
                final VeiculoEstadoAcoplamento veiculoEstadoAcoplamento = new VeiculoEstadoAcoplamento(
                        codVeiculo,
                        NullIf.equalOrLess(rSet.getLong("cod_processo_acoplamento_vinculado"), 0),
                        rSet.getShort("posicao_acoplado"),
                        rSet.getBoolean("motorizado"),
                        rSet.getBoolean("possui_hubodometro"));
                veiculosEstadoAcoplamento.put(codVeiculo, veiculoEstadoAcoplamento);
            } while (rSet.next());
            return new AcomplamentoValidacaoHolder(veiculosEstadoAcoplamento);
        } else {
            return new AcomplamentoValidacaoHolder(Collections.emptyMap());
        }
    }
}
