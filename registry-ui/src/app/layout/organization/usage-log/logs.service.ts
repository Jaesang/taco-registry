import {Injectable, Injector} from "@angular/core";
import {Logs} from "./logs.value";
import * as moment from "moment";

/**
 * Logs 서비스
 */
@Injectable()
export class LogsService {

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Protected Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Constructor
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Override Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  public getDescription(log: Logs.Entity) {
    let desc = '';

    switch (log.kind) {
      case Logs.Kind.create_repo:
        desc = `Create Repository <em>${log.metadata.namespace}</em>/<em>${log.metadata.repo}</em>`;
        break;
      case Logs.Kind.delete_repo:
        desc = `Delete repository: <em>${log.metadata.repo}</em>`;
        break;
      case Logs.Kind.change_repo_visibility:
        desc = `Change visibility for repository <em>${log.metadata.namespace}</em>/<em>${log.metadata.repo}</em> to <em>${log.metadata.visibility}</em>`;
        break;
      case Logs.Kind.set_repo_description:
        desc = `Change description for repository <em>${log.metadata.namespace}</em>/<em>${log.metadata.repo}</em>`;
        break;
      case Logs.Kind.change_repo_permission:
        desc = `Change permission for user <em>${log.metadata.username}</em> in repository <em>${log.metadata.namespace}</em>/<em>${log.metadata.repo}</em> to <em>${log.metadata.role}</em>`;
        break;
      case Logs.Kind.delete_repo_permission:
        desc = `Remove permission for user <em>${log.metadata.username}</em> from repository <em>${log.metadata.namespace}</em>/<em>${log.metadata.repo}</em>`
        break;

      case Logs.Kind.push_repo:
        desc = `Push of <em>${log.metadata.tag}</em> to repository <em>${log.metadata.namespace}</em>/<em>${log.metadata.repo}</em>`;
        break;

      case Logs.Kind.pull_repo:
        desc = `Pull of tag <em>${log.metadata.tag}</em> from repository <em>${log.metadata.namespace}</em>/<em>${log.metadata.repo}</em> via token`;
        break;

      case Logs.Kind.build_dockerfile:
        desc = `Build image from Dockerfile for repository <em>${log.metadata.namespace}</em>/<em>${log.metadata.repo}</em>`;
        break;

      case Logs.Kind.create_tag:
        desc = `Tag <em>${log.metadata.tag}</em> created in repository <em>${log.metadata.namespace}</em>/<em>${log.metadata.repo}</em> on image <em>${this.substr(log.metadata.image)}</em> by user <em>${log.metadata.username}</em>`;
        break;

      case Logs.Kind.delete_tag:
        desc = `Tag <em>${log.metadata.tag}</em> deleted in repository <em>${log.metadata.namespace}</em>/<em>${log.metadata.repo}</em> by user <em>${log.metadata.username}</em>`;
        break;

      case Logs.Kind.change_tag_expiration:
        desc = `Tag <em>${log.metadata.tag}</em> set to expire on <em>${moment(log.metadata.expiration_date * 1000).format('YYYY-MM-DD HH:mm')}</em>`;
        if (log.metadata.old_expiration_date) {
          desc += ` (previously <em>${moment(log.metadata.old_expiration_date * 1000).format('YYYY-MM-DD HH:mm')}</em>)`;
        }
        break;

      case Logs.Kind.revert_tag:
        if (log.metadata.original_image) {
          desc = `Tag <em>${log.metadata.tag}</em> restored to image <em>${this.substr(log.metadata.image)}</em> from image <em>${this.substr(log.metadata.original_image)}</em>`;
        } else {
          desc = `Tag <em>${log.metadata.tag}</em> recreated pointing to image <em>${this.substr(log.metadata.image)}</em>`;
        }
        break;

      case Logs.Kind.org_create_team:
        desc = `Create team <em>${log.metadata.team}</em>`;
        break;

      case Logs.Kind.org_delete_team:
        desc = `Delete team <em>${log.metadata.team}</em>`;
        break;

      case Logs.Kind.org_add_team_member:
        desc = `Add member <em>${log.metadata.member}</em> to team <em>${log.metadata.team}</em>`;
        break;

      case Logs.Kind.org_remove_team_member:
        desc = `Remove member <em>${log.metadata.member}</em> from team <em>${log.metadata.team}</em>`;
        break;

      case Logs.Kind.org_set_team_role:
        desc = `Change permission of team <em>${log.metadata.team}</em> to <em>${log.metadata.role}</em>`;
        break;

      case Logs.Kind.org_set_team_description:
        desc = `Change description of team <em>${log.metadata.team}</em>: <em>${log.metadata.description}</em>`;
        break;

      case Logs.Kind.account_change_password:
        desc = `Change password`;
        break;
    }

    return desc;
  }

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Protected Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	private substr(str: string, length: number = 12) {
	  return str.substr(0, length);
  }

}
