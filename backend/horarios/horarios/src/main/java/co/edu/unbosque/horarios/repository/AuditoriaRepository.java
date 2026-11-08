package co.edu.unbosque.horarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.horarios.entity.Auditoria;

/**
 * Interfaz de Repositorio encargada de proveer las operaciones de acceso a datos (CRUD)
 * y persistencia para la entidad {@link Auditoria} mediante Spring Data JPA.
 * @author Prisma Web
 * @version 1.0
 */
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
	
}
