package br.com.zalf.prolog.webservice.gente.controlejornada.model;

import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.gente.controlejornada.ControleJornadaResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Classe utilizada para retornar informações das marcações que são sincronizadas através dos métodos
 * de inserção de marcações.
 * * {@link ControleJornadaResource#insertIntervalo(String, long, Integer, IntervaloMarcacao)}.
 *
 * O seu {@link #status} é referente ao insert do próprio {@link Intervalo}:
 * * status {@link #OK} significa inserido com sucesso;
 * * status {@link #ERROR} significa que ocorreu algum problema e não foi possível inserir o {@link Intervalo}.
 *
 * A diferença dessa classe para a classe {@link ResponseWithCod} é que ela também retorna um atributo
 * {@link EstadoVersaoIntervalo}, referente a versão dos dados de intervalo que recebemos do App
 * no Header do request de insert. O servidor compara a versão recebida com a versão no banco de
 * dados do <b>ProLog</b>.
 *
 * O {@link #estadoVersaoIntervalo} pode ou não existir, independente do {@link #status}.
 */
public final class ResponseIntervalo extends ResponseWithCod {
    private EstadoVersaoIntervalo estadoVersaoIntervalo;

    public static ResponseIntervalo ok(@NotNull final String msg,
                                       @NotNull final EstadoVersaoIntervalo estadoVersaoIntervalo) {
        final ResponseIntervalo r = new ResponseIntervalo();
        r.estadoVersaoIntervalo = estadoVersaoIntervalo;
        r.setStatus(OK);
        r.setMsg(msg);
        return r;
    }

    public static ResponseIntervalo ok(@NotNull final Long codigo,
                                       @NotNull final String msg,
                                       @NotNull final EstadoVersaoIntervalo estadoVersaoIntervalo) {
        final ResponseIntervalo r = new ResponseIntervalo();
        r.estadoVersaoIntervalo = estadoVersaoIntervalo;
        r.setCodigo(codigo);
        r.setStatus(OK);
        r.setMsg(msg);
        return r;
    }

    public static ResponseIntervalo error(@NotNull final String msg,
                                          @Nullable final EstadoVersaoIntervalo estadoVersaoIntervalo) {
        final ResponseIntervalo r = new ResponseIntervalo();
        r.estadoVersaoIntervalo = estadoVersaoIntervalo;
        r.setStatus(ERROR);
        r.setMsg(msg);
        return r;
    }
}