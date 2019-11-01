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

  constructor(page: Page = null) {
    if (page) {
      this.totalElements = page.totalElements;
      this.totalPages = page.totalPages;
      this.last = page.last;
      this.numberOfElements = page.numberOfElements;
      this.first = page.first;
      this.last = page.last;
      this.size = page.size;
      this.number = page.number;
    }
  }

  // 전체 목록 개수
  public totalElements: number;

  // 전체 페이지 개수
  public totalPages: number;

  // 마지막 페이지 여부
  public last: boolean;

  // 현재 페이지의 목록 개수
  public numberOfElements: number;

  // 첫 페이지 여부
  public first: boolean;

  // Sort 정보
  public sort: Sort;

  // 한 페이지당 최대목록 개수
  public size: number;

  // 현재 페이지 번호
  public number: number;

  // Data 목록
  public content: Object;

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
