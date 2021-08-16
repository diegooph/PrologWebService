package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(setterPrefix = "with")
public class TireSizeVisualization {
    private final Long id;
    private final Double height;
    private final Double width;
    private final Double rim;
    private final String additionalId;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final Long createdAtUserId;
    private final String createdAtUserName;
    private final LocalDateTime lastedUpdateAt;
    private final Long lastedUpdateUserId;
    private final String lastedUpdateUserName;
    private final String registerOrigin;
}
