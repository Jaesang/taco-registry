import {Main} from "../../main/main.value";
export namespace Logs {

  /**
   * Kind
   */
  export enum Kind {
    create_repo = <any>'create_repo',
    delete_repo = <any>'delete_repo',
    change_repo_visibility = <any>'change_repo_visibility',
    change_repo_permission = <any>'change_repo_permission',
    delete_repo_permission = <any>'delete_repo_permission',
    set_repo_description = <any>'set_repo_description',
    push_repo = <any>'push_repo',
    pull_repo = <any>'pull_repo',
    build_dockerfile = <any>'build_dockerfile',
    revert_tag = <any>'revert_tag',
    create_tag = <any>'create_tag',
    delete_tag = <any>'delete_tag',
    change_tag_expiration = <any>'change_tag_expiration',

    org_create_team = <any>'org_create_team',
    org_delete_team = <any>'org_delete_team',
    org_set_team_role = <any>'org_set_team_role',
    org_add_team_member = <any>'org_add_team_member',
    org_remove_team_member = <any>'org_remove_team_member',
    org_set_team_description = <any>'org_set_team_description',

    account_change_password = <any>'account_change_password'
  }

	export class Entity {
    public datetime: string;
    public ip: string;
    public kind: Kind;
    public metadata: Metadata;
    public performer: Main.Entity;
	}

	export class Metadata {
    public buildId: string;
    public dockerTags: Array<string>;
    public isManual: boolean;
    public namespace: string;
    public repo: string;
    public role: string;
    public team: string;
    public member: string;
    public tag: string;
    public image: string;
    public username: string;
    public originalImage: string;
    public visibility: string;
    public description: string;
    public expirationDate: number;
    public oldExpirationDate: number;
  }

	export class Result {
    public endTime: string;
    public logs: Array<Entity>;
    public nextPage: string;
    public startTime: string;
  }
}
