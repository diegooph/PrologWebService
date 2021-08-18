package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model;

import br.com.zalf.prolog.webservice.v3.validation.CompanyId;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;

@Data
public class TireSizeUpdateDto {
    @CompanyId
    @NotNull(message = "O código da empresa é obrigatório!")
    private final Long companyId;
    @NotNull(message = "O código da dimensão é obrigatório!")
    private final Long id;
    @NotNull(message = "A altura da dimensão é obrigatória!")
    private final Double height;
    @NotNull(message = "A largura da dimensão é obrigatória!")
    private final Double width;
    @NotNull(message = "O aro da dimensão é obrigatório!")
    private final Double rim;
    @Nullable
    private final String additionalId;
    @NotNull(message = "O status da dimensão é obrigatório!")
    private final boolean isActive;
}
