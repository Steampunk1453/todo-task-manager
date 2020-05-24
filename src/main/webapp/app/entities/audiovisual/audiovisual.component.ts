import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IAudiovisual } from 'app/shared/model/audiovisual.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { AudiovisualService } from './audiovisual.service';
import { AudiovisualDeleteDialogComponent } from './audiovisual-delete-dialog.component';

@Component({
  selector: 'jhi-audiovisual',
  templateUrl: './audiovisual.component.html'
})
export class AudiovisualComponent implements OnInit, OnDestroy {
  audiovisuals?: IAudiovisual[];
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;

  constructor(
    protected audiovisualService: AudiovisualService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadPage(page?: number): void {
    const pageToLoad: number = page || this.page;

    this.audiovisualService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort()
      })
      .subscribe(
        (res: HttpResponse<IAudiovisual[]>) => this.onSuccess(res.body, res.headers, pageToLoad),
        () => this.onError()
      );
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(data => {
      this.page = data.pagingParams.page;
      this.ascending = data.pagingParams.ascending;
      this.predicate = data.pagingParams.predicate;
      this.ngbPaginationPage = data.pagingParams.page;
      this.loadPage();
    });
    this.registerChangeInAudiovisuals();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IAudiovisual): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInAudiovisuals(): void {
    this.eventSubscriber = this.eventManager.subscribe('audiovisualListModification', () => this.loadPage());
  }

  delete(audiovisual: IAudiovisual): void {
    const modalRef = this.modalService.open(AudiovisualDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.audiovisual = audiovisual;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected onSuccess(data: IAudiovisual[] | null, headers: HttpHeaders, page: number): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    this.router.navigate(['/audiovisual'], {
      queryParams: {
        page: this.page,
        size: this.itemsPerPage,
        sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc')
      }
    });
    this.audiovisuals = data || [];
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page;
  }
}
