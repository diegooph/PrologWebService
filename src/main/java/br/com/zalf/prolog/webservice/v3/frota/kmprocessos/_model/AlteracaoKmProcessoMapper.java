package br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model;

import br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor.AlteracaoKmProcesso;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Component
public final class AlteracaoKmProcessoMapper {

    @NotNull
    public AlteracaoKmProcesso toAlteracaoKmProcesso(@NotNull final AlteracaoKmProcessoDto dto) {
        switch (dto.getTipoProcesso()) {
            case AFERICAO:
                return new AfericaoKmProcesso(
                        dto.getCodEmpresa(),
                        dto.getCodProcesso(),
                        dto.getNovoKm());
            case FECHAMENTO_SERVICO_PNEU:
                return new ServicoPneuKmProcesso(
                        dto.getCodEmpresa(),
                        dto.getCodProcesso(),
                        dto.getNovoKm());
            case CHECKLIST:
                return new ChecklistKmProcesso(
                        dto.getCodEmpresa(),
                        dto.getCodProcesso(),
                        dto.getNovoKm());
            case FECHAMENTO_ITEM_CHECKLIST:
                return new ChecklistOrdemServicoItemKmProcesso(
                        dto.getCodEmpresa(),
                        dto.getCodProcesso(),
                        dto.getNovoKm());
            case MOVIMENTACAO:
                return new MovimentacaoKmProcesso(
                        dto.getCodEmpresa(),
                        dto.getCodProcesso(),
                        dto.getNovoKm());
            case SOCORRO_EM_ROTA:
                return new SocorroRotaKmProcesso(
                        dto.getCodEmpresa(),
                        dto.getCodProcesso(),
                        dto.getNovoKm());
            case TRANSFERENCIA_DE_VEICULO:
                return new TransferenciaVeiculoKmProcesso(
                        dto.getCodEmpresa(),
                        dto.getCodProcesso(),
                        dto.getNovoKm());
            case ACOPLAMENTO:
            case EDICAO_DE_VEICULOS:
            default:
                throw new IllegalStateException();
        }
    }
}
