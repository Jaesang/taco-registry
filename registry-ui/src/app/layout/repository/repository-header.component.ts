import {Component, OnInit, ElementRef, Injector, ViewChild} from "@angular/core";
import {Organization} from "../organization/organization.value";
import {RepositoryService} from "./repository.service";
import {AbstractComponent} from "../../common/component/abstract.component";
import {FileService} from "../../common/service/file.service";
import {DockerService} from "../../common/service/docker.service";
import {FileUploadComponent} from "../../common/component/file-upload/file-upload.component";
import {BuildHistoryService} from "./build-history/build-history.service";
import {Repository} from "./repository.value";
import {CommonConstant} from "../../common/constant/common-constant";
import {Alert} from "../../common/utils/alert-util";

@Component({
  selector: '[repository-header]',
  templateUrl: 'repository-header.component.html'
})
export class RepositoryHeaderComponent extends AbstractComponent implements OnInit {

  @ViewChild(FileUploadComponent)
  private fileUploader: FileUploadComponent;

  public org: Organization.Entity;
  public repo: Repository.Entity;
  public orgName: string;
  public repoName: string;

  public mine: boolean = true;

  public showBuildPopup: boolean = false;
  public showCopyAsPopup: boolean = false;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private repositoryService: RepositoryService,
              private fileService: FileService,
              private dockerService: DockerService,
              private buildHistoryService: BuildHistoryService) {

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

          this.repo = this.repositoryService.repository;

        })
    );

    this.subscriptions.push(
      this.commonService.lnbWriter.subscribe(
        value => {
          this.mine = value;
        }
      )
    );
  }

  public backClick() {
    let url = '';
    if (this.repo.is_organization) {
      url = `app/organization/${this.repo.namespace}/repository`;
    } else {
      url = `app/user/${this.repo.namespace}/repository`;
    }
    this.router.navigate([url]);
  }

  public startNewBuildClick() {
    this.showBuildPopup = true;
  }

  public buildPopupClose() {
    this.showBuildPopup = false;
  }

  public copyAsPopupClose() {
    this.showCopyAsPopup = false;
  }

  /**
   * copy as 클릭
   */
  public copyAsClick() {
    this.showCopyAsPopup = true;
  }

  /**
   * 파일 선택
   * @param contents
   */
  public fileChange(contents: string) {
    this.fileService.createFile(contents).then(result => {
      if (result.ok) {
        this.buildHistoryService.build(this.orgName, this.repoName, result.file_id).then(result => {
          this.buildHistoryService.newBuild.next(result);
        });
      }
    });
  }

  /**
   * 별 체크 변경
   * @param item
   */
  public starChange(repo: Repository.Entity) {
    repo.is_starred = !repo.is_starred;
    if (repo.is_starred) {
      this.repositoryService.starredRepository(repo.namespace, repo.name).then(result => {

      });
    } else {
      this.repositoryService.deleteStarredRepository(repo.namespace, repo.name).then(result => {

      });
    }

  }

}
