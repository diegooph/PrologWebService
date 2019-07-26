package br.com.zalf.prolog.webservice.geral.dispositivo_movel;

import br.com.zalf.prolog.webservice.geral.dispositivo_movel.model.DispositivoMovel;
import br.com.zalf.prolog.webservice.geral.dispositivo_movel.model.MarcaDispositivoMovelSelecao;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 16/07/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class DispositivoMovelConverter {

    private DispositivoMovelConverter() {
        throw new IllegalStateException(DispositivoMovelConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static MarcaDispositivoMovelSelecao createMarcaCelularSelecao(@NotNull final ResultSet rSet) throws Throwable {
        return new MarcaDispositivoMovelSelecao(
                rSet.getLong("COD_MARCA"),
                rSet.getString("NOME_MARCA"));
    }

    @NotNull
    static List<DispositivoMovel> createDispositivoMovelListagem(@NotNull final ResultSet rSet) throws Throwable {
        final List<DispositivoMovel> dispositivos = new ArrayList<>();
        DispositivoMovel dispositivo = null;
        List<String> imeis = new ArrayList<>();
        if (rSet.next()) {
            do {
                if (dispositivo == null) {
                    dispositivo = DispositivoMovelConverter.createDispositivoMovel(rSet, imeis);
                    imeis.add(rSet.getString("IMEI"));
                } else {
                    if (dispositivo.getCodDispositivo() == rSet.getLong("COD_DISPOSITIVO")) {
                        imeis.add(rSet.getString("IMEI"));
                    } else {
                        dispositivos.add(dispositivo);
                        imeis = new ArrayList<>();
                        imeis.add(rSet.getString("IMEI"));
                        dispositivo = DispositivoMovelConverter.createDispositivoMovel(rSet, imeis);
                    }
                }
            } while (rSet.next());
            dispositivos.add(dispositivo);
        } else {
            throw new IllegalStateException("Nenhum dispositivo móvel foi encontrado.");
        }
        return dispositivos;
    }

    @NotNull
    static DispositivoMovel createDispositivoMovelVisualizacao(@NotNull final ResultSet rSet) throws Throwable {
        List<String> imeis = new ArrayList<>();
        DispositivoMovel dispositivo = null;
        if (rSet.next()) {
            dispositivo = DispositivoMovelConverter.createDispositivoMovel(rSet, imeis);
            do {
                imeis.add(rSet.getString("IMEI"));
            } while (rSet.next());
        } else {
            throw new IllegalStateException("Nenhum dispositivo móvel foi encontrado.");
        }
        return dispositivo;
    }

    @NotNull
    static DispositivoMovel createDispositivoMovel(@NotNull final ResultSet rSet, @NotNull final List<String> imeis) throws Throwable {
        return new DispositivoMovel(
                rSet.getLong("COD_DISPOSITIVO"),
                rSet.getLong("COD_EMPRESA"),
                imeis,
                rSet.getLong("COD_MARCA"),
                rSet.getString("MARCA"),
                rSet.getString("MODELO"),
                rSet.getString("DESCRICAO")
        );
    }
}