package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;

/**
 * Created on 2020-11-11
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data(staticConstructor = "of")
public final class VeiculoAcoplamentoProcessoInsert {
    @NotNull
    private final Long codUnidadeAcoplamento;
    @NotNull
    private final Long codColaboradorRealizacao;
    @NotNull
    private final OffsetDateTime dataHoraAtual;
    @Nullable
    private final String observacao;

    @Nullable
    public String getObservacao() {
        return StringUtils.trimToNull(observacao);
    }
}
