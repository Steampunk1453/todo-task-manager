import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { ToDoTaskManagerTestModule } from '../../../test.module';
import { AudiovisualUpdateComponent } from 'app/entities/audiovisual/audiovisual-update.component';
import { AudiovisualService } from 'app/entities/audiovisual/audiovisual.service';
import { Audiovisual } from 'app/shared/model/audiovisual.model';

xdescribe('Component Tests', () => {
  describe('Audiovisual Management Update Component', () => {
    let comp: AudiovisualUpdateComponent;
    let fixture: ComponentFixture<AudiovisualUpdateComponent>;
    let service: AudiovisualService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ToDoTaskManagerTestModule],
        declarations: [AudiovisualUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(AudiovisualUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AudiovisualUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(AudiovisualService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Audiovisual(123, 'title', 'genre', 'platform', 'platformUrl', undefined, undefined, 0, undefined);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Audiovisual(undefined, undefined, undefined, undefined, undefined, undefined, undefined, 0, undefined);
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
