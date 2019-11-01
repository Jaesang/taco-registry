import {Avatar} from '../../../common/value/avatar.value';
import {CommonResult} from "../../../common/value/result-value";

export namespace SuperUser {

  export class Entity {
    username: string;
    kind: string;
    verified: boolean;
    name: string;
    superuser: boolean;
    enabled: boolean;
    email: string;
    avatar: Avatar;
    password: string;

    newEmail: string;
  }

  export namespace Result {
    export class Users {
      users: Entity[];
    }
  }

}
