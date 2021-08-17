package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Data
@Builder(setterPrefix = "with")
public class TireSizeVisualization {
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
    private final boolean active;
    @NotNull
    private final LocalDateTime createdAt;
    @Nullable
    private final Long createdAtUserId;
    @Nullable
    private final String createdAtUserName;
    @Nullable
    private final LocalDateTime lastedUpdateAt;
    @Nullable
    private final Long lastedUpdateUserId;
    @Nullable
    private final String lastedUpdateUserName;
    @Nullable
    private final String registerOrigin;
}
