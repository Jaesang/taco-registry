export namespace Lnb {
	/**
	 * POST 타입
	 */
	export enum LnbType {
		ORG = <any>'ORG',
		ORG_USER = <any>'ORG_USER',
		REPO = <any>'REPO',
    ADMIN = <any>'ADMIN'
	}

	export class Entity {
	  public lnbType: LnbType;
	  public pathVariable: string;
	  public index: number;
  }

}
