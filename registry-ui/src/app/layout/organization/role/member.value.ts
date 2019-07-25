import {Main} from "../../main/main.value";
import {Avatar} from "../../../common/value/avatar.value";

export namespace Member {

	export class Entity {
    public name: string;
    public avatar: Avatar;
    public invited: boolean;
    public isRobot: boolean;
    public kind: Main.Type;
    public isOrgMember: boolean;
    public role: string;
  }

  export class MemberList {
	  public canEdit: boolean;
	  public members: Entity[];
	  public name: string;
  }

  export class Results {
	  public content: Entity[];
  }

  export class Permissions {
	  public members: Entity[];
  }
}
