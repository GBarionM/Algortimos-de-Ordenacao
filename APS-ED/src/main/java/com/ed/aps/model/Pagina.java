package com.ed.aps.model;

import java.util.List;

public class Pagina<T> {
    private List<T> conteudo;
    private int paginaAtual;
    private int totalPaginas;
    private int tamanhoPagina;
    private long totalElementos;

    public Pagina(List<T> todosElementos, int paginaAtual, int tamanhoPagina) {
        if (tamanhoPagina < 1) throw new IllegalArgumentException("tamanhoPagina deve ser >= 1");
        this.paginaAtual = paginaAtual;
        this.tamanhoPagina = tamanhoPagina;
        this.totalElementos = todosElementos.size();

        this.totalPaginas = (int) Math.ceil((double) totalElementos / tamanhoPagina);
        if (this.totalPaginas == 0) this.totalPaginas = 1;

        // Mantém a página dentro dos limites válidos
        if (this.paginaAtual < 1) this.paginaAtual = 1;
        if (this.paginaAtual > this.totalPaginas) this.paginaAtual = this.totalPaginas;

        // Recorta apenas os registros da página atual
        int indiceInicio = (this.paginaAtual - 1) * tamanhoPagina;
        int indiceFim = Math.min(indiceInicio + tamanhoPagina, todosElementos.size());

        this.conteudo = todosElementos.subList(indiceInicio, indiceFim);
    }

    public List<T> getConteudo() { return conteudo; }
    public int getPaginaAtual() { return paginaAtual; }
    public int getTamanhoPagina() { return tamanhoPagina; }
    public int getTotalPaginas() { return totalPaginas; }
    public long getTotalElementos() { return totalElementos; }
    /** Primeiro registro exibido (base 1); 0 se não há registros. */
    public long getPrimeiroRegistro() {
        return totalElementos == 0 ? 0 : (long) (paginaAtual - 1) * tamanhoPagina + 1;
    }
    /** Último registro exibido (base 1). */
    public long getUltimoRegistro() {
        return Math.min((long) paginaAtual * tamanhoPagina, totalElementos);
    }
    public boolean hasAnterior() { return paginaAtual > 1; }
    public boolean hasProximo() { return paginaAtual < totalPaginas; }
}
