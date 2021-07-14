package br.com.zalf.prolog.webservice.v3.fleet.veiculo;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import br.com.zalf.prolog.webservice.frota.veiculo.validator.VeiculoValidator;
import br.com.zalf.prolog.webservice.integracao.OperacoesBloqueadasYaml;
import br.com.zalf.prolog.webservice.v3.OffsetBasedPageRequest;
import br.com.zalf.prolog.webservice.v3.fleet.veiculo._model.VeiculoCadastroDto;
import br.com.zalf.prolog.webservice.v3.fleet.veiculo._model.VeiculoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.veiculo.diagrama.DiagramaService;
import br.com.zalf.prolog.webservice.v3.fleet.veiculo.diagrama._model.DiagramaEntity;
import br.com.zalf.prolog.webservice.v3.fleet.veiculo.modelo.ModeloVeiculoService;
import br.com.zalf.prolog.webservice.v3.fleet.veiculo.modelo._model.ModeloVeiculoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.veiculo.tipoveiculo.TipoVeiculoService;
import br.com.zalf.prolog.webservice.v3.fleet.veiculo.tipoveiculo._model.TipoVeiculoEntity;
import br.com.zalf.prolog.webservice.v3.general.unidade.UnidadeService;
import br.com.zalf.prolog.webservice.v3.general.unidade._model.UnidadeEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class VeiculoService {
    @NotNull
    private static final String TAG = VeiculoService.class.getSimpleName();
    @NotNull
    private final VeiculoDao veiculoDao;
    @NotNull
    private final UnidadeService unidadeService;
    @NotNull
    private final TipoVeiculoService tipoVeiculoService;
    @NotNull
    private final ModeloVeiculoService modeloVeiculoService;
    @NotNull
    private final DiagramaService diagramaService;
    @NotNull
    private final VeiculoMapper mapper;
    @NotNull
    private final OperacoesBloqueadasYaml operacoesBloqueadas;

    @Autowired
    public VeiculoService(@NotNull final VeiculoDao veiculoDao,
                          @NotNull final UnidadeService unidadeService,
                          @NotNull final ModeloVeiculoService modeloVeiculoService,
                          @NotNull final TipoVeiculoService tipoVeiculoService,
                          @NotNull final DiagramaService diagramaService,
                          @NotNull final OperacoesBloqueadasYaml operacoesBloqueadas,
                          @NotNull final VeiculoMapper mapper) {
        this.veiculoDao = veiculoDao;
        this.unidadeService = unidadeService;
        this.modeloVeiculoService = modeloVeiculoService;
        this.tipoVeiculoService = tipoVeiculoService;
        this.diagramaService = diagramaService;
        this.operacoesBloqueadas = operacoesBloqueadas;
        this.mapper = mapper;
    }

    @NotNull
    @Transactional
    public SuccessResponse insert(@Nullable final String tokenIntegracao,
                                  @NotNull final VeiculoCadastroDto veiculoCadastroDto) throws Throwable {
        operacoesBloqueadas.validateEmpresaUnidadeBloqueada(veiculoCadastroDto.getCodEmpresaAlocado(),
                                                            veiculoCadastroDto.getCodUnidadeAlocado());
        VeiculoValidator.validacaoMotorizadoSemHubodometro(veiculoCadastroDto.getPossuiHubodometro(),
                                                           veiculoCadastroDto.getCodTipoVeiculo());
        final UnidadeEntity unidadeEntity = unidadeService.getByCod(veiculoCadastroDto.getCodUnidadeAlocado());
        final ModeloVeiculoEntity modeloVeiculoEntity =
                modeloVeiculoService.getByCod(veiculoCadastroDto.getCodModeloVeiculo());
        final TipoVeiculoEntity tipoVeiculoEntity =
                tipoVeiculoService.getByCod(veiculoCadastroDto.getCodTipoVeiculo());
        final DiagramaEntity diagramaEntity = diagramaService.getByCod(tipoVeiculoEntity.getCodDiagrama());
        final VeiculoEntity saved = veiculoDao.save(mapper.toEntity(veiculoCadastroDto,
                                                                    unidadeEntity,
                                                                    diagramaEntity,
                                                                    tipoVeiculoEntity,
                                                                    modeloVeiculoEntity,
                                                                    getOrigemCadastro(tokenIntegracao)));
        return new SuccessResponse(saved.getCodigo(), "Veículo inserido com sucesso.");
    }

    @NotNull
    public VeiculoEntity getByCodigo(@NotNull final Long codigo) {
        return veiculoDao.getOne(codigo);
    }

    @NotNull
    @Transactional
    public List<VeiculoEntity> getListagemVeiculos(@NotNull final List<Long> codUnidades,
                                                   final boolean incluirInativos,
                                                   final int limit,
                                                   final int offset) {
        return veiculoDao.getListagemVeiculos(codUnidades,
                                              incluirInativos,
                                              OffsetBasedPageRequest.of(limit, offset, Sort.unsorted()));
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

    @NotNull
    private OrigemAcaoEnum getOrigemCadastro(@Nullable final String tokenIntegracao) {
        return tokenIntegracao != null ? OrigemAcaoEnum.API : OrigemAcaoEnum.PROLOG_WEB;
    }
}
