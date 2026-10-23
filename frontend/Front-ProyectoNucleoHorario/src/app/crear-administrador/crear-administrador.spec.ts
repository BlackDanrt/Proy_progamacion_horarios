import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrearAdministrador } from './crear-administrador';

describe('CrearAdministrador', () => {
  let component: CrearAdministrador;
  let fixture: ComponentFixture<CrearAdministrador>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CrearAdministrador],
    }).compileComponents();

    fixture = TestBed.createComponent(CrearAdministrador);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
