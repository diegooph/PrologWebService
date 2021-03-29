package br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.veiculo.v3.VeiculoV3Service;
import br.com.zalf.prolog.webservice.frota.veiculo.v3._model.VeiculoEntity;
import br.com.zalf.prolog.webservice.v3.frota.afericao.AfericaoService;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.checklist.ChecklistService;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistEntity;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico.ChecklistOrdemServicoService;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoItemEntity;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.*;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao.MovimentacaoProcessoService;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoProcessoEntity;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu.ServicoPneuService;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuEntity;
import br.com.zalf.prolog.webservice.v3.frota.socorrorota.SocorroRotaService;
import br.com.zalf.prolog.webservice.v3.frota.socorrorota._model.AberturaSocorroRotaEntity;
import br.com.zalf.prolog.webservice.v3.frota.transferenciaveiculo.TransferenciaVeiculoService;
import br.com.zalf.prolog.webservice.v3.frota.transferenciaveiculo._model.TransferenciaVeiculoInformacaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.transferenciaveiculo._model.TransferenciaVeiculoProcessoEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Component
public final class AlteracaoKmProcessoVisitorImpl implements AlteracaoKmProcessoVisitor {
    @NotNull
    private final VeiculoV3Service veiculoService;
    @NotNull
    private final AfericaoService afericaoService;
    @NotNull
    private final ServicoPneuService servicoPneuService;
    @NotNull
    private final ChecklistService checklistService;
    @NotNull
    private final ChecklistOrdemServicoService checklistOrdemServicoService;
    @NotNull
    private final MovimentacaoProcessoService movimentacaoProcessoService;
    @NotNull
    private final SocorroRotaService socorroRotaService;
    @NotNull
    private final TransferenciaVeiculoService transferenciaVeiculoService;

    @Autowired
    public AlteracaoKmProcessoVisitorImpl(@NotNull final VeiculoV3Service veiculoService,
                                          @NotNull final AfericaoService afericaoService,
                                          @NotNull final ServicoPneuService servicoPneuService,
                                          @NotNull final ChecklistService checklistService,
                                          @NotNull final ChecklistOrdemServicoService checklistOrdemServicoService,
                                          @NotNull final MovimentacaoProcessoService movimentacaoProcessoService,
                                          @NotNull final SocorroRotaService socorroRotaService,
                                          @NotNull final TransferenciaVeiculoService transferenciaVeiculoService) {
        this.veiculoService = veiculoService;
        this.afericaoService = afericaoService;
        this.servicoPneuService = servicoPneuService;
        this.checklistService = checklistService;
        this.checklistOrdemServicoService = checklistOrdemServicoService;
        this.movimentacaoProcessoService = movimentacaoProcessoService;
        this.socorroRotaService = socorroRotaService;
        this.transferenciaVeiculoService = transferenciaVeiculoService;
    }

    @NotNull
    @Override
    public AlteracaoKmResponse visit(@NotNull final AfericaoKmProcesso afericaoKmProcesso) {
        AfericaoEntity entity = afericaoService.getByCodigo(afericaoKmProcesso.getCodProcesso());
        final long kmAntigo = entity.getKmColetadoVeiculo();
        applyValidations(afericaoKmProcesso.getCodEmpresa(),
                         afericaoKmProcesso.getCodVeiculo(),
                         entity.getCodVeiculo());
        entity = entity
                .toBuilder()
                .withKmColetadoVeiculo(afericaoKmProcesso.getNovoKm())
                .build();
        afericaoService.update(entity);
        return AlteracaoKmResponse.of(kmAntigo);
    }

    @NotNull
    @Override
    public AlteracaoKmResponse visit(@NotNull final ServicoPneuKmProcesso servicoPneuKmProcesso) {
        ServicoPneuEntity entity = servicoPneuService.getByCodigo(servicoPneuKmProcesso.getCodProcesso());
        final AfericaoEntity afericao = afericaoService.getByCodigo(entity.getCodAfericao());
        final long kmAntigo = entity.getKmColetadoVeiculoFechamentoServico();
        applyValidations(servicoPneuKmProcesso.getCodEmpresa(),
                         servicoPneuKmProcesso.getCodVeiculo(),
                         afericao.getCodVeiculo());
        entity = entity
                .toBuilder()
                .withKmColetadoVeiculoFechamentoServico(servicoPneuKmProcesso.getNovoKm())
                .build();
        servicoPneuService.update(entity);
        return AlteracaoKmResponse.of(kmAntigo);
    }

    @NotNull
    @Override
    public AlteracaoKmResponse visit(@NotNull final ChecklistKmProcesso checklistKmProcesso) {
        ChecklistEntity entity = checklistService.getByCodigo(checklistKmProcesso.getCodProcesso());
        final long kmAntigo = entity.getKmColetadoVeiculo();
        applyValidations(checklistKmProcesso.getCodEmpresa(),
                         checklistKmProcesso.getCodVeiculo(),
                         entity.getCodVeiculo());
        entity = entity
                .toBuilder()
                .withKmColetadoVeiculo(checklistKmProcesso.getNovoKm())
                .build();
        checklistService.update(entity);
        return AlteracaoKmResponse.of(kmAntigo);
    }

    @NotNull
    @Override
    public AlteracaoKmResponse visit(
            @NotNull final ChecklistOrdemServicoItemKmProcesso checklistOrdemServicoItemKmProcesso) {
        ChecklistOrdemServicoItemEntity entity =
                checklistOrdemServicoService.getByCodigo(checklistOrdemServicoItemKmProcesso.getCodProcesso());
        final long kmAntigo = entity.getKmColetadoVeiculoFechamentoItem();
        applyValidations(checklistOrdemServicoItemKmProcesso.getCodEmpresa(),
                         checklistOrdemServicoItemKmProcesso.getCodVeiculo(),
                         null);
        entity = entity
                .toBuilder()
                .withKmColetadoVeiculoFechamentoItem(checklistOrdemServicoItemKmProcesso.getNovoKm())
                .build();
        checklistOrdemServicoService.update(entity);
        return AlteracaoKmResponse.of(kmAntigo);
    }

    @NotNull
    @Override
    public AlteracaoKmResponse visit(@NotNull final MovimentacaoKmProcesso movimentacaoKmProcesso) {
        final MovimentacaoProcessoEntity entity =
                movimentacaoProcessoService.getByCodigo(movimentacaoKmProcesso.getCodProcesso());
        final List<MovimentacaoEntity> movimentacoes =
                entity.getMovimentacoesNoVeiculo(movimentacaoKmProcesso.getCodVeiculo());
        if (!movimentacoes.isEmpty()) {
            entity
                    .getCodVeiculo()
                    .ifPresentOrElse(
                            codVeiculo -> applyValidations(movimentacaoKmProcesso.getCodEmpresa(),
                                                           movimentacaoKmProcesso.getCodVeiculo(),
                                                           codVeiculo),
                            () -> {
                                throw new IllegalStateException();
                            });
            //noinspection OptionalGetWithoutIsPresent
            final long kmAntigo = entity.getKmColetado().get();
            movimentacaoProcessoService.updateKmColetado(entity, movimentacaoKmProcesso.getNovoKm());
            return AlteracaoKmResponse.of(kmAntigo);
        } else {
            throw new IllegalStateException(
                    String.format("O veículo %d não está presente no processo de movimentação de %d.",
                                  movimentacaoKmProcesso.getCodVeiculo(),
                                  movimentacaoKmProcesso.getCodProcesso()));
        }
    }

    @NotNull
    @Override
    public AlteracaoKmResponse visit(@NotNull final SocorroRotaKmProcesso socorroRotaKmProcesso) {
        AberturaSocorroRotaEntity entity =
                socorroRotaService.getAberturaSocorroRotaByCodSocorro(socorroRotaKmProcesso.getCodProcesso());
        final long kmAntigo = entity.getKmColetadoVeiculoAberturaSocorro();
        applyValidations(socorroRotaKmProcesso.getCodEmpresa(),
                         socorroRotaKmProcesso.getCodVeiculo(),
                         entity.getCodVeiculo());
        entity = entity
                .toBuilder()
                .withKmColetadoVeiculoAberturaSocorro(socorroRotaKmProcesso.getNovoKm())
                .build();
        socorroRotaService.update(entity);
        return AlteracaoKmResponse.of(kmAntigo);
    }

    @NotNull
    @Override
    public AlteracaoKmResponse visit(@NotNull final TransferenciaVeiculoKmProcesso transferenciaVeiculoKmProcesso) {
        final TransferenciaVeiculoProcessoEntity entity =
                transferenciaVeiculoService.getByCodigo(transferenciaVeiculoKmProcesso.getCodProcesso());
        final Optional<TransferenciaVeiculoInformacaoEntity> informacoesTransferenciaVeiculo =
                entity.getInformacoesTransferenciaVeiculo(transferenciaVeiculoKmProcesso.getCodVeiculo());
        if (informacoesTransferenciaVeiculo.isPresent()) {
            final TransferenciaVeiculoInformacaoEntity infoVeiculo = informacoesTransferenciaVeiculo.get();
            applyValidations(transferenciaVeiculoKmProcesso.getCodEmpresa(),
                             transferenciaVeiculoKmProcesso.getCodVeiculo(),
                             infoVeiculo.getCodVeiculo());
            final long kmAntigo = infoVeiculo.getKmColetadoVeiculoMomentoTransferencia();
            final TransferenciaVeiculoInformacaoEntity updateEntity = infoVeiculo
                    .toBuilder()
                    .withKmColetadoVeiculoMomentoTransferencia(transferenciaVeiculoKmProcesso.getNovoKm())
                    .build();
            transferenciaVeiculoService.updateInformacoesVeiculo(updateEntity);
            return AlteracaoKmResponse.of(kmAntigo);
        } else {
            throw new IllegalStateException(
                    String.format("O veículo %d não está presente no processo de transferência %d.",
                                  transferenciaVeiculoKmProcesso.getCodVeiculo(),
                                  transferenciaVeiculoKmProcesso.getCodProcesso()));
        }
    }

    private void applyValidations(@NotNull final Long codEmpresaRecebido,
                                  @NotNull final Long codVeiculoRecebido,
                                  @NotNull final Long codVeiculoBanco) {
        if (!codVeiculoRecebido.equals(codVeiculoBanco)) {
            fail();
        }
        final VeiculoEntity veiculo = veiculoService.getByCodigo(codVeiculoBanco);
        // Garantindo que a empresa do veículo é a mesma recebida já garantimos que o processo editado é da empresa
        // em questão.
        if (!codEmpresaRecebido.equals(veiculo.getCodEmpresa())) {
            fail();
        }
    }

    private void fail() {
        throw new GenericException("Só é possível alterar o KM de veículos e processos da sua empresa!");
    }
}
