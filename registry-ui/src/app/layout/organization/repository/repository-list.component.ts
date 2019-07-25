import {Component, OnInit, ElementRef, Injector, Input} from '@angular/core';
import {RepositoryListBaseComponent} from "./repository-list-base.component";
import {Repository} from "../../repository/repository.value";
import {RepositoryService} from "../../repository/repository.service";
import {Main} from "../../main/main.value";

@Component({
  selector: 'repository-list',
  templateUrl: 'repository-list.component.html'
})
export class RepositoryListComponent extends RepositoryListBaseComponent implements OnInit {

  public sortProperty: string;
  public sortDirection: string;

  public MainType: typeof Main.Type = Main.Type;

  @Input()
  public namespaceType: Main.Type;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              protected repositoryService: RepositoryService) {

    super(elementRef, injector, repositoryService);
  }

  ngOnInit() {
  }

  /**
   * 별 체크 변경
   * @param item
   */
  public starChange(item: Repository.Entity) {
    item.isStarred = !item.isStarred;
    this.changeStarred(item.isStarred, item.namespace, item.name);
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
}
