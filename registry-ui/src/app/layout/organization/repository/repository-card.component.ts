import {Component, OnInit, ElementRef, Injector} from "@angular/core";
import {RepositoryService} from "../../repository/repository.service";
import {RepositoryListBaseComponent} from "./repository-list-base.component";
import {Repository} from "../../repository/repository.value";

@Component({
  selector: 'repository-card',
  templateUrl: 'repository-card.component.html'
})
export class RepositoryCardComponent extends RepositoryListBaseComponent implements OnInit {

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              protected repositoryService: RepositoryService) {

    super(elementRef, injector, repositoryService);
  }

  ngOnInit() {
  }

  /**
   * repository list 변경
   */
  protected changeRepositoryList() {
    this.setStarredList();
  }

  /**
   * 별 체크 변경
   * @param item
   */
  public starChange(item: Repository.Entity) {
    item.is_starred = !item.is_starred;
    this.changeStarred(item.is_starred, item.namespace, item.name);

    this.setStarredList();
  }

  /**
   * 별 목록 세팅
   */
  private setStarredList() {
    this.starredList = [];
    if (this.repositoryList) {
      this.repositoryList.forEach(value => {
        if (value.is_starred) {
          this.starredList.push(value);
        }
      });
    }
  }

}
