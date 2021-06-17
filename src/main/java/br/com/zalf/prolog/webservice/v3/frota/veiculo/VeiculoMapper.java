package br.com.zalf.prolog.webservice.v3.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoCadastroDto;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class VeiculoMapper {
    @NotNull
    public VeiculoEntity toEntity(@NotNull final VeiculoCadastroDto dto) {
        return VeiculoEntity.builder()
                .withCodEmpresa(dto.getCodEmpresaAlocado())
                .withCodUnidade(dto.getCodUnidadeAlocado())
                .withCodUnidadeCadastro(dto.getCodUnidadeAlocado())
                .withPlaca(dto.getPlacaVeiculo())
                .withIdentificadorFrota(dto.getIdentificadorFrota())
                .withCodModelo(dto.getCodModeloVeiculo())
                .withCodTipo(dto.getCodTipoVeiculo())
                .withKm(dto.getKmAtualVeiculo())
                .withPossuiHobodometro(dto.getPossuiHubodometro())
                .withDataHoraCadatro(Now.getOffsetDateTimeUtc())
                .withStatusAtivo(true)
                .build();
    }

    @NotNull
    private VeiculoListagemDto createVeiculoListagemDto(@NotNull final VeiculoEntity veiculoEntity) {
        final ModeloVeiculoEntity modeloVeiculoEntity = veiculoEntity.getModeloVeiculoEntity();
        final TipoVeiculoEntity tipoVeiculoEntity = veiculoEntity.getTipoVeiculoEntity();
        final DiagramaEntity diagramaEntity = veiculoEntity.getDiagramaEntity();
        final UnidadeEntity unidadeEntity = veiculoEntity.getUnidadeEntity();
        final Optional<AcoplamentoAtualEntity> acoplamentoOptional
                = Optional.ofNullable(veiculoEntity.getAcoplamentoAtualEntity());

        return new VeiculoListagemDto(
                veiculoEntity.getCodigo(),
                veiculoEntity.getPlaca(),
                veiculoEntity.getIdentificadorFrota(),
                veiculoEntity.isMotorizado(),
                veiculoEntity.isPossuiHobodometro(),
                modeloVeiculoEntity.getMarcaVeiculoEntity().getCodigo(),
                modeloVeiculoEntity.getMarcaVeiculoEntity().getNome(),
                modeloVeiculoEntity.getCodigo(),
                modeloVeiculoEntity.getNome(),
                diagramaEntity.getCodigo(),
                diagramaEntity.getEixosDiagramaEntities()
                        .stream()
                        .filter(eixosEntity -> eixosEntity.getTipoEixo() == 'D')
                        .count(),
                diagramaEntity.getEixosDiagramaEntities()
                        .stream()
                        .filter(eixosEntity -> eixosEntity.getTipoEixo() == 'T')
                        .count(),
                tipoVeiculoEntity.getCodigo(),
                tipoVeiculoEntity.getNome(),
                unidadeEntity.getCodigo(),
                unidadeEntity.getNome(),
                unidadeEntity.getGrupo().getCodigo(),
                unidadeEntity.getGrupo().getNome(),
                veiculoEntity.getKm(),
                veiculoEntity.isStatusAtivo(),
                veiculoEntity.getQtdPneusAplicados(),
                veiculoEntity.isAcoplado(),
                acoplamentoOptional.map(acoplamentoAtualEntity -> createVeiculoAcoplamentoAtual(
                        acoplamentoAtualEntity.getAcoplamentoProcessoEntity())).orElse(null));
    }

    private VeiculosAcopladosListagemDto createVeiculoAcoplamentoAtual(final AcoplamentoProcessoEntity acoplamentoProcessoEntity) {
        return new VeiculosAcopladosListagemDto(acoplamentoProcessoEntity.getCodigo(),
                                                acoplamentoProcessoEntity.getAcoplamentoAtualEntities()
                                                        .stream()
                                                        .map(this::createVeiculoAcopladoListagemDto)
                                                        .collect(Collectors.toList()));
    }

    private VeiculoAcopladoListagemDto createVeiculoAcopladoListagemDto(final AcoplamentoAtualEntity acoplamentoAtualEntity) {
        return new VeiculoAcopladoListagemDto(acoplamentoAtualEntity.getVeiculoEntity().getCodigo(),
                                              acoplamentoAtualEntity.getVeiculoEntity().getPlaca(),
                                              acoplamentoAtualEntity.getVeiculoEntity().getIdentificadorFrota(),
                                              acoplamentoAtualEntity.getVeiculoEntity().isMotorizado(),
                                              acoplamentoAtualEntity.getCodPosicao());
    }
}
