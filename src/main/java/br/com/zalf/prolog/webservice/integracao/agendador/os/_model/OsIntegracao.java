package br.com.zalf.prolog.webservice.integracao.agendador.os._model;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 2020-08-18
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class OsIntegracao {
    @NotNull
    private final Long codUnidade;
    @Nullable
    private final String codAuxiliarUnidade;
    @NotNull
    private final Long codInternoOsProlog;
    @NotNull
    private final Long codOsProlog;
    @NotNull
    private final LocalDateTime dataHoraAbertura;
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final Long kmVeiculoNaAbertura;
    @NotNull
    private final String cpfColaboradorChecklist;
    @NotNull
    private final List<ItemOsIntegracao> itensNok;

    @Nullable
    public String getCodFilial() {
        if (StringUtils.isNullOrEmpty(StringUtils.trimToNull(codAuxiliarUnidade))) {
            return null;
        }
        //noinspection ConstantConditions
        final String[] split = codAuxiliarUnidade.split(":");
        return StringUtils.trimToNull(split[0]);
    }

    @Nullable
    public String getCodUnidade() {
        if (StringUtils.isNullOrEmpty(StringUtils.trimToNull(codAuxiliarUnidade))) {
            return null;
        }
        //noinspection ConstantConditions
        final String[] split = codAuxiliarUnidade.split(":");
        return StringUtils.trimToNull(split.length >= 2 ? split[1] : null);
    }
}
