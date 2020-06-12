import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IAudiovisual, Audiovisual } from 'app/shared/model/audiovisual.model';
import { AudiovisualService } from './audiovisual.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { ITitle } from 'app/shared/model/title.model';
import { IGenre } from 'app/shared/model/genre.model';
import { IPlatform } from 'app/shared/model/platform.model';
import { GenreService } from 'app/entities/genre.service';

@Component({
  selector: 'jhi-audiovisual-update',
  templateUrl: './audiovisual-update.component.html'
})
export class AudiovisualUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];
  titles: ITitle[] = [];
  genres: IGenre[] = [];
  platforms: IPlatform[] = [];
  mapUrls: any;

  editForm = this.fb.group({
    id: [],
    title: [null, [Validators.required]],
    suggestedTitle: [],
    genre: [],
    platform: [],
    platformUrl: [],
    startDate: [null, [Validators.required]],
    deadline: [null, [Validators.required]],
    check: [],
    user: []
  });

  constructor(
    protected audiovisualService: AudiovisualService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected genreService: GenreService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ audiovisual }) => {
      if (!audiovisual.id) {
        const today = moment().startOf('day');
        audiovisual.startDate = today;
        audiovisual.deadline = today;
      }
      this.updateForm(audiovisual);
      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));
    });
    this.audiovisualService.titles().subscribe(titles => {
      if (titles) {
        this.titles = titles;
      }
    });
    this.genreService.genres().subscribe(genres => {
      if (genres) {
        this.genres = genres.filter(g => g.literary !== 1);
      }
    });
    this.audiovisualService.platforms().subscribe(platforms => {
      if (platforms) {
        this.platforms = platforms;
      }
      this.mapUrls = new Map(this.platforms.map(p => [p.name, p.url]));
    });
  }

  updateForm(audiovisual: IAudiovisual): void {
    this.editForm.patchValue({
      id: audiovisual.id,
      title: audiovisual.title,
      genre: audiovisual.genre,
      platform: audiovisual.platform,
      platformUrl: audiovisual.platformUrl,
      startDate: audiovisual.startDate ? audiovisual.startDate.format(DATE_TIME_FORMAT) : undefined,
      deadline: audiovisual.deadline ? audiovisual.deadline.format(DATE_TIME_FORMAT) : undefined,
      check: audiovisual.check,
      user: audiovisual.user
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const audiovisual = this.createFromForm();
    // convert booleans to ints
    audiovisual.check = audiovisual.check ? 1 : 0;
    if (audiovisual.id !== undefined) {
      this.subscribeToSaveResponse(this.audiovisualService.update(audiovisual));
    } else {
      this.subscribeToSaveResponse(this.audiovisualService.create(audiovisual));
    }
  }

  onOptionsSelected(event: any): void {
    this.editForm.controls.title.setValue(event.target.value);
  }

  private createFromForm(): IAudiovisual {
    return {
      ...new Audiovisual(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      genre: this.editForm.get(['genre'])!.value,
      platform: this.editForm.get(['platform'])!.value,
      platformUrl: this.mapUrls.get(this.editForm.get(['platform'])!.value),
      startDate: this.editForm.get(['startDate'])!.value ? moment(this.editForm.get(['startDate'])!.value, DATE_TIME_FORMAT) : undefined,
      deadline: this.editForm.get(['deadline'])!.value ? moment(this.editForm.get(['deadline'])!.value, DATE_TIME_FORMAT) : undefined,
      check: this.editForm.get(['check'])!.value,
      user: this.editForm.get(['user'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAudiovisual>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: IUser): any {
    return item.id;
  }
}
