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
import {TagService} from "../tag-info/tag.service";
import {Repository} from "../repository.value";
import {Image} from "../tag-info/image.value";

@Component({
  selector: '[tag-layer-popup]',
  templateUrl: 'tag-layer-popup.component.html'
})
export class TagLayerPopupComponent extends AbstractComponent implements OnInit, OnChanges {

  @Input()
  public show: boolean = false;

  @Input()
  public imageId: string;

  @Output()
  public onClose: EventEmitter<any> = new EventEmitter();

  public repo: Repository.Entity;

  public orgName: string;
  public repoName: string;

  public layerList: Image.Layer[] = [];

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              public buildHistoryService: BuildHistoryService,
              private repositoryService: RepositoryService,
              private tagService: TagService) {

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

        this.getImageDetail();
      }
    }
  }

  public close() {
    this.show = false;
    this.onClose.emit();
  }

  /**
   * 이미지 상세 조회
   */
  private getImageDetail() {
    this.tagService.getImageDetail(this.orgName, this.repoName, this.imageId).then(result => {
      this.layerList = [];
      this.setCommand(result);
      this.layerList.push(result);

      result.history.reverse();
      result.history.forEach(value => {
        this.setCommand(value);
        this.layerList.push(value);
      });
    });
  }

  /**
   * command 분리 작업
   * ["/bin/sh", "-c", "#(nop) ", "CMD ["nginx"]"]
   * ["/bin/sh -c #(nop) ADD file:13f0f6484071addf07e8399246be51c3a1d9e26ccd7e6d19d75797f37387dc12 in / "]
   * @param layer
   */
  private setCommand(layer: Image.Layer) {
    if (layer.command.length > 1) {
      let findText = '#(nop) ';
      if (layer.command.indexOf(findText) > -1) {
        let index = layer.command.indexOf(findText);
        let command = layer.command[index + 1].toString();
        layer.commandName = command.substr(0, command.indexOf(' '));
        layer.commandText = command.substr(command.indexOf(' '));
      } else {
        findText = '-c';
        let index = layer.command.indexOf(findText);
        layer.commandName = 'RUN';
        layer.commandText = layer.command[index + 1].toString();
      }
    } else {
      let findText = '#(nop) ';
      if (layer.command[0].toString().indexOf(findText) > -1) {
        let command = layer.command[0].toString().substr(layer.command[0].toString().indexOf(findText) + findText.length).trim();
        layer.commandName = command.substr(0, command.indexOf(' '));
        layer.commandText = command.substr(command.indexOf(' '));
      } else {
        findText = '-c';
        let command = layer.command[0].toString().substr(layer.command[0].toString().indexOf(findText) + findText.length).trim();
        layer.commandName = 'RUN';
        layer.commandText = command;
      }

    }
  }

}
