package com.cibertec.cobranzasservice.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@FeignClient(name="socios-service")
public interface SociosClient {
 @GetMapping("/api/socios") List<SocioInfo> todos();
 @GetMapping("/api/socios/etapas") List<SocioInfo> porEtapas(@RequestParam("etapas") List<Integer> etapas);
 record SocioInfo(Long id,String nombres,String apellidos,Integer etapa) {}
}
