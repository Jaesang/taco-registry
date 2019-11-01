import {Component, ElementRef, Injector, Input, OnDestroy, OnInit, Output, EventEmitter} from "@angular/core";
import {AbstractComponent} from "../abstract.component";
import {Page} from "../../value/result-value";

@Component({
  selector: '[pagination]',
  templateUrl: 'pagination.component.html',
})
export class PaginationComponent extends AbstractComponent implements OnInit, OnDestroy {

  public page: Page = new Page();

  @Input()
  public showElements: boolean = false;

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
    if (this.page.number > 0) {
      this.onPageClick.emit(this.page.number - 1);
    }
  }

  /**
   * 다음 클릭
   */
  public nextClick() {
    if (!this.page.last) {
      this.onPageClick.emit(this.page.number + 1);
    }
  }

}
