import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAudiovisual } from 'app/shared/model/audiovisual.model';

@Component({
  selector: 'jhi-audiovisual-detail',
  templateUrl: './audiovisual-detail.component.html'
})
export class AudiovisualDetailComponent implements OnInit {
  audiovisual: IAudiovisual | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ audiovisual }) => (this.audiovisual = audiovisual));
  }

  previousState(): void {
    window.history.back();
  }
}
