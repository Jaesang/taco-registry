import {OnInit, ElementRef, Injector, OnDestroy} from '@angular/core';
import {AbstractComponent} from "./abstract.component";

export class PageComponent extends AbstractComponent implements OnInit, OnDestroy {

  constructor(protected elementRef: ElementRef,
              protected injector: Injector) {

    super(elementRef, injector);
  }

  ngOnInit() {
  }

  ngOnDestroy() {
    super.ngOnDestroy();

    this.commonService.showLnb.next(true);
  }

}
