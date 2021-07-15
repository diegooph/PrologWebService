package br.com.zalf.prolog.webservice.v3.general.branch._model;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2021-01-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Component
public final class BranchMapper {
    @NotNull
    public List<BranchDto> toDto(@NotNull final List<UnidadeEntity> branches) {
        return branches.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @NotNull
    public BranchDto toDto(@NotNull final UnidadeEntity branch) {
        return new BranchDto(branch.getId(),
                             branch.getName(),
                             branch.getTotalUsers(),
                             branch.getTimezone(),
                             branch.getCreatedAt(),
                             branch.isActive(),
                             branch.getAdditionalId(),
                             branch.getBranchLatitude(),
                             branch.getBranchLongitude(),
                             branch.getGroup().getId(),
                             branch.getGroup().getName());
    }

    @NotNull
    public UnidadeEntity toEntity(@NotNull final BranchUpdateDto dto) {
        return UnidadeEntity.builder()
                .withId(dto.getBranchId())
                .withName(dto.getBranchName())
                .withAdditionalId(dto.getBranchAdditionalId())
                .withBranchLatitude(dto.getBranchLatitude())
                .withBranchLongitude(dto.getBranchLongitude())
                .build();
    }
}
