package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "with")
public class TireSizeListing {
    private final Long id;
    private Long companyId;
    private Double height;
    private Double width;
    private Double rim;
    private String additionalId;
    private boolean isActive;
}
