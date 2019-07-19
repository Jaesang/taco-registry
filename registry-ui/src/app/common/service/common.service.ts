import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {Lnb} from '../../layout/lnb/lnb.value';

@Injectable()
export class CommonService {

  public showLnb: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);

  public lnb: BehaviorSubject<Lnb.Entity> = new BehaviorSubject<Lnb.Entity>(new Lnb.Entity());

  public lnbWriter: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);

  public lnbAdmin: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);

  public lnbMember: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);

  public changeSearchText: BehaviorSubject<string> = new BehaviorSubject<string>('');

  public logedIn: boolean = false;

  public clearInputValue(inputElement: HTMLInputElement) {
    inputElement.value = '';
  }

}
