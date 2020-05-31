export interface ITitle {
  id?: number;
  name?: string;
}

export class Title implements ITitle {
  constructor(public id?: number, public name?: string) {}
}
