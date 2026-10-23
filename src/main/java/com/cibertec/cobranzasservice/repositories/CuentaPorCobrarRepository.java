package com.cibertec.cobranzasservice.repositories;
import com.cibertec.cobranzasservice.entities.CuentaPorCobrar;
import com.cibertec.cobranzasservice.entities.enums.EstadoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CuentaPorCobrarRepository extends JpaRepository<CuentaPorCobrar,Long> {
 List<CuentaPorCobrar> findBySocioId(Long socioId);
 List<CuentaPorCobrar> findByPuestoId(Long puestoId);
 List<CuentaPorCobrar> findBySocioIdAndEstado(Long socioId,EstadoCuenta estado);
 List<CuentaPorCobrar> findByPuestoIdAndEstado(Long puestoId,EstadoCuenta estado);
 List<CuentaPorCobrar> findByServicioId(Long servicioId);
}
