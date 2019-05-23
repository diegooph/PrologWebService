package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 02/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ItemOSAbertaGlobus {
    private Long codOsGlobus;
    private Long codItemGlobus;
    private Long codChecklistProLog;
    private Long codUnidadeItemOs;
    private Long codPerguntaItemOs;
    private Long codAlternativaItemOs;

    public ItemOSAbertaGlobus() {
    }

    @NotNull
    public static ItemOSAbertaGlobus getDummy() {
        final ItemOSAbertaGlobus itemGlobus = new ItemOSAbertaGlobus();
        itemGlobus.setCodOsGlobus(1L);
        itemGlobus.setCodItemGlobus(100L);
        itemGlobus.setCodChecklistProLog(13873L);
        itemGlobus.setCodUnidadeItemOs(5L);
        itemGlobus.setCodPerguntaItemOs(501L);
        itemGlobus.setCodAlternativaItemOs(1010L);
        return itemGlobus;
    }

    public Long getCodOsGlobus() {
        return codOsGlobus;
    }

    public void setCodOsGlobus(final Long codOsGlobus) {
        this.codOsGlobus = codOsGlobus;
    }

    public Long getCodItemGlobus() {
        return codItemGlobus;
    }

    public void setCodItemGlobus(final Long codItemGlobus) {
        this.codItemGlobus = codItemGlobus;
    }

    public Long getCodChecklistProLog() {
        return codChecklistProLog;
    }

    public void setCodChecklistProLog(final Long codChecklistProLog) {
        this.codChecklistProLog = codChecklistProLog;
    }

    public Long getCodUnidadeItemOs() {
        return codUnidadeItemOs;
    }

    public void setCodUnidadeItemOs(final Long codUnidadeItemOs) {
        this.codUnidadeItemOs = codUnidadeItemOs;
    }

    public Long getCodPerguntaItemOs() {
        return codPerguntaItemOs;
    }

    public void setCodPerguntaItemOs(final Long codPerguntaItemOs) {
        this.codPerguntaItemOs = codPerguntaItemOs;
    }

    public Long getCodAlternativaItemOs() {
        return codAlternativaItemOs;
    }

    public void setCodAlternativaItemOs(final Long codAlternativaItemOs) {
        this.codAlternativaItemOs = codAlternativaItemOs;
    }
}
