import {Injector} from '@angular/core';
import {Router} from '@angular/router';
import {Headers, Http, RequestOptions, Response, URLSearchParams} from '@angular/http';
import {CookieService} from 'ng2-cookies';
import {CookieConstant} from "../constant/cookie-constant";
import {environment} from "../../../environments/environment";
import {LoaderService} from "../component/loader/loader.service";
import {Loader} from "../component/loader/loader.value";
import {ConfirmPopupService} from "../component/confirm-popup/confirm-popup.service";
import {CommonService} from "./common.service";
import {Alert} from "../utils/alert-util";

export abstract class AbstractService {

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 | Private Variables
	 |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 | Protected Variables
	 |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	// 기본 헤더
	protected DEFAULT_HEADER: { [ name: string ]: string } = {
		'Content-Type': 'application/json',
		'X-Requested-With': 'XMLHttpRequest'
	};

	// HTTP
	protected http: Http;

	// Router
	protected router: Router;

  // Cookie 서비스
  protected cookieService: CookieService;

  protected loaderService: LoaderService;

  protected CSRF_TOKEN: string = 'dGFjb3JlZ2lzdHJ5';

  private confirmPopupService: ConfirmPopupService;

  private commonService: CommonService;

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 | Public Variables
	 |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 | Constructor
	 |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	// 생성자
	protected constructor(protected injector: Injector) {

		this.router = injector.get(Router);
		this.http = injector.get(Http);
		this.cookieService = injector.get(CookieService);
		this.loaderService = injector.get(LoaderService);
		this.confirmPopupService = injector.get(ConfirmPopupService);
		this.commonService = injector.get(CommonService);
	}

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 | Override Method
	 |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 | Public Method
	 |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 | Protected Method
	 |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/**
	 * GET 호출
	 *
	 * @param {string} url
	 * @param params
	 * @returns {any}
	 */
	protected get(url: string, params?: any): Promise<any> {

		console.log(`[${this[ '__proto__' ].constructor.name}] > GET ${url}`);
		console.log(`[${this[ '__proto__' ].constructor.name}] > PARAMS`, params);

		const options: RequestOptions = this.getDefaultRequestOptions();

		if (params) {

			const searchParams: URLSearchParams = new URLSearchParams();

			for (const key in params) {

				if (params.hasOwnProperty(key)) {

					const value = params[ key ];

					if (Array.isArray(value)) {

						// 배열일 경우 처리
						value.forEach((item) => {
							searchParams.append(key, item);
						});
					} else {
						searchParams.set(key, params[ key ]);
					}

				}
			}

			options.search = searchParams;
		}

		let currentUrl = this.router.url;

		// 호출
		return this.http
			.get(url, options)
			.toPromise()
			.then(response => this.errorCheck(response, currentUrl))
			.catch((error) => this.errorHandler(error));
	}

	/**
	 * POST 호출
	 *
	 * @param {string} url
	 * @param data
	 * @param {ContentType} contentType
	 * @returns {Promise<any>}
	 */
	protected post(url: string, data: any, contentType: string = 'json'): Promise<any> {

		// console.log(`[${this[ '__proto__' ].constructor.name}] > POST ${url}`);
		// console.log(`[${this[ '__proto__' ].constructor.name}] > DATA`, data);
		// console.log(`[${this[ '__proto__' ].constructor.name}] > ContentType`, contentType);

		if (contentType === 'json') {
      return this.sendJsonDataToPostMethod(url, data);
    } else {
      return this.sendFormDataToPostMethod(url, data);
		}
	}

	/**
	 * POST 호출(token 없이)
	 *
	 * @param {string} url
	 * @param data
	 * @returns {any}
	 */
	protected postWithoutToken(url: string, data: any): Promise<any> {

		console.log(`[${this[ '__proto__' ].constructor.name}] > POST ${url}`);
		console.log(`[${this[ '__proto__' ].constructor.name}] > DATA`, data);

		// 호출
		return this.http
			.post(url, JSON.stringify(data), this.getWithoutTokenRequestOptions())
			.toPromise()
			.then(response => this.errorCheck(response, null))
			.catch((error) => this.errorHandler(error));
	}

	/**
	 * PUT 호출
	 *
	 * @param {string} url
	 * @param data
	 * @returns {any}
	 */
	protected put(url: string, data: any): Promise<any> {

		console.log(`[${this[ '__proto__' ].constructor.name}] > PUT ${url}`);
		console.log(`[${this[ '__proto__' ].constructor.name}] > DATA`, data);

		// 호출
		return this.http
			.put(url, data, this.getDefaultRequestOptions())
			.toPromise()
			.then(response => this.errorCheck(response, null))
			.catch((error) => this.errorHandler(error));
	}

	/**
	 * DELETE 호출
	 *
	 * @param {string} url
	 * @returns {any}
	 */
	protected delete(url: string, params?: any): Promise<any> {

		console.log(`[${this[ '__proto__' ].constructor.name}] > DELETE ${url}`);

		const options = this.getDefaultRequestOptions();

		if (params) {

			const searchParams: URLSearchParams = new URLSearchParams();

			for (const key in params) {

				if (params.hasOwnProperty(key)) {

					const value = params[ key ];

					if (Array.isArray(value)) {

						// 배열일 경우 처리
						value.forEach((item) => {
							searchParams.append(key, item);
						});
					} else {
						searchParams.set(key, params[ key ]);
					}

				}
			}

			options.search = searchParams;
		}

		// 호출
		return this.http
			.delete(url, options)
			.toPromise()
			.then(response => this.errorCheck(response, null))
			.catch((error) => this.errorHandler(error));
	}

	public fileDownload(url: string, fileName: string): Promise<any> {
		return this.download(url, fileName, 'GET', null);
	}

	protected fileUploa(url: string, fileName: string, params: any): Promise<any> {
		return this.download(url, fileName, 'POST', params);
	}

	protected excelFileDownLoad(url: string, fileName: string, params?: any): Promise<any> {
		const date: Date = new Date();
		const timestamp: string = date.getFullYear().toString() + (date.getMonth() + 1).toString() + date.getDate().toString() + date.getHours().toString() + date.getMinutes().toString();
		return this.download(url, `${fileName}-${timestamp}.xlsx`, 'POST', params);
	}

	/**
	 * 에러 핸들러
	 *
	 * @param error
	 * @returns {Promise<never>}
	 */
	protected errorHandler(error: any) {
		console.log(`[${this[ '__proto__' ].constructor.name}] > ERROR MESSAGE`, error);

		switch (error.status) {
      case 503: {
        let body = JSON.parse(error._body);
        Alert.error(body.message);

        break;
      }
			case 500: {

        this.router.navigate([ 'app/error/500' ]);

				break;
			}
			case 400: {

				console.log(`[${this[ '__proto__' ].constructor.name}] > INVALID_REQUEST`);

				break;
			}
			case 401: {

				console.log(`[${this[ '__proto__' ].constructor.name}] > UNAUTORIZED`);

				if (this.commonService.logedIn) {
          this.confirmPopupService.showAlert(
            'Session Expired',
            'Your user session has expired. Please sign in to continue.',
            null,
            () => {
              this.navigateToLogin(window.location.href);
            },
            'Sign In'
          );
        } else {
          this.navigateToLogin(window.location.href);
        }

				break;
			}
			case 403: {
        this.router.navigate([ 'app/error/403' ]);

				break;
			}
			default: {

				console.log(`[${this[ '__proto__' ].constructor.name}] > (SERVER_ERROR|INVALID_REQUEST|UNAUTORIZED|FORBIDDEN) ELSE ERROR`);

        this.router.navigate([ 'app/error/500' ]);

				break;
			}
		}

    this.loaderService.show.next(false);

		return Promise.reject(error);
	}

	/**
	 * 에러 체크
	 *
	 * @param {Response} response
	 * @param {string} url
	 * @returns {any}
	 */
	protected errorCheck(response: Response, url: string) {

		console.log(`[${this[ '__proto__' ].constructor.name}] > RESPONSE`, response.toString());
		console.log(`[${this[ '__proto__' ].constructor.name}] > RESPONSE BODY\n`, response[ '_body' ]);

		if (response.status >= 400 && response.status <= 500) {
			if (response.status === 401) {
				this.navigateToLogin(null);
			}
		} else {
			return response.json();
		}

		// 에러 발생
		throw new Error(response.json().message);
	}

	/**
	 * 로그인 페이지로 이동
	 *
	 * @param {string} returnUrl
	 * @returns {any}
	 */
	public navigateToLogin(returnUrl: string): void {

		// this.logout();

    this.router.navigate([ '/login' ]);
	}

	/**
	 * Logout
	 *
	 * @returns {Promise<any>}
	 */
	public logout(): Promise<any> {

    return this.delete(`${environment.host}/api/oauth/revoke`, null).then(result => {
      this.cookieService.delete(CookieConstant.KEY.TOKEN);
    });
	}

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 | Private Method
	 |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/**
	 * 기본 옵션 정보 생성
	 *
	 * @returns {RequestOptions}
	 */
	protected getDefaultRequestOptions(): RequestOptions {

		const token = this.cookieService.get(CookieConstant.KEY.TOKEN);

		// 헤더
		const headers = new Headers(this.DEFAULT_HEADER);
		headers.set('Authorization', `Bearer ${token}`);

		// 옵션
		const options = new RequestOptions({ headers: headers, withCredentials: true });

		return options;
	}

	/**
	 * 기본 옵션 정보 생성(without token)
	 *
	 * @returns {RequestOptions}
	 */
	private getWithoutTokenRequestOptions(): RequestOptions {

		// 헤더
		const headers = new Headers(this.DEFAULT_HEADER);

		// 옵션
		const options = new RequestOptions({ headers: headers });

		return options;
	}

	/**
	 * 파일 다운로드
	 *
	 * @param {string} url
	 * @param {string} fileName
	 * @param {string} requestMethod
	 * @param params
	 * @returns {Promise<any>}
	 */
	private download(url: string, fileName: string, requestMethod: string, params?: any): Promise<any> {

		console.log(`[${this[ '__proto__' ].constructor.name}] > DOWNLOAD GET ${url}`);
		console.log(`[${this[ '__proto__' ].constructor.name}] > DOWNLOAD FILENAME`, fileName);
		console.log(`[${this[ '__proto__' ].constructor.name}] > DOWNLOAD METHOD TYPE`, requestMethod);
		console.log(`[${this[ '__proto__' ].constructor.name}] > DOWNLOAD PARAMS`, params);

		if (requestMethod === 'GET' && params) {
			url += this.formatParams(params);
		}

		const promise = new Promise((resolve, reject) => {

			const xhttp = new XMLHttpRequest();

			xhttp.onload = () => {

				if (xhttp.status >= 200 && xhttp.status < 300) {

					// IE 인 경우 별도 처리, 그 외 다운로드 트릭 링크 설정.
					if (window.navigator.msSaveBlob) {
						window.navigator.msSaveBlob(xhttp.response, fileName);
					} else {
						const a = document.createElement('a');
						a.href = window.URL.createObjectURL(xhttp.response);
						a.download = fileName;
						a.style.display = 'none';
						document.body.appendChild(a);
						a.click();
						a.parentNode.removeChild(a);
					}

					resolve('success');
				} else {
					// this.status가 2xx와 다른 경우 함수 "reject" 수행
					reject('error');
				}
			};

			// 에러시
			xhttp.onerror = () => {
				reject('error');
			};

			xhttp.open(requestMethod, encodeURI(url));
			xhttp.setRequestHeader('Authorization', `Bearer ${this.cookieService.get(CookieConstant.KEY.TOKEN)}`);
			xhttp.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
			xhttp.responseType = 'blob';

			if (requestMethod === 'GET') {
				xhttp.send();
			} else {
				xhttp.send(JSON.stringify(params));
			}

		});

		return promise;
	}

	private formatParams(params): string {
		return '&' + Object
			.keys(params)
			.map((key) => `${key}=${encodeURIComponent(params[ key ])}`)
			.join('&');
	}

	/**
	 * JSON Data POST
	 *
	 * @param url
	 * @param data
	 * @returns {Promise<any>}
	 */
	private sendJsonDataToPostMethod(url, data): Promise<any> {
		return this.http
			.post(url, JSON.stringify(data), this.getDefaultRequestOptions())
			.toPromise()
			.then(response => this.errorCheck(response, null))
			.catch((error) => this.errorHandler(error));
	}

	/**
	 * FORM Data POST
	 *
	 * @param url
	 * @param data
	 * @returns {Promise<any>}
	 */
	private sendFormDataToPostMethod(url, data): Promise<any> {

		const getMultiPartFormDataHeader: () => RequestOptions = () => {

			// const token = this.cookieService.get(CookieConstant.KEY.TOKEN);

			// 헤더
			const headers = new Headers();
			// headers.set('Authorization', `Bearer ${token}`);
			headers.set('enctype', `multipart/form-data`);

			// 옵션
			const options = new RequestOptions({ headers: headers, withCredentials: true });

			return options;
		};

		// 호출
		return this.http
			.post(url, data, getMultiPartFormDataHeader())
			.toPromise()
			.then(response => this.errorCheck(response, null))
			.catch((error) => this.errorHandler(error));
	}

	/**
	 * iframe 안 인지 여부
	 * @returns {boolean}
	 */
	private isInIFrame() {
		const isInIframe = (window.location != window.parent.location);

		return isInIframe;
	}

}
