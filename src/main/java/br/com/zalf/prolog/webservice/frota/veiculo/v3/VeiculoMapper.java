package br.com.zalf.prolog.webservice.frota.veiculo.v3;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.frota.veiculo.v3._model.VeiculoCadastroDto;
import br.com.zalf.prolog.webservice.frota.veiculo.v3._model.VeiculoEntity;
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
}
