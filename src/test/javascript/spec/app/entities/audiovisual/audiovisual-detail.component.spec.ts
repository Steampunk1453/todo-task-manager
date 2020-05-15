import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ToDoTaskManagerTestModule } from '../../../test.module';
import { AudiovisualDetailComponent } from 'app/entities/audiovisual/audiovisual-detail.component';
import { Audiovisual } from 'app/shared/model/audiovisual.model';

describe('Component Tests', () => {
  describe('Audiovisual Management Detail Component', () => {
    let comp: AudiovisualDetailComponent;
    let fixture: ComponentFixture<AudiovisualDetailComponent>;
    const route = ({ data: of({ audiovisual: new Audiovisual(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ToDoTaskManagerTestModule],
        declarations: [AudiovisualDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(AudiovisualDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(AudiovisualDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load audiovisual on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.audiovisual).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
