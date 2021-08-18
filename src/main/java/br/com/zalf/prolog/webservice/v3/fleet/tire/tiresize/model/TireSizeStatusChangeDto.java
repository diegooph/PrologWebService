package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model;

import br.com.zalf.prolog.webservice.v3.validation.CompanyId;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TireSizeStatusChangeDto {
    @CompanyId
    @NotNull(message = "O código da empresa é obrigatório!")
    private final Long companyId;
    @NotNull(message = "O código da dimensão é obrigatório!")
    private final Long tireSizeId;
    @NotNull(message = "O status da dimensão é obrigatório!")
    private final boolean isActive;
}
