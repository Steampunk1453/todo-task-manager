export interface IEditorial {
  id?: number;
  name?: string;
  url?: string;
}

export class Editorial implements IEditorial {
  constructor(public id?: number, public name?: string, public url?: string) {}
}
