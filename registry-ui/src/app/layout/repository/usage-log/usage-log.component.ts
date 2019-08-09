import {Component, OnInit, ElementRef, Injector} from '@angular/core';
import {PageComponent} from "../../../common/component/page.component";
import * as moment from "moment";
import {Logs} from "../../organization/usage-log/logs.value";
import {UsageLogService} from "../../organization/usage-log/usage-log.service";
import {Page} from "../../../common/value/result-value";

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

  public pageable: Page = new Page();

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
  public getLogList() {
    this.usageLogService.getRepoLogList(this.orgName, this.repoName, this.startDate, this.endDate, this.pageable.number).then(result => {
      this.logList = this.logList.concat(result.content);
      this.pageable = result;
      if (!result.last) {
        this.pageable.number = this.pageable.number + 1;
        this.getLogList();
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
