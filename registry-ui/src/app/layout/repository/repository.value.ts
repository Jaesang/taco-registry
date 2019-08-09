import {Tag} from "./tag-info/tag.value";
import {CommonResult, Page} from "../../common/value/result-value";
export namespace Repository {

	export class Entity {
    public canAdmin: boolean;
    public canWrite: boolean;
	  public description:string;
    public isPublic: boolean;
    public isStarred: boolean;
    public kind: string;
    public lastModified: string;
    public name: string;
    public namespace: string;
    public popularity: number;
    public isOrganization: boolean;
    public stats: Stat[] = [];
    public tags: any;
	}

	export class Stat {
	  public count: number;
	  public date: string;
  }

	export class Result extends Page {
    public content: Entity[] = [];
  }
}
