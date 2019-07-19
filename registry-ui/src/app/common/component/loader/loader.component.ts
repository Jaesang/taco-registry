import {Component, ElementRef, OnInit, Renderer, Injector} from '@angular/core';
import {AbstractComponent} from "../abstract.component";

@Component({
  selector: '[loader]',
  templateUrl: './loader.component.html'
})
export class LoaderComponent extends AbstractComponent implements OnInit {

  public show: boolean;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector) {
    super(elementRef, injector);
  }

  ngOnInit() {

    this.subscriptions.push(
      this.loaderService.show.subscribe(
        value => {
          this.show = value;
        }
      )
    );

  }

}
