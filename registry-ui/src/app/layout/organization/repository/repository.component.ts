import {Component, OnInit, ElementRef, Injector, ViewChild} from "@angular/core";
import {RepositoryService} from "../../repository/repository.service";
import {Repository} from "../../repository/repository.value";
import {PageComponent} from "../../../common/component/page.component";
import {Organization} from "../organization.value";
import {Main} from "../../main/main.value";
import {PaginationComponent} from "../../../common/component/pagination/pagination.component";
import {Page, Sort} from "../../../common/value/result-value";

@Component({
  selector: 'repository',
  templateUrl: 'repository.component.html'
})
export class RepositoryComponent extends PageComponent implements OnInit {

  @ViewChild('pagination')
  public pagination: PaginationComponent;

  public showCard: boolean = true;

  public repositoryList: Repository.Entity[] = [];

  public searchKey: string = '';

  public org: Organization.Entity;
  public orgName: string;

  public MainType: typeof Main.Type = Main.Type;

  public namespaceType: Main.Type;

  public isSearching: boolean = false;

  public page: Page;
  public sort: Sort;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private repositoryService: RepositoryService) {

    super(elementRef, injector);
  }

  ngOnInit() {

    // parameter 가져오기
    this.subscriptions.push(
      this.activatedRoute.params
        .subscribe(params => {
          if (params[ 'org' ]) {
            this.orgName = params[ 'org' ];
            this.namespaceType = Main.Type.organization;
          }

          if (params[ 'user' ]) {
            this.orgName = params[ 'user' ];
            this.namespaceType = Main.Type.user;
          }

          this.initPage();
          this.getRepositoryList(this.orgName);
        })
    );

  }

  public search() {
    this.getRepositoryList(this.orgName);
  }

  /**
   * paging
   * @param page
   */
  public pageClick(page: number) {
    this.page.number = page;

    this.getRepositoryList(this.orgName);
  }

  /**
   * sort click
   * @param sort
   */
  public sortClick(sort: Sort) {
    this.page.sort = sort;

    this.getRepositoryList(this.orgName);
  }

  /**
   * repository 목록 조회
   * @param orgName
   */
  private getRepositoryList(orgName: string) {
    this.loaderService.show.next(true);

    this.isSearching = true;

    this.repositoryService.getRepositoryList(orgName, this.searchKey, this.page).then(result => {
      this.repositoryList = result.content;
      this.initPage(result);

      this.pagination.init(this.page);

      this.isSearching = false;

      this.loaderService.show.next(false);

    });
  }

  /**
   * page 초기화
   * @param page
   */
  private initPage(page: Page = null) {
    let result: Page = new Page(page);
    if (page) {
      result.sort = this.sort;
    } else {
      result.number = 0;
      result.size = 20;
      this.sort = new Sort();
      this.sort.property = 'createdDate';
      this.sort.direction = 'desc';
      result.sort = this.sort;
    }

    this.page = result;
  }

}
