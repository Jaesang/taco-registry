import {Avatar} from "../../common/value/avatar.value";
import {Team} from "../../common/value/team.value";

export namespace Organization {

	export class Entity {
    public name: string;
    public is_org_admin: boolean;
    public can_create_repo: boolean;
    public preferred_namespace: false;
    public public: boolean;
    public avatar: Avatar;
    public ordered_teams: string[];
    public teams: Object;
    public is_admin: boolean;
    public is_member: boolean;
  }
}
