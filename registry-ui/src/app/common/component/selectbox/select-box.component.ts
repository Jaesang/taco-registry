import {Component, ElementRef, EventEmitter, HostListener, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Select} from './select.value';
import {Utils} from '../../utils/utils';
import * as _ from 'lodash';

// noinspection SpellCheckingInspection
@Component({
  selector: '[select-box]',
  templateUrl: './select-box.component.html',
  host: {
    '[class.component-selectbox]': 'true',
    '[class.is-disabled]': 'isError'
  }
})
export class SelectBoxComponent implements OnInit, OnDestroy {

  /**
   * 기본 플레이스 홀더
   */
  private readonly DEFAULT_PLACEHOLDER: string = 'Please select.';

  /**
   * 유니크 아이디 생성에 접두어로 사용할 UUID
   */
  public readonly UUID = Utils.Generate.UUID();

  /**
   * 플레이스 홀더
   */
  @Input()
  public placeholder = this.DEFAULT_PLACEHOLDER;

  /**
   * 초기화시에만 사용할 수 있는 아이템 목록
   */
  @Input()
  public initializeValues: Select.Value[] = [];

  /**
   * 초기화시에만 사용할 수 있는 플래그 ( 전체 아이템을 사용여부 )
   */
  @Input()
  public initializeAllEnabled: boolean = false;

  @Input()
  public isMulti: boolean = false;

  /**
   *
   */
  @Output()
  public onSelected: EventEmitter<Select.Value> = new EventEmitter();

  /**
   *
   */
  @Output()
  public onSelectedValues: EventEmitter<Select.Value[]> = new EventEmitter();

  /**
   * 셀렉트 박스의 목록이 접혀있는지 구분하기 위한 값
   */
  public isFolding: boolean = true;

  /**
   * 선택된 셀렉트 박스 값
   */
  public selectedValue: Select.Value = null;

  /**
   * 선택된 셀렉트 박스 값
   */
  public selectedValues: Select.Value[] = [];

  public enableAll: boolean;

  public currentLabelClass: string;

  /**
   * 에러 상태
   */
  private isError: boolean = false;

  /**
   * 셀렉트 박스 아이템 목록
   */
  public selectValues: Select.Value[] = [];

  constructor(private elementRef: ElementRef) { }

  ngOnInit() {
    if (this.initializeValues.length > 0) {
      this.init(_.cloneDeep(this.initializeValues), this.initializeAllEnabled);
    }
  }

  ngOnDestroy(): void {
  }

  /**
   * 셀렉트 아이템 목록이 펼쳐져 있는 상태에서 외부 ( 도큐먼트 ) 를 클릭하면
   * 펼쳐져 있는 목록을 닫아준다.
   *
   * @param event
   */
  @HostListener('document:click', [ '$event' ])
  documentClick(event) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.isFolding = true;
    }
  }

  /**
   * 생성
   *
   * @param selectValues
   * @param enabledAll
   */
  public init(selectValues: Select.Value[], enabledAll: boolean = false, isMulti: boolean = false): void {

    this.isError = false;
    this.initialize();
    this.selectValues = selectValues;
    this.isMulti = isMulti;
    this.enableAll = enabledAll;
    if (enabledAll) {
      this.selectValues.unshift(Select.Value.ofAll(this.getCheckedSelectValues(this.selectValues).length <= 0));
      if (this.isMulti) {
        this.selectedValues = [this.selectValues[0]];
      }
    }
    if (this.validation(this.selectValues)) {
      return;
    }
    if (this.getCheckedSelectValues(this.selectValues).length === 1) {
      this.selectedValue = this.getCheckedSelectValue(this.selectValues);
      this.currentLabelClass = this.selectedValue.labelClass;
    }
  }

  /**
   * 펼치기
   */
  public folding(): void {

    if (this.isSelectValuesNotExist(this.selectValues)) {
      return;
    }

    this.isFolding = !this.isFolding;
  }

  // noinspection JSUnusedGlobalSymbols
  /**
   * 선택되어 있는 셀렉트 값을 가져오기
   *  - 값이 없는 경우 null 값 반환
   */
  public getSelectedValue(): Select.Value {
    return this.selectedValue;
  }

  /**
   * 셀렉트 박스 값 선택
   * @param value
   */
  public clickSelectValue(value: Select.Value): void {

    if (this.isMulti) {
      if (this.enableAll) {
        if (value.value.toString() == 'ALL') {
          if (value.checked) {
            return;
          }
          this.selectValues.forEach((value) => {
            if (value.value.toString() != 'ALL') {
              value.checked = false;
            }
          });
        } else {
          this.selectValues[ 0 ].checked = false;
        }
      }

      value.checked = !value.checked;

      let selectedItems = [];
      this.selectValues.forEach((value) => {
        if (value.checked) {
          selectedItems.push(value);
        }
      });
      this.selectedValues = selectedItems;

      this.onSelectedValues.emit(this.selectedValues);
    } else {
      if (this.selectedValue !== null) {
        this.selectedValue.checked = false;
      }

      value.checked = true;
      this.selectedValue = value;
      this.currentLabelClass = value.labelClass;
      this.isFolding = true;
      this.onSelected.emit(this.selectedValue);
    }
  }

  /**
   *
   * @returns {string}
   */
  public getSelectedClass() {
    let className = 'txt-label';
    if (this.currentLabelClass) {
      className += ' ' + this.currentLabelClass;
    }

    return className;
  }

  /**
   * 에러나는 경우 처리할 함수
   * @param message
   */
  private thrownError(message: string = ''): void {
    this.isError = true;
    if (!_.isEmpty(message)) {
      console.error('ERROR MSG', message);
      console.error('ERROR DATA', this.selectValues);
    }
    this.initialize();
  }

  /**
   * 초기화
   */
  private initialize(): void {
    this.selectValues = [];
    this.isFolding = true;
    this.selectedValue = null;
  }

  /**
   * 유효성 검증
   * @param selectValues
   */
  private validation(selectValues: Select.Value[]): boolean {
    if (this.isSelectValuesNotExist(selectValues)) {
      this.thrownError(`SelectBox item does not exist`);
      return true;
    }
    if (this.getCheckedSelectValues(selectValues).length > 0 && this.isOnlyOneItemIsNotChecked(selectValues)) {
      this.thrownError(`The selected item is not one.`);
      return true;
    }
    return false;
  }

  // noinspection JSMethodCanBeStatic
  /**
   * 셀렉트 박스의 바인딩될 목록이 존재하지 않는 경우
   */
  private isSelectValuesNotExist(selectValues: Select.Value[]): boolean {
    return selectValues.length === 0;
  }

  /**
   * 셀렉트 박스에 바인딩될 목록의 값중에 체크 표시된 값이 하나가 아닌 경우
   * @param selectValues
   */
  private isOnlyOneItemIsNotChecked(selectValues: Select.Value[]): boolean {
    return this.getCheckedSelectValues(selectValues).length !== 1;
  }

  // noinspection JSMethodCanBeStatic
  /**
   * 셀렉트박스 아이템에서 체크된 아이템 목록을 가져온다
   * @param selectValues
   */
  private getCheckedSelectValues(selectValues: Select.Value[]): Select.Value[] {
    return selectValues.filter(value => {
      return value.checked;
    });
  }

  /**
   * 셀렉트박스 아이템에서 체크된 아이템의 첫번째를 꺼낸다
   * @param selectValues
   */
  private getCheckedSelectValue(selectValues): Select.Value {
    return this.getCheckedSelectValues(selectValues)[ 0 ];
  }

}
