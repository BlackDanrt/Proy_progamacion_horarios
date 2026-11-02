import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { TimeoutError, finalize, timeout } from 'rxjs';
import { AdministradorService } from '../services/administrador.service';
import { AdministradorModel } from '../models/administrador.model';

@Component({
  selector: 'app-crear-administrador',
  standalone: false,
  templateUrl: './crear-administrador.html',
  styleUrl: './crear-administrador.css',
})
export class CrearAdministrador {

  nombre: string = '';
  apellido: string = '';
  documento: number | null = null;
  correo: string = '';
  codigoAdministrador: string = '';
  estado: boolean = true;
  contrasenia: string = '';

  mostrarContrasenia: boolean = false;
  estaCargando: boolean = false;

  modalError = {
    visible: false,
    titulo: 'Error',
    mensaje: '',
    tipo: 'error' as 'error' | 'conexion' | 'exito'
  };

  private readonly router = inject(Router);
  private readonly administradorService = inject(AdministradorService);
  private readonly cdr = inject(ChangeDetectorRef);

  toggleMostrarContrasenia(): void {
    this.mostrarContrasenia = !this.mostrarContrasenia;
  }

  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  mostrarMensaje(
    titulo: string,
    mensaje: string,
    tipo: 'error' | 'conexion' | 'exito' = 'error'
  ): void {
    this.modalError = {
      visible: true,
      titulo,
      mensaje,
      tipo
    };
    this.cdr.markForCheck();
  }

  cerrarModal(): void {
    this.modalError = { ...this.modalError, visible: false };
    this.cdr.markForCheck();
  }

  /**
   * Verifica que el nombre o apellido cumpla las reglas establecidas.
   * Debe contener entre una y dos palabras, únicamente letras y espacios.
   */
  verificarNombre(nombre: string): string | null {

    if (nombre === null || nombre.trim().length === 0) {
      return 'El nombre no puede estar vacío.';
    }

    if (nombre.includes('  ')) {
      return 'El nombre no puede contener espacios dobles.';
    }

    if (!/^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$/.test(nombre)) {
      return 'El nombre solo debe contener letras y espacios.';
    }

    const palabras = nombre.trim().split(/\s+/);

    if (palabras.length < 1 || palabras.length > 2) {
      return 'El nombre debe tener mínimo una palabra y máximo dos.';
    }

    return null;
  }

  guardarAdministrador(): void {

    if (this.estaCargando) {
      return;
    }

    // Validar campos obligatorios
    if (
      !this.nombre.trim() ||
      !this.apellido.trim() ||
      this.documento === null ||
      !this.correo.trim() ||
      !this.codigoAdministrador.trim() ||
      !this.contrasenia.trim()
    ) {
      this.mostrarMensaje(
        'Campos incompletos',
        'Debes completar todos los campos del formulario.'
      );
      return;
    }

    // Validar nombre
    const errorNombre = this.verificarNombre(this.nombre);

    if (errorNombre !== null) {
      this.mostrarMensaje('Nombre inválido', errorNombre);
      return;
    }

    // Validar apellido con las mismas reglas
    const errorApellido = this.verificarNombre(this.apellido);

    if (errorApellido !== null) {
      this.mostrarMensaje('Apellido inválido', errorApellido);
      return;
    }

    // Validar documento
    const documento = this.documento;

    if (
      !Number.isInteger(documento) ||
      documento <= 0 ||
      String(documento).length < 6 ||
      String(documento).length > 10
    ) {
      this.mostrarMensaje(
        'Documento inválido',
        'El documento debe ser un número entero de mínimo 6 y máximo 10 dígitos.'
      );
      return;
    }

    // Validar correo institucional
    const expresionCorreo = /^[A-Za-z0-9._%+-]+@unbosque\.edu\.co$/;

    if (!expresionCorreo.test(this.correo.trim())) {
      this.mostrarMensaje(
        'Correo inválido',
        'Debes ingresar un correo institucional terminado en @unbosque.edu.co.'
      );
      return;
    }

    // Validar contraseña
    if (
      this.contrasenia.length < 8 ||
      !/[A-Z]/.test(this.contrasenia) ||
      !/[0-9]/.test(this.contrasenia) ||
      /\s/.test(this.contrasenia)
    ) {
      this.mostrarMensaje(
        'Contraseña inválida',
        'La contraseña debe tener mínimo 8 caracteres, una mayúscula y un número. No debe contener espacios.'
      );
      return;
    }

    // Construir administrador
    const nuevoAdministrador: AdministradorModel = {
      nombre: this.nombre.trim(),
      apellido: this.apellido.trim(),
      documento: documento,
      correo: this.correo.trim(),
      contrasenia: this.contrasenia,
      codigoAdministrador: this.codigoAdministrador.trim(),
      estado: this.estado
    };

    // Iniciar proceso de guardado
    this.estaCargando = true;
    this.cdr.markForCheck();

    this.administradorService.crearAdministrador(nuevoAdministrador)
      .pipe(
        timeout(30000),
        finalize(() => {
          this.estaCargando = false;
          this.cdr.markForCheck();
        })
      )
      .subscribe({

        next: (respuesta: string) => {
          this.mostrarMensaje(
            'Administrador creado',
            respuesta || 'El administrador se ha registrado correctamente.',
            'exito'
          );
        },

        error: (error: unknown) => {

          // Tiempo de espera agotado
          if (error instanceof TimeoutError) {
            this.mostrarMensaje(
              'Tiempo de espera agotado',
              'El servidor tardó demasiado en responder. Verifica la conexión e inténtalo nuevamente.',
              'conexion'
            );
            return;
          }

          // Error no HTTP
          if (!(error instanceof HttpErrorResponse)) {
            this.mostrarMensaje(
              'Error inesperado',
              'Ocurrió un problema al intentar crear el administrador.'
            );
            console.error('Error al crear administrador:', error);
            return;
          }

          if (error.status === 0) {
            this.mostrarMensaje(
              'Error de conexión',
              'No fue posible conectarse con el servidor. Verifica tu conexión e inténtalo nuevamente.',
              'conexion'
            );
            return;
          }

          const mensajeBackend =
            typeof error.error === 'string'
              ? error.error
              : error.error?.message || error.error?.mensaje || '';

          if (error.status === 400) {
            this.mostrarMensaje(
              'No se pudo crear el administrador',
              mensajeBackend || 'Verifica los datos ingresados.'
            );

          } else if (error.status === 409) {
            this.mostrarMensaje(
              'Registro duplicado',
              mensajeBackend || 'Ya existe un administrador con esos datos.'
            );

          } else {
            this.mostrarMensaje(
              'Error inesperado',
              mensajeBackend || 'Ocurrió un error al intentar crear el administrador.'
            );
          }

          console.error('Error al crear administrador:', error);
        }
      });
  }

  cancelar(): void {
    this.router.navigate(['/gestion-bases-datos']);
  }

  volverAGestion(): void {
    this.cerrarModal();
    this.router.navigate(['/gestion-bases-datos']);
  }
}
