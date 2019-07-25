import {Component, OnInit, ElementRef, Injector} from '@angular/core';
import {PageComponent} from "../../../common/component/page.component";
import {Repository} from "../repository.value";
import {RepositoryService} from "../repository.service";
import {Build} from "../build-history/build-history.value";
import {BuildHistoryService} from "../build-history/build-history.service";
import * as moment from "moment";
import {Utils} from "../../../common/utils/utils";
import {CommonConstant} from "../../../common/constant/common-constant";
import {Alert} from "../../../common/utils/alert-util";
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'info',
  templateUrl: 'info.component.html'
})
export class InfoComponent extends PageComponent implements OnInit {

  public Phase: typeof Build.Phase = Build.Phase;

  public mine: boolean = true;

  public repo: Repository.Entity = new Repository.Entity();
  public orgName: string;
  public repoName: string;

  public sortProperty: string;
  public sortDirection: string;

  public dockerPullCommand: string;

  public buildHistoryList: Array<Build.Entity> = [];

  public currentBuildId: string;
  public showBuildDetailPopup: boolean = false;

  public stats: Array<Object> = [];
  public monthList: Array<string>;

  public showBuildPopup: boolean = false;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private repositoryService: RepositoryService,
              public buildHistoryService: BuildHistoryService) {

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

          this.dockerPullCommand = `docker pull ${environment.hostName}/${this.orgName}/${this.repoName}`;

          this.getRepository();
          this.getBuildHistory();
        })
    );

    this.subscriptions.push(
      this.buildHistoryService.newBuild.subscribe(
        value => {
          this.getBuildHistory();
        }
      )
    );

    this.subscriptions.push(
      this.commonService.lnbWriter.subscribe(
        value => {
          this.mine = value;
        }
      )
    );
  }

  /**
   * docker pull command 복사
   */
  public dockerPullCommandCopyClick() {
    Utils.StringUtil.copyToClipboard(this.dockerPullCommand);

    Alert.success(CommonConstant.MESSAGE.COPIED);
  }

  /**
   * history view more 클릭
   */
  public historyViewMoreClick() {
    this.router.navigate([`app/image/${this.orgName}/${this.repoName}/build`]);
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

  /**
   * popup build detail
   * @param item
   */
  public popupBuildDetail(item: Build.Entity) {
    if (!this.mine) {
      return;
    }

    this.currentBuildId = item.id;
    this.showBuildDetailPopup = true;
  }

  public buildDetailPopupClose() {
    this.showBuildDetailPopup = false;
  }

  public startNewBuildClick() {
    this.showBuildPopup = true;
  }

  public buildPopupClose() {
    this.showBuildPopup = false;
  }

  /**
   * save click
   */
  public saveClick() {
    this.repositoryService.updateRepository(this.orgName, this.repoName, this.repo).then(result => {
      Alert.success(CommonConstant.MESSAGE.SUCCESS);
    });
  }

  /**
   * repository 상세 조회
   */
  private getRepository() {
    this.repositoryService.getRepository(`${this.orgName}/${this.repoName}`, true).then(result => {
      this.repo = result;

      this.stats = [];
      this.monthList = [];

      let now = new Date();
      let month1 = new Date().setMonth(now.getMonth() - 1);
      let month2 = new Date().setMonth(now.getMonth() - 2);

      this.monthList.push(moment(month2).format('MMMM'));
      this.monthList.push(moment(month1).format('MMMM'));
      this.monthList.push(moment(now).format('MMMM'));

      for(let i = 0; i < 31; i++) {
        let data = [];
        this.monthList.forEach((value, index) => {
          let d = this.getDataByDate(this.repo.stats, value, i + 1);
          data.push(d ? d.count : -1);
        });

        this.stats.push(data);
      }

    });
  }

  private getDataByDate(list: Repository.Stat[], month, date) {
    let data;
    list.forEach(value => {
      let m = moment(value.date).format('MMMM');
      let d = moment(value.date).format('D');
      if (m == month && d == date) {
        data = value;
      }
    });

    return data;
  }

  /**
   * build history 조회
   */
  private getBuildHistory() {
    this.loaderService.show.next(true);

    this.repositoryService.getBuildHistory(this.orgName, this.repoName, 5).then(result => {
      result.builds.forEach(value => {
        value.formattedStarted = moment(value.started).format('YYYY-MM-DD HH:mm');
      });
      this.buildHistoryList = result.builds;

      this.loaderService.show.next(false);
    });
  }
}
