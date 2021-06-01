package br.com.zalf.prolog.webservice.v3.frota.servicopneu;

import br.com.zalf.prolog.webservice.v3.frota.afericao.valores._model.AfericaoPneuValorEntity;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuEntity;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuListagemDto;
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
public class ServicoPneuListagemMapper {
    @NotNull
    public List<ServicoPneuListagemDto> toDto(@NotNull final List<ServicoPneuEntity> servicosPneu) {
        return servicosPneu.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @NotNull
    private ServicoPneuListagemDto toDto(@NotNull final ServicoPneuEntity servicoPneu) {
        final Optional<AfericaoPneuValorEntity> valor = servicoPneu.getValorAfericaoRelatedToPneu();
        final ServicoPneuListagemDto.ServicoPneuListagemDtoBuilder builder = ServicoPneuListagemDto.builder()
                .codServico(servicoPneu.getCodigo())
                .codUnidadeServico(servicoPneu.getCodUnidade())
                .tipoServico(servicoPneu.getTipoServico())
                .quantidadeApontamentos(servicoPneu.getQuantidadeApontamentos())
                .dataResolucao(servicoPneu.getDataHoraResolucao())
                .fechadoAutomaticamente(servicoPneu.isFechadoAutomaticamente())
                .formaColetaDados(servicoPneu.getFormaColetaDadosFechamento())
                .psiInserida(servicoPneu.getPsiAposConserto())
                .kmConserto(servicoPneu.getKmColetadoVeiculoFechamentoServico())
                .status(servicoPneu.getStatus())
                .codPneu(servicoPneu.getPneu().getCodigo())
                .codCliente(servicoPneu.getPneu().getCodigoCliente())
                .codDimensaoPneu(servicoPneu.getPneu().getCodDimensao())
                .sulcoInterno(servicoPneu.getPneu().getAlturaSulcoInterno())
                .sulcoCentralInterno(servicoPneu.getPneu().getAlturaSulcoCentralInterno())
                .sulcoCentralExterno(servicoPneu.getPneu().getAlturaSulcoCentralExterno())
                .sulcoExterno(servicoPneu.getPneu().getAlturaSulcoExterno())
                .menorSulco(servicoPneu.getPneu().getMenorSulco())
                .psi(servicoPneu.getPneu().getPressaoAtual())
                .psiRecomendada(servicoPneu.getPneu().getPressaoRecomendada())
                .vidaAtual(servicoPneu.getPneu().getVidaAtual())
                .vidaTotal(servicoPneu.getPneu().getVidaTotal())
                .psiAfericao(valor.map(AfericaoPneuValorEntity::getPsi).orElse(null))
                .posicaoPneuAberturaServico(valor.map(AfericaoPneuValorEntity::getPosicao).orElse(null));
        builder.codAfericao(servicoPneu.getAfericao().getCodigo())
                .dataHoraAbertura(servicoPneu.getAfericao().getDataHora())
                .codVeiculo(servicoPneu.getAfericao().getVeiculo().getCodigo())
                .placa(servicoPneu.getAfericao().getVeiculo().getPlaca())
                .identificadorFrota(servicoPneu.getAfericao().getVeiculo().getIdentificadorFrota());
        if (servicoPneu.getMecanico() != null) {
            builder.codMecanico(servicoPneu.getMecanico().getCodigo())
                    .nomeMecanico(servicoPneu.getMecanico().getNome())
                    .cpfMecanico(servicoPneu.getMecanico().getCpfFormatado());
        }
        if (servicoPneu.getAlternativa() != null) {
            builder.problemaApontado(servicoPneu.getAlternativa().getAlternativa());
        }
        return builder.build();
    }
}
