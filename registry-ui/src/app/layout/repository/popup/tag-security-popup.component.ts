import {
  Component,
  OnInit,
  ElementRef,
  Injector,
  Input,
  Output,
  EventEmitter,
  OnChanges,
  SimpleChanges,
  ViewChild
} from "@angular/core";
import {AbstractComponent} from "../../../common/component/abstract.component";
import {BuildHistoryService} from "../build-history/build-history.service";
import {RepositoryService} from "../repository.service";
import {Repository} from "../repository.value";
import {TagService} from "../tag-info/tag.service";
import {Tag} from "../tag-info/tag.value";
import {Image} from "../tag-info/image.value";
import {TagSecurityVulnerabilityComponent} from "./tag-security-vulnerability.component";
import {TagSecurityPackagesComponent} from "./tag-security-packages.component";

@Component({
  selector: '[tag-security-popup]',
  templateUrl: 'tag-security-popup.component.html'
})
export class TagSecurityPopupComponent extends AbstractComponent implements OnInit, OnChanges {

  @ViewChild(TagSecurityVulnerabilityComponent)
  private vulnerabilityComponent: TagSecurityVulnerabilityComponent;

  @ViewChild(TagSecurityPackagesComponent)
  private packageComponent: TagSecurityPackagesComponent;

  @Input()
  public show: boolean = false;

  @Input()
  public imageId: string;

  @Input()
  public tag: Tag.Entity;

  @Output()
  public onClose: EventEmitter<any> = new EventEmitter();

  public orgName: string;
  public repoName: string;

  public repo: Repository.Entity = new Repository.Entity();

  public security: Tag.Security;
  public layerList: Image.Layer[] = [];

  public tabIndex: number = 0;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private repositoryService: RepositoryService,
              private tagService: TagService,
              private buildService: BuildHistoryService) {

    super(elementRef, injector);
  }

  ngOnInit() {
  }

  public ngOnChanges(changes: SimpleChanges): void {
    for (let propName in changes) {
      if (propName === 'imageId' && this.imageId) {
        this.orgName = '';
        this.repo = this.repositoryService.repository;
        this.orgName = this.repo.namespace;
        this.repoName = this.repo.name;

        this.getSecurity();
      }
    }
  }

  public close() {
    this.show = false;
    this.onClose.emit();
  }

  /**
   * security scan 조회
   */
  public getSecurity() {
    this.loaderService.show.next(true);

    this.tagService.getSecurity(this.orgName, this.repoName, this.tag.name).then(r => {
      this.security = r;

      this.tagService.getImageDetail(this.orgName, this.repoName, this.tag.name).then(result => {
        this.layerList = [];
        this.tagService.setCommand(result);
        this.layerList.push(result);

        // result.history.reverse();
        result.history.forEach(value => {
          this.tagService.setCommand(value);
          this.layerList.push(value);
        });

        this.tabIndex = 0;
        this.vulnerabilityComponent.init(this.security, this.layerList);
        this.packageComponent.init(this.security, this.layerList);

        this.loaderService.show.next(false);
      });
    });
  }

}
