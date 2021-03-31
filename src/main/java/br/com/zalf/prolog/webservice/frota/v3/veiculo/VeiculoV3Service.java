package br.com.zalf.prolog.webservice.frota.v3.veiculo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.v3.veiculo._model.VeiculoEntity;
import br.com.zalf.prolog.webservice.frota.v3.veiculo.diagrama.DiagramaService;
import br.com.zalf.prolog.webservice.frota.v3.veiculo.diagrama._model.DiagramaEntity;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.v3.TipoVeiculoV3Service;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.v3._model.TipoVeiculoEntity;
import br.com.zalf.prolog.webservice.frota.veiculo.validator.VeiculoValidator;
import br.com.zalf.prolog.webservice.integracao.OperacoesBloqueadasYaml;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class VeiculoV3Service {
    @NotNull
    private static final String TAG = VeiculoV3Service.class.getSimpleName();
    @NotNull
    private final VeiculoV3Dao veiculoDao;
    @NotNull
    private final TipoVeiculoV3Service tipoVeiculoService;
    @NotNull
    private final DiagramaService diagramaService;
    @NotNull
    private final OperacoesBloqueadasYaml operacoesBloqueadas;

    @Autowired
    public VeiculoV3Service(@NotNull final VeiculoV3Dao veiculoDao,
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
    private OrigemAcaoEnum getOrigemCadastro(@Nullable final String tokenIntegracao) {
        return tokenIntegracao != null ? OrigemAcaoEnum.API : OrigemAcaoEnum.PROLOG_WEB;
    }
}
