package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada;

import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.FolhaPontoTipoIntervalo;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Created on 09/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class FolhaPontoJornadaRelatorio {
    private String cpfColaborador;
    private String nomeColaborador;

    private List<FolhaPontoJornadaDia> jornadasDia;
    private Set<FolhaPontoTipoIntervalo> tiposMarcacoesMarcadas;

    @SerializedName("dataHoraGeracaoRelatorio")
    @NotNull
    private LocalDateTime dataHoraGeracaoRelatorioZoned;

    private Duration totalJornadaBrutaPeriodo;
    private Duration totalJornadaLiquidaPeriodo;
}
