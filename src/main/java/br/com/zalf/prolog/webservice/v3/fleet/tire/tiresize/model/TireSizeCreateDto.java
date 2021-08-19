package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model;

import br.com.zalf.prolog.webservice.v3.validation.CompanyId;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@Builder(setterPrefix = "with")
public class TireSizeCreateDto {
    @CompanyId
    @NotNull
    private final Long companyId;
    @NotNull
    private final Double height;
    @NotNull
    private final Double width;
    @NotNull
    private final Double rim;
    @Nullable
    private final String additionalId;
}
