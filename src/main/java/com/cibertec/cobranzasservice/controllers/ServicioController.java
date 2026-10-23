package com.cibertec.cobranzasservice.controllers;
import com.cibertec.cobranzasservice.models.ServicioDTO;
import com.cibertec.cobranzasservice.services.ServicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/servicios") @RequiredArgsConstructor
public class ServicioController {
 private final ServicioService service;
 @GetMapping public List<ServicioDTO> listar(){return service.listar();}
 @GetMapping("/{id}") public ServicioDTO buscar(@PathVariable Long id){return service.buscar(id);}
 @PostMapping public ResponseEntity<ServicioDTO> crear(@Valid @RequestBody ServicioDTO dto){return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));}
 @PutMapping("/{id}") public ServicioDTO editar(@PathVariable Long id,@Valid @RequestBody ServicioDTO dto){return service.editar(id,dto);}
 @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id){service.eliminar(id);return ResponseEntity.noContent().build();}
}
