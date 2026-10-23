package com.cibertec.cobranzasservice.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
@FeignClient(name="puestos-service")
public interface PuestosClient {
 @GetMapping("/api/puestos/{id}") PuestoInfo buscar(@PathVariable("id") Long id);
 record PuestoInfo(Long id,String numero,Long socioId,Long giroId) {}
}
