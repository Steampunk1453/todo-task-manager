import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IAudiovisual } from 'app/shared/model/audiovisual.model';
import { AudiovisualService } from './audiovisual.service';

@Component({
  templateUrl: './audiovisual-delete-dialog.component.html'
})
export class AudiovisualDeleteDialogComponent {
  audiovisual?: IAudiovisual;

  constructor(
    protected audiovisualService: AudiovisualService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.audiovisualService.delete(id).subscribe(() => {
      this.eventManager.broadcast('audiovisualListModification');
      this.activeModal.close();
    });
  }
}
