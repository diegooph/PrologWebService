package br.com.zalf.prolog.webservice.commons.report;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Classe genérica que representa um relatório.
 */
public class Report {
    @NotNull
    private List<String> header;
    @NotNull
    private List<List<String>> data;

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return "Report{" +
                "header=" + header +
                ", data=" + data +
                '}';
    }
}