import {Component, OnInit, ElementRef, Injector} from "@angular/core";
import {RepositoryService} from "../../repository/repository.service";
import {Repository} from "../../repository/repository.value";
import {PageComponent} from "../../../common/component/page.component";
import {Organization} from "../organization.value";
import {Main} from "../../main/main.value";

@Component({
  selector: 'repository',
  templateUrl: 'repository.component.html'
})
export class RepositoryComponent extends PageComponent implements OnInit {

  public showCard: boolean = true;

  public repositoryList: Repository.Entity[] = [];

  public searchKey: string = '';

  public org: Organization.Entity;
  public orgName: string;

  public MainType: typeof Main.Type = Main.Type;

  public namespaceType: Main.Type;

  public isSearching: boolean = false;

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

          this.getRepositoryList(this.orgName);
        })
    );

  }

  /**
   * repository 목록 조회
   * @param orgName
   */
  private getRepositoryList(orgName: string) {
    this.loaderService.show.next(true);

    this.isSearching = true;

    this.repositoryService.getRepositoryList(orgName).then(result => {
      this.repositoryList = result.content;

      this.isSearching = false;

      this.loaderService.show.next(false);

    });
  }

}
