package co.edu.unbosque.horarios.security;

import co.edu.unbosque.horarios.entity.Administrador;
import co.edu.unbosque.horarios.entity.Profesor;
import co.edu.unbosque.horarios.repository.AdministradorRepository;
import co.edu.unbosque.horarios.repository.ProfesorRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementación personalizada de {@link UserDetailsService}.
 * <p>
 * Esta clase se encarga de cargar los datos de un usuario desde la base de
 * datos utilizando su correo electrónico.
 * </p>
 *
 * <p>
 * Spring Security utiliza esta implementación durante el proceso de
 * autenticación.
 * </p>
 *
 * @version 1.0
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	/**
	 * Repositorios utilizados para consultar usuarios en la base de datos.
	 */
	private final AdministradorRepository adminRepo;
	private final ProfesorRepository profesorRepo;

	/**
	 * Constructor de la clase.
	 *
	 * @param adminRepo    repositorio de administradores.
	 * @param profesorRepo repositorio de profesores.
	 */
	public UserDetailsServiceImpl(AdministradorRepository adminRepo, ProfesorRepository profesorRepo) {
		this.adminRepo = adminRepo;
		this.profesorRepo = profesorRepo;
	}

	/**
	 * Carga un usuario a partir de su correo electrónico buscando en
	 * Administradores y Profesores.
	 * <p>
	 * Si el usuario existe, se retorna como un objeto {@link UserDetails}. En caso
	 * contrario, se lanza una excepción indicando que el usuario no fue encontrado.
	 * </p>
	 *
	 * @param correo correo electrónico del usuario.
	 * @return detalles del usuario autenticado.
	 * @throws UsernameNotFoundException si no existe un usuario con el correo dado.
	 * @throws DisabledException         si el administrador se encuentra inactivo.
	 */
	@Override
	public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
		Optional<Administrador> admin = adminRepo.findByCorreo(correo);
		if (admin.isPresent()) {
			Administrador administrador = admin.get();

			if (!administrador.isEstado()) {
				throw new DisabledException("Cuenta de administrador inactiva. Contacte a soporte.");
			}

			return administrador;
		}

		Optional<Profesor> prof = profesorRepo.findByCorreo(correo);
		if (prof.isPresent()) {
			return prof.get();
		}

		throw new UsernameNotFoundException("No se encontró el usuario con correo: " + correo);
	}
}