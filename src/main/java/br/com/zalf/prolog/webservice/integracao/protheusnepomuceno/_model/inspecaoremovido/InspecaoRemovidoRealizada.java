package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.inspecaoremovido;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Data
public class InspecaoRemovidoRealizada {
    @NotNull
    private final String codSistemaPneu;
    @NotNull
    private final String codFogoPneu;
    @NotNull
    private final Double menorSulcoPneu;
    @NotNull
    private final Double maiorSulcoPneu;
    @Nullable
    private final Double pressaoPneu;
    @NotNull
    private final String codLipPneu;
    @NotNull
    private final String codOrigemFilial;
    @NotNull
    private final String codDestinoPneu;
    @Nullable
    private final String codCausaSucataPneu;
    @NotNull
    private final LocalDateTime dataHoraAnaliseUtc;
    @NotNull
    @SerializedName("dataHoraAnalise")
    private final LocalDateTime dataHoraAfericaoTimeZoneAplicado;
    @NotNull
    private final String cpfResponsavelAnalise;
    @NotNull
    private final String nomeResponsavelAnalise;
    @Nullable
    private final String observacaoAnalise;
}