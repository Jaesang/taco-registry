import {Component, OnInit, ElementRef, Injector, Output, EventEmitter, Input, ViewChild} from "@angular/core";
import {AbstractComponent} from "../../../common/component/abstract.component";
import {Logs} from "./logs.value";
import {Select} from "../../../common/component/selectbox/select.value";
import {SelectBoxComponent} from "../../../common/component/selectbox/select-box.component";
import {LogsService} from "./logs.service";

@Component({
  selector: '[logs]',
  templateUrl: 'logs.component.html'
})
export class LogsComponent extends AbstractComponent implements OnInit {

  @ViewChild(SelectBoxComponent)
  private selectBox: SelectBoxComponent;

  public startEndDate: any;

  public startDate: Date;
  public endDate: Date;

  public searchKey: string;
  public searchKinds: Logs.Kind[] = [];
  public filteredList: Logs.Entity[] = [];

  public selectList: Select.Value[] = [];

  public _logList: Logs.Entity[] = [];

  @Input()
  public set logList(list: Logs.Entity[]) {
    this._logList = list;

    // kind 로 중복 제거
    let selectList = list.filter((item, i) => {
      return list.findIndex((item2, j) => {
          return item.kind === item2.kind;
        }) === i;
    });

    // select list values
    this.selectList = selectList.map(value => {
      return new Select.Value(value.kind.toString(), value.kind, false);
    });

    this.filteredList = this.logList.filter(value => {
      return (this.searchKinds.length > 0 && this.searchKinds.indexOf(value.kind) > -1) || this.searchKinds.length == 0;
    });

    this.selectBox.init(this.selectList, true, true);
  }

  public get logList() {
    return this._logList;
  }

  @Output()
  private onDateSelect: EventEmitter<Object> = new EventEmitter();

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              public logsService: LogsService) {

    super(elementRef, injector);
  }

  ngOnInit() {
    const endDate = new Date();
    const startDate = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate() - 7);
    const minDate = new Date(endDate.getFullYear(), endDate.getMonth() - 1, endDate.getDate());

    this.startEndDate = this.jQuery('#dateInput')
      .datepicker({
        language: 'en',
        autoClose: true,
        class: 'dtp-datepicker',
        dateFormat: 'yyyy-mm-dd',
        navTitles: { days: 'yyyy<span>년&nbsp;</span> MM' },
        onHide: function () {},
        position: 'bottom left',
        timepicker: false,
        toggleSelected: false,
        range: true,
        multipleDatesSeparator: ' ~ ',
        minDate: minDate,
        maxDate: endDate
      })
      .data('datepicker');

    const self = this;
    this.startEndDate.update('onSelect', function (formattedDate, date, inst) {
      self.startEndDateSelect();
    });

    this.startEndDate.selectDate([ startDate, endDate ]);
  }

  /**
   * 아이디 클릭
   * @param item
   */
  public performerClick(item: Logs.Entity) {
    if (item.performer) {
      this.router.navigate([`app/user/${item.performer.name}/image`]);
    }
  }

  /**
   * select value change
   * @param values
   */
  public selectValues(values: Select.Value[]) {
    if (values.length == 1 && values[0].value == 'ALL') {
      this.searchKinds = [];
    } else {
      this.searchKinds = values.map(value => {
        return value.value;
      });
    }

    this.filteredList = this.logList.filter(value => {
      return (this.searchKinds.length > 0 && this.searchKinds.indexOf(value.kind) > -1) || this.searchKinds.length == 0;
    });
  }

  /**
   * cliear search 클릭
   */
  public clearSearchClick() {
    this.searchKey = '';
  }

  /**
   * 날짜 선택
   */
  private startEndDateSelect() {
    const selectedDates = this.startEndDate.selectedDates;
    if (selectedDates.length > 1) {
      this.startDate = selectedDates[ 0 ];
      this.endDate = selectedDates[ 1 ];

      this.onDateSelect.emit({
        startDate: this.startDate,
        endDate: this.endDate
      });
    } else {
      this.endDate = null;
    }
  }

}
