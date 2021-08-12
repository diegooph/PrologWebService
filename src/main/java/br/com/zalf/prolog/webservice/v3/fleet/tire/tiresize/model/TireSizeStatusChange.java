package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model;

import br.com.zalf.prolog.webservice.v3.validation.CompanyId;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TireSizeStatusChange {
    @CompanyId
    private final Long companyId;
    @NotNull(message = "O status da dimensão é obrigatório!")
    private final Boolean isActive;
}
