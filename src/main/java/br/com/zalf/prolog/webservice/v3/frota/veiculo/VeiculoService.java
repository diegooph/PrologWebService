package br.com.zalf.prolog.webservice.v3.frota.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.v3.TipoVeiculoV3Service;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.v3._model.TipoVeiculoEntity;
import br.com.zalf.prolog.webservice.frota.veiculo.validator.VeiculoValidator;
import br.com.zalf.prolog.webservice.integracao.OperacoesBloqueadasYaml;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoEntity;
import br.com.zalf.prolog.webservice.v3.frota.veiculo.diagrama.DiagramaService;
import br.com.zalf.prolog.webservice.v3.frota.veiculo.diagrama._model.DiagramaEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;

@Service
public class VeiculoService {
    @NotNull
    private static final String TAG = VeiculoService.class.getSimpleName();
    @NotNull
    private final VeiculoDao veiculoDao;
    @NotNull
    private final TipoVeiculoV3Service tipoVeiculoService;
    @NotNull
    private final DiagramaService diagramaService;
    @NotNull
    private final OperacoesBloqueadasYaml operacoesBloqueadas;

    @Autowired
    public VeiculoService(@NotNull final VeiculoDao veiculoDao,
                          @NotNull final TipoVeiculoV3Service tipoVeiculoService,
                          @NotNull final DiagramaService diagramaService,
                          @NotNull final OperacoesBloqueadasYaml operacoesBloqueadas) {
        this.veiculoDao = veiculoDao;
        this.tipoVeiculoService = tipoVeiculoService;
        this.diagramaService = diagramaService;
        this.operacoesBloqueadas = operacoesBloqueadas;
    }

    @NotNull
    @Transactional
    public SuccessResponse insert(@Nullable final String tokenIntegracao,
                                  @NotNull final VeiculoEntity veiculoEntity) {
        try {
            operacoesBloqueadas.validateEmpresaUnidadeBloqueada(veiculoEntity.getCodEmpresa(),
                                                                veiculoEntity.getCodUnidade());
            VeiculoValidator.validacaoMotorizadoSemHubodometro(veiculoEntity.isPossuiHobodometro(),
                                                               veiculoEntity.getCodTipo());
            final TipoVeiculoEntity tipoVeiculoEntity = tipoVeiculoService.getByCod(veiculoEntity.getCodTipo());
            final DiagramaEntity diagramaEntity = diagramaService.getByCod(tipoVeiculoEntity.getCodDiagrama());
            final VeiculoEntity veiculoInsert = veiculoEntity.toBuilder()
                    .withMotorizado(diagramaEntity.isMotorizado())
                    .withCodDiagrama(tipoVeiculoEntity.getCodDiagrama().longValue())
                    .withOrigemCadastro(getOrigemCadastro(tokenIntegracao))
                    .build();
            final VeiculoEntity saved = veiculoDao.save(veiculoInsert);
            return new SuccessResponse(saved.getCodigo(), "Veículo inserido com sucesso.");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir veículo.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir veículo, tente novamente.");
        }
    }

    @NotNull
    public VeiculoEntity getByCodigo(@NotNull final Long codigo) {
        return veiculoDao.getOne(codigo);
    }

    @NotNull
    private OrigemAcaoEnum getOrigemCadastro(@Nullable final String tokenIntegracao) {
        return tokenIntegracao != null ? OrigemAcaoEnum.API : OrigemAcaoEnum.PROLOG_WEB;
    }

    @NotNull
    public Long updateKmVeiculo(@NotNull final Long codUnidade,
                                @NotNull final Long codVeiculo,
                                @NotNull final Long veiculoCodProcesso,
                                @NotNull final VeiculoTipoProcesso veiculoTipoProcesso,
                                @NotNull final OffsetDateTime dataHoraProcesso,
                                final long kmVeiculo,
                                final boolean devePropagarKmParaReboques) {
        return veiculoDao.updateKmByCodVeiculo(codUnidade,
                                               codVeiculo,
                                               veiculoCodProcesso,
                                               VeiculoTipoProcesso.valueOf(veiculoTipoProcesso.toString()),
                                               dataHoraProcesso,
                                               kmVeiculo,
                                               devePropagarKmParaReboques);
    }
}
