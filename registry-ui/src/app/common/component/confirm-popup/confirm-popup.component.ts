import {Component, ElementRef, OnDestroy, OnInit, Renderer} from '@angular/core';
import {ConfirmPopupService} from './confirm-popup.service';
import {Modal} from './confirm.value';
import {Subscription} from 'rxjs/Subscription';
import * as _ from 'lodash';

@Component({
  selector: '[confirm-popup]',
  templateUrl: './confirm-popup.component.html',
  host: {
    '[class.layout-popup]': 'true'
  }
})
export class ConfirmPopupComponent implements OnInit, OnDestroy {

  private subscription: Subscription;

  public data: Modal.Confirm;

  public isAlert: boolean = false;

  constructor(private elementRef: ElementRef,
              private render: Renderer,
              private confirmPopupService: ConfirmPopupService) {
  }

  ngOnInit() {

    this.subscription = this.confirmPopupService
      .notification$
      .subscribe((data: Modal.Confirm) => {
        this.isAlert = data.isAlert;
        this.confirm(data);
      });

    this.hide();
  }

  ngOnDestroy(): void {
    if (this.subscription !== null) {
      this.subscription.unsubscribe();
    }
  }

  public cancel() {
    this.data.cancelCallBackFunction.call(this);
    this.hide();
  }

  public done() {
    this.data.doneCallBackFunction.call(this);
    this.hide();
  }

  public  close() {
    this.hide();
  }

  /**
   * 컨펌 팝업 실행
   *
   * @param data
   */
  private confirm(data: Modal.Confirm) {
    this.data = ConfirmPopupComponent.map(data);
    this.show();
  }

  private show() {
    this.render.setElementStyle(this.elementRef.nativeElement, 'display', 'block');
  }

  private hide() {
    this.render.setElementStyle(this.elementRef.nativeElement, 'display', 'none');
  }

  /**
   * 컨펌 데이터 매핑
   * @param confirmData
   */
  private static map(confirmData: Modal.Confirm): Modal.Confirm {
    const data = new Modal.Confirm();
    data.title = _.isNil(confirmData.title) ? Modal.Confirm.DEFAULT_TITLE : confirmData.title;
    data.content = _.isNil(confirmData.content) ? Modal.Confirm.DEFAULT_CONTENT : confirmData.content;
    data.accentContent = _.isNil(confirmData.accentContent) ? null : confirmData.accentContent;
    data.cancelButtonLabel = _.isNil(confirmData.cancelButtonLabel) ? Modal.Confirm.DEFAULT_CANCEL_BUTTON_LABEL : confirmData.cancelButtonLabel;
    data.doneButtonLabel = _.isNil(confirmData.doneButtonLabel) ? Modal.Confirm.DEFAULT_DONE_BUTTON_LABEL : confirmData.doneButtonLabel;
    data.cancelCallBackFunction = confirmData.cancelCallBackFunction;
    data.doneCallBackFunction = confirmData.doneCallBackFunction;
    return data;
  }

}
