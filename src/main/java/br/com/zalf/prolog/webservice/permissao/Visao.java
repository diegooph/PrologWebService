package br.com.zalf.prolog.webservice.permissao;

import br.com.zalf.prolog.webservice.permissao.pilares.FuncaoProLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilar;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by luiz on 4/18/16.
 */
public class Visao {
    List<Pilar> pilares;

    public List<Pilar> getPilares() {
        return pilares;
    }

    public void setPilares(List<Pilar> pilares) {
        this.pilares = pilares;
    }

    public boolean hasAccessToFunction(int codPilar, int codPermissao) {
        if (pilares == null)
            return false;

        for (Pilar pilar : pilares) {
            if (pilar.codigo == codPilar) {
                if (pilar.funcoes == null)
                    return false;

                for (FuncaoProLog funcao : pilar.funcoes) {
                    if (funcao != null && funcao.getCodigo() == codPermissao)
                        return true;
                }

                return false;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "Visao{" +
                "pilares=" + pilares +
                '}';
    }

    @NotNull
    public static Visao createDummy() {
        final Visao visao = new Visao();
        // Cria pilar frota.
        final List<Pilar> pilares = new ArrayList<>();
        final Pilar frota = new Pilar();
        frota.setCodigo(Pilares.FROTA);
        frota.setNome("Frota");

        // Cria função de realizar aferição.
        final List<FuncaoProLog> funcoesFrota = new ArrayList<>();
        final FuncaoProLog realizarAfericao = new FuncaoProLog();
        realizarAfericao.setCodigo(Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA);
        funcoesFrota.add(realizarAfericao);

        frota.setFuncoes(funcoesFrota);
        pilares.add(frota);
        visao.setPilares(pilares);
        return visao;
    }
}