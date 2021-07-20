package br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance;

import br.com.zalf.prolog.webservice.v3.fleet.afericao._model.AfericaoAlternativaEntity;
import br.com.zalf.prolog.webservice.v3.fleet.afericao.valores._model.AfericaoPneuValorEntity;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.ColaboradorEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.ServicoPneuEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
public class TireMaintenanceMapper {
    @NotNull
    public List<TireMaintenanceDto> toDto(@NotNull final List<ServicoPneuEntity> tireMaintenances) {
        return tireMaintenances.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @NotNull
    private TireMaintenanceDto toDto(@NotNull final ServicoPneuEntity tireMaintenance) {
        final Optional<AfericaoPneuValorEntity> valor = tireMaintenance.getValorAfericaoRelatedToPneu();
        final Optional<ColaboradorEntity> mecanico = Optional.ofNullable(tireMaintenance.getMecanico());
        final Optional<AfericaoAlternativaEntity> alternativa = Optional.ofNullable(tireMaintenance.getAlternativa());
        return TireMaintenanceDto.builder()
                .tireMaintenanceId(tireMaintenance.getCodigo())
                .tireMaintenanceBranchId(tireMaintenance.getCodUnidade())
                .tireMaintenanceType(tireMaintenance.getTipoServico())
                .quantidadeApontamentos(tireMaintenance.getQuantidadeApontamentos())
                .dataResolucao(tireMaintenance.getDataHoraResolucao())
                .fechadoAutomaticamente(tireMaintenance.isFechadoAutomaticamente())
                .formaColetaDados(tireMaintenance.getFormaColetaDadosFechamento())
                .psiInserida(tireMaintenance.getPsiAposConserto())
                .kmConserto(tireMaintenance.getKmColetadoVeiculoFechamentoServico())
                .status(tireMaintenance.getStatus())
                .codPneu(tireMaintenance.getPneu().getCodigo())
                .codCliente(tireMaintenance.getPneu().getCodigoCliente())
                .codDimensaoPneu(tireMaintenance.getPneu().getDimensaoPneu().getCodigo())
                .sulcoInterno(tireMaintenance.getPneu().getAlturaSulcoInterno())
                .sulcoCentralInterno(tireMaintenance.getPneu().getAlturaSulcoCentralInterno())
                .sulcoCentralExterno(tireMaintenance.getPneu().getAlturaSulcoCentralExterno())
                .sulcoExterno(tireMaintenance.getPneu().getAlturaSulcoExterno())
                .menorSulco(tireMaintenance.getPneu().getMenorSulco())
                .psi(tireMaintenance.getPneu().getPressaoAtual())
                .psiRecomendada(tireMaintenance.getPneu().getPressaoRecomendada())
                .vidaAtual(tireMaintenance.getPneu().getVidaAtual())
                .vidaTotal(tireMaintenance.getPneu().getVidaTotal())
                .psiAfericao(valor.map(AfericaoPneuValorEntity::getPsi).orElse(null))
                .posicaoPneuAberturaServico(valor.map(AfericaoPneuValorEntity::getPosicao).orElse(null))
                .codAfericao(tireMaintenance.getAfericao().getCodigo())
                .dataHoraAbertura(tireMaintenance.getAfericao().getDataHora())
                .codVeiculo(tireMaintenance.getAfericao().getVeiculo().getId())
                .placa(tireMaintenance.getAfericao().getVeiculo().getPlate())
                .identificadorFrota(tireMaintenance.getAfericao().getVeiculo().getFleetId())
                .codMecanico(mecanico.map(ColaboradorEntity::getCodigo).orElse(null))
                .nomeMecanico(mecanico.map(ColaboradorEntity::getNome).orElse(null))
                .cpfMecanico(mecanico.map(ColaboradorEntity::getCpfFormatado).orElse(null))
                .problemaApontado(alternativa.map(AfericaoAlternativaEntity::getAlternativa).orElse(null))
                .build();
    }
}
