import {Component, OnInit, ElementRef, Injector, ViewChild} from "@angular/core";
import {PageComponent} from "../../common/component/page.component";
import {MainService} from "./main.service";
import {Main} from "./main.value";
import {PaginationComponent} from "../../common/component/pagination/pagination.component";

@Component({
  selector: 'main',
  templateUrl: './main.component.html'
})
export class MainComponent extends PageComponent implements OnInit {

  @ViewChild('paginationTop')
  public paginationTop: PaginationComponent;

  @ViewChild('paginationBottom')
  public paginationBottom: PaginationComponent;

  public searchKey: string = '';

  public repositoryList: Main.Entity[] = [];

  public currentPage: number;

  public isSearching: boolean = false;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private mainService: MainService) {

    super(elementRef, injector);

    this.commonService.showLnb.next(false);
  }

  ngOnInit() {

    // parameter 가져오기
    this.subscriptions.push(
      this.activatedRoute.queryParams
        .subscribe(params => {
          this.searchKey = '';

          if (params[ 'query' ]) {
            this.searchKey = params[ 'query' ];
          }

          this.currentPage = 1;
          this.getRepositoryList();
        })
    );
  }

  /**
   * 검색
   * @param searchKey
   */
  public searchClick(searchKey: string) {
    this.searchKey = searchKey;
    this.currentPage = 1;

    this.getRepositoryList();
  }

  /**
   * paging
   * @param page
   */
  public pageClick(page: number) {
    this.currentPage = page;

    this.getRepositoryList();
  }

  /**
   * 상세 페이지 이동
   * @param namespace
   * @param repository
   */
  public moveToDetail(namespace: string, repository: string) {
    this.router.navigate([`app/image/${namespace}/${repository}/info`]);
  }

  /**
   * search clear 클릭
   */
  public clearSearchClick() {
    this.commonService.changeSearchText.next('');
    this.router.navigate(['app/main']);
  }

  /**
   * repository 목록 조회
   * @param orgName
   */
  private getRepositoryList() {
    this.loaderService.show.next(true);
    this.isSearching = true;

    this.mainService.getRepositoryList(this.currentPage, this.searchKey).then(result => {
      this.repositoryList = result.results;
      this.paginationTop.init(result);
      this.paginationBottom.init(result);
      this.isSearching = false;

      this.loaderService.show.next(false);
    });
  }

}
