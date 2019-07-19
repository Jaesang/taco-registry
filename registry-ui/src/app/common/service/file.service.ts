import {Injectable, Injector} from "@angular/core";
import {AbstractService} from "./abstract.service";
import {environment} from "../../../environments/environment";
import {Response} from "@angular/http";

@Injectable()
export class FileService extends AbstractService {

  constructor(protected injector: Injector) {
    super(injector);
  }


  /**
   * 파일 ID 생성
   * @returns {Promise<any>}
   */
  public getFileId(): Promise<any> {
    let data = {
      mimeType: 'application/octet-stream'
    };

    return this.post(`${environment.apiUrl}/filedrop/`, data);
  }

  /**
   * 파일 업로드
   * @param fileId
   * @param content
   * @returns {Promise<R>|Promise<U>|promise.Promise<R>|any|Maybe<T>|Observable<R|T>}
   */
  public uploadFile(fileId: string, content: string): Promise<any> {
    // 호출
    return this.http
      .put(`${environment.host}/userfiles/${fileId}`, content, this.getDefaultRequestOptions())
      .toPromise()
      .then(response => this.errorCheckUploadResult(response, fileId))
      .catch((error) => this.errorHandler(error));
  }

  /**
   * file 생성 (getFileId + 파일 업로드)
   * @param content
   * @returns {any}
   */
  public createFile(content: string):Promise<any> {
    return this.getFileId().then(result => {
      if (result.file_id) {
        return Promise.resolve()
          .then(() => this.uploadFile(result.file_id, content))
      }

      return new Promise(function (resolve) {
        resolve(result);
      });
    })
  }

  /**
   * 파일 조회
   * @param fileId
   * @returns {Promise<any>}
   */
  public getFile(fileId: string): Promise<any> {
    // return this.get(`${environment.host}/userfiles/${fileId}`);
    return this.http
      .get(`${environment.host}/userfiles/${fileId}`, this.getDefaultRequestOptions())
      .toPromise()
      .then(response => this.errorCheckUploadResult(response, fileId))
      .catch((error) => this.errorHandler(error));
  }

  /**
   * 에러 체크
   *
   * @param {Response} response
   * @param {string} url
   * @returns {any}
   */
  protected errorCheckUploadResult(response: Response, fileId: string) {

    console.log(`[${this[ '__proto__' ].constructor.name}] > RESPONSE`, response.toString());
    console.log(`[${this[ '__proto__' ].constructor.name}] > RESPONSE BODY\n`, response[ '_body' ]);

    if (response.status >= 400 && response.status <= 500) {
      if (response.status === 401) {
        this.navigateToLogin(null);
      }
    } else {
      response['file_id'] = fileId;
      return response;
    }

    // 에러 발생
    throw new Error(response.toString());
  }
}
