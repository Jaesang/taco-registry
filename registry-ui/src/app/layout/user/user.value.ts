import {Avatar} from '../../common/value/avatar.value';
import {Organization} from '../organization/organization.value';

export namespace User {

  export class Entity {
    public username: string;
    public organizations: Array<Organization.Entity>;
    public logins: Array<Object>;
    public company: string;
    public tag_expiration_s: number;
    public anonymous: boolean;
    public preferred_namespace: boolean;
    public family_name: string;
    public verified: string;
    public location: string;
    public is_me: boolean;
    public invoice_email_address: string;
    public prompts: Array<Object>;
    public avatar: Avatar;
    public invoice_email: boolean;
    public can_create_repo: boolean;
    public email: string;
    public given_name: string;
    public superuser: boolean;
  }

  export class List {
    public users: Entity[];
  }

  /**
   * 조직 목록이 존재하는지 검사
   */
  export function isOrganizationsExist(organization: Array<Organization.Entity>): boolean {
    return typeof organization !== 'undefined' && organization.length > 0;
  }

}
