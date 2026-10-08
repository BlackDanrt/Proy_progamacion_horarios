import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionBasesDatos } from './gestion-bases-datos';

describe('GestionBasesDatos', () => {
  let component: GestionBasesDatos;
  let fixture: ComponentFixture<GestionBasesDatos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GestionBasesDatos],
    }).compileComponents();

    fixture = TestBed.createComponent(GestionBasesDatos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
