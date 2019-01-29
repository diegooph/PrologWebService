package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.andamento;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created on 29/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class MarcacaoDentroJornada {
    @NotNull
    private final Long codTipoMarcacao;
    @NotNull
    private final String nomeTipoMarcacao;
    @Nullable
    private final Long codMarcacaoInicio;
    @Nullable
    private final Long codMarcacaoFim;
    @Nullable
    private final LocalDateTime dataHoraMarcacaoInicio;
    @Nullable
    private final LocalDateTime dataHoraMarcacaoFim;
    private final boolean inicioFoiAjustado;
    private final boolean fimFoiAjustado;
    private final boolean marcacaoEmAndamento;

    public MarcacaoDentroJornada(@NotNull final Long codTipoMarcacao,
                                 @NotNull final String nomeTipoMarcacao,
                                 @Nullable final Long codMarcacaoInicio,
                                 @Nullable final Long codMarcacaoFim,
                                 @Nullable final LocalDateTime dataHoraMarcacaoInicio,
                                 @Nullable final LocalDateTime dataHoraMarcacaoFim,
                                 final boolean inicioFoiAjustado,
                                 final boolean fimFoiAjustado,
                                 final boolean marcacaoEmAndamento) {
        this.codTipoMarcacao = codTipoMarcacao;
        this.nomeTipoMarcacao = nomeTipoMarcacao;
        this.codMarcacaoInicio = codMarcacaoInicio;
        this.codMarcacaoFim = codMarcacaoFim;
        this.dataHoraMarcacaoInicio = dataHoraMarcacaoInicio;
        this.dataHoraMarcacaoFim = dataHoraMarcacaoFim;
        this.inicioFoiAjustado = inicioFoiAjustado;
        this.fimFoiAjustado = fimFoiAjustado;
        this.marcacaoEmAndamento = marcacaoEmAndamento;
    }

    @NotNull
    public static MarcacaoDentroJornada createDummy(final boolean marcacaoCompleta) {
        if (marcacaoCompleta) {
            return new MarcacaoDentroJornada(
                    1L,
                    "Refeição",
                    232L,
                    null,
                    LocalDateTime.now(),
                    null,
                    true,
                    false,
                    true);
        } else {
            return new MarcacaoDentroJornada(
                    1L,
                    "Descanso",
                    232L,
                    233L,
                    LocalDateTime.now(),
                    LocalDateTime.now().plus(10, ChronoUnit.MINUTES),
                    false,
                    true,
                    false);
        }
    }

    @NotNull
    public Long getCodTipoMarcacao() {
        return codTipoMarcacao;
    }

    @NotNull
    public String getNomeTipoMarcacao() {
        return nomeTipoMarcacao;
    }

    @Nullable
    public Long getCodMarcacaoInicio() {
        return codMarcacaoInicio;
    }

    @Nullable
    public Long getCodMarcacaoFim() {
        return codMarcacaoFim;
    }

    @Nullable
    public LocalDateTime getDataHoraMarcacaoInicio() {
        return dataHoraMarcacaoInicio;
    }

    @Nullable
    public LocalDateTime getDataHoraMarcacaoFim() {
        return dataHoraMarcacaoFim;
    }

    public boolean isInicioFoiAjustado() {
        return inicioFoiAjustado;
    }

    public boolean isFimFoiAjustado() {
        return fimFoiAjustado;
    }

    public boolean isMarcacaoEmAndamento() {
        return marcacaoEmAndamento;
    }
}