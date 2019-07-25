import {Component, OnInit, ElementRef, Injector} from '@angular/core';
import {PageComponent} from "../../../common/component/page.component";
import * as moment from "moment";
import {Logs} from "../../organization/usage-log/logs.value";
import {UsageLogService} from "../../organization/usage-log/usage-log.service";

@Component({
  selector: 'usage-log',
  templateUrl: 'usage-log.component.html'
})
export class UsageLogComponent extends PageComponent implements OnInit {

  public orgName: string;
  public repoName: string;

  private startDate: string;
  private endDate: string;

  public logList: Logs.Entity[] = [];

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private usageLogService: UsageLogService) {

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

        })
    );
  }

  /**
   * 로그 조회
   * @param nextPage
   */
  public getLogList(nextPage: string = null) {
    this.usageLogService.getRepoLogList(this.orgName, this.repoName, this.startDate, this.endDate, nextPage).then(result => {
      this.logList = this.logList.concat(result.logs);
      if (result.nextPage) {
        this.getLogList(result.nextPage);
      } else {
        this.loaderService.show.next(false);
      }
    });
  }

  /**
   * 날짜 선택
   * @param dates
   */
  public dateSelect(dates) {
    this.loaderService.show.next(true);

    this.startDate = encodeURIComponent(moment(dates.startDate).format('M/D/YYYY'));
    this.endDate = encodeURIComponent(moment(dates.endDate).format('M/D/YYYY'));

    this.logList = [];
    this.getLogList();
  }

}
