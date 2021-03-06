import {Component, OnInit, ElementRef, Injector} from '@angular/core';
import {PageComponent} from "../../../common/component/page.component";
import {OrganizationService} from "../organization.service";
import {UsageLogService} from "./usage-log.service";
import * as moment from "moment";
import {Logs} from "./logs.value";
import {Main} from "../../main/main.value";
import {Page} from "../../../common/value/result-value";

@Component({
  selector: 'usage-log',
  templateUrl: 'usage-log.component.html'
})
export class UsageLogComponent extends PageComponent implements OnInit {

  public logType: Main.Type;
  public orgName: string;

  public startDate: string;
  public endDate: string;

  public logList: Logs.Entity[] = [];

  public MainType: typeof Main.Type = Main.Type;

  public pageable: Page = new Page();

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private organizationService: OrganizationService,
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
            this.logType = Main.Type.organization;
          }

          if (params[ 'user' ]) {
            this.orgName = params[ 'user' ];
            this.logType = Main.Type.user;
          }

        })
    );
  }

  /**
   * 로그 조회
   * @param nextPage
   */
  public getLogList() {
    if (this.logType == Main.Type.organization) {
      this.usageLogService.getOrgLogList(this.orgName, this.startDate, this.endDate, this.pageable.number).then(result => {
        this.setLogList(result);
      });
    } else {
      this.usageLogService.getUserLogList(this.startDate, this.endDate, this.pageable.number).then(result => {
        this.setLogList(result);
      });
    }

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

  /**
   * set log list
   * @param result
   */
  private setLogList(result: Logs.Result) {
    this.pageable = result;
    this.logList = this.logList.concat(result.content);
    if (!result.last) {
      this.pageable.number = this.pageable.number + 1;
      this.getLogList();
    } else {
      this.loaderService.show.next(false);
    }
  }

}
