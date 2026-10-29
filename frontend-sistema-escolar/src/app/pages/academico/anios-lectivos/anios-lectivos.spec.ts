import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AniosLectivos } from './anios-lectivos';

describe('AniosLectivos', () => {
  let component: AniosLectivos;
  let fixture: ComponentFixture<AniosLectivos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AniosLectivos],
    }).compileComponents();

    fixture = TestBed.createComponent(AniosLectivos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
