package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import org.jetbrains.annotations.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 27/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public abstract class Afericao {

    @Exclude
    @NotNull
    private final TipoProcessoColetaAfericao tipoProcessoColetaAfericao;
    private Long codigo;
    /**
     * O código da {@link Unidade} onde a aferição foi realizada.
     */
    private Long codUnidade;
    private LocalDateTime dataHora;
    private Colaborador colaborador;
    /**
     * Armazena o tempo que o colaborador levou para realizar a aferição, em milisegundos.
     */
    private long tempoRealizacaoAfericaoInMillis;
    private TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao;
    private FormaColetaDadosAfericaoEnum formaColetaDadosAfericao;

    @NotNull
    public static RuntimeTypeAdapterFactory<Afericao> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(Afericao.class, "tipoProcessoColetaAfericao")
                .registerSubtype(AfericaoPlaca.class, TipoProcessoColetaAfericao.PLACA.asString())
                .registerSubtype(AfericaoAvulsa.class, TipoProcessoColetaAfericao.PNEU_AVULSO.asString());
    }

    @NotNull
    public abstract List<Pneu> getPneusAferidos();
}