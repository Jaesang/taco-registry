export namespace Select {

  export class Value {

    public label: string;
    public value: any;
    public checked: boolean;
    public desc: string;
    public labelClass: string;

    constructor(label: string, value: any, checked: boolean, desc: string = null, labelClass: string = null) {
      this.label = label;
      this.value = value;
      this.checked = checked;
      this.desc = desc;
      this.labelClass = labelClass;
    }

    public static ofAll(isChecked: boolean): Value {
      return new Value('ALL', 'ALL', isChecked);
    }

  }

}
