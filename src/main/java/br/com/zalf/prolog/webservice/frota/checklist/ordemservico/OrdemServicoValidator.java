package br.com.zalf.prolog.webservice.frota.checklist.ordemservico;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Created on 15/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class OrdemServicoValidator {

    private OrdemServicoValidator() {
        throw new IllegalStateException(OrdemServicoValidator.class.getSimpleName() + " cannot be instantiated!");
    }

    static void validaResolucaoItem(@NotNull final ZoneId zoneIdCliente,
                                    @NotNull final ResolverItemOrdemServico item) throws ProLogException {
        validaDataHoraInicioFimResolucao(
                item.getDataHoraInicioResolucao(),
                item.getDataHoraFimResolucao(),
                zoneIdCliente);
    }

    static void validaResolucaoMultiplosItens(
            @NotNull final ZoneId zoneIdCliente,
            @NotNull final ResolverMultiplosItensOs itensResolucao) throws ProLogException {
        validaDataHoraInicioFimResolucao(
                itensResolucao.getDataHoraInicioResolucao(),
                itensResolucao.getDataHoraFimResolucao(),
                zoneIdCliente);
    }

    private static void validaDataHoraInicioFimResolucao(@NotNull final LocalDateTime dataHoraInicioResolucao,
                                                         @NotNull final LocalDateTime dataHoraFimResolucao,
                                                         @NotNull final ZoneId zoneIdCliente) throws ProLogException {
        if (dataHoraInicioResolucao.isAfter(dataHoraFimResolucao)) {
            throw new GenericException("A data/hora de início da resolução não pode ser posterior a data/hora de fim");
        }

        final LocalDateTime dataHoraAtualUtc = Now.getLocalDateTimeUtc();
        final LocalDateTime dataHoraInicioUtc = dataHoraInicioResolucao
                .atZone(zoneIdCliente)
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();
        final LocalDateTime dataHoraFimUtc = dataHoraFimResolucao
                .atZone(zoneIdCliente)
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();
        if (dataHoraInicioUtc.isAfter(dataHoraAtualUtc)) {
            throw new GenericException("A data/hora de início da resolução não pode ser posterior a data/hora atual");
        }
        if (dataHoraFimUtc.isAfter(dataHoraAtualUtc)) {
            throw new GenericException("A data/hora de fim da resolução não pode ser posterior a data/hora atual");
        }
    }
}