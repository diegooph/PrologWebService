package br.com.zalf.prolog.webservice.permissao;

import br.com.zalf.prolog.webservice.commons.util.MathUtils;
import br.com.zalf.prolog.webservice.permissao.pilares.FuncaoProLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilar;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by luiz on 4/18/16.
 */
public class Visao {
    List<Pilar> pilares;

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
        realizarAfericao.setDescricao("Permite realizar a aferição");
        funcoesFrota.add(realizarAfericao);

        frota.setFuncoes(funcoesFrota);
        pilares.add(frota);
        visao.setPilares(pilares);
        return visao;
    }

    public List<Pilar> getPilares() {
        return pilares;
    }

    public void setPilares(final List<Pilar> pilares) {
        this.pilares = pilares;
    }

    @Deprecated
    public boolean hasAccessToFunction(final int codPilar, final int codPermissao) {
        return hasAccessToFunction(codPermissao);
    }

    public boolean hasAccessToFunction(final int codPermissao) {
        if (pilares == null) {
            return false;
        }

        // O primeiro dígito de uma permissão é sempre o código do pilar.
        final int codPilar = MathUtils.getNumberInPosition(codPermissao, 1);
        for (final Pilar pilar : pilares) {
            if (pilar.codigo == codPilar) {
                if (pilar.funcoes == null) {
                    return false;
                }

                for (final FuncaoProLog funcao : pilar.funcoes) {
                    if (funcao != null && funcao.getCodigo() == codPermissao) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public void removePilar(final int codPilar) {
        pilares = pilares
                .stream()
                .filter(p -> p.getCodigo() != codPilar)
                .collect(Collectors.toList());
    }
}