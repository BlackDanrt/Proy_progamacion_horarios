import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeleccionCrearRegistro } from './seleccion-crear-registro';

describe('SeleccionCrearRegistro', () => {
  let component: SeleccionCrearRegistro;
  let fixture: ComponentFixture<SeleccionCrearRegistro>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SeleccionCrearRegistro],
    }).compileComponents();

    fixture = TestBed.createComponent(SeleccionCrearRegistro);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
