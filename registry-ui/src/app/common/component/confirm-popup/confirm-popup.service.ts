import {Injectable} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {Modal} from './confirm.value';
import {Observable} from 'rxjs/Observable';

/**
 * 공통 컨펌 모달 서비스
 */
@Injectable()
export class ConfirmPopupService {

  private readonly notification: Subject<Modal.Confirm> = new Subject();

  public readonly notification$: Observable<Modal.Confirm> = this.notification.asObservable();

  constructor() { }

  /**
   * 컨펌 팝업 열기
   *
   * @param title
   * @param content
   * @param cancelCallBackFunction
   * @param doneCallBackFunction
   * @param cancelButtonLabel
   * @param doneButtonLabel
   */
  public show(title: string,
              content: string,
              accentContent: string,
              cancelCallBackFunction: () => void,
              doneCallBackFunction: () => void,
              cancelButtonLabel: string = Modal.Confirm.DEFAULT_CANCEL_BUTTON_LABEL,
              doneButtonLabel: string = Modal.Confirm.DEFAULT_DONE_BUTTON_LABEL) {
    this.notification.next(
      ConfirmPopupService.map(title, content, accentContent, cancelButtonLabel, doneButtonLabel, cancelCallBackFunction, doneCallBackFunction)
    );
  }

  /**
   * 컨펌 팝업 열기
   *
   * @param title
   * @param content
   * @param cancelCallBackFunction
   * @param doneCallBackFunction
   * @param cancelButtonLabel
   * @param doneButtonLabel
   */
  public showAlert(title: string,
              content: string,
              accentContent: string,
              doneCallBackFunction: () => void,
              doneButtonLabel: string = Modal.Confirm.DEFAULT_DONE_BUTTON_LABEL) {
    this.notification.next(
      ConfirmPopupService.map(title, content, accentContent, null, doneButtonLabel, null, doneCallBackFunction, true)
    );
  }

  /**
   * 매핑
   *
   * @param title
   * @param content
   * @param cancelButtonLabel
   * @param doneButtonLabel
   * @param cancelCallBackFunction
   * @param doneCallBackFunction
   */
  private static map(title: string,
                     content: string,
                     accentContent: string,
                     cancelButtonLabel: string,
                     doneButtonLabel: string,
                     cancelCallBackFunction: () => void,
                     doneCallBackFunction: () => void,
                     isAlert: boolean = false) {
    const data = new Modal.Confirm();
    data.title = title;
    data.content = content;
    data.accentContent = accentContent;
    data.cancelButtonLabel = cancelButtonLabel;
    data.doneButtonLabel = doneButtonLabel;
    data.cancelCallBackFunction = cancelCallBackFunction;
    data.doneCallBackFunction = doneCallBackFunction;
    data.isAlert = isAlert;
    return data;
  }
}
