import {Main} from "../../main/main.value";
import {Avatar} from "../../../common/value/avatar.value";
import {Page} from "../../../common/value/result-value";

export namespace Member {

	export class Entity {
    public name: string;
    public avatar: Avatar;
    public invited: boolean;
    public isRobot: boolean;
    public kind: Main.Type;
    public isOrgMember: boolean;
    public role: string;
    public username: string;
  }

  export class MemberList extends Page {
	  public canEdit: boolean;
	  public content: Entity[];
	  public name: string;
  }

  export class Results {
	  public content: Entity[];
  }

  export class Permissions extends Page {
	  public content: Entity[];
  }
}
