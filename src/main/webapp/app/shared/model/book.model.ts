import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';

export interface IBook {
  id?: number;
  title?: string;
  author?: string;
  genre?: string;
  editorial?: string;
  editorialUrl?: string;
  bookshop?: string;
  bookshopUrl?: string;
  startDate?: Moment;
  deadline?: Moment;
  check?: number;
  user?: IUser;
}

export class Book implements IBook {
  constructor(
    public id?: number,
    public title?: string,
    public author?: string,
    public genre?: string,
    public editorial?: string,
    public editorialUrl?: string,
    public bookshop?: string,
    public bookshopUrl?: string,
    public startDate?: Moment,
    public deadline?: Moment,
    public check?: number,
    public user?: IUser
  ) {}
}
