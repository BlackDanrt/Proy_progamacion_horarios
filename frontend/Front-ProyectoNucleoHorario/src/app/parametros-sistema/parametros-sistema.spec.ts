import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParametrosSistema } from './parametros-sistema';

describe('ParametrosSistema', () => {
  let component: ParametrosSistema;
  let fixture: ComponentFixture<ParametrosSistema>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ParametrosSistema],
    }).compileComponents();

    fixture = TestBed.createComponent(ParametrosSistema);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
