import {Injectable} from "@angular/core";
import {BehaviorSubject} from "rxjs";

/**
 * 공통 컨펌 모달 서비스
 */
@Injectable()
export class LoaderService {

  public show: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor() { }

}
