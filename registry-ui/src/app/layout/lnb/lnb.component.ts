import {Component, OnInit, ElementRef, Injector, OnDestroy} from '@angular/core';
import {AbstractComponent} from "../../common/component/abstract.component";
import {Lnb} from "./lnb.value";

@Component({
  selector: 'lnb',
  templateUrl: './lnb.component.html'
})
export class LnbComponent extends AbstractComponent implements OnInit, OnDestroy {

  public LnbType: typeof Lnb.LnbType = Lnb.LnbType;

  public currentLnbType: Lnb.LnbType = Lnb.LnbType.ORG;

  public selectedIndex: number = 0;

  public show: boolean = true;
  public pathVariable: string = '';

  public mine: boolean = true;

  public admin: boolean = true;
  public writer: boolean = true;
  public member: boolean = true;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector) {

    super(elementRef, injector);

    this.subscriptions.push(
      this.commonService.showLnb.subscribe(
        value => {
          this.show = value;
        }
      )
    );

    this.subscriptions.push(
      this.commonService.lnb.subscribe(
        value => {
          this.currentLnbType = value.lnbType;
          this.pathVariable = value.pathVariable;
          this.selectedIndex = value.index;
        }
      )
    );

    this.subscriptions.push(
      this.commonService.lnbWriter.subscribe(
        value => {
          this.writer = value;
        }
      )
    );

    this.subscriptions.push(
      this.commonService.lnbAdmin.subscribe(
        value => {
          this.admin = value;
        }
      )
    );

    this.subscriptions.push(
      this.commonService.lnbMember.subscribe(
        value => {
          this.member = value;
        }
      )
    );
  }

  ngOnInit() {
  }

}
