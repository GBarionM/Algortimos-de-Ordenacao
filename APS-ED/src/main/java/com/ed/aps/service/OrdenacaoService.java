package com.ed.aps.service;

import com.ed.aps.model.AlgoritmosOrdenacao;
import com.ed.aps.model.FocoIncendio;
import com.ed.aps.model.Pagina;
import com.ed.aps.model.ResultadoOrdenacao;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class OrdenacaoService {

    private static final String ARQUIVO_CSV = "focos_br_sp_ref_2023.csv";
    private static final int TAMANHO_PAGINA = 50;

    private final AlgoritmosOrdenacao algoritmos = new AlgoritmosOrdenacao();

    // O CSV é lido uma só vez e reaproveitado nas próximas requisições
    private volatile List<FocoIncendio> cache;

    private List<FocoIncendio> dados() {
        List<FocoIncendio> local = cache;
        if (local == null) {
            synchronized (this) {
                if (cache == null) {
                    cache = carregarDadosCSV(ARQUIVO_CSV);
                }
                local = cache;
            }
        }
        return local;
    }

    /** Lê o CSV do classpath em UTF-8, localizando as colunas pelo cabeçalho. */
    public List<FocoIncendio> carregarDadosCSV(String nomeArquivo) {
        List<FocoIncendio> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ClassPathResource(nomeArquivo).getInputStream(), StandardCharsets.UTF_8))) {

            String cabecalho = br.readLine();
            if (cabecalho == null) return lista;
            List<String> nomes = Arrays.stream(cabecalho.replace("\"", "").replace("﻿", "").split(","))
                    .map(String::trim).map(String::toLowerCase).toList();

            int iData = nomes.indexOf("data_pas");
            int iBioma = nomes.indexOf("bioma");
            int iMunicipio = nomes.indexOf("municipio");
            if (iData < 0 || iBioma < 0 || iMunicipio < 0) {
                throw new IllegalStateException("Cabeçalho do CSV sem as colunas data_pas/bioma/municipio: " + cabecalho);
            }
            int maiorIndice = Math.max(iData, Math.max(iBioma, iMunicipio));

            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.isBlank()) continue;
                String[] c = linha.replace("\"", "").split(",", -1);
                if (c.length <= maiorIndice) continue;
                lista.add(new FocoIncendio(c[iData].trim(), c[iBioma].trim(), c[iMunicipio].trim()));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao ler o arquivo CSV: " + ARQUIVO_CSV, e);
        }
        return lista;
    }

    public ResultadoRelatorio processarRelatorio(String algoritmo, String criterio, int numPagina) {
        // Cópia do cache: a ordenação não pode alterar os dados originais
        FocoIncendio[] vetor = dados().toArray(new FocoIncendio[0]);

        String alg = normalizarAlgoritmo(algoritmo);
        AlgoritmosOrdenacao.Estatisticas est = new AlgoritmosOrdenacao.Estatisticas();
        long inicio = System.nanoTime();

        switch (alg) {
            case "bubble" -> algoritmos.bubbleSort(vetor, criterio, est);
            case "selection" -> algoritmos.selectionSort(vetor, criterio, est);
            case "insertion" -> algoritmos.insertionSort(vetor, criterio, est);
            case "merge" -> algoritmos.mergeSort(vetor, criterio, est);
            default -> algoritmos.quickSort(vetor, criterio, est);
        }
        long fim = System.nanoTime();

        Pagina<FocoIncendio> pagina = new Pagina<>(Arrays.asList(vetor), numPagina, TAMANHO_PAGINA);

        ResultadoOrdenacao metricas = new ResultadoOrdenacao(
                alg.toUpperCase(), "", "", "",
                est.comparacoes, est.escritas, est.trocas, (fim - inicio), ""
        );
        return new ResultadoRelatorio(pagina, metricas);
    }

    private String normalizarAlgoritmo(String algoritmo) {
        String a = algoritmo == null ? "" : algoritmo.toLowerCase();
        return switch (a) {
            case "bubble", "selection", "insertion", "merge" -> a;
            default -> "quick";
        };
    }

    public static class ResultadoRelatorio {
        public final Pagina<FocoIncendio> pagina;
        public final ResultadoOrdenacao metricas;

        public ResultadoRelatorio(Pagina<FocoIncendio> pagina, ResultadoOrdenacao metricas) {
            this.pagina = pagina;
            this.metricas = metricas;
        }
    }
}
