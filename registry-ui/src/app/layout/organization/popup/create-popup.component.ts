import {
  Component, OnInit, ElementRef, Injector, Input, Output, EventEmitter, OnChanges,
  SimpleChanges
} from "@angular/core";
import {UserService} from "../../user/user.service";
import {AbstractComponent} from "../../../common/component/abstract.component";
import {OrganizationService} from "../organization.service";
import {Alert} from "../../../common/utils/alert-util";
import {CommonConstant} from "../../../common/constant/common-constant";

@Component({
  selector: '[create-org-popup]',
  templateUrl: 'create-popup.component.html'
})
export class CreatePopupComponent extends AbstractComponent implements OnInit, OnChanges {

  @Input()
  public show: boolean = false;

  @Output()
  public onClose: EventEmitter<any> = new EventEmitter();

  public orgName: string;
  public errorMsg: string = '';

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private orgranizationService: OrganizationService,
              private userService: UserService) {

    super(elementRef, injector);
  }

  ngOnInit() {

  }

  public ngOnChanges(changes: SimpleChanges): void {
    for (let propName in changes) {
      if (propName === 'show') {
        this.errorMsg = '';
        this.orgName = '';
      }
    }
  }

  public close() {
    this.show = false;
    this.onClose.emit();
  }

  public createOrgClick() {
    this.errorMsg = '';

    this.orgranizationService.createOrganization(this.orgName).then(result => {
      this.close();

      this.userService.changeOrganizationList.next(true);

      this.router.navigate([`app/organization/${this.orgName}`]);

      Alert.success(CommonConstant.MESSAGE.SUCCESS);
    }).catch(reason => {
      let body = JSON.parse(reason._body);
      this.errorMsg = body.detail;
    });
  }

}
