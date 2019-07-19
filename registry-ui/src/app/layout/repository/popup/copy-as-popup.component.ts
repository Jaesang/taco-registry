import {
  Component,
  OnInit,
  ElementRef,
  Injector,
  OnDestroy,
  Input,
  Output,
  EventEmitter,
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

@Component({
  selector: '[copy-as-popup]',
  templateUrl: 'copy-as-popup.component.html'
})
export class CopyAsPopupComponent extends AbstractComponent implements OnInit, OnDestroy {

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

  public orgName: string;
  public errorMsg: string = '';

  public errorRepoName: boolean;

  public dockerFileContent: string;

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
    this.orgName = '';
    this.baseRepo = this.repositoryService.repository;
    this.repo = new Repository.Entity();
    this.repo.is_public = false;
    this.errorRepoName = false;
    this.dockerFileContent = '';
    this.getLastDockerfile();
    this.initSelectList();
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

  /**
   * namespace 선택
   * @param item
   */
  public namespaceSelect(item: Select.Value) {
    this.repo.namespace = item.value;
  }

  /**
   * repository name 변경
   */
  public repoNameChange() {
    if (this.errorMsg != '') {
      this.errorRepoName = true;
    } else {
      this.errorRepoName = false;
    }
  }

  /**
   * 생성 버튼 클릭
   */
  public createRepoClick() {
    this.loaderService.show.next(true);

    this.dockerFileContent = this.editor.value;

    this.repositoryService.createRepository(this.repo).then(result => {

      this.fileService.createFile(this.dockerFileContent).then(result => {
        this.buildService.build(this.repo.namespace, this.repo.name, result.file_id).then(result => {
          this.close();
          this.router.navigate([`app/image/${this.repo.namespace}/${this.repo.name}/build`]);

          this.loaderService.show.next(false);

          Alert.success(CommonConstant.MESSAGE.SUCCESS);
        });
      }).catch(reason => {

      });
    }).catch(reason => {
      let body = JSON.parse(reason._body);
      Alert.error(body.detail);
    });
  }

  /**
   * check validation
   * @returns {boolean}
   */
  public checkValidate() {

    if (!Validate.checkValidateWithPattern('^[a-z0-9_-]+$', this.repo.namespace) || Validate.isEmpty(this.repo.namespace)) {
      return false;
    }

    if (!Validate.checkValidateWithPattern('^[a-z0-9_-]+$', this.repo.name) || Validate.isEmpty(this.repo.name)) {
      if (!Validate.isEmpty(this.repo.name)) {
        this.errorMsg = 'error';
      }
      return false;
    }

    if (!this.dockerService.validDockerFile(this.editor.value)) {
      return false;
    }

    this.errorMsg = '';

    return true;
  }

  /**
   * select 초기화
   */
  private initSelectList() {
    let orgList = [new Select.Value(this.userService.user.username, this.userService.user.username, true)];

    this.userService.user.organizations.forEach(value => {
      orgList.push(new Select.Value(value.name, value.name, false));
    });

    this.selectBox.init(orgList);
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
      list = result.builds.sort((a: Build.Entity, b: Build.Entity) => {
        return new Date(a.started).getTime() > new Date(a.started).getTime() ? -1 : new Date(a.started).getTime() < new Date(a.started).getTime() ? 1 : 0;
      });

      if (list.length) {
        let resourceKey = list[0].resource_key;
        this.fileService.getFile(resourceKey).then(result => {
          let fileContent = result._body;
          this.editor.setText(fileContent);

          this.loaderService.show.next(false);
        });
      } else {
        this.loaderService.show.next(false);
      }
    });
  }
}
