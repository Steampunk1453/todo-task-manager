export interface IGenre {
  id?: number;
  name?: string;
  literary?: number;
}

export class Genre implements IGenre {
  constructor(public id?: number, public name?: string, public literary?: number) {}
}
