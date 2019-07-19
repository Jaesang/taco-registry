import {Main} from "../../main/main.value";
import {Avatar} from "../../../common/value/avatar.value";

export namespace Member {

	export class Entity {
    public name: string;
    public avatar: Avatar;
    public invited: boolean;
    public is_robot: boolean;
    public kind: Main.Type;
    public is_org_member: boolean;
    public role: string;
  }

  export class MemberList {
	  public can_edit: boolean;
	  public members: Entity[];
	  public name: string;
  }

  export class Results {
	  public results: Entity[];
  }

  export class Permissions {
	  public permissions: Array<any>;
  }
}
