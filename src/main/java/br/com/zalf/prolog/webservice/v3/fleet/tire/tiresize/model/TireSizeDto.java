package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model;

import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Data
@Builder(setterPrefix = "with")
public class TireSizeDto {
    @NotNull
    private final Long id;
    @NotNull
    private final Double height;
    @NotNull
    private final Double width;
    @NotNull
    private final Double rim;
    @Nullable
    private final String additionalId;
    private final boolean isActive;
    @NotNull
    private final LocalDateTime createdAt;
    @Nullable
    private final Long createdByUserId;
    @Nullable
    private final String createdByUserName;
    @Nullable
    private final LocalDateTime lastUpdateAt;
    @Nullable
    private final Long lastUpdateByUserId;
    @Nullable
    private final String lastUpdateByUserName;
    @Nullable
    private final OrigemAcaoEnum registerOrigin;
}
