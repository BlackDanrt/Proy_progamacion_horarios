package co.edu.unbosque.horarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.unbosque.horarios.entity.ParametrosSistema;

public interface ParametrosSistemaRepository extends JpaRepository<ParametrosSistema, Long> {
}