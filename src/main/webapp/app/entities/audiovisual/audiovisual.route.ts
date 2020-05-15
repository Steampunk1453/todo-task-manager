import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IAudiovisual, Audiovisual } from 'app/shared/model/audiovisual.model';
import { AudiovisualService } from './audiovisual.service';
import { AudiovisualComponent } from './audiovisual.component';
import { AudiovisualDetailComponent } from './audiovisual-detail.component';
import { AudiovisualUpdateComponent } from './audiovisual-update.component';

@Injectable({ providedIn: 'root' })
export class AudiovisualResolve implements Resolve<IAudiovisual> {
  constructor(private service: AudiovisualService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAudiovisual> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((audiovisual: HttpResponse<Audiovisual>) => {
          if (audiovisual.body) {
            return of(audiovisual.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Audiovisual());
  }
}

export const audiovisualRoute: Routes = [
  {
    path: '',
    component: AudiovisualComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: [Authority.USER],
      defaultSort: 'id,asc',
      pageTitle: 'toDoTaskManagerApp.audiovisual.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: AudiovisualDetailComponent,
    resolve: {
      audiovisual: AudiovisualResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'toDoTaskManagerApp.audiovisual.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: AudiovisualUpdateComponent,
    resolve: {
      audiovisual: AudiovisualResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'toDoTaskManagerApp.audiovisual.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: AudiovisualUpdateComponent,
    resolve: {
      audiovisual: AudiovisualResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'toDoTaskManagerApp.audiovisual.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
