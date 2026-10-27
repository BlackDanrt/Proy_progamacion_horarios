import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultaHorario } from './consulta-horario';

describe('ConsultaHorario', () => {
  let component: ConsultaHorario;
  let fixture: ComponentFixture<ConsultaHorario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConsultaHorario],
    }).compileComponents();

    fixture = TestBed.createComponent(ConsultaHorario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
