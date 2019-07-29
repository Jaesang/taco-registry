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

@Component({
  selector: '[create-repo-popup]',
  templateUrl: 'create-popup.component.html'
})
export class CreateRepoPopupComponent extends AbstractComponent implements OnInit, OnChanges  {

  @ViewChild(SelectBoxComponent)
  private selectBox: SelectBoxComponent;

  @Input()
  public show: boolean = false;

  @Output()
  public onClose: EventEmitter<any> = new EventEmitter();

  public namespaceType: Main.Type;

  public repo:Repository.Entity = new Repository.Entity();

  public orgName: string;
  public errorMsg: string = '';

  public errorRepoName: boolean;

  public fileStatus: string;
  public dockerFileContent: string;

  public withDockerfile: boolean = false;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private repositoryService: RepositoryService,
              private userService: UserService,
              private dockerService: DockerService,
              private fileService: FileService,
              private buildService: BuildHistoryService) {

    super(elementRef, injector);
  }

  ngOnInit() {
  }

  public ngOnChanges(changes: SimpleChanges): void {
    for (let propName in changes) {
      if (propName === 'show') {
        this.errorMsg = '';
        this.orgName = '';
        this.repo = new Repository.Entity();
        this.repo.isPublic = false;
        this.withDockerfile = false;
        this.errorRepoName = false;
        this.dockerFileContent = '';
        this.initSelectList();
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
   * 생성 타입 변경
   * @param index
   */
  public changeCreateType(index: number) {
    this.fileStatus = null;

    if (index == 1) {
      this.withDockerfile = true;
    } else {
      this.withDockerfile = false;
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
   * 생성 버튼 클릭
   */
  public createRepoClick() {
    this.loaderService.show.next(true);

    this.repo.isOrganization = this.repo.namespace == this.userService.user.username ? false : true;
    this.repositoryService.createRepository(this.repo).then(result => {
      if (this.withDockerfile) {

        this.fileService.createFile(this.dockerFileContent).then(result => {
          this.buildService.build(this.repo.namespace, this.repo.name, result.file_id).then(result => {
            this.close();
            this.router.navigate([`app/image/${this.repo.namespace}/${this.repo.name}/build`]);

            this.loaderService.show.next(false);

            Alert.success(CommonConstant.MESSAGE.SUCCESS);
          });
        }).catch(reason => {

        });
      } else {
        this.close();
        this.router.navigate([`app/image/${this.repo.namespace}/${this.repo.name}/info`]);

        this.loaderService.show.next(false);

        Alert.success(CommonConstant.MESSAGE.SUCCESS);
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

    if (!Validate.checkValidateWithPattern('^[a-z0-9_-]+$', this.repo.namespace) || Validate.isEmpty(this.repo.namespace)) {
      return false;
    }

    // if (!Validate.checkValidateWithPattern('^[a-z0-9_-]+$', this.repo.name) || Validate.isEmpty(this.repo.name)) {
    //   if (!Validate.isEmpty(this.repo.name)) {
    //     this.errorMsg = 'error';
    //   }
    //   return false;
    // }

    this.errorMsg = '';

    if (this.withDockerfile) {
      if (this.fileStatus != 'success') {
        return false;
      }
    }

    return true;
  }

  /**
   * select 초기화
   */
  private initSelectList() {
    let orgList = [new Select.Value(this.userService.user.username, this.userService.user.username, false)];

    this.userService.user.organizations.forEach(value => {
      orgList.push(new Select.Value(value.name, value.name, false));
    });

    this.selectBox.init(orgList);
  }
}
