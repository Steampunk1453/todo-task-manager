export interface IPlatform {
  id?: number;
  name?: string;
  url?: string;
}

export class Platform implements IPlatform {
  constructor(public id?: number, public name?: string, public url?: string) {}
}
