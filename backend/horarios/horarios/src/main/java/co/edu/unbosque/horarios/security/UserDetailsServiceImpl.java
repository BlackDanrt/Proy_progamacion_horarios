package co.edu.unbosque.horarios.security;

//import co.edu.unbosque.horarios.repository.UsuarioRepository; ----- POR REVISAR
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementación personalizada de {@link UserDetailsService}.
 * <p>
 * Esta clase se encarga de cargar los datos de un usuario
 * desde la base de datos utilizando su correo electrónico.
 * </p>
 *
 * <p>
 * Spring Security utiliza esta implementación durante
 * el proceso de autenticación.
 * </p>
 *
 * @version 1.0
 */
//@Service
//public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * Repositorio utilizado para consultar usuarios en la base de datos.
     */
//    private final UsuarioRepository userRepository;

    /**
     * Constructor de la clase.
     *
     * @param userRepository repositorio de usuarios.
     */
//    public UserDetailsServiceImpl(UsuarioRepository userRepository) {
//        this.userRepository = userRepository;
//    }

    /**
     * Carga un usuario a partir de su correo electrónico.
     * <p>
     * Si el usuario existe, se retorna como un objeto
     * {@link UserDetails}. En caso contrario, se lanza
     * una excepción indicando que el usuario no fue encontrado.
     * </p>
     *
     * @param correo correo electrónico del usuario.
     * @return detalles del usuario autenticado.
     * @throws UsernameNotFoundException si no existe un usuario con el correo dado.
     */
//    @Override
//    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
//        return userRepository.findByCorreo(correo)
//            .orElseThrow(() ->
//                new UsernameNotFoundException(
//                    "No se encontró el usuario con correo: " + correo
//                )
//            );
//    }
//}