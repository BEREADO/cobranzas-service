package com.cibertec.cobranzasservice.services;
import com.cibertec.cobranzasservice.clients.*;
import com.cibertec.cobranzasservice.entities.*;
import com.cibertec.cobranzasservice.entities.enums.*;
import com.cibertec.cobranzasservice.models.*;
import com.cibertec.cobranzasservice.repositories.*;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service @RequiredArgsConstructor
public class CuentaPorCobrarService {
 private final CuentaPorCobrarRepository repo;
 private final ServicioRepository servicios;
 private final PuestosClient puestos;
 private final SociosClient socios;

 @Transactional(readOnly=true) public CuentaPorCobrarDTO buscar(Long id) {return dto(obtener(id));}
 @Transactional(readOnly=true) public List<CuentaPorCobrarDTO> listarPorSocio(Long id) {return repo.findBySocioId(id).stream().map(this::dto).toList();}
 @Transactional(readOnly=true) public List<CuentaPorCobrarDTO> listarPorPuesto(Long id) {return repo.findByPuestoId(id).stream().map(this::dto).toList();}

 @Transactional public List<CuentaPorCobrarDTO> generarPuestos(GenerarCuentasPuestoRequest r) {
   Servicio s=servicio(r.getServicioId());
   if(s.getCargoA()!=CargoA.PUESTO) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Servicio no corresponde a puestos");
   // Primero validar todos los IDs remotos; ninguna cuenta se guarda si falla una validacion.
   for(Long id:r.getPuestoIds()) validarPuesto(id);
   List<CuentaPorCobrar> nuevas=new ArrayList<>();
   for(Long id:r.getPuestoIds()) nuevas.add(nueva(s,r.getPeriodo(),null,id,s.getCosto(),null,null));
   return repo.saveAll(nuevas).stream().map(this::dto).toList();
 }

 @Transactional public CuentaPorCobrarDTO generarConsumo(GenerarCuentaConsumoRequest r) {
   Servicio s=servicio(r.getServicioId());
   if(!Boolean.TRUE.equals(s.getEsConsumo()) || s.getCostoUnitario()==null)
     throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Servicio sin consumo o costo unitario");
   if(s.getCargoA()!=CargoA.PUESTO) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Consumo requiere servicio de puesto");
   validarPuesto(r.getPuestoId());
   BigDecimal diferencia=r.getLecturaFinal().subtract(r.getLecturaInicial());
   BigDecimal monto=diferencia.max(BigDecimal.ZERO).multiply(s.getCostoUnitario());
   return dto(repo.save(nueva(s,r.getPeriodo(),null,r.getPuestoId(),monto,r.getLecturaInicial(),r.getLecturaFinal())));
 }

 // El Feign remoto se ejecuta fuera de transaccion; @CircuitBreaker actua sobre un metodo publico invocado desde controller.
 @CircuitBreaker(name="sociosGeneracion",fallbackMethod="sociosFallback")
 public List<SociosClient.SocioInfo> consultarSocios(List<Integer> etapas) {
   return etapas!=null&&!etapas.isEmpty() ? socios.porEtapas(etapas) : socios.todos();
 }
 public List<SociosClient.SocioInfo> sociosFallback(List<Integer> etapas,Throwable ex) {
   throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"No es posible consultar socios para generar cobranzas",ex);
 }

 @Transactional public List<CuentaPorCobrarDTO> generarSocios(GenerarCuentasSocioRequest r,List<SociosClient.SocioInfo> seleccionados) {
   Servicio s=servicio(r.getServicioId());
   if(s.getCargoA()!=CargoA.SOCIO) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Servicio no corresponde a socios");
   BigDecimal monto=r.getMonto()!=null?r.getMonto():s.getCosto();
   if(monto.compareTo(BigDecimal.ZERO)<0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Monto negativo");
   Set<String> vistos=new HashSet<>();
   List<CuentaPorCobrar> nuevos=new ArrayList<>();
   for(var socio: seleccionados) {
     String key=String.valueOf(socio.nombres())+"|"+String.valueOf(socio.apellidos());
     if(r.isSoloUnicos()&&!vistos.add(key.toLowerCase(Locale.ROOT))) continue;
     nuevos.add(nueva(s,r.getPeriodo(),socio.id(),null,monto,null,null));
   }
   return repo.saveAll(nuevos).stream().map(this::dto).toList();
 }

 @Transactional public CuentaPorCobrarDTO abonar(Long id) {CuentaPorCobrar c=obtener(id);if(c.getEstado()==EstadoCuenta.EXONERADA)throw new ResponseStatusException(HttpStatus.CONFLICT,"Cuenta exonerada"); if(c.getEstado()==EstadoCuenta.ABONADA) return dto(c); c.setEstado(EstadoCuenta.ABONADA);c.setFechaPago(LocalDate.now());return dto(repo.save(c));}
 @Transactional public CuentaPorCobrarDTO exonerar(Long id) {CuentaPorCobrar c=obtener(id);if(c.getEstado()==EstadoCuenta.ABONADA)throw new ResponseStatusException(HttpStatus.CONFLICT,"Cuenta abonada"); c.setEstado(EstadoCuenta.EXONERADA);return dto(repo.save(c));}
 @Transactional(readOnly=true) public BigDecimal total(List<Long> ids) {
   if(repo.findAllById(ids).size()!=new HashSet<>(ids).size()) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Alguna cuenta no existe");
   return repo.findAllById(ids).stream().filter(c->c.getEstado()==EstadoCuenta.ABONADA).map(CuentaPorCobrar::getMonto).reduce(BigDecimal.ZERO,BigDecimal::add);
 }

 private void validarPuesto(Long id) {
   try { puestos.buscar(id); }
   catch(FeignException.NotFound ex) {throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Puesto no encontrado: "+id,ex);}
   catch(FeignException ex) {throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"puestos-service no disponible",ex);}
 }
 private Servicio servicio(Long id) {return servicios.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Servicio no encontrado"));}
 private CuentaPorCobrar obtener(Long id) {return repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Cuenta no encontrada"));}
 private CuentaPorCobrar nueva(Servicio s,String periodo,Long socioId,Long puestoId,BigDecimal monto,BigDecimal inicio,BigDecimal fin) {
   return CuentaPorCobrar.builder().servicio(s).socioId(socioId).puestoId(puestoId).periodo(periodo).monto(monto)
      .estado(EstadoCuenta.PENDIENTE).fechaGeneracion(LocalDate.now()).lecturaInicial(inicio).lecturaFinal(fin).build();
 }
 private CuentaPorCobrarDTO dto(CuentaPorCobrar c) {
    return new CuentaPorCobrarDTO(c.getId(),c.getServicio().getId(),c.getSocioId(),c.getPuestoId(),c.getPeriodo(),c.getMonto(),c.getEstado(),c.getLecturaInicial(),c.getLecturaFinal(),c.getFechaGeneracion(),c.getFechaPago());
 }
}
