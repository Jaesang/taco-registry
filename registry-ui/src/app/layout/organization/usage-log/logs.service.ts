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
      case Logs.Kind.create_image:
        desc = `Create Repository <em>${log.namespace}</em>/<em>${log.image}</em>`;
        break;
      case Logs.Kind.delete_image:
        desc = `Delete repository: <em>${log.image}</em>`;
        break;
      case Logs.Kind.change_image_visibility:
        desc = `Change visibility for repository <em>${log.namespace}</em>/<em>${log.image}</em> to <em>${log.visibility}</em>`;
        break;
      case Logs.Kind.set_image_description:
        desc = `Change description for repository <em>${log.namespace}</em>/<em>${log.image}</em>`;
        break;
      case Logs.Kind.change_image_permission:
        desc = `Change permission for user <em>${log.member}</em> in repository <em>${log.namespace}</em>/<em>${log.image}</em> to <em>${log.role}</em>`;
        break;
      case Logs.Kind.delete_image_permission:
        desc = `Remove permission for user <em>${log.member}</em> from repository <em>${log.namespace}</em>/<em>${log.image}</em>`
        break;

      case Logs.Kind.push_image:
        desc = `Push of <em>${log.tag}</em> to repository <em>${log.namespace}</em>/<em>${log.image}</em>`;
        break;

      case Logs.Kind.pull_image:
        desc = `Pull of tag <em>${log.tag}</em> from repository <em>${log.namespace}</em>/<em>${log.image}</em> via token`;
        break;

      case Logs.Kind.build_dockerfile:
        desc = `Build image from Dockerfile for repository <em>${log.namespace}</em>/<em>${log.image}</em>`;
        break;

      case Logs.Kind.create_tag:
        desc = `Tag <em>${log.tag}</em> created in repository <em>${log.namespace}</em>/<em>${log.image}</em> on image <em>${this.substr(log.dockerImageId)}</em> by user <em>${log.member}</em>`;
        break;

      case Logs.Kind.delete_tag:
        desc = `Tag <em>${log.tag}</em> deleted in repository <em>${log.namespace}</em>/<em>${log.image}</em> by user <em>${log.member}</em>`;
        break;

      case Logs.Kind.change_tag_expiration:
        desc = `Tag <em>${log.tag}</em> set to expire on <em>${moment(log.expirationDate).format('YYYY-MM-DD HH:mm')}</em>`;
        if (log.oldExpirationDate) {
          desc += ` (previously <em>${moment(log.oldExpirationDate).format('YYYY-MM-DD HH:mm')}</em>)`;
        }
        break;

      case Logs.Kind.revert_tag:
        if (log.originalDockerImageId) {
          desc = `Tag <em>${log.tag}</em> restored to image <em>${this.substr(log.dockerImageId)}</em> from image <em>${this.substr(log.originalDockerImageId)}</em>`;
        } else {
          desc = `Tag <em>${log.tag}</em> recreated pointing to image <em>${this.substr(log.dockerImageId)}</em>`;
        }
        break;

      case Logs.Kind.org_add_member:
        desc = `Add member <em>${log.member}</em>`;
        break;

      case Logs.Kind.org_remove_member:
        desc = `Remove member <em>${log.member}</em>`;
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
