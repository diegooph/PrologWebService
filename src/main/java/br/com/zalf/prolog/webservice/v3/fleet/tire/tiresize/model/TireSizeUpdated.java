package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@Builder(setterPrefix = "with")
public class TireSizeUpdated {
    @NotNull
    private final Long companyId;
    @NotNull
    private final Long tireSizeId;
    @NotNull
    private final Double tireSizeHeight;
    @NotNull
    private final Double tireSizeWidth;
    @NotNull
    private final Double tireSizeRim;
    @Nullable
    private final String additionalId;
    @NotNull
    private final Boolean active;
}
