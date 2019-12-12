import {
  Component,
  ElementRef,
  EventEmitter,
  Injector,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild
} from "@angular/core";
import {RepositoryService} from "../repository.service";
import {AbstractComponent} from "../../../common/component/abstract.component";
import {Alert} from "../../../common/utils/alert-util";
import {CommonConstant} from "../../../common/constant/common-constant";
import {BuildHistoryService} from "../build-history/build-history.service";
import {Repository} from "../repository.value";
import {Main} from "../../main/main.value";
import {Select} from "../../../common/component/selectbox/select.value";
import {UserService} from "../../user/user.service";
import {FileService} from "../../../common/service/file.service";
import {SelectBoxComponent} from "../../../common/component/selectbox/select-box.component";
import {Validate} from "../../../common/utils/validate-util";
import {CodemirrorComponent} from "../../../common/component/codemirror/codemirror.component";
import {DockerService} from "../../../common/service/docker.service";
import {Build} from "../build-history/build-history.value";
import CreateType = Build.CreateType;

@Component({
  selector: '[copy-as-popup]',
  templateUrl: 'copy-as-popup.component.html'
})
export class CopyAsPopupComponent extends AbstractComponent implements OnInit, OnDestroy {

  public Main = Main;
  public CreateType = CreateType;

  @ViewChild(SelectBoxComponent)
  private selectBox: SelectBoxComponent;

  /**
   * 에디터
   */
  @ViewChild('codemirror')
  private editor: CodemirrorComponent;

  @Input()
  public show: boolean = false;

  @Output()
  public onClose: EventEmitter<any> = new EventEmitter();

  public namespaceType: Main.Type;

  public baseRepo:Repository.Entity = new Repository.Entity();
  public repo:Repository.Entity = new Repository.Entity();
  public tag: string;

  public orgName: string;
  public errorMsg: string = '';

  public errorRepoName: boolean;
  public errorTagName: boolean;

  public validate: boolean;

  public dockerFileContent: string;

  public sourceBuild: Build.Entity;
  public createType: CreateType;
  public minioPath: string;

  /**
   * 에디터 옵션
   */
  public config = {
    mode: 'text/x-mariadb',
    indentWithTabs: true,
    lineNumbers: true,
    matchBrackets: true,
    autofocus: true,
    indentUnit: 4,
    showSearchButton: true,
    extraKeys: {
      'Ctrl-Space': 'autocomplete',
      'Ctrl-/': 'toggleComment',
      'Shift-Tab': 'indentLess',
      'Tab': 'indentMore',
      'Shift-Ctrl-Space': 'autocomplete',
      'Cmd-Alt-Space': 'autocomplete'
    },
    hintOptions: {
      tables: {}
    }
  };

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              public buildHistoryService: BuildHistoryService,
              private userService: UserService,
              private fileService: FileService,
              private buildService: BuildHistoryService,
              private dockerService: DockerService,
              private repositoryService: RepositoryService) {

    super(elementRef, injector);
  }

  ngOnInit() {
    this.errorMsg = '';
    this.baseRepo = this.repositoryService.repository;
    this.repo = new Repository.Entity();
    this.repo.isPublic = false;
    this.errorRepoName = false;
    this.errorTagName = false;
    this.dockerFileContent = '';
    this.minioPath = '';
    this.namespaceType = Main.Type.user;
    this.repo.namespace = this.userService.user.username;
    this.validate = false;
    this.getLastDockerfile();
  }

  ngOnDestroy() {
    super.ngOnDestroy();

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
   * 생성 버튼 클릭
   */
  public createRepoClick() {
    this.loaderService.show.next(true);

    this.dockerFileContent = this.editor.value;

    this.repo.isOrganization = this.repo.namespace == this.userService.user.username ? false : true;
    this.repositoryService.createRepository(this.repo).then(result => {

      let build: Build.Entity = new Build.Entity();
      build.dockerfile = this.dockerFileContent;
      build.noCache = true;

      if (this.createType == CreateType.MINIO) {
        build.minioPath = this.minioPath;
      }
      build.tag = this.tag;

      this.buildService.build(this.repo.namespace, this.repo.name, build, this.sourceBuild).then(result => {
        this.close();
        this.router.navigate([`app/image/${this.repo.namespace}/${this.repo.name}/build`]);

        this.loaderService.show.next(false);

        Alert.success(CommonConstant.MESSAGE.SUCCESS);
      });
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

    if (this.createType != CreateType.MINIO && !this.dockerService.validDockerFile(this.editor.value)) {
      result = false;
    }

    if (this.createType == CreateType.MINIO && Validate.isEmpty(this.minioPath)) {
      result = false;
    }

    this.validate = result;

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

  /**
   * 마지막 성공한 dockerfile 내용 조회
   */
  private getLastDockerfile() {
    this.loaderService.show.next(true);

    this.repositoryService.getBuildHistory(this.baseRepo.namespace, this.baseRepo.name).then(result => {
      // complete 된 것 중에
      let list = result.builds.filter(value => {
        return value.phase == Build.Phase.complete;
      });

      // 최근 순 정렬
      list = list.sort((a: Build.Entity, b: Build.Entity) => {
        return new Date(a.started).getTime() > new Date(a.started).getTime() ? -1 : new Date(a.started).getTime() < new Date(a.started).getTime() ? 1 : 0;
      });

      if (list.length) {
        let build = list[0];
        this.sourceBuild = build;
        if (!Validate.isEmpty(build.dockerfile)) {
          this.editor.setText(build.dockerfile ? atob(build.dockerfile) : "");
          this.createType = CreateType.DOCKERFILE;
        } else if (this.userService.user.minioEnabled && !Validate.isEmpty(build.minioPath)) {
          this.createType = CreateType.MINIO;
        } else {
          this.editor.setText(`FROM ${this.userService.user.registryUrl}/${this.baseRepo.namespace}/${this.baseRepo.name}`);
          this.createType = CreateType.GIT;
        }
        this.loaderService.show.next(false);
      } else {
        this.loaderService.show.next(false);
      }
    });
  }
}
