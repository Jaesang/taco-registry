import {
  Component,
  ElementRef,
  EventEmitter,
  Injector,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild
} from "@angular/core";
import {RepositoryService} from "../repository.service";
import * as moment from "moment";
import {Utils} from "../../../common/utils/utils";
import {AbstractComponent} from "../../../common/component/abstract.component";
import {Alert} from "../../../common/utils/alert-util";
import {CommonConstant} from "../../../common/constant/common-constant";
import {Build} from "../build-history/build-history.value";
import {BuildHistoryService} from "../build-history/build-history.service";
import {environment} from "../../../../environments/environment";
import Phase = Build.Phase;
import LogType = Build.LogType;

@Component({
  selector: '[build-detail-popup]',
  templateUrl: 'build-detail-popup.component.html'
})
export class BuildDetailPopupComponent extends AbstractComponent implements OnInit, OnDestroy, OnChanges {

  public LogType = LogType;

  @ViewChild('fileDownload')
  private fileDownload: ElementRef;

  @Input()
  public show: boolean = false;

  @Output()
  public onClose: EventEmitter<any> = new EventEmitter();

  public BuildPhase: typeof Build.Phase = Build.Phase;

  @Input()
  public namespace: string;
  @Input()
  public repoName: string;
  @Input()
  public buildId: string;

  public build: Build.Entity = new Build.Entity();

  public buildLog: Build.LogResult;
  public buildLogList: Build.Log[] = [];

  public buildLogListTop: Build.Log[] = [];
  public buildLogListMiddle: Build.Log[] = [];
  public buildLogListBottom: Build.Log[] = [];

  public logNotFound: boolean;

  private timeoutId: any;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              public buildHistoryService: BuildHistoryService,
              private repositoryService: RepositoryService) {

    super(elementRef, injector);
  }

  ngOnInit() {

  }

  ngOnDestroy() {
    super.ngOnDestroy();

    if (this.timeoutId) {
      clearInterval(this.timeoutId);
    }
  }

  public ngOnChanges(changes: SimpleChanges): void {
    for (let propName in changes) {
      if (propName === 'show' && this.show == true) {
        this.init();

        this.getBuildDetail();
        if (this.timeoutId) {
          clearInterval(this.timeoutId);
        }

        this.timeoutId = setInterval(() => { this.getBuildDetail(); }, 5000);
      }
    }
  }

  /**
   * 닫기
   */
  public close() {
    this.show = false;
    this.onClose.emit();
  }

  public init() {
    this.build = new Build.Entity();
    this.buildLogList = [];
    this.buildLogListTop = [];
    this.buildLogListMiddle = [];
    this.buildLogListBottom = [];
  }

  /**
   * 빌드 취소 클릭
   */
  public cancelBuildClick() {
    this.buildHistoryService.deleteBuild(this.namespace, this.repoName, this.buildId).then(result => {
      this.getBuildDetail();

      Alert.success(CommonConstant.MESSAGE.CANCELD);
    }).catch(reason => {
      let body = JSON.parse(reason._body);
      Alert.error(body.message);
    });
  }

  /**
   * download logs 클릭
   */
  public downloadLogsClick() {
    // this.buildHistoryService.getBuildLogUrl(this.namespace, this.repoName, this.buildId).then(result => {
    //   this.fileDownload.nativeElement.href = result.logsUrl;
    //   this.fileDownload.nativeElement.download = 'fileName';
    //   this.fileDownload.nativeElement.click();
    // });

    this.fileDownload.nativeElement.href = `${environment.host}/logarchive/${this.buildId}`;
    this.fileDownload.nativeElement.download = this.buildId;
    this.fileDownload.nativeElement.type = 'application/json';
    this.fileDownload.nativeElement.click();
  }

  /**
   * copy logs 클릭
   */
  public copyLogsClick() {
    Utils.StringUtil.copyToClipboard(JSON.stringify(this.buildLog));
    Alert.success(CommonConstant.MESSAGE.COPIED);
  }

  /**
   * build step class
   * @param item
   * @param index
   * @returns {string}
   */
  public getClassBuildStep(item: Build.Log, index: number = -1) {
    let className = 'build-step';
    if (item.message == Build.LogPhase.error.toString() || item.type == Build.LogType.error) {
      if (item.message == 'error') {
        className += ' type-error';
      } else {
        className = 'build-error';
      }
    } else if (item.message == Build.LogPhase.pushing.toString()) {
      className += ' type-pushing';
    } else if (item.message == Build.LogPhase.complete.toString()) {
      className += ' type-complete';
    } else {
      if (item.message == null) {
        className = 'build-time';
      } else if (index == 0) {
        className += ' type-1st';
      } else if (index == 1) {
        className += ' type-2nd';
      } else if (index == 2) {
        className += ' type-3rd';
      } else if (index == 3) {
        className += ' type-4th';
      } else if (index == 4) {
        className += ' type-5th';
      } else if (index == 5) {
        className += ' type-6th';
      }
    }

    return className;
  }

  /**
   * build message
   * @param item
   * @returns {string}
   */
  public getBuildMessage(item: Build.Log) {
    let message = '';
    if (item.type == Build.LogType.phase) {
      switch (item.message) {
        case Build.LogPhase.build_scheduled.toString():
          message = 'Preparing build node';
          break;

        case Build.LogPhase.unpacking.toString():
          message = 'Unpacking build package';
          break;

        case Build.LogPhase.pulling.toString():
          message = 'Pulling base image';
          break;

        case Build.LogPhase.checking_cache.toString():
          message = 'Looking up cached images';
          break;

        case Build.LogPhase.priming_cache.toString():
          message = 'Priming cache for build';
          break;

        case Build.LogPhase.building.toString():
          message = 'Building image from Dockerfile';
          break;

        case Build.LogPhase.pushing.toString():
          message = 'Pushing image built from Dockerfile';
          break;

        case Build.LogPhase.complete.toString():
          message = 'Dockerfile build completed and pushed';
          break;

        case Build.LogPhase.error.toString():
          message = 'Dockerfile build failed';
          break;
      }
    } else if (item.type == Build.LogType.error) {
      message = item.message;
    } else {
      message = moment(item.datetime).format('YYYY-MM-DD HH:mm');
    }

    return message;
  }

  /**
   * build 상세 조회
   */
  private getBuildDetail() {
    this.buildHistoryService.getBuildDetail(this.namespace, this.repoName, this.buildId).then(result => {
      this.build = result;

      switch (this.build.phase) {
        case Build.Phase.error:
        case Build.Phase.complete:
          clearInterval(this.timeoutId);
          break;
      }

      this.getBuildLogList();
    });
  }

  /**
   * build log 조회
   */
  private getBuildLogList() {
    // this.loaderService.show.next(true);
    this.logNotFound = false;

    this.buildHistoryService.getBuildLogList(this.namespace, this.repoName, this.buildId).then(result => {
      this.setLogs(result);

      // this.loaderService.show.next(false);
    }).catch(reason => {
      this.logNotFound = true;
      // this.loaderService.show.next(false);
      clearInterval(this.timeoutId);
    });
  }

  private setLogs(logs: Build.LogResult) {
    this.buildLog = logs;
    this.buildLogList = logs.logs;

    this.buildLogListTop = [];
    this.buildLogListMiddle = [];
    this.buildLogListBottom = [];

    let middleStart = false;
    let bottomStart = false;
    let parent: Build.Log;

    // log 타입에 따라 상중하로 나눠서 데이터 생성
    this.buildLogList.forEach(value => {
      if (!middleStart) {
        if (value.type == Build.LogType.command) {
          middleStart = true;
        } else {
          this.buildLogListTop.push(value);
          if (value.type == Build.LogType.phase && value.message == Build.LogPhase.building.toString()) {
            let log: Build.Log = new Build.Log();
            log.datetime = value.datetime;
            this.buildLogListTop.push(log);
          }
        }
      }

      if (middleStart && !bottomStart) {
        if (value.type == Build.LogType.phase) {
          bottomStart = true;
        } else if (value.type == Build.LogType.command) {
          parent = value;
          value.commandName = value.message.substr(value.message.indexOf(': ') + 2).split(' ')[0];
          value.command = value.message.substr(value.message.indexOf(value.commandName) + value.commandName.length);
          if (this.build.phase == Phase.building || this.build.phase == Phase.pulling || this.build.phase == Phase.pushing || this.build.phase == Phase.waiting) {
            value.expand = true;
          } else {
            value.expand = false;
          }
          value.children = [];
          this.buildLogListMiddle.push(value);
        } else {
          parent.children.push(value);
        }
      }

      if (bottomStart) {
        this.buildLogListBottom.push(value);
      }
      return true;
    });
  }

}
