import {Avatar} from "./avatar.value";
export namespace Team {

  export enum RoleType {
    admin = <any>'admin',
    creator = <any>'creator',
    member = <any>'member'
  }

	export class Entity {
    public canView: boolean;
    public description: string;
    public member_count: number;
    public name: string;
    public repo_count: number;
    public role: RoleType;
    public new_team: boolean;
    public avatar: Avatar;
  }
}
