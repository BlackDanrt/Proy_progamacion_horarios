package co.edu.unbosque.horarios.util.enums;

/**
 * Enum que representa las dedicaciones de los profesores según el Estatuto
 * Docente de la Universidad El Bosque (artículo 8). Cada tipo define el tope de
 * horas semanales que puede tener un profesor.
 *
 * @author Prisma Web
 */
public enum TipoVinculacion {

	/** Tiempo completo: de 31 a 40 horas semanales. */
	TIEMPO_COMPLETO(40),

	/** Tres cuartos de tiempo: de 21 a 30 horas semanales. */
	TRES_CUARTOS_DE_TIEMPO(30),

	/** Medio tiempo: de 11 a 20 horas semanales. */
	MEDIO_TIEMPO(20),

	/** Un cuarto de tiempo: de 1 a 10 horas semanales. */
	CUARTO_DE_TIEMPO(10);

	/** Tope de horas semanales de la dedicación. */
	private final int horasMaximas;

	/**
	 * Construye el tipo de vinculación con su tope de horas semanales.
	 *
	 * @param horasMaximas tope de horas semanales.
	 */
	TipoVinculacion(int horasMaximas) {
		this.horasMaximas = horasMaximas;
	}

	/**
	 * Obtiene el tope de horas semanales de la dedicación.
	 *
	 * @return horas máximas semanales permitidas.
	 */
	public int getHorasMaximas() {
		return horasMaximas;
	}

}