import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EdicionHorarios } from './edicion-horarios';

describe('EdicionHorarios', () => {
  let component: EdicionHorarios;
  let fixture: ComponentFixture<EdicionHorarios>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EdicionHorarios],
    }).compileComponents();

    fixture = TestBed.createComponent(EdicionHorarios);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
