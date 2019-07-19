export namespace Autocomplete {

  export enum Type {
    ALL = <any> 'ALL',
    MEMBER = <any> 'MEMBER'
  }

  export enum ItemType {
    USER = <any> 'USER',
    ORG = <any> 'ORG',
    REPO = <any> 'IMG',
  }

  export class Entity {

    public label: string;
    public value: object;
    public matchText: string;
    public type: ItemType;

    constructor(label: string, value: any, type: ItemType = ItemType.USER) {
      this.label = label;
      this.value = value;
      this.type = type;
    }

  }

}
