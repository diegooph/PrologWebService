package br.com.zalf.prolog.webservice.entrega;

import org.jetbrains.annotations.Nullable;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by jean on 18/01/16.
 */
public class ImportUtils {

    /**
     * Converte uma string contendo data e hora para um timestamp
     *
     * @param data
     * @return
     */
    public static Date toTimestamp(String data) {
        DateFormat dateFormat;
        Date date = null;
        try {
            if (data.trim().replace(" ", "").length() == 13) {
                dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
                date = dateFormat.parse(data);
            } else if (data.trim().length() == 10) {
                dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                date = dateFormat.parse(data);
            } else if (data.trim().replace(" ", "").length() == 15) {
                dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                date = dateFormat.parse(data);
            } else {
                return null;
            }
            date = dateFormat.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @SuppressWarnings("DuplicateExpressions")
    @Nullable
    public static LocalDate toLocalDate(@Nullable final String data) {
        if (data == null) {
            return null;
        }

        try {
            final String noWithespaces = data.trim().replace(" ", "");
            if (noWithespaces.length() == 13) {
                // Está nesse padrão "dd/MM/yy HH:mm", removemos "HH:mm", que contém 5 caracteres.
                return LocalDate.parse(
                        data.substring(0, noWithespaces.length() - 5),
                        DateTimeFormatter.ofPattern("dd/MM/yy"));
            } else if (noWithespaces.length() == 15) {
                // Está nesse padrão "dd/MM/yyyy HH:mm", removemos "HH:mm", que contém 5 caracteres.
                return LocalDate.parse(
                        data.substring(0, noWithespaces.length() - 5),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } else if (noWithespaces.length() == 10) {
                // Está nesse padrão "dd/MM/yyyy", basta realizar o parse.
                return LocalDate.parse(
                        noWithespaces,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
            // Qualquer outro nós não sabemos como tratar.
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Converte uma String para um Time
     *
     * @param hora uma String contendo uma hora
     * @return um Time
     */
    public static Time toTime(String hora) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        hora = hora.replace(" ", "");
        // verifica quando tem 2 espaços extras na hora (tabela mapa)
        if (hora.length() == 6) {
            hora = hora.substring(1, 6);
            dateFormat = new SimpleDateFormat("HH:mm");
        }
        if (hora.length() == 4 || hora.length() == 5 || hora.length() == 8) {
            dateFormat = new SimpleDateFormat("HH:mm");
        }
        Time time = null;
        try {
            Date date = dateFormat.parse(hora);
            time = new Time(date.getTime());
        } finally {
        }
        return time;
    }

}
