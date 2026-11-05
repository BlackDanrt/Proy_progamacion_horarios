import {ChangeDetectorRef, Component, HostListener, inject, signal} from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { TimeoutError, finalize, timeout } from 'rxjs';
import { AdministradorService } from '../services/administrador.service';
import { AdministradorModel } from '../models/administrador.model';
import {AuthAdminService} from '../services/auth-admin';

@Component({
  selector: 'app-crear-administrador',
  standalone: false,
  templateUrl: './crear-administrador.html',
  styleUrl: './crear-administrador.css',
})
export class CrearAdministrador {

  mostrarMenuUsuario = signal<boolean>(false);

  // Propiedades del formulario
  nombre: string = '';
  apellido: string = '';
  documento: number | null = null;
  correo: string = '';
  codigoAdministrador: string = '';
  contrasenia: string = '';
  estado: boolean = true;
  mostrarContrasenia: boolean = false;
  estaCargando: boolean = false;

  modalError = {
    visible: false,
    titulo: '',
    mensaje: '',
    tipo: 'error'
  };

  constructor(
    private router: Router,
    private authAdminService: AuthAdminService
  ) {}

  ngOnInit(): void {}

  // Abrir/Cerrar menú del avatar
  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenuUsuario.update(v => !v);
  }

  // Cierre de sesión enviando al login
  cerrarSesion(): void {
    this.mostrarMenuUsuario.set(false);
    if (this.authAdminService?.cerrarSesionAdmin) {
      this.authAdminService.cerrarSesionAdmin();
    }
    this.router.navigate(['/loginn']);
  }

  // Ocultar al hacer clic fuera del desplegable
  @HostListener('document:click')
  cerrarMenuAlHacerClicFuera(): void {
    if (this.mostrarMenuUsuario()) {
      this.mostrarMenuUsuario.set(false);
    }
  }

  toggleMostrarContrasenia(): void {
    this.mostrarContrasenia = !this.mostrarContrasenia;
  }

  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  guardarAdministrador(): void {
    // Lógica para guardar
  }

  cancelar(): void {
    this.router.navigate(['/gestion-bases-datos']);
  }

  volverAGestion(): void {
    this.router.navigate(['/gestion-bases-datos']);
  }

  cerrarModal(): void {
    this.modalError.visible = false;
  }
}
