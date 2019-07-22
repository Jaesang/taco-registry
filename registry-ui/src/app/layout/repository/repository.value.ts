import {Tag} from "./tag-info/tag.value";
import {CommonResult} from "../../common/value/result-value";
export namespace Repository {

	export class Entity {
    public can_admin: boolean;
    public can_write: boolean;
	  public description:string;
    public is_public: boolean;
    public is_starred: boolean;
    public kind: string;
    public last_modified: string;
    public name: string;
    public namespace: string;
    public popularity: number;
    public is_organization: boolean;
    public stats: Stat[] = [];
    public tags: any;
	}

	export class Stat {
	  public count: number;
	  public date: string;
  }

	export class Result {
    public images: Entity[] = [];
  }
}
