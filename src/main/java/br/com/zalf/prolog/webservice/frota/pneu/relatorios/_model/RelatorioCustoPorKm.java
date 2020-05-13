package br.com.zalf.prolog.webservice.frota.pneu.relatorios._model;

import br.com.zalf.prolog.webservice.commons.report.CsvReport;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created on 13/05/20.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 *
 * TODO: Este arquivo deverá conter a lógica para o tratamento dos dados vindos do banco, para gerar o relatório de CPK.
 */
public class RelatorioCustoPorKm implements CsvReport {
    private List<String> header;
    private Map<String, List<String>> table;
    @NotNull
    private final ResultSet rSet;

    public RelatorioCustoPorKm(@NotNull final ResultSet rSet) {
        this.rSet = rSet;
    }

    @NotNull
    @Override
    public List<String> getHeader() throws SQLException {
        if (header == null) {
            processResultSet();
        }
        return header;
    }

    @NotNull
    @Override
    public Iterable<?> getData() throws SQLException {
        if (table == null) {
            processResultSet();
        }
        return table.values();
    }

    private void processResultSet() throws SQLException {
        if (rSet.isClosed()) {
            throw new IllegalStateException("ResultSet can't be closed!!!");
        }
        header = criaHeader();
        table = new HashMap<>();
        while (rSet.next()) {
            addInfosCPKToTable();
        }
    }

    @SuppressWarnings("Duplicates")
    private void addInfosCPKToTable() throws SQLException {

    }

    @NotNull
    private List<String> criaHeader() {
        final List<String> header = new ArrayList<>();

        return header;
    }

    @NotNull
    private List<String> criaLinha() {
        final List<String> linha = new ArrayList<>(header.size());

        for (int i = 0; i < header.size(); i++) {
            linha.add(null);
        }
        return linha;
    }
}