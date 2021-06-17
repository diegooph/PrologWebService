package br.com.zalf.prolog.webservice.v3.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.v3.frota.acoplamento._model.AcoplamentoAtualEntity;
import br.com.zalf.prolog.webservice.v3.frota.acoplamento._model.AcoplamentoProcessoEntity;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.*;
import br.com.zalf.prolog.webservice.v3.frota.veiculo.diagrama._model.DiagramaEntity;
import br.com.zalf.prolog.webservice.v3.frota.veiculo.modelo._model.ModeloVeiculoEntity;
import br.com.zalf.prolog.webservice.v3.frota.veiculo.tipo._model.TipoVeiculoEntity;
import br.com.zalf.prolog.webservice.v3.geral.unidade._model.UnidadeEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class VeiculoMapper {
    @NotNull
    public VeiculoEntity toEntity(@NotNull final VeiculoCadastroDto dto,
                                  @NotNull final UnidadeEntity unidadeEntity,
                                  @NotNull final DiagramaEntity diagramaEntity,
                                  @NotNull final TipoVeiculoEntity tipoVeiculoEntity,
                                  @NotNull final ModeloVeiculoEntity modeloVeiculoEntity,
                                  @NotNull final OrigemAcaoEnum origemCadastro) {
        return VeiculoEntity.builder()
                .withCodEmpresa(dto.getCodEmpresaAlocado())
                .withUnidadeEntity(unidadeEntity)
                .withUnidadeEntityCadastro(unidadeEntity)
                .withDiagramaEntity(diagramaEntity)
                .withMotorizado(diagramaEntity.isMotorizado())
                .withTipoVeiculoEntity(tipoVeiculoEntity)
                .withModeloVeiculoEntity(modeloVeiculoEntity)
                .withPlaca(dto.getPlacaVeiculo())
                .withIdentificadorFrota(dto.getIdentificadorFrota())
                .withKm(dto.getKmAtualVeiculo())
                .withPossuiHobodometro(dto.getPossuiHubodometro())
                .withDataHoraCadatro(Now.getOffsetDateTimeUtc())
                .withStatusAtivo(true)
                .withOrigemCadastro(origemCadastro)
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
