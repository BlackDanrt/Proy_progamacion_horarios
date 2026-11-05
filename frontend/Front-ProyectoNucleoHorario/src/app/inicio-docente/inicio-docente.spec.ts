import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InicioDocente } from './inicio-docente';

describe('InicioDocente', () => {
  let component: InicioDocente;
  let fixture: ComponentFixture<InicioDocente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InicioDocente],
    }).compileComponents();

    fixture = TestBed.createComponent(InicioDocente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
