package br.ufpb.dcx.flow.dev.s.controller;

import br.ufpb.dcx.flow.dev.s.dto.SaleRequest;
import br.ufpb.dcx.flow.dev.s.dto.SaleResponse;
import br.ufpb.dcx.flow.dev.s.service.SaleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sales")
@CrossOrigin(origins = "*")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<SaleResponse> checkout(@RequestBody SaleRequest request) {
        SaleResponse response = saleService.finishSale(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> getSale(@PathVariable Long id) {
        SaleResponse response = saleService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SaleResponse>> getAllSales() {
        return ResponseEntity.ok(saleService.findAllResponses());
    }
}