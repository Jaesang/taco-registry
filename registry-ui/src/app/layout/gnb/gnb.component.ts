import {Component, OnInit, Injector, ElementRef} from '@angular/core';
import {AbstractComponent} from "../../common/component/abstract.component";
import {Autocomplete} from "../../common/component/autocomplete/autocomplete.value";
import {MainService} from "../main/main.service";
import {Main} from "../main/main.value";
import {Validate} from "../../common/utils/validate-util";
import {UserService} from "../user/user.service";
import {User} from "../user/user.value";

@Component({
  selector: 'gnb',
  templateUrl: './gnb.component.html'
})
export class GnbComponent extends AbstractComponent implements OnInit {

  public AutocompleteType: typeof Autocomplete.Type = Autocomplete.Type;

  public searchKey: string;

  public matchList: Autocomplete.Entity[] = [];

  public isAdmin: boolean = false;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private mainService: MainService,
              private userService: UserService) {

    super(elementRef, injector);

    this.isAdmin = this.userService.user.superuser;
  }

  ngOnInit() {

    this.subscriptions.push(
      this.commonService.changeSearchText.subscribe(
        value => {
          this.searchKey = value;
        }
      )
    );
  }

  /**
   * quick search
   * @param text
   */
  public searchEvent(text: string) {
    this.mainService.getAllList(text).then(result => {
      let list = result.content.map(value => {
        let type: Autocomplete.ItemType;
        let name: string = value.name;
        if (value.kind == Main.Type.organization) {
          type = Autocomplete.ItemType.ORG;
        } else if (value.kind == Main.Type.repository) {
          type = Autocomplete.ItemType.REPO;
          name = `${value.namespace.name}/${value.name}`;
        } else {
          type = Autocomplete.ItemType.USER;
        }
        return new Autocomplete.Entity(name, value, type);
      });
      this.matchList = list;
    });
  }

  /**
   * enter search
   * @param text
   */
  public searchEnterEvent(text: string) {
    this.searchKey = text;
    let q;
    if (!Validate.isEmpty(this.searchKey)) {
      q = {queryParams: {query: this.searchKey}};
    }

    this.router.navigate([`app/main`], q);
  }

  /**
   * autocomplete item click
   * @param value
   */
  public searchAutocompleteSelect(value: Main.Entity) {
    if (value.kind == Main.Type.organization) {
      this.router.navigate([`app/organization/${value.name}`]);
    } else if (value.kind == Main.Type.repository) {
      this.router.navigate([`app/image/${value.namespace.name}/${value.name}`]);
    } else {
      this.router.navigate([`app/user/${value.name}`]);
    }
  }

  /**
   * admin 클릭
   */
  public adminClick() {
    this.router.navigate(['app/admin']);
  }

  /**
   * logo 클릭
   */
  public logoClick() {
    this.router.navigate(['app/main'])
  }

}
