package com.ed.aps.model;

import java.text.Collator;
import java.util.Locale;

public class AlgoritmosOrdenacao {

    // Ordem alfabética em português (acentos incluídos)
    private final Collator collator = Collator.getInstance(Locale.of("pt", "BR"));

    public static class Estatisticas {
        public long comparacoes = 0;
        public long escritas = 0;
        public long trocas = 0;
    }

    private int compararFocos(FocoIncendio f1, FocoIncendio f2, String criterio) {
        return switch (criterio.toLowerCase()) {
            case "data" -> f1.getData().compareTo(f2.getData());
            case "bioma" -> collator.compare(f1.getBioma(), f2.getBioma());
            default -> collator.compare(f1.getMunicipio(), f2.getMunicipio());
        };
    }

    public void bubbleSort(FocoIncendio[] vetor, String criterio, Estatisticas e) {
        int n = vetor.length;
        for (int i = 0; i < n - 1; i++) {
            boolean trocou = false;
            for (int j = 0; j < n - 1 - i; j++) {
                e.comparacoes++;
                if (compararFocos(vetor[j], vetor[j + 1], criterio) > 0) {
                    FocoIncendio temp = vetor[j];
                    vetor[j] = vetor[j + 1];
                    vetor[j + 1] = temp;
                    e.escritas += 2;
                    e.trocas++;
                    trocou = true;
                }
            }
            if (!trocou) break;
        }
    }

    public void selectionSort(FocoIncendio[] vetor, String criterio, Estatisticas e) {
        int n = vetor.length;
        for (int i = 0; i < n - 1; i++) {
            int menor = i;
            for (int j = i + 1; j < n; j++) {
                e.comparacoes++;
                if (compararFocos(vetor[j], vetor[menor], criterio) < 0) {
                    menor = j;
                }
            }
            if (menor != i) {
                FocoIncendio temp = vetor[i];
                vetor[i] = vetor[menor];
                vetor[menor] = temp;
                e.escritas += 2;
                e.trocas++;
            }
        }
    }

    public void insertionSort(FocoIncendio[] vetor, String criterio, Estatisticas e) {
        for (int i = 1; i < vetor.length; i++) {
            FocoIncendio chave = vetor[i];
            int j = i - 1;
            while (j >= 0) {
                e.comparacoes++;
                if (compararFocos(vetor[j], chave, criterio) > 0) {
                    vetor[j + 1] = vetor[j];
                    e.escritas++;
                    j--;
                } else {
                    break;
                }
            }
            vetor[j + 1] = chave;
            e.escritas++;
        }
    }

    public void mergeSort(FocoIncendio[] vetor, String criterio, Estatisticas e) {
        FocoIncendio[] auxiliar = new FocoIncendio[vetor.length];
        mergeSort(vetor, auxiliar, 0, vetor.length - 1, criterio, e);
    }

    private void mergeSort(FocoIncendio[] vetor, FocoIncendio[] auxiliar, int inicio, int fim, String criterio, Estatisticas e) {
        if (inicio >= fim) return;
        int meio = inicio + (fim - inicio) / 2;
        mergeSort(vetor, auxiliar, inicio, meio, criterio, e);
        mergeSort(vetor, auxiliar, meio + 1, fim, criterio, e);

        int i = inicio, j = meio + 1, k = inicio;
        while (i <= meio && j <= fim) {
            e.comparacoes++;
            if (compararFocos(vetor[i], vetor[j], criterio) <= 0) {
                auxiliar[k++] = vetor[i++];
            } else {
                auxiliar[k++] = vetor[j++];
            }
            e.escritas++;
        }
        while (i <= meio) { auxiliar[k++] = vetor[i++]; e.escritas++; }
        while (j <= fim) { auxiliar[k++] = vetor[j++]; e.escritas++; }
        for (int p = inicio; p <= fim; p++) { vetor[p] = auxiliar[p]; e.escritas++; }
    }

    public void quickSort(FocoIncendio[] vetor, String criterio, Estatisticas e) {
        quickSort(vetor, 0, vetor.length - 1, criterio, e);
    }

    // Pivô central + partição em 3 vias: evita O(n²) e estouro de pilha com chaves repetidas
    private void quickSort(FocoIncendio[] vetor, int inicio, int fim, String criterio, Estatisticas e) {
        while (inicio < fim) {
            FocoIncendio pivo = vetor[inicio + (fim - inicio) / 2];
            int lt = inicio, i = inicio, gt = fim;
            while (i <= gt) {
                e.comparacoes++;
                int cmp = compararFocos(vetor[i], pivo, criterio);
                if (cmp < 0) {
                    if (lt != i) trocar(vetor, lt, i, e);
                    lt++;
                    i++;
                } else if (cmp > 0) {
                    trocar(vetor, i, gt, e);
                    gt--;
                } else {
                    i++;
                }
            }
            // Recursão só na menor parte (o resto vira laço): profundidade O(log n)
            if (lt - inicio < fim - gt) {
                quickSort(vetor, inicio, lt - 1, criterio, e);
                inicio = gt + 1;
            } else {
                quickSort(vetor, gt + 1, fim, criterio, e);
                fim = lt - 1;
            }
        }
    }

    private void trocar(FocoIncendio[] vetor, int i, int j, Estatisticas e) {
        FocoIncendio temp = vetor[i];
        vetor[i] = vetor[j];
        vetor[j] = temp;
        e.escritas += 2;
        e.trocas++;
    }
}
