import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegistroProfesor } from './registro-profesor';

describe('RegistroProfesor', () => {
  let component: RegistroProfesor;
  let fixture: ComponentFixture<RegistroProfesor>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegistroProfesor],
    }).compileComponents();

    fixture = TestBed.createComponent(RegistroProfesor);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
