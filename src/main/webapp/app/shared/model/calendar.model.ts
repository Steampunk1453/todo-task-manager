import { Moment } from 'moment';

export interface ICalendar {
  id?: number;
  title?: string;
  startDate?: Moment;
  deadline?: Moment;
  check?: number;
}

export class Calendar implements ICalendar {
  constructor(public id?: number, public name?: string, public startDate?: Moment, public deadline?: Moment, public check?: number) {}
}
