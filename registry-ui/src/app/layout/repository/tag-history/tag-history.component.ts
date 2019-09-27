import {Component, OnInit, ElementRef, Injector} from '@angular/core';
import {PageComponent} from "../../../common/component/page.component";
import {TagService} from "../tag-info/tag.service";
import {Tag} from "../tag-info/tag.value";
import * as moment from "moment";
import * as _ from 'lodash';
import {Alert} from "../../../common/utils/alert-util";
import {CommonConstant} from "../../../common/constant/common-constant";

@Component({
  selector: 'tag-history',
  templateUrl: 'tag-history.component.html'
})
export class TagHistoryComponent extends PageComponent implements OnInit {

  public orgName: string;
  public repoName: string;

  public sortProperty: string = 'date';
  public sortDirection: string = 'desc';

  public searchKey: string;

  public historyList: Tag.History[] = [];

  public showTagLayerPopup: boolean = false;
  public selectedImageId: string;
  public selectedTag: Tag.Entity;

  public showRestoreImagePopup: boolean = false;
  public selectedHistory: Tag.History = new Tag.History();

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private tagService: TagService) {

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

          this.getHistory();
        })
    );
  }

  /**
   * 정렬
   * @param colName
   * @param sort
   */
  public sortClick(property: string) {
    if (this.sortProperty == property) {
      this.sortDirection = this.sortDirection == 'desc' ? 'asc' : 'desc';
    } else {
      this.sortDirection = 'desc';
    }
    this.sortProperty = property;
  }

  /**
   * 이미지 클릭
   * @param history
   */
  public imageClick(history: Tag.History) {
    this.selectedImageId = history.dockerImageId;
    const tag = new Tag.Entity();
    tag.name = history.name;
    this.selectedTag = tag;
    this.showTagLayerPopup = true;
  }

  /**
   * revert/restore 이미지 클릭
   * @param history
   */
  public restoreImageClick(history: Tag.History, isDeleted: boolean) {
    this.selectedHistory = _.cloneDeep(history);
    this.selectedHistory.isDeleted = isDeleted;
    this.showRestoreImagePopup = true;
  }

  /**
   * restore image
   */
  public restoreImage() {
    this.loaderService.show.next(true);

    let tagName = this.selectedHistory.name;
    let imageId = this.selectedHistory.dockerImageId;
    let manifrestDigest = null;

    if (this.selectedHistory.isDeleted) {
      manifrestDigest = this.selectedHistory.manifestDigest;
    }

    this.tagService.restoreImage(this.orgName, this.repoName, tagName, imageId, manifrestDigest).then(result => {
      this.getHistory();
      this.showRestoreImagePopup = false;

      Alert.success(CommonConstant.MESSAGE.SUCCESS);
    }).catch(reason => {
      let body = JSON.parse(reason._body);
      Alert.error(body.message);

      this.loaderService.show.next(false);
    });
  }

  /**
   * 검색 clear 버튼 클릭
   */
  public clearSearchClick() {
    this.searchKey = '';
  }

  /**
   * history 조회
   */
  private getHistory() {
    this.loaderService.show.next(true);

    this.tagService.getHistory(this.orgName, this.repoName).then(result => {
      this.historyList = [];
      result.content.forEach((value, index) => {
        this.historyList.push(value);
        value.date = value.startTs;
        value.formattedDate = moment(value.startTs).format('YYYY-MM-DD HH:mm');

        if (value.endTs) {
          // moved 인 경우 삭제된 내용은 화면에 표시하지 않음
          let isMoved: boolean = false;
          result.content.forEach((v, i) => {
            if (v.name == value.name && v.startTs == value.endTs) {
              isMoved = true;
              return;
            }
          });

          if (!isMoved) {
            let history = new Tag.History();
            history.name = value.name;
            history.isDeleted = true;
            history.date = value.endTs;
            history.formattedDate = moment(value.endTs).format('YYYY-MM-DD HH:mm');
            history.beforeHistory = value;
            this.historyList.push(history);
          }
        }

        result.content.forEach((v, i) => {
          if (i > index && v.name == value.name && v.endTs >= value.startTs){
            value.isMoved = true;
            value.beforeHistory = v;
            return;
          }
        });
      });

      this.loaderService.show.next(false);
    });
  }

}
