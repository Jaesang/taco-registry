/**
 * Modal
 */
export namespace Modal {

  /**
   * Confirm
   */
  export class Confirm {

    public static readonly DEFAULT_CANCEL_BUTTON_LABEL: string = 'Cancel';
    public static readonly DEFAULT_DONE_BUTTON_LABEL: string = 'OK';
    public static readonly EMPTY: string = '';
    public static readonly DEFAULT_TITLE: string = Confirm.EMPTY;
    public static readonly DEFAULT_CONTENT: string = '';

    public title: string;
    public content: string;
    public accentContent: string;
    public cancelCallBackFunction: Function;
    public doneCallBackFunction: Function;
    public cancelButtonLabel: string;
    public doneButtonLabel: string;
    public isAlert: boolean;

    constructor() {
    }

  }

}
