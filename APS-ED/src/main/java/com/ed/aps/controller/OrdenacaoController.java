package com.ed.aps.controller;

import com.ed.aps.service.OrdenacaoService;
import com.ed.aps.service.OrdenacaoService.ResultadoRelatorio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrdenacaoController {

    private final OrdenacaoService service;

    public OrdenacaoController(OrdenacaoService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/ordenacao";
    }

    @GetMapping("/ordenacao")
    public String exibirRelatorio(
            @RequestParam(defaultValue = "quick") String algoritmo,
            @RequestParam(defaultValue = "municipio") String criterio,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        ResultadoRelatorio relatorio = service.processarRelatorio(algoritmo, criterio, page);

        model.addAttribute("algoritmoSelecionado", algoritmo);
        model.addAttribute("criterioSelecionado", criterio);
        model.addAttribute("pagina", relatorio.pagina);
        model.addAttribute("metricas", relatorio.metricas);

        return "ordenacao";
    }
}

