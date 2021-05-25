package br.com.zalf.prolog.webservice.v3.frota.pneu;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2021-03-15
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
public class PneuMapper {
    @NotNull
    public PneuEntity toEntity(@NotNull final PneuCadastroDto dto) {
        return PneuEntity.builder()
                .codEmpresa(dto.getCodEmpresaAlocado())
                .codUnidade(dto.getCodUnidadeAlocado())
                .codigoCliente(dto.getCodigoCliente())
                .modeloPneu(createModeloPneu(dto.getCodModeloPneu()))
                .dimensaoPneu(createDimensaoPneuEntity(dto.getCodDimensaoPneu()))
                .pressaoRecomendada(dto.getPressaoRecomendadaPneu())
                .status(StatusPneu.ESTOQUE)
                .vidaAtual(dto.getVidaAtualPneu())
                .vidaTotal(dto.getVidaTotalPneu())
                .modeloBanda(createModeloBanda(dto.getCodModeloBanda()))
                .dot(dto.getDotPneu())
                .valor(dto.getValorPneu())
                .dataHoraCadastro(Now.getOffsetDateTimeUtc())
                .pneuNovoNuncaRodado(dto.getPneuNovoNuncaRodado())
                .codUnidadeCadastro(dto.getCodUnidadeAlocado())
                .build();
    }

    @NotNull
    public List<PneuListagemDto> toPneuListagemDto(@NotNull final List<PneuEntity> pneus) {
        return pneus.stream().map(this::toPneuListagemDto).collect(Collectors.toList());
    }

    @NotNull
    public PneuListagemDto toPneuListagemDto(@NotNull final PneuEntity pneu) {
        return null;
    }

    @NotNull
    private DimensaoPneuEntity createDimensaoPneuEntity(@NotNull final Long codDimensaoPneu) {
        return DimensaoPneuEntity.builder().withCodigo(codDimensaoPneu).build();
    }

    @NotNull
    private ModeloPneuEntity createModeloPneu(@NotNull final Long codModeloPneu) {
        return ModeloPneuEntity.builder().withCodigo(codModeloPneu).build();
    }

    @NotNull
    private ModeloBandaEntity createModeloBanda(@NotNull final Long codModeloBanda) {
        return ModeloBandaEntity.builder().withCodigo(codModeloBanda).build();
    }
}
