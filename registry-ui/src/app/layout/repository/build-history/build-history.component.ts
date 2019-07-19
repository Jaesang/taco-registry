import {Component, OnInit, ElementRef, Injector, OnDestroy} from '@angular/core';
import {PageComponent} from "../../../common/component/page.component";
import {BuildHistoryService} from "./build-history.service";
import {RepositoryService} from "../repository.service";
import {Build} from "./build-history.value";
import * as moment from "moment";

@Component({
  selector: 'build-history',
  templateUrl: 'build-history.component.html'
})
export class BuildHistoryComponent extends PageComponent implements OnInit, OnDestroy {

  public Phase: typeof Build.Phase = Build.Phase;

  public orgName: string;
  public repoName: string;

  public sortProperty: string;
  public sortDirection: string;

  public buildHistoryList: Array<Build.Entity> = [];

  public showBuildPopup: boolean = false;

  public currentBuildId: string;
  public showBuildDetailPopup: boolean = false;

  private timeoutId;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              public buildHistoryService: BuildHistoryService,
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
          }

          if (params[ 'repo' ]) {
            this.repoName = params[ 'repo' ];
          }

          if (this.timeoutId) {
            clearInterval(this.timeoutId);
          }

          this.timeoutId = setInterval(() => { this.getBuildHistory(false); }, 30000);
        })
    );

    this.subscriptions.push(
      this.buildHistoryService.newBuild.subscribe(
        value => {
          this.getBuildHistory();
        }
      )
    );

  }

  ngOnDestroy() {
    super.ngOnDestroy();

    if (this.timeoutId) {
      clearInterval(this.timeoutId);
    }
  }

  /**
   * 정렬
   * @param colName
   * @param sort
   */
  public sortClick(property: string) {
    if (this.sortProperty == property) {
      this.sortDirection = this.sortDirection == 'desc' ? 'asc' : 'desc';
    } else {
      this.sortDirection = 'desc';
    }
    this.sortProperty = property;
  }

  public startNewBuildClick() {
    this.showBuildPopup = true;
  }

  /**
   * build popup hide
   */
  public buildPopupClose() {
    this.showBuildPopup = false;
  }

  /**
   * popup build detail
   * @param item
   */
  public popupBuildDetail(item: Build.Entity) {
    this.currentBuildId = item.id;
    this.showBuildDetailPopup = true;
  }

  public buildDetailPopupClose() {
    this.showBuildDetailPopup = false;
  }

  /**
   * build history 조회
   */
  private getBuildHistory(showLoader: boolean = true) {
    if (showLoader) {
      this.loaderService.show.next(true);
    }

    this.repositoryService.getBuildHistory(this.orgName, this.repoName).then(result => {
      result.builds.forEach(value => {
        value.formatted_started = moment(value.started).format('YYYY-MM-DD HH:mm');
      });
      this.buildHistoryList = result.builds;

      if (showLoader) {
        this.loaderService.show.next(false);
      }
    });
  }
}
