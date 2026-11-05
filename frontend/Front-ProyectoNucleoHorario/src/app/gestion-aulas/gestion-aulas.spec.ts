import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionAulas } from './gestion-aulas';

describe('GestionAulas', () => {
  let component: GestionAulas;
  let fixture: ComponentFixture<GestionAulas>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GestionAulas],
    }).compileComponents();

    fixture = TestBed.createComponent(GestionAulas);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
