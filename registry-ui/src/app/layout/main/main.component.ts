import {Component, OnInit, ElementRef, Injector, ViewChild} from "@angular/core";
import {PageComponent} from "../../common/component/page.component";
import {MainService} from "./main.service";
import {Main} from "./main.value";
import {PaginationComponent} from "../../common/component/pagination/pagination.component";
import {TagService} from "../repository/tag-info/tag.service";
import {Image} from "../repository/tag-info/image.value";
import {Repository} from "../repository/repository.value";
import {RepositoryService} from "../repository/repository.service";
import {Tag} from "../repository/tag-info/tag.value";

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

  public securityData: Object = {};
  public selectedImage: string;
  public selectedTag: Tag.Entity = new Tag.Entity();
  public showTagSecurityPopup: boolean = false;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private repositoryService: RepositoryService,
              private mainService: MainService,
              private tagService: TagService) {

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

          this.currentPage = 0;
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
    this.currentPage = 0;

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
   * scan 클릭
   * @param tag
   */
  public scanClick(image: Main.Entity) {
    this.selectedImage = `${image.namespace.name}/${image.name}:latest`;
    this.selectedTag.name = 'latest';
    let repo: Repository.Entity = new Repository.Entity();
    repo.namespace = image.namespace.name;
    repo.name = image.name;
    this.repositoryService.repository = repo;
    this.showTagSecurityPopup = true;
  }

  /**
   * repository 목록 조회
   * @param orgName
   */
  private getRepositoryList() {
    this.loaderService.show.next(true);
    this.isSearching = true;

    this.mainService.getRepositoryList(this.currentPage, this.searchKey).then(result => {
      this.repositoryList = result.content;
      this.paginationTop.init(result);
      this.paginationBottom.init(result);
      this.isSearching = false;

      this.loaderService.show.next(false);

      this.repositoryList.forEach(value => {
        this.getSecurityData(value);
      });
    });
  }

  /**
   * security scan 조회
   * @param imageId
   */
  private getSecurityData(image: Main.Entity) {
    this.tagService.getSecurity(image.namespace.name, image.name, 'latest').then(result => {
      this.tagService.setSecurityCount(result);

      this.securityData[`${image.namespace.name}/${image.name}`] = result;

    });
  }

}
