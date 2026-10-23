package com.cibertec.cobranzasservice.services;
import com.cibertec.cobranzasservice.entities.Servicio;import com.cibertec.cobranzasservice.models.ServicioDTO;import com.cibertec.cobranzasservice.repositories.ServicioRepository;import lombok.RequiredArgsConstructor;import org.springframework.http.HttpStatus;import org.springframework.stereotype.Service;import org.springframework.web.server.ResponseStatusException;import org.springframework.transaction.annotation.Transactional;import java.util.List;
@Service @RequiredArgsConstructor public class ServicioService {
 private final ServicioRepository repo;
 @Transactional(readOnly=true) public List<ServicioDTO> listar(){return repo.findAll().stream().map(this::dto).toList();}
 @Transactional(readOnly=true) public ServicioDTO buscar(Long id){return dto(obtener(id));}
 @Transactional public ServicioDTO crear(ServicioDTO d){Servicio s=new Servicio();asignar(s,d);return dto(repo.save(s));}
 @Transactional public ServicioDTO editar(Long id,ServicioDTO d){Servicio s=obtener(id);asignar(s,d);return dto(repo.save(s));}
 @Transactional public void eliminar(Long id){repo.delete(obtener(id));}
 Servicio obtener(Long id){return repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Servicio no encontrado"));}
 private void asignar(Servicio s,ServicioDTO d){s.setNombre(d.getNombre());s.setRecurrencia(d.getRecurrencia());s.setCosto(d.getCosto());s.setMoneda(d.getMoneda());s.setCargoA(d.getCargoA());s.setEsConsumo(d.getEsConsumo());s.setCostoUnitario(d.getCostoUnitario());}
 private ServicioDTO dto(Servicio s){return new ServicioDTO(s.getId(),s.getNombre(),s.getRecurrencia(),s.getCosto(),s.getMoneda(),s.getCargoA(),s.getEsConsumo(),s.getCostoUnitario());}
}
