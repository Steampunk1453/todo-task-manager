import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IBook, Book } from 'app/shared/model/book.model';
import { BookService } from './book.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { IGenre } from 'app/shared/model/genre.model';
import { IBookshop } from 'app/shared/model/bookshop.model';
import { GenreService } from 'app/entities/genre.service';

@Component({
  selector: 'jhi-book-update',
  templateUrl: './book-update.component.html'
})
export class BookUpdateComponent implements OnInit {
  isSaving = false;
  users: IUser[] = [];
  genres: IGenre[] = [];
  bookshops: IBookshop[] = [];
  mapUrls: any;

  editForm = this.fb.group({
    id: [],
    title: [null, [Validators.required]],
    author: [],
    genre: [],
    editorial: [],
    bookshop: [],
    bookshopUrl: [],
    startDate: [null, [Validators.required]],
    deadline: [null, [Validators.required]],
    check: [],
    user: []
  });

  constructor(
    protected bookService: BookService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected genreService: GenreService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ book }) => {
      if (!book.id) {
        const today = moment().startOf('day');
        book.startDate = today;
        book.deadline = today;
      }
      this.updateForm(book);

      this.genreService.genres().subscribe(genres => {
        if (genres) {
          this.genres = genres.filter(g => g.literary !== 0);
        }
      });
      this.bookService.bookshops().subscribe(bookshops => {
        if (bookshops) {
          this.bookshops = bookshops;
        }
        this.mapUrls = new Map(this.bookshops.map(b => [b.name, b.url]));
      });

      this.userService.query().subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));
    });
  }

  updateForm(book: IBook): void {
    this.editForm.patchValue({
      id: book.id,
      title: book.title,
      author: book.author,
      genre: book.genre,
      editorial: book.editorial,
      bookshop: book.bookshop,
      bookshopUrl: book.bookshopUrl,
      startDate: book.startDate ? book.startDate.format(DATE_TIME_FORMAT) : undefined,
      deadline: book.deadline ? book.deadline.format(DATE_TIME_FORMAT) : undefined,
      check: book.check,
      user: book.user
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const book = this.createFromForm();
    // convert booleans to ints
    book.check = book.check ? 1 : 0;
    if (book.id !== undefined) {
      this.subscribeToSaveResponse(this.bookService.update(book));
    } else {
      this.subscribeToSaveResponse(this.bookService.create(book));
    }
  }

  private createFromForm(): IBook {
    return {
      ...new Book(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      author: this.editForm.get(['author'])!.value,
      genre: this.editForm.get(['genre'])!.value,
      editorial: this.editForm.get(['editorial'])!.value,
      bookshop: this.editForm.get(['bookshop'])!.value,
      bookshopUrl: this.mapUrls.get(this.editForm.get(['bookshop'])!.value),
      startDate: this.editForm.get(['startDate'])!.value ? moment(this.editForm.get(['startDate'])!.value, DATE_TIME_FORMAT) : undefined,
      deadline: this.editForm.get(['deadline'])!.value ? moment(this.editForm.get(['deadline'])!.value, DATE_TIME_FORMAT) : undefined,
      check: this.editForm.get(['check'])!.value,
      user: this.editForm.get(['user'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBook>>): void {
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
