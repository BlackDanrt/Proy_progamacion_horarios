package co.edu.unbosque.horarios.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Inserta los salones de los bloques A a O (excepto C, D, G, H y J) que todavía no existan (compara por bloque + número).
 * Los salones que ya están en la base NO se tocan ni se duplican.
 * Cuando ya estén cargados, borre esta clase.
 *
 * Supone la tabla "salon" y las columnas bloque, numero_salon, capacidad,
 * tiene_computadores, tiene_sillas_moviles. Si su entidad usa otros nombres, cámbielos abajo.
 *
 * LOS DATOS SON DE EJEMPLO: ajuste las reglas de computadores, sillas y capacidad a la realidad.
 */
@Component
public class CargaSalonesRunner implements CommandLineRunner {

	private static final String BLOQUES = "ABEFIKLMNO"; // sin C, D, G, H ni J
	private static final String BLOQUES_CON_COMPUTADORES = "ABF";

	private final JdbcTemplate jdbc;

	public CargaSalonesRunner(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public void run(String... args) {
		// La columna "bloque" quedó como ENUM de MySQL solo con las letras que existían en el enum Java
		// (por eso "Data truncated for column 'bloque'"). Se pasa a VARCHAR para aceptar A a O.
		try {
			jdbc.execute("ALTER TABLE salon MODIFY COLUMN bloque VARCHAR(5) NOT NULL");
		} catch (Exception e) {
			System.out.println("[CargaSalones] No se pudo cambiar el tipo de 'bloque': " + e.getMessage());
		}

		int insertados = 0;
		int yaExistian = 0;

		for (char b : BLOQUES.toCharArray()) {
			boolean computadores = BLOQUES_CON_COMPUTADORES.indexOf(b) >= 0;
			for (int piso = 1; piso <= 4; piso++) {
				for (int n = 1; n <= 4; n++) {
					int numero = piso * 100 + n;

					Integer hay = jdbc.queryForObject(
							"SELECT COUNT(*) FROM salon WHERE bloque = ? AND numero_salon = ?",
							Integer.class, String.valueOf(b), numero);
					if (hay != null && hay > 0) {
						yaExistian++;
						continue;
					}

					boolean sillas = (n == 3 || n == 4) && !computadores;
					int capacidad = 40;

					jdbc.update(
							"INSERT INTO salon (bloque, numero_salon, capacidad, tiene_computadores, tiene_sillas_moviles) "
									+ "VALUES (?, ?, ?, ?, ?)",
							String.valueOf(b), numero, capacidad, computadores, sillas);
					insertados++;
				}
			}
		}
		System.out.println("[CargaSalones] Insertados: " + insertados + " | ya existían: " + yaExistian);
	}
}