import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IGenre } from 'app/shared/model/genre.model';
import { SERVER_API_URL } from 'app/app.constants';

@Injectable({
  providedIn: 'root'
})
export class GenreService {
  constructor(protected http: HttpClient) {}

  genres(): Observable<IGenre[]> {
    return this.http.get<IGenre[]>(SERVER_API_URL + 'api/genres');
  }
}
