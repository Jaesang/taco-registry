import * as _ from 'lodash';
import {Component, OnInit, ElementRef, Injector, Input} from '@angular/core';
import {RepositoryService} from "../../repository/repository.service";
import {Repository} from "../../repository/repository.value";
import {AbstractComponent} from "../../../common/component/abstract.component";

export class RepositoryListBaseComponent extends AbstractComponent implements OnInit {

  @Input()
  public searchKey: string = '';

  public starredList: Repository.Entity[] = [];

  private _repositoryList: Repository.Entity[] = [];

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              protected repositoryService: RepositoryService) {

    super(elementRef, injector);
  }

  @Input()
  public set repositoryList(list: Repository.Entity[]) {
    this._repositoryList = list;

    this.changeRepositoryList();
  }

  public get repositoryList(): Repository.Entity[] {
    return this._repositoryList;
  }

  ngOnInit() {
  }

  /**
   * 별 추가/삭제
   * @param value
   * @param namespace
   * @param repository
   */
  protected changeStarred(value: boolean, namespace: string, repository: string) {
    if (value) {
      this.repositoryService.starredRepository(namespace, repository).then(result => {

      });
    } else {
      this.repositoryService.deleteStarredRepository(namespace, repository).then(result => {

      });
    }

  }

  /**
   * 상세 페이지 이동
   * @param namespace
   * @param repository
   */
  protected moveToDetail(namespace: string, repository: string) {
    this.router.navigate([`app/image/${namespace}/${repository}/info`]);
  }

  /**
   * repository list 변경 시 호출
   */
  protected changeRepositoryList() {

  }

}
