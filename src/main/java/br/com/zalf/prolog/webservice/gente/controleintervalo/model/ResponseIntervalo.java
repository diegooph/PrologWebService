package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.gente.controleintervalo.DeprecatedControleIntervaloResource;

/**
 * Está classe será usada como retorno para a função de insert de um {@link Intervalo}
 * ({@link DeprecatedControleIntervaloResource#insertIntervalo(long, Intervalo)}). O seu {@link #status} é
 * referente ao insert do próprio {@link Intervalo}, status {@link #OK} significa inserido com
 * sucesso; {@link #ERROR} significa que ocorreu algum problema e não foi inserido.
 *
 * A diferença dessa classe para o {@link AbstractResponse} normal é que ela também retorna um
 * {@link EstadoVersaoIntervalo}, referente a versão dos dados de intervalo que recebemos do App
 * no Header do request de insert. O servidor compara a versão recebida com a versão no banco de
 * dados do <b>ProLog</b>.
 *
 * O {@link #estadoVersaoIntervalo} pode ou não existir, independente do {@link #status}.
 */
public final class ResponseIntervalo extends AbstractResponse {
    private EstadoVersaoIntervalo estadoVersaoIntervalo;

    public static ResponseIntervalo ok(String msg, EstadoVersaoIntervalo estadoVersaoIntervalo) {
        final ResponseIntervalo r = new ResponseIntervalo();
        r.estadoVersaoIntervalo = estadoVersaoIntervalo;
        r.setStatus(OK);
        r.setMsg(msg);
        return r;
    }

    public static ResponseIntervalo error(String msg, EstadoVersaoIntervalo estadoVersaoIntervalo) {
        final ResponseIntervalo r = new ResponseIntervalo();
        r.estadoVersaoIntervalo = estadoVersaoIntervalo;
        r.setStatus(ERROR);
        r.setMsg(msg);
        return r;
    }
}