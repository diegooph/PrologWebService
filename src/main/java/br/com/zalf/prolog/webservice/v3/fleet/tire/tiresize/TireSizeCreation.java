package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.v3.validation.CompanyId;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class TireSizeCreation {
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
