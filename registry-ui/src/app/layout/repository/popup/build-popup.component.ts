import {
  Component, OnInit, ElementRef, Injector, Input, Output, EventEmitter, OnChanges,
  SimpleChanges, ViewChild
} from "@angular/core";
import {AbstractComponent} from "../../../common/component/abstract.component";
import {DockerService} from "../../../common/service/docker.service";
import {FileService} from "../../../common/service/file.service";
import {FileUploadComponent} from "../../../common/component/file-upload/file-upload.component";
import {BuildHistoryService} from "../build-history/build-history.service";
import {RepositoryService} from "../repository.service";
import {OrganizationService} from "../../organization/organization.service";
import {CommonConstant} from "../../../common/constant/common-constant";
import {Alert} from "../../../common/utils/alert-util";
import {Build} from "../build-history/build-history.value";
import {Validate} from "../../../common/utils/validate-util";
import {Utils} from "../../../common/utils/utils";
import {UserService} from "../../user/user.service";

@Component({
  selector: '[build-popup]',
  templateUrl: 'build-popup.component.html'
})
export class BuildPopupComponent extends AbstractComponent implements OnInit, OnChanges {

  @ViewChild(FileUploadComponent)
  private fileUploader: FileUploadComponent;

  @Input()
  public show: boolean = false;

  @Output()
  public onClose: EventEmitter<any> = new EventEmitter();

  public fileStatus: string;

  public orgName: string;
  public repoName: string;
  public errorMsg: string = '';

  public CreateType: typeof Build.CreateType = Build.CreateType;
  public createType: Build.CreateType;

  public gitPath: string;
  public gitUsername: string;
  public gitPassword: string;

  public noCache: boolean = false;

  public minioUrl: string;
  public minioPath: string;

  public uuid: string = Utils.Generate.UUID();

  private dockerFileContent: string;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              public userService: UserService,
              private repositoryService: RepositoryService,
              private organizationService: OrganizationService,
              private dockerService: DockerService,
              private fileService: FileService,
              private buildService: BuildHistoryService) {

    super(elementRef, injector);

    this.minioUrl = this.userService.user.minioUrl;
  }

  ngOnInit() {
  }

  public ngOnChanges(changes: SimpleChanges): void {
    for (let propName in changes) {
      if (propName === 'show') {
        this.errorMsg = '';
        this.orgName = '';
        this.createType = this.CreateType.DOCKERFILE;
        this.dockerFileContent = '';
        this.gitPath = '';
        this.gitUsername = '';
        this.gitPassword = '';
        this.minioPath = '';
        this.noCache = false;
      }
    }
  }

  public close() {
    if (this.fileUploader) {
      this.fileUploader.init();
    }
    this.fileStatus = null;
    this.show = false;
    this.onClose.emit();
  }

  /**
   * 생성 타입 변경
   * @param index
   */
  public changeCreateType(index: number) {
    this.fileStatus = null;
    this.noCache = false;

    if (index == 0) {
      this.createType = this.CreateType.DOCKERFILE;
    } else if (index == 1) {
      this.createType = this.CreateType.GIT;
    } else {
      this.createType = this.CreateType.MINIO;
    }
  }

  /**
   * 파일 선택
   * @param text
   */
  public fileChange(text: string) {
    if (this.dockerService.validDockerFile(text)) {
      this.dockerFileContent = text;
      this.fileStatus = 'success';
    } else {
      this.fileStatus = 'error';
    }
  }

  /**
   * start build 클릭
   */
  public startBuildClick() {
    this.loaderService.show.next(true);

    this.orgName = this.repositoryService.repository.namespace;
    this.repoName = this.repositoryService.repository.name;

    let build: Build.Entity = new Build.Entity();
    if (this.createType == this.CreateType.DOCKERFILE) {
      build.dockerfile = this.dockerFileContent;
    } else if (this.createType == this.CreateType.GIT) {
      build.gitPath = this.gitPath;
      build.gitUsername = this.gitUsername;
      build.gitPassword = this.gitPassword;
    } else {
      build.minioPath = this.minioPath;
    }
    build.noCache = this.noCache;

    this.buildService.build(this.orgName, this.repoName, build).then(result => {
      this.buildService.newBuild.next(result);

      this.close();

      Alert.success(CommonConstant.MESSAGE.SUCCESS);

      this.loaderService.show.next(false);
    }).catch(reason => {
      let body = JSON.parse(reason._body);
      Alert.error(body.message);
    });
  }

  /**
   * check validation
   * @returns {boolean}
   */
  public checkValidate() {

    this.errorMsg = '';

    if (this.createType == this.CreateType.DOCKERFILE) {
      if (this.fileStatus != 'success') {
        return false;
      }
    } else if (this.createType == this.CreateType.GIT) {
      if (Validate.isEmpty(this.gitPath)) {
        return false;
      }

      if ((!Validate.isEmpty(this.gitUsername) && Validate.isEmpty(this.gitPassword)) ||
        (Validate.isEmpty(this.gitUsername) && !Validate.isEmpty(this.gitPassword))) {
        return false;
      }
    } else if (this.createType == this.CreateType.MINIO) {
      if (Validate.isEmpty(this.minioPath)) {
        return false;
      }
    }

    return true;
  }

}
