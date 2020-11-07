package br.com.zalf.prolog.webservice.integracao.avacorpavilan._model;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 2020-08-19
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class ItemOsIntegracao {
    @NotNull
    private final Long codItemOs;
    @NotNull
    private final Long codAlternativa;
    @Nullable
    private final String codAuxiliarAlternativa;
    @NotNull
    private final String descricaoAlternativa;
    @NotNull
    private final PrioridadeAlternativa prioridadeAlternativa;
    @NotNull
    private final StatusItemOrdemServico statusItemOrdemServico;
    private final boolean alternativaTipoOutros;
    @Nullable
    private final String descricaoAlternativaTipoOutros;
    @Nullable
    private final LocalDateTime dataHoraFechamento;
    @Nullable
    private final String descricaoFechamentoItem;
    @Nullable
    private final Long kmVeiculoFechamentoItem;
    @Nullable
    private final LocalDateTime dataHoraInicioResolucao;
    @Nullable
    private final LocalDateTime dataHoraFimResolucao;

    @Nullable
    public String getCodDefeito() {
        if (StringUtils.isNullOrEmpty(StringUtils.trimToNull(codAuxiliarAlternativa))) {
            return null;
        }
        //noinspection ConstantConditions
        final String[] split = codAuxiliarAlternativa.split(":");
        return StringUtils.trimToNull(split[0]);
    }

    @Nullable
    public String getCodServico() {
        if (StringUtils.isNullOrEmpty(StringUtils.trimToNull(codAuxiliarAlternativa))) {
            return null;
        }
        //noinspection ConstantConditions
        final String[] split = codAuxiliarAlternativa.split(":");
        return StringUtils.trimToNull(split.length >= 2 ? split[1] : null);
    }
}
