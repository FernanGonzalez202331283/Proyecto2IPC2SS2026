import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Grados } from './grados';

describe('Grados', () => {
  let component: Grados;
  let fixture: ComponentFixture<Grados>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Grados],
    }).compileComponents();

    fixture = TestBed.createComponent(Grados);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
