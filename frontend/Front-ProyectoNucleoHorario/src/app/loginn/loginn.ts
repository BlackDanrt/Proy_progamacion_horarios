import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthAdminService } from '../services/auth-admin';
import { AuthProfesorService } from '../services/auth-profesor';

@Component({
  selector: 'app-loginn',
  standalone: false,
  templateUrl: './loginn.html',
  styleUrl: './loginn.css',
})
/** Gestiona el inicio de sesión para docentes y administradores. */
export class Loginn {

  /** Pestaña del formulario que se muestra actualmente. */
  pestanaActiva: 'docente' | 'administrador' = 'docente';
  /** Controla si la contraseña se muestra en texto legible. */
  mostrarContrasenia: boolean = false;

  /** Correo institucional ingresado en el formulario. */
  correo: string = '';
  /** Contraseña ingresada en el formulario. */
  contrasenia: string = '';

  /** Indica si hay una solicitud de autenticación en curso. */
  estaCargando: boolean = false;
  /** Controla la cortina visual que aparece tras iniciar sesión. */
  cortinaNegra: boolean = false;

  /** URL del logo institucional mostrado en la pantalla de acceso. */
  logoUniversidad: string = 'https://images.squarespace-cdn.com/content/v1/5f46720407ab6957e16cb0c5/340ef60e-0fdf-4d0b-96d4-8a9a01d966b2/Logo_de_la_Universidad_El_Bosque.svg.png';
  /** URL de la imagen institucional complementaria. */
  escudoUniversidad: string = 'https://images.squarespace-cdn.com/content/v1/5f46720407ab6957e16cb0c5/1598452620676-V2SN83HBTNR4IZ354YR4/ciencias.jpg';
  /** URL de la imagen del campus usada en la pantalla de acceso. */
  imagenCampus: string = 'https://www.semana.com/resizer/v2/C7LTUNRGBFFHZCU7TSLVVV7PCE.jpeg?auth=6b3e2d3142679403466c2bd9f31c0ab3be783fd449b710b3bcde9fd1600dda3f&smart=true&quality=75&width=1920';

  /** Estado y contenido del modal usado para informar errores de acceso. */
  modalError = {
    visible: false,
    titulo: 'Error',
    mensaje: '',
    tipo: 'error' as 'error' | 'conexion',
  };

  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private authAdminService = inject(AuthAdminService);
  private authProfesorService = inject(AuthProfesorService);

  /** Cambia el tipo de usuario y cierra cualquier mensaje abierto. */
  cambiarPestana(pestana: 'docente' | 'administrador'): void {
    this.pestanaActiva = pestana;
    this.cerrarModal();
  }

  /** Alterna la visibilidad de la contraseña en el formulario. */
  toggleMostrarContrasenia(): void {
    this.mostrarContrasenia = !this.mostrarContrasenia;
  }

  /** Configura y muestra un mensaje de error o de conexión. */
  mostrarError(titulo: string, mensaje: string, tipo: 'error' | 'conexion' = 'error'): void {
    this.modalError.titulo = titulo;
    this.modalError.mensaje = mensaje;
    this.modalError.tipo = tipo;
    this.modalError.visible = true;
    this.cdr.detectChanges();
  }

  /** Oculta el modal de error. */
  cerrarModal(): void {
    this.modalError.visible = false;
  }

  /** Valida las credenciales, inicia sesión y navega según el rol. */
  iniciarSesion(): void {
    if (this.estaCargando) return;

    if (!this.correo || !this.contrasenia) {
      this.mostrarError('Campos incompletos', 'Debes completar todos los campos antes de continuar.');
      return;
    }

    const pattern = /^[A-Za-z0-9._%+-]+@unbosque\.edu\.co$/;

    if (!pattern.test(this.correo)) {
      this.mostrarError(
        'Formato inválido',
        'El correo ingresado no es válido.\nDebe utilizar un correo institucional de la Universidad El Bosque.\nEjemplo: usuario@unbosque.edu.co'
      );
      return;
    }

    this.estaCargando = true;
    const esAdmin = this.pestanaActiva === 'administrador';

    const peticion = esAdmin
      ? this.authAdminService.login(this.correo, this.contrasenia)
      : this.authProfesorService.login(this.correo, this.contrasenia);

    peticion.subscribe({
      next: () => {
        // Login exitoso: activar cortina y navegar
        this.cortinaNegra = true;
        this.cdr.detectChanges();

        setTimeout(() => {
          this.estaCargando = false;
          this.cdr.detectChanges();
          this.router.navigate([esAdmin ? '/inicio-administrador' : '/registro-profesor']);
        }, 800);
      },
      error: (error) => {
        this.estaCargando = false;
        this.cortinaNegra = false;

        if (error.status === 401) {
          this.mostrarError(
            'Acceso denegado',
            'Correo o contraseña incorrectos.\nVerifica tus credenciales e\nintenta de nuevo.'
          );
        } else if (error.status === 403) {
          // Credenciales válidas, pero la cuenta es del otro rol
          this.mostrarError(
            'Cuenta incorrecta',
            esAdmin
              ? 'Esta cuenta no es de administrador.\nUsa la pestaña "Ingreso Docente".'
              : 'Esta cuenta no es de docente.\nUsa la pestaña "Ingreso Administrador".'
          );
        } else if (error.status === 0) {
          this.mostrarError(
            'Sin conexión',
            'No se pudo conectar con el\nservidor. Revisa tu conexión\no intenta más tarde.',
            'conexion'
          );
        } else {
          this.mostrarError(
            'Error inesperado',
            `Ocurrió un error al intentar\ningresar. Código: ${error.status}.\nIntenta de nuevo.`
          );
        }

        console.error('Error en el login:', error);
      },
    });
  }
}
