/**
 * Common result
 *
 *  - 공통 결과 클래스
 */
export class CommonResult {
	code: string;
	message: string;
	data: any;
	contacts: any;
	status: string
}

/**
 * Page
 *
 *  - 페이지 객체
 */
export class Page {

  public number: number = 0;
  public size: number;
  public content: Object;
  public start_index: number;
  public first: boolean;
  public last: boolean;
  public totalElements: number;
  public totalPages: number;
  public sort: Sort = new Sort();

}

/**
 * Sort
 *
 *  - 소팅 클래스
 */
export class Sort {
	// 방향 (ASC or DESC)
	direction: string;
	// Order by
	property: string;
	// ignore 여부
	ignoreCase: boolean;
	// nullHandling
	nullHandling: string;
	// ASC 여부
	ascending: boolean;
}
