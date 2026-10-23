package com.cibertec.cobranzasservice.controllers;
import com.cibertec.cobranzasservice.models.*;
import com.cibertec.cobranzasservice.services.CuentaPorCobrarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/cuentas-por-cobrar") @RequiredArgsConstructor
public class CuentaPorCobrarController {
 private final CuentaPorCobrarService service;
 @GetMapping("/{id}") public CuentaPorCobrarDTO buscar(@PathVariable Long id) {return service.buscar(id);}
 @GetMapping("/socio/{socioId}") public List<CuentaPorCobrarDTO> socio(@PathVariable Long socioId){return service.listarPorSocio(socioId);}
 @GetMapping("/puesto/{puestoId}") public List<CuentaPorCobrarDTO> puesto(@PathVariable Long puestoId){return service.listarPorPuesto(puestoId);}
 @PostMapping("/generar/puestos") public ResponseEntity<List<CuentaPorCobrarDTO>> generarPuestos(@Valid @RequestBody GenerarCuentasPuestoRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.generarPuestos(r));}
 @PostMapping("/generar/consumo") public ResponseEntity<CuentaPorCobrarDTO> consumo(@Valid @RequestBody GenerarCuentaConsumoRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.generarConsumo(r));}
 @PostMapping("/generar/socios") public ResponseEntity<List<CuentaPorCobrarDTO>> generarSocios(@Valid @RequestBody GenerarCuentasSocioRequest r){
   var socios=service.consultarSocios(r.getEtapas());
   return ResponseEntity.status(HttpStatus.CREATED).body(service.generarSocios(r,socios));
 }
 @PatchMapping("/{id}/abonar") public CuentaPorCobrarDTO abonar(@PathVariable Long id){return service.abonar(id);}
 @PatchMapping("/{id}/exonerar") public CuentaPorCobrarDTO exonerar(@PathVariable Long id){return service.exonerar(id);}
 @PostMapping("/total") public TotalResponse total(@Valid @RequestBody TotalCuentasRequest r){return new TotalResponse(service.total(r.getCuentaIds()));}
}
