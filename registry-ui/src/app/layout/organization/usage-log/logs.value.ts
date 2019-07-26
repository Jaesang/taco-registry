import {Main} from "../../main/main.value";
export namespace Logs {

  /**
   * Kind
   */
  export enum Kind {
    create_image = <any>'create_image',
    delete_image = <any>'delete_image',
    change_image_visibility = <any>'change_image_visibility',
    change_image_permission = <any>'change_image_permission',
    delete_image_permission = <any>'delete_image_permission',
    set_image_description = <any>'set_image_description',
    push_image = <any>'push_image',
    pull_image = <any>'pull_image',
    build_dockerfile = <any>'build_dockerfile',
    revert_tag = <any>'revert_tag',
    create_tag = <any>'create_tag',
    delete_tag = <any>'delete_tag',
    change_tag_expiration = <any>'change_tag_expiration',

    org_add_member = <any>'org_add_member',
    org_remove_member = <any>'org_remove_member',

    account_change_password = <any>'account_change_password'
  }

	export class Entity {
    public datetime: string;
    public ip: string;
    public kind: Kind;
    public performer: Main.Entity;

    public namespace: string;
    public image: string;
    public user: string;
    public role: string;
    public team: string;
    public member: string;
    public tag: string;
    public username: string;
    public dockerImageId: string;
    public originalDockerImageId: string;
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
