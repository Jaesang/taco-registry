import {
  Component, OnInit, ElementRef, Injector, Input, Output, EventEmitter, OnChanges,
  SimpleChanges, ViewChild
} from "@angular/core";
import {AbstractComponent} from "../../../common/component/abstract.component";
import {RepositoryService} from "../repository.service";
import {Repository} from "../repository.value";
import {Main} from "../../main/main.value";
import {UserService} from "../../user/user.service";
import {Select} from "../../../common/component/selectbox/select.value";
import {SelectBoxComponent} from "../../../common/component/selectbox/select-box.component";
import {Validate} from "../../../common/utils/validate-util";
import {DockerService} from "../../../common/service/docker.service";
import {FileService} from "../../../common/service/file.service";
import {BuildHistoryService} from "../build-history/build-history.service";
import {Alert} from "../../../common/utils/alert-util";
import {CommonConstant} from "../../../common/constant/common-constant";
import {Build} from "../build-history/build-history.value";

@Component({
  selector: '[create-repo-popup]',
  templateUrl: 'create-popup.component.html'
})
export class CreateRepoPopupComponent extends AbstractComponent implements OnInit, OnChanges  {
  public Main = Main;

  @ViewChild(SelectBoxComponent)
  private selectBox: SelectBoxComponent;

  @Input()
  public show: boolean = false;

  @Output()
  public onClose: EventEmitter<any> = new EventEmitter();

  public namespaceType: Main.Type;

  public repo:Repository.Entity = new Repository.Entity();
  public tag: string;

  public orgName: string;
  public errorMsg: string = '';

  public errorRepoName: boolean;
  public errorTagName: boolean;

  public fileStatus: string;
  public dockerFileContent: string;

  public CreateType: typeof Build.CreateType = Build.CreateType;
  public createType: Build.CreateType;

  public gitPath: string;
  public gitUsername: string;
  public gitPassword: string;

  public noCache: boolean = false;

  public minioPath: string;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private repositoryService: RepositoryService,
              public userService: UserService,
              private dockerService: DockerService,
              private fileService: FileService,
              private buildService: BuildHistoryService) {

    super(elementRef, injector);
  }

  ngOnInit() {
  }

  public ngOnChanges(changes: SimpleChanges): void {
    for (let propName in changes) {
      if (propName === 'show' && this.show) {
        this.errorMsg = '';
        this.orgName = '';
        this.repo = new Repository.Entity();
        this.repo.isPublic = false;
        this.createType = this.CreateType.DEFAULT;
        this.errorRepoName = false;
        this.errorTagName = false;
        this.dockerFileContent = '';
        this.gitPath = '';
        this.gitUsername = '';
        this.gitPassword = '';
        this.minioPath = '';
        this.noCache = false;
        this.namespaceType = Main.Type.user;
        this.repo.namespace = this.userService.user.username;
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

  public namespaceTypeChange() {
    if (this.namespaceType == Main.Type.user) {
      this.repo.namespace = this.userService.user.username;
    } else {
      this.repo.namespace = null;
      this.initSelectList();
    }
    this.checkValidate();
  }

  /**
   * namespace 선택
   * @param item
   */
  public namespaceSelect(item: Select.Value) {
    this.repo.namespace = item.value;
    this.checkValidate();
  }

  /**
   * 생성 타입 변경
   * @param index
   */
  public changeCreateType(index: number) {
    this.fileStatus = null;
    this.noCache = false;

    if (index == 0) {
      this.createType = this.CreateType.DEFAULT;
    } else if (index == 1) {
      this.createType = this.CreateType.DOCKERFILE;
    } else if (index == 2) {
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
    this.checkValidate();
  }

  /**
   * 생성 버튼 클릭
   */
  public createRepoClick() {
    this.loaderService.show.next(true);

    this.repo.isOrganization = this.repo.namespace == this.userService.user.username ? false : true;
    this.repositoryService.createRepository(this.repo).then(result => {
      if (this.createType == this.CreateType.DEFAULT) {
        this.close();
        this.router.navigate([`app/image/${this.repo.namespace}/${this.repo.name}/info`]);

        this.loaderService.show.next(false);

        Alert.success(CommonConstant.MESSAGE.SUCCESS);
      } else {
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
        build.tag = this.tag;

        this.buildService.build(this.repo.namespace, this.repo.name, build).then(result => {
          this.close();
          this.router.navigate([`app/image/${this.repo.namespace}/${this.repo.name}/build`]);

          this.loaderService.show.next(false);

          Alert.success(CommonConstant.MESSAGE.SUCCESS);
        });
      }
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
    this.errorRepoName = false;
    this.errorTagName = false;
    let result = true;

    if (!Validate.checkValidateWithPattern('^[a-z0-9_-]+$', this.repo.namespace) || Validate.isEmpty(this.repo.namespace)) {
      result = false;
    }

    if (!Validate.checkValidateWithPattern('^[a-z0-9_-]+$', this.repo.name) || Validate.isEmpty(this.repo.name)) {
      if (!Validate.isEmpty(this.repo.name)) {
        this.errorRepoName = true;
      }
      result = false;
    }

    if (!Validate.isEmpty(this.tag) && !Validate.checkValidateWithPattern(/^[a-zA-Z0-9\_\-\.]+$/, this.tag)) {
      this.errorTagName = true;
      result = false;
    }

    if (this.createType == this.CreateType.DOCKERFILE) {
      if (this.fileStatus != 'success') {
        result = false;
      }
    } else if (this.createType == this.CreateType.GIT) {
      if (Validate.isEmpty(this.gitPath)) {
        result = false;
      }

      if ((!Validate.isEmpty(this.gitUsername) && Validate.isEmpty(this.gitPassword)) ||
        (Validate.isEmpty(this.gitUsername) && !Validate.isEmpty(this.gitPassword))) {
        result = false;
      }
    } else if (this.createType == this.CreateType.MINIO) {
      if (Validate.isEmpty(this.minioPath)) {
        result = false;
      }
    }


    return result;
  }

  /**
   * select 초기화
   */
  private initSelectList() {
    let orgList = [];

    this.userService.user.organizations.forEach(value => {
      orgList.push(new Select.Value(value.name, value.name, false));
    });

    if (orgList.length) {
      this.selectBox.init(orgList);
    }
  }
}
