import {Component, ElementRef, Injector, Input, OnDestroy, OnInit, Output, EventEmitter} from "@angular/core";
import {AbstractComponent} from "../abstract.component";
import {Page} from "../../value/result-value";

@Component({
  selector: '[pagination]',
  templateUrl: 'pagination.component.html',
})
export class PaginationComponent extends AbstractComponent implements OnInit, OnDestroy {

  public page: Page = new Page();

  @Output()
  public onPageClick: EventEmitter<number> = new EventEmitter();

  constructor(protected elementRef: ElementRef,
              protected injector: Injector) {
    super(elementRef, injector);
  }

  ngOnInit() {
  }

  ngOnDestroy(): void {
  }

  public init(page: Page) {
    this.page = page;
  }

  /**
   * 이전 클릭
   */
  public prevClick() {
    if (this.page.page > 1) {
      this.onPageClick.emit(this.page.page - 1);
    }
  }

  /**
   * 다음 클릭
   */
  public nextClick() {
    if (this.page.has_additional) {
      this.onPageClick.emit(this.page.page + 1);
    }
  }

}
