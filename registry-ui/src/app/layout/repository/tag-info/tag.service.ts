import {Injectable, Injector} from "@angular/core";
import {AbstractService} from "../../../common/service/abstract.service";
import {environment} from "../../../../environments/environment";
import {Tag} from "./tag.value";
import {Image} from "./image.value";

/**
 * tag 서비스
 */
@Injectable()
export class TagService extends AbstractService {

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

	constructor(protected injector: Injector) {
		super(injector);
	}

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Override Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  /**
   * security scan 조회
   * @param namespace
   * @param repository
   * @param imageId
   * @returns {Promise<any>}
   */
  public getSecurity(namespace: string, repository: string, imageId: string): Promise<Tag.Security> {
    return this.get(`${environment.apiUrl}/image/${namespace}/${repository}/image/${imageId}/security?vulnerabilities=true`);
  }

  /**
   * tag 생성
   * @param namespace
   * @param repository
   * @param tag
   * @returns {Promise<any>}
   */
  public createTag(namespace: string, repository: string, tag: Tag.Entity): Promise<any> {
    let data = {
      image: tag.image_id
    };

    return this.put(`${environment.apiUrl}/image/${namespace}/${repository}/tag/${tag.name}`, data);
  }

  /**
   * tag 수정
   * @param namespace
   * @param repository
   * @param tag
   * @returns {Promise<any>}
   */
  public updateTag(namespace: string, repository: string, tag: Tag.Entity): Promise<any> {
    return this.put(`${environment.apiUrl}/image/${namespace}/${repository}/tag/${tag.name}`, tag);
  }

  /**
   * tag 삭제
   * @param namespace
   * @param repository
   * @param tagName
   * @returns {Promise<any>}
   */
  public deleteTag(namespace: string, repository: string, tagName: string): Promise<any> {
    return this.delete(`${environment.apiUrl}/image/${namespace}/${repository}/tag/${tagName}`);
  }

  /**
   * 이미지 상세 조회
   * @param namespace
   * @param repository
   * @param imageId
   * @returns {Promise<any>}
   */
  public getImageDetail(namespace: string, repository: string, imageId: string): Promise<Image.Layer> {
    return this.get(`${environment.apiUrl}/image/${namespace}/${repository}/image/${imageId}`);
  }

  /**
   * tag history 목록 조회
   * @param namespace
   * @param repository
   * @returns {Promise<any>}
   */
  public getHistory(namespace: string, repository: string): Promise<Tag.HistoryResult> {
    return this.get(`${environment.apiUrl}/image/${namespace}/${repository}/tag/`);
  }

  /**
   * restore image
   * @param namespace
   * @param repository
   * @param tag
   * @returns {Promise<any>}
   */
  public restoreImage(namespace: string, repository: string, tag: string, imageId: string, manifestDigest: string = null): Promise<any> {
    let data = {
      image: imageId
    };

    if (manifestDigest) {
      data['manifestDigest'] = manifestDigest;
    }

    return this.post(`${environment.apiUrl}/image/${namespace}/${repository}/tag/${tag}/restore`, data);
  }

  /**
   * vulnerability 관련 카운트 세팅
   * @param security
   */
  public setSecurityCount(security: Tag.Security) {
    security.fixableCount = 0;
    security.vulnerabilityHighCount = 0;
    security.fixablePackageCount = 0;

    if (security.status != 'queued') {
      security.data.Layer.Features.forEach(value => {
        if (value.Vulnerabilities) {
          let added = false;
          value.Vulnerabilities.forEach(v => {
            if (v.FixedBy) {
              security.fixableCount++;
              if (!added) {
                security.fixablePackageCount++;
                added = true;
              }
            }

            if (v.Severity == Tag.VulnerabilityType.High) {
              security.vulnerabilityHighCount++;
            }
          });
        }
      });
    }
  }

  /**
   * command 분리 작업
   * ["/bin/sh", "-c", "#(nop) ", "CMD ["nginx"]"]
   * ["/bin/sh -c #(nop) ADD file:13f0f6484071addf07e8399246be51c3a1d9e26ccd7e6d19d75797f37387dc12 in / "]
   * @param layer
   */
  public setCommand(layer: Image.Layer) {
    if (layer.command.length > 1) {
      let findText = '#(nop) ';
      if (layer.command.indexOf(findText) > -1) {
        let index = layer.command.indexOf(findText);
        let command = layer.command[index + 1].toString();
        layer.commandName = command.substr(0, command.indexOf(' '));
        layer.commandText = command.substr(command.indexOf(' '));
      } else {
        findText = '-c';
        let index = layer.command.indexOf(findText);
        layer.commandName = 'RUN';
        layer.commandText = layer.command[index + 1].toString();
      }
    } else {
      let findText = '#(nop) ';
      if (layer.command[0].toString().indexOf(findText) > -1) {
        let command = layer.command[0].toString().substr(layer.command[0].toString().indexOf(findText) + findText.length).trim();
        layer.commandName = command.substr(0, command.indexOf(' '));
        layer.commandText = command.substr(command.indexOf(' '));
      } else {
        findText = '-c';
        let command = layer.command[0].toString().substr(layer.command[0].toString().indexOf(findText) + findText.length).trim();
        layer.commandName = 'RUN';
        layer.commandText = command;
      }

    }
  }

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Protected Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

}
