package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model;

import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoInicioFim;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * Classe utilizada quando uma marcação é adicionada para algum colaborador. É importante destacar que esse
 * ajuste é apenas para inclusão de uma marcação de início ou fim, mas nunca de ambas. Caso esteja sendo adicionado um
 * início, o fim já deve existir, se um fim estiver sendo adicionado, o início já deve existir.
 *
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class MarcacaoAjusteAdicao extends MarcacaoAjuste {
    /**
     * O código da marcação com a qual está será vinculada.
     */
    private Long codMarcacaoVinculo;

    /**
     * Data e hora com que esta marcação deve ser criada.
     */
    private LocalDateTime dataHoraInserida;

    /**
     * O tipo da marcação que deve ser criada, se é de início ou fim.
     */
    private TipoInicioFim tipoInicioFim;

    public MarcacaoAjusteAdicao() {
        super(TipoAcaoAjuste.ADICAO);
    }

    @NotNull
    public static MarcacaoAjusteAdicao createDummy() {
        final MarcacaoAjusteAdicao ajusteAdicao = new MarcacaoAjusteAdicao();
        ajusteAdicao.setCodMarcacaoVinculo(101010L);
        ajusteAdicao.setDataHoraInserida(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        ajusteAdicao.setCodJustificativaAjuste(5L);
        ajusteAdicao.setObservacaoAjuste("Dummy Data");
        ajusteAdicao.setDataHoraAjuste(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        ajusteAdicao.setTipoInicioFim(TipoInicioFim.MARCACAO_INICIO);
        return ajusteAdicao;
    }

    public TipoInicioFim getTipoInicioFim() {
        return tipoInicioFim;
    }

    public void setTipoInicioFim(final TipoInicioFim tipoInicioFim) {
        this.tipoInicioFim = tipoInicioFim;
    }

    public Long getCodMarcacaoVinculo() {
        return codMarcacaoVinculo;
    }

    public void setCodMarcacaoVinculo(final Long codMarcacaoVinculo) {
        this.codMarcacaoVinculo = codMarcacaoVinculo;
    }

    public LocalDateTime getDataHoraInserida() {
        return dataHoraInserida;
    }

    public void setDataHoraInserida(final LocalDateTime dataHoraInserida) {
        this.dataHoraInserida = dataHoraInserida;
    }

    @Override
    public String toString() {
        return "MarcacaoAjusteAdicao{" +
                "codMarcacaoVinculo=" + codMarcacaoVinculo +
                ", dataHoraInserida=" + dataHoraInserida +
                '}';
    }
}