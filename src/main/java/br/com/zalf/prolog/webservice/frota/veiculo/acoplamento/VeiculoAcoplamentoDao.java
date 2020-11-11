package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamento;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created on 2020-11-03
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface VeiculoAcoplamentoDao {

    void removeAcoplamentoAtual(@NotNull final Connection conn,
                                @NotNull final Long codProcessoAcoplamento);

    @NotNull
    Long insertProcessoAcoplamento(@NotNull final Connection conn,
                                   @NotNull final Long codUnidadeAcoplamento,
                                   @NotNull final Long codColaboradorRealizacao,
                                   @NotNull final OffsetDateTime dataHoraAtual,
                                   @Nullable final String observacao);

    void insertHistoricoAcoplamentos(@NotNull final Connection conn,
                                     @NotNull final Long codProcessoAcoplamento,
                                     @NotNull final List<VeiculoAcoplamento> acoplamentos);

    void insertEstadoAtualAcoplamentos(@NotNull final Connection conn,
                                       @NotNull final Long codProcessoAcoplamento,
                                       @NotNull final Long codUnidadeAcoplamento,
                                       @NotNull final List<VeiculoAcoplamento> acoplamentos);
}
