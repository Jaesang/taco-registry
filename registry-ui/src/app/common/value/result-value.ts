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

  public has_additional: boolean;
  public page: number = 0;
  public page_size: number;
  public results: Object;
  public start_index: number;

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
