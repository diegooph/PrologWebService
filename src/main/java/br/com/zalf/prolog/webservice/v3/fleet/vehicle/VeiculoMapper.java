package br.com.zalf.prolog.webservice.v3.fleet.vehicle;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.v3.fleet.acoplamento._model.AcoplamentoAtualEntity;
import br.com.zalf.prolog.webservice.v3.fleet.acoplamento._model.AcoplamentoProcessoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.*;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.diagrama._model.DiagramaEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.diagrama.eixos._model.EixoDiagramaEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.modelo._model.ModeloVeiculoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.tipoveiculo._model.TipoVeiculoEntity;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class VeiculoMapper {

    @NotNull
    public List<VeiculoListagemDto> toDto(@NotNull final List<VeiculoEntity> veiculoEntities) {
        return veiculoEntities.stream()
                .map(this::createVeiculoListagemDto)
                .collect(Collectors.toList());
    }

    @NotNull
    public VeiculoEntity toEntity(@NotNull final VeiculoCadastroDto dto,
                                  @NotNull final BranchEntity branchEntity,
                                  @NotNull final DiagramaEntity diagramaEntity,
                                  @NotNull final TipoVeiculoEntity tipoVeiculoEntity,
                                  @NotNull final ModeloVeiculoEntity modeloVeiculoEntity,
                                  @NotNull final OrigemAcaoEnum origemCadastro) {
        return VeiculoEntity.builder()
                .withCodEmpresa(dto.getCodEmpresaAlocado())
                .withBranchEntity(branchEntity)
                .withBranchEntityCadastro(branchEntity)
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
        final BranchEntity branchEntity = veiculoEntity.getBranchEntity();
        final Optional<AcoplamentoProcessoEntity> acoplamentoProcessoEntity =
                veiculoEntity.getAcoplamentoProcessoEntity();

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
                diagramaEntity.getQtdEixos(EixoDiagramaEntity.EIXO_DIANTEIRO),
                diagramaEntity.getQtdEixos(EixoDiagramaEntity.EIXO_TRASEIRO),
                tipoVeiculoEntity.getCodigo(),
                tipoVeiculoEntity.getNome(),
                branchEntity.getId(),
                branchEntity.getName(),
                branchEntity.getGroup().getId(),
                branchEntity.getGroup().getName(),
                veiculoEntity.getKm(),
                veiculoEntity.isStatusAtivo(),
                veiculoEntity.getQtdPneusAplicados(),
                veiculoEntity.isAcoplado(),
                veiculoEntity.getPosicaoAcopladoAtual(),
                acoplamentoProcessoEntity.map(acoplamentoProcesso -> createVeiculosAcoplamentos(veiculoEntity.getCodigo(),
                                                                                                acoplamentoProcesso.getCodigo(),
                                                                                                acoplamentoProcesso.getAcoplamentoAtualEntities()))
                        .orElse(null));
    }

    @NotNull
    private VeiculosAcopladosListagemDto createVeiculosAcoplamentos(
            @NotNull final Long codVeiculo,
            @NotNull final Long codProcessoAcoplamento,
            @NotNull final Set<AcoplamentoAtualEntity> acoplamentosAtuais) {
        return new VeiculosAcopladosListagemDto(
                codProcessoAcoplamento,
                acoplamentosAtuais.stream()
                        .filter(acoplamento -> !acoplamento.getCodVeiculoAcoplamentoAtual().equals(codVeiculo))
                        .map(this::createVeiculoAcoplado)
                        .collect(Collectors.toList()));
    }

    @NotNull
    private VeiculoAcopladoListagemDto createVeiculoAcoplado(
            @NotNull final AcoplamentoAtualEntity acoplamentoAtualEntity) {
        return new VeiculoAcopladoListagemDto(acoplamentoAtualEntity.getVeiculoEntity().getCodigo(),
                                              acoplamentoAtualEntity.getVeiculoEntity().getPlaca(),
                                              acoplamentoAtualEntity.getVeiculoEntity().getIdentificadorFrota(),
                                              acoplamentoAtualEntity.getVeiculoEntity().isMotorizado(),
                                              acoplamentoAtualEntity.getCodPosicao());
    }
}
