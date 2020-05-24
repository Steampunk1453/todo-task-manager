import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';

export interface IAudiovisual {
  id?: number;
  title?: string;
  genre?: string;
  platform?: string;
  platformUrl?: string;
  startDate?: Moment;
  deadline?: Moment;
  check?: number;
  user?: IUser;
}

export class Audiovisual implements IAudiovisual {
  constructor(
    public id?: number,
    public title?: string,
    public genre?: string,
    public platform?: string,
    public platformUrl?: string,
    public startDate?: Moment,
    public deadline?: Moment,
    public check?: number,
    public user?: IUser
  ) {}
}
