package co.edu.unbosque.horarios.util.enums;

/**
 * Enum que representa los bloques (edificios) disponibles en la universidad.
 * <p>
 * Se guarda en la base de datos como texto (la letra del bloque), por lo que el
 * orden de las constantes no afecta los datos ya almacenados. Cada salón
 * pertenece a uno de estos bloques.
 *
 * @author Prisma Web
 */
public enum Bloque {

	/** Bloque A. */
	A,

	/** Bloque B. */
	B,

	/** Bloque E. */
	E,

	/** Bloque F. */
	F,

	/** Bloque I. */
	I,

	/** Bloque K. */
	K,

	/** Bloque L. */
	L,

	/** Bloque M. */
	M,

	/** Bloque N. */
	N,

	/** Bloque O. */
	O
}