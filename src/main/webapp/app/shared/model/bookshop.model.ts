export interface IBookshop {
  id?: number;
  name?: string;
  url?: string;
}

export class Bookshop implements IBookshop {
  constructor(public id?: number, public name?: string, public url?: string) {}
}
