import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionAsignatura } from './gestion-asignatura';

describe('GestionAsignatura', () => {
  let component: GestionAsignatura;
  let fixture: ComponentFixture<GestionAsignatura>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GestionAsignatura],
    }).compileComponents();

    fixture = TestBed.createComponent(GestionAsignatura);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
