package co.edu.unbosque.horarios.service;

import java.util.List;

/**
 * Interfaz genérica que define las operaciones básicas CRUD
 * (Create, Read, Update, Delete) para cualquier tipo de dato.
 * <p>
 * Permite estandarizar la lógica de servicios en la aplicación,
 * facilitando la reutilización y el mantenimiento del código.
 * </p>
 * 
 * @param <T> tipo de dato sobre el cual se aplican las operaciones
 * 
 */
public interface CRUDOperation<T> {
	
	/**
	 * Crea un nuevo registro.
	 * 
	 * @param data objeto con la información a guardar
	 * @return 0 si se crea correctamente, 1 si ocurre algún error o ya existe
	 */
	public int create(T data);

	/**
	 * Obtiene todos los registros.
	 * 
	 * @return lista de elementos
	 */
	public List<T> getAll();

	/**
	 * Elimina un registro por su identificador.
	 * 
	 * @param id identificador del registro
	 * @return 0 si se elimina correctamente, 1 si no existe
	 */
	public int deleteById(Long id);

	/**
	 * Actualiza un registro existente.
	 * 
	 * @param id identificador del registro
	 * @param newData nuevos datos del objeto
	 * @return 0 si se actualiza correctamente, 1 si ocurre algún error
	 */
	public int updateById(Long id, T newData);

	/**
	 * Cuenta el número total de registros.
	 * 
	 * @return cantidad de elementos almacenados
	 */
	public long count();

	/**
	 * Verifica si existe un registro por su identificador.
	 * 
	 * @param id identificador del registro
	 * @return true si existe, false si no
	 */
	public boolean exist(Long id);
}