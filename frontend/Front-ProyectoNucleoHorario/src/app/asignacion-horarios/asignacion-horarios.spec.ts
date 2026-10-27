import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AsignacionHorarios } from './asignacion-horarios';

describe('AsignacionHorarios', () => {
  let component: AsignacionHorarios;
  let fixture: ComponentFixture<AsignacionHorarios>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AsignacionHorarios],
    }).compileComponents();

    fixture = TestBed.createComponent(AsignacionHorarios);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
