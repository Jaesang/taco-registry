import {Avatar} from "../../common/value/avatar.value";
import {Team} from "../../common/value/team.value";

export namespace Organization {

	export class Entity {
    public name: string;
    public isOrgAdmin: boolean;
    public canCreateRepo: boolean;
    public preferredNamespace: false;
    public public: boolean;
    public avatar: Avatar;
    public orderedTeams: string[];
    public teams: Object;
    public isAdmin: boolean;
    public isMember: boolean;
  }
}
