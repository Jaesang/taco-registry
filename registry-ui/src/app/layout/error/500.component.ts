import {Component, OnInit, ElementRef, Injector} from '@angular/core';
import {PageComponent} from "../../common/component/page.component";

@Component({
  selector: 'error-500',
  templateUrl: './500.component.html'
})
export class Error500Component extends PageComponent implements OnInit {

  constructor(protected elementRef: ElementRef,
              protected injector: Injector) {

    super(elementRef, injector);

    this.commonService.showLnb.next(false);
  }

  ngOnInit() {
  }

  public mainPageClick() {
    this.router.navigate(['app/main']);
  }

}
