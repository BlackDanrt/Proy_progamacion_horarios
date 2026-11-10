import { ChangeDetectorRef, Component, HostListener, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AdministradorService } from '../services/administrador.service';
import { AdministradorModel } from '../models/administrador.model';
import { AuthAdminService } from '../services/auth-admin';

@Component({
  selector: 'app-crear-administrador',
  standalone: false,
  templateUrl: './crear-administrador.html',
  styleUrl: './crear-administrador.css',
})
export class CrearAdministrador {
  /** Controla la apertura del menú de usuario. */
  mostrarMenuUsuario = signal<boolean>(false);

  /** Nombre del administrador que se va a registrar. */
  nombre: string = '';
  /** Apellido del administrador que se va a registrar. */
  apellido: string = '';
  /** Número de documento ingresado en el formulario. */
  documento: number | null = null;
  /** Correo electrónico del administrador. */
  correo: string = '';
  /** Código institucional del administrador. */
  codigoAdministrador: string = '';
  /** Contraseña de acceso del administrador. */
  contrasenia: string = '';
  /** Estado inicial de la cuenta registrada. */
  estado: boolean = true;
  /** Controla si la contraseña se muestra en texto legible. */
  mostrarContrasenia: boolean = false;
  /** Indica si el envío del formulario está en curso. */
  estaCargando: boolean = false;

  /** Estado y contenido del modal de resultado del registro. */
  modalError = {
    visible: false,
    titulo: '',
    mensaje: '',
    tipo: 'error', // 'error' | 'exito'
  };

  constructor(
    private router: Router,
    private authAdminService: AuthAdminService,
    private adminService: AdministradorService,
  ) {}

  /** Inicializa los servicios de navegación y administración. */
  ngOnInit(): void {}

  /** Alterna el menú de usuario y evita propagar el clic al documento. */
  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenuUsuario.update((v) => !v);
  }

  /** Cierra la sesión administrativa y navega a la pantalla de acceso. */
  cerrarSesion(): void {
    this.mostrarMenuUsuario.set(false);
    if (this.authAdminService?.cerrarSesionAdmin) {
      this.authAdminService.cerrarSesionAdmin();
    }
    this.router.navigate(['/loginn']);
  }

  @HostListener('document:click')
  /** Cierra el menú cuando se hace clic fuera de él. */
  cerrarMenuAlHacerClicFuera(): void {
    if (this.mostrarMenuUsuario()) {
      this.mostrarMenuUsuario.set(false);
    }
  }

  /** Alterna la visibilidad de la contraseña. */
  toggleMostrarContrasenia(): void {
    this.mostrarContrasenia = !this.mostrarContrasenia;
  }

  /** Navega a la ruta indicada. */
  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  /** Valida y envía al backend los datos del nuevo administrador. */
  guardarAdministrador(): void {
    // Validaciones básicas antes de enviar
    if (
      !this.nombre ||
      !this.apellido ||
      !this.documento ||
      !this.correo ||
      !this.codigoAdministrador ||
      !this.contrasenia
    ) {
      this.modalError = {
        visible: true,
        titulo: 'Campos incompletos',
        mensaje: 'Por favor, diligencie todos los campos obligatorios.',
        tipo: 'error',
      };
      return;
    }

    this.estaCargando = true;

    const nuevoAdmin: AdministradorModel = {
      nombre: this.nombre,
      apellido: this.apellido,
      documento: this.documento,
      correo: this.correo,
      contrasenia: this.contrasenia,
      codigoAdministrador: this.codigoAdministrador,
      estado: this.estado,
    };

    this.adminService.crearAdministrador(nuevoAdmin).subscribe({
      next: (response) => {
        this.estaCargando = false;
        this.modalError = {
          visible: true,
          titulo: '¡Registro Exitoso!',
          mensaje: 'El administrador ha sido creado correctamente en el sistema.',
          tipo: 'exito',
        };
      },
      error: (error: HttpErrorResponse) => {
        this.estaCargando = false;
        let msjError =
          'Ocurrió un error al intentar crear el administrador. Verifica tu conexión o intenta nuevamente.';

        if (error.error && typeof error.error === 'string') {
          msjError = error.error;
        } else if (error.error && error.error.message) {
          msjError = error.error.message;
        }

        this.modalError = {
          visible: true,
          titulo: 'Error de validación',
          mensaje: msjError,
          tipo: 'error',
        };
      },
    });
  }

  /** Cancela el registro y vuelve a la gestión de bases de datos. */
  cancelar(): void {
    this.router.navigate(['/gestion-bases-datos']);
  }

  /** Regresa a la pantalla de gestión de bases de datos. */
  volverAGestion(): void {
    this.router.navigate(['/gestion-bases-datos']);
  }

  /** Oculta el modal de confirmación o error. */
  cerrarModal(): void {
    this.modalError.visible = false;
  }
}
