import {Avatar} from '../../common/value/avatar.value';
import {Organization} from '../organization/organization.value';

export namespace User {

  export class Entity {
    public username: string;
    public organizations: Array<Organization.Entity>;
    public canCreateRepo: boolean;
    public email: string;
    public superuser: boolean;
    public registryUrl: string;
    public minioEnabled: boolean;
    public minioHost: string;
    public minioPort: string;

    public get minioUrl() {
      if (this.minioEnabled) {
        return `http://${this.minioHost}:${this.minioPort}/minio`;
      }

      return null;
    }
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
