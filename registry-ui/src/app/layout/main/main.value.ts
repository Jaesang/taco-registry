import {Avatar} from "../../common/value/avatar.value";
import {Page} from "../../common/value/result-value";

export namespace Main {
  export enum Type {
    repository = <any>'repository',
    user = <any>'user',
    organization = <any>'organization'
  }

	export class Entity {
    public avatar: Avatar;
    public description: string;
    public href: string;
    public is_public: boolean;
    public kind: Type;
    public name: string;
    public namespace: Entity;
    public popularity: number;
    public score: number;
    public stars: number;
    public title: string;
  }

  export class PageResult extends Page {
    public results: Entity[];
  }

}
