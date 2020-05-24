import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IAudiovisual } from 'app/shared/model/audiovisual.model';

type EntityResponseType = HttpResponse<IAudiovisual>;
type EntityArrayResponseType = HttpResponse<IAudiovisual[]>;

@Injectable({ providedIn: 'root' })
export class AudiovisualService {
  public resourceUrl = SERVER_API_URL + 'api/audiovisuals';

  constructor(protected http: HttpClient) {}

  create(audiovisual: IAudiovisual): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(audiovisual);
    return this.http
      .post<IAudiovisual>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(audiovisual: IAudiovisual): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(audiovisual);
    return this.http
      .put<IAudiovisual>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IAudiovisual>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IAudiovisual[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  titles(): Observable<string[]> {
    return this.http.get<string[]>(SERVER_API_URL + 'api/titles');
  }

  genres(): Observable<string[]> {
    return this.http.get<string[]>(SERVER_API_URL + 'api/genres');
  }

  platforms(): Observable<string[]> {
    return this.http.get<string[]>(SERVER_API_URL + 'api/platforms');
  }

  protected convertDateFromClient(audiovisual: IAudiovisual): IAudiovisual {
    const copy: IAudiovisual = Object.assign({}, audiovisual, {
      startDate: audiovisual.startDate && audiovisual.startDate.isValid() ? audiovisual.startDate.toJSON() : undefined,
      deadline: audiovisual.deadline && audiovisual.deadline.isValid() ? audiovisual.deadline.toJSON() : undefined
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.startDate = res.body.startDate ? moment(res.body.startDate) : undefined;
      res.body.deadline = res.body.deadline ? moment(res.body.deadline) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((audiovisual: IAudiovisual) => {
        audiovisual.startDate = audiovisual.startDate ? moment(audiovisual.startDate) : undefined;
        audiovisual.deadline = audiovisual.deadline ? moment(audiovisual.deadline) : undefined;
      });
    }
    return res;
  }
}
