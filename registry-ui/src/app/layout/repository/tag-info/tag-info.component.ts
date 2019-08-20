import {Component, OnInit, ElementRef, Injector, ViewChild} from '@angular/core';
import {PageComponent} from "../../../common/component/page.component";
import {RepositoryService} from "../repository.service";
import {Tag} from "./tag.value";
import {TagService} from "./tag.service";
import * as _ from 'lodash';
import * as moment from "moment";
import {ConfirmPopupService} from "../../../common/component/confirm-popup/confirm-popup.service";
import {Alert} from "../../../common/utils/alert-util";
import {Select} from "../../../common/component/selectbox/select.value";
import {SelectBoxComponent} from "../../../common/component/selectbox/select-box.component";
import {environment} from "../../../../environments/environment";
import {Utils} from "../../../common/utils/utils";
import {CommonConstant} from "../../../common/constant/common-constant";

@Component({
  selector: 'tag-info',
  templateUrl: 'tag-info.component.html'
})
export class TagInfoComponent extends PageComponent implements OnInit {

  @ViewChild('fetchPopupSelect')
  private fetchPopupSelect: SelectBoxComponent;

  public orgName: string;
  public repoName: string;

  public sortProperty: string = 'formattedLastModified';
  public sortDirection: string = 'desc';

  public searchKey: string;

  public tagList: Tag.Entity[] = [];

  public securityData: Object;

  public currentSettingIndex: number = -1;
  public currentSelectedTag: Tag.Entity = new Tag.Entity();
  public currentSelectedTagList: Tag.Entity[] = [];

  public showManifestPopup: boolean = false;
  public showCreateTagPopup: boolean = false;
  public showDeleteTagPopup: boolean = false;
  public showChangeExpirationPopup: boolean = false;
  public showFetchTagPopup: boolean = false;

  public showTagLayerPopup: boolean = false;
  public showTagSecurityPopup: boolean = false;

  public createTag: Tag.Entity = new Tag.Entity();
  public createErrorMsg: string;

  public fetchPullPath: string;
  public fetchSelectData: Select.Value[] = [
    new Select.Value('Docker Pull (by Tag)', 0, true),
    new Select.Value('Docker Pull (by Digest)', 1, false)
  ];

  public selectExpirationDate: Date;
  public expirationDate: any;
  public selectedExpirationType: number;

  public selectedImageId: string;

  public writer: boolean = false;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private tagService: TagService,
              private confirmPopupService: ConfirmPopupService,
              private repositoryService: RepositoryService) {

    super(elementRef, injector);
  }

  ngOnInit() {

    const minDate = new Date();

    this.expirationDate = this.jQuery('#dateInput')
      .datepicker({
        language: 'en',
        autoClose: true,
        class: 'dtp-datepicker',
        dateFormat: 'yyyy-mm-dd',
        navTitles: { days: 'yyyy<span>년&nbsp;</span> MM' },
        onHide: function () {},
        position: 'bottom left',
        timepicker: true,
        minDate: minDate
      })
      .data('datepicker');

    // const self = this;
    // this.expirationDate.update('onSelect', function (formattedDate, date, inst) {
    //
    //   // self.startEndDateSelect();
    // });

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

          this.securityData = {};
          this.getTagList();
        })
    );

    this.subscriptions.push(
      this.commonService.lnbWriter.subscribe(
        value => {
          this.writer = value;
        }
      )
    );
  }

  /**
   * 전체 체크 변경
   */
  public allCheckChange(e) {
    let checked = e.target.checked;
    this.tagList.forEach(value => {
      value.checked = checked;
    });
  }

  /**
   * check 변경
   * @param tag
   */
  public checkChange(tag: Tag.Entity) {
    this.tagList.forEach(value => {
      if (tag.name == value.name) {
        value.checked = !value.checked;
        return;
      }
    });
  }

  /**
   * check 상태 표시
   * @param tag
   * @returns {boolean}
   */
  public checkStatus(tag: Tag.Entity) {
    let checked = false;
    this.tagList.forEach(value => {
      if (tag.name == value.name) {
        checked = value.checked;
        return;
      }
    });

    return checked;
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

  public clearSearchClick() {
    this.searchKey = '';
  }

  /**
   * scan 클릭
   * @param tag
   */
  public scanClick(tag: Tag.Entity) {
    this.currentSelectedTag = tag;
    this.selectedImageId = tag.dockerImageId;
    this.showTagSecurityPopup = true;
  }

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   | Image
   |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  /**
   * manifest 클릭(팝업)
   * @param tag
   */
  public manifestClick(tag: Tag.Entity) {
    this.currentSelectedTag = tag;
    this.showManifestPopup = true;
  }

  /**
   * manifest copy 클릭
   */
  public manifestCopyClick() {
    Utils.StringUtil.copyToClipboard(this.currentSelectedTag.manifestDigest);
    Alert.success(CommonConstant.MESSAGE.COPIED);
  }

  /**
   * image 클릭
   * @param tag
   */
  public imageClick(tag: Tag.Entity) {
    this.currentSelectedTag = tag;
    this.selectedImageId = tag.dockerImageId;
    this.showTagLayerPopup = true;
  }

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   | // Image
   |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   | Fetch
   |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  /**
   * fetch tag 클릭(팝업)
   * @param tag
   */
  public fetchTagClick(tag: Tag.Entity) {
    this.currentSettingIndex = -1;
    this.currentSelectedTag = tag;
    this.showFetchTagPopup = true;
    this.fetchPullPath = '';
    this.fetchPopupSelect.init(_.cloneDeep(this.fetchSelectData));
    this.fetchSelect(this.fetchSelectData[0]);
  }

  /**
   * fetch select change
   * @param item
   */
  public fetchSelect(item: Select.Value) {
    if (item.value == 0) {
      this.fetchPullPath = `docker pull ${environment.hostName}/${this.orgName}/${this.repoName}:${this.currentSelectedTag.name}`;
    } else {
      this.fetchPullPath = `docker pull ${environment.hostName}/${this.orgName}/${this.repoName}@${this.currentSelectedTag.manifestDigest}`;
    }
  }

  /**
   * fetch copy command 클릭
   */
  public fetchCopyCommandClick() {
    Utils.StringUtil.copyToClipboard(this.fetchPullPath);
    Alert.success(CommonConstant.MESSAGE.COPIED);
  }

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   | // Fetch
   |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   | Setting
   |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  /**
   * setting 클릭
   * @param index
   */
  public settingClick(index: number) {
    if (this.currentSettingIndex == index) {
      this.currentSettingIndex = -1;
    } else {
      this.currentSettingIndex = index;
    }
  }

  /**
   * add new tag 클릭(팝업)
   * @param tag
   */
  public addNewTagPopupClick(tag: Tag.Entity) {
    this.currentSettingIndex = -1;
    this.currentSelectedTag = tag;
    this.showCreateTagPopup = true;
    this.createTag = new Tag.Entity();
    this.createTag.dockerImageId = tag.dockerImageId;
    this.createErrorMsg = '';
  }

  /**
   * add new tag
   */
  public addTagClick() {
    this.createErrorMsg = '';
    this.tagService.createTag(this.orgName, this.repoName, this.createTag).then(result => {
      this.getTagList();
      this.showCreateTagPopup = false;
    }).catch(reason => {
      let body = JSON.parse(reason._body);
      this.createErrorMsg = body.message;
    });
  }

  /**
   * delete tag 클릭(팝업)
   * @param tag
   */
  public deleteTagPopupClick(tag: Tag.Entity) {
    this.currentSettingIndex = -1;
    this.showDeleteTagPopup = true;
    this.currentSelectedTagList = [tag];
  }

  /**
   * delete tag
   */
  public deleteTagClick() {

    if (this.currentSelectedTagList.length) {
      this.loaderService.show.next(true);
    }

    let count = 0;
    this.currentSelectedTagList.forEach(value => {
      this.tagService.deleteTag(this.orgName, this.repoName, value.name).then(result => {
        count++;

        if (this.currentSelectedTagList.length == count) {
          this.loaderService.show.next(false);
          this.getTagList();

          this.showDeleteTagPopup = false;

          Alert.success(CommonConstant.MESSAGE.DELETED);
        }
      }).catch(reason => {
        let body = JSON.parse(reason._body);
        Alert.error(body.message);

        this.loaderService.show.next(false);
      });
    });
  }

  /**
   * change expiration 클릭(팝업)
   * @param tag
   */
  public changeExpirationPopupClick(tag: Tag.Entity) {
    this.currentSettingIndex = -1;
    this.currentSelectedTagList = [tag];
    if (tag.expiration) {
      this.selectedExpirationType = 1;
      this.expirationDate.selectDate(new Date(tag.expiration));
    } else {
      this.selectedExpirationType = 0;
      this.expirationDate.clear();
    }

    this.showChangeExpirationPopup = true;
  }

  public changeExpirationClick() {
    if (this.currentSelectedTagList.length) {
      this.loaderService.show.next(true);
    }

    let count = 0;
    this.currentSelectedTagList.forEach(value => {
      let tag = new Tag.Entity();
      tag.expiration = this.selectedExpirationType ? Math.round(this.expirationDate.selectedDates[0].getTime()) : null;
      tag.name = value.name;

      this.tagService.updateTag(this.orgName, this.repoName, tag).then(result => {
        count++;

        if (this.currentSelectedTagList.length == count) {
          this.loaderService.show.next(false);
          this.getTagList();

          this.showChangeExpirationPopup = false;

          Alert.success(CommonConstant.MESSAGE.SUCCESS);
        }
      }).catch(reason => {
        let body = JSON.parse(reason._body);
        Alert.error(body.message);

        this.loaderService.show.next(false);
      });
    });
  }

  /*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   | // Setting
   |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

  /**
   * tag 다중 삭제 클릭
   */
  public deleteTags() {
    this.currentSelectedTagList = [];
    this.tagList.forEach(value => {
      if (value.checked) {
        this.currentSelectedTagList.push(_.cloneDeep(value));
      }
    });

    if (!this.currentSelectedTagList.length) {
      return;
    }

    this.showDeleteTagPopup = true;
  }

  /**
   * tag 다중 만료일 변경 클릭
   */
  public changeExpirationTags() {
    this.currentSelectedTagList = [];
    this.tagList.forEach(value => {
      if (value.checked) {
        this.currentSelectedTagList.push(_.cloneDeep(value));
      }
    });

    if (!this.currentSelectedTagList.length) {
      return;
    }

    this.selectedExpirationType = 0;
    this.expirationDate.clear();
    this.showChangeExpirationPopup = true;
  }

  /**
   * 태그 목록 조회
   */
  private getTagList() {
    this.loaderService.show.next(true);

    this.repositoryService.getRepository(`${this.orgName}/${this.repoName}`).then(result => {
      this.tagList = [];

      for (var key in result.tags) {
        let tag = result.tags[key];
        tag.formattedLastModified = moment(tag.lastModified).format('YYYY-MM-DD HH:mm');
        if (tag.expiration) {
          tag.formattedExpiration = moment(tag.expiration).format('YYYY-MM-DD HH:mm');
        } else {
          tag.formattedExpiration = '';
        }
        this.tagList.push(tag)
      }

      this.loaderService.show.next(false);

      // dockerImageId 로 중복 제거
      var imageList = _.uniqBy(this.tagList, 'dockerImageId');
      imageList.forEach(value => {
        this.getSecurityData(value.dockerImageId);
      });
    });
  }

  /**
   * security scan 조회
   * @param imageId
   */
  private getSecurityData(imageId: string) {
    this.loaderService.show.next(true);

    this.tagService.getSecurity(this.orgName, this.repoName, imageId).then(result => {
      this.tagService.setSecurityCount(result);

      this.securityData[imageId] = result;

      this.loaderService.show.next(false);
    });
  }

  /**
   * image id 로 security data 가져오기
   * @param imageId
   * @returns {any}
   */
  public getSecurity(imageId: string) {
    let security = this.securityData[imageId];
    if (!security) {
      security = new Tag.Security();
    }

    return security;
  }

}
