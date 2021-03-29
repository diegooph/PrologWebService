package br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Component
public final class AlteracaoKmProcessoMapper {

    @NotNull
    public AlteracaoKmProcesso toAlteracaoKmProcesso(@NotNull final AlteracaoKmProcessoDto dto,
                                                     @Nullable final Long codColaboradorAlteracao) {
        switch (dto.getTipoProcesso()) {
            case AFERICAO:
                return new AfericaoKmProcesso(
                        dto.getCodEmpresa(),
                        dto.getCodVeiculo(),
                        dto.getCodProcesso(),
                        dto.getTipoProcesso(),
                        codColaboradorAlteracao,
                        dto.getNovoKm());
            case FECHAMENTO_SERVICO_PNEU:
                return new ServicoPneuKmProcesso(
                        dto.getCodEmpresa(),
                        dto.getCodVeiculo(),
                        dto.getCodProcesso(),
                        dto.getTipoProcesso(),
                        codColaboradorAlteracao,
                        dto.getNovoKm());
            case CHECKLIST:
                return new ChecklistKmProcesso(
                        dto.getCodEmpresa(),
                        dto.getCodVeiculo(),
                        dto.getCodProcesso(),
                        dto.getTipoProcesso(),
                        codColaboradorAlteracao,
                        dto.getNovoKm());
            case FECHAMENTO_ITEM_CHECKLIST:
                return new ChecklistOrdemServicoItemKmProcesso(
                        dto.getCodEmpresa(),
                        dto.getCodVeiculo(),
                        dto.getCodProcesso(),
                        dto.getTipoProcesso(),
                        codColaboradorAlteracao,
                        dto.getNovoKm());
            case MOVIMENTACAO:
                return new MovimentacaoKmProcesso(
                        dto.getCodEmpresa(),
                        dto.getCodVeiculo(),
                        dto.getCodProcesso(),
                        dto.getTipoProcesso(),
                        codColaboradorAlteracao,
                        dto.getNovoKm());
            case SOCORRO_EM_ROTA:
                return new SocorroRotaKmProcesso(
                        dto.getCodEmpresa(),
                        dto.getCodVeiculo(),
                        dto.getCodProcesso(),
                        dto.getTipoProcesso(),
                        codColaboradorAlteracao,
                        dto.getNovoKm());
            case TRANSFERENCIA_DE_VEICULOS:
                return new TransferenciaVeiculoKmProcesso(
                        dto.getCodEmpresa(),
                        dto.getCodVeiculo(),
                        dto.getCodProcesso(),
                        dto.getTipoProcesso(),
                        codColaboradorAlteracao,
                        dto.getNovoKm());
            case ACOPLAMENTO:
            case EDICAO_DE_VEICULOS:
            default:
                throw new IllegalStateException();
        }
    }
}
