import {Component, OnInit, ElementRef, Injector, ViewChild} from '@angular/core';
import {AbstractComponent} from "../common/component/abstract.component";
import {SelectBoxComponent} from "../common/component/selectbox/select-box.component";
import {Select} from "../common/component/selectbox/select.value";

@Component({
  selector: 'app-test',
  templateUrl: './test.component.html'
})
export class TestComponent extends AbstractComponent implements OnInit {

  @ViewChild('select1')
  private selectBox: SelectBoxComponent;

  @ViewChild('select2')
  private selectBox2: SelectBoxComponent;

  public minMaxDate: any;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector) {

    super(elementRef, injector);
  }

  ngOnInit() {
    this.selectBox.init([
      new Select.Value('label1', 'label1', false),
      new Select.Value('label2', 'label2', false),
      new Select.Value('label3', 'label3', false)
    ], true, true);

    this.selectBox2.init([
      new Select.Value('label1', 'label1', false),
      new Select.Value('label2', 'label2', false),
      new Select.Value('label3', 'label3', false)
    ], true, false);

    this.minMaxDate = this.jQuery('#minMaxDate')
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
        onSelect: function (formattedDate, date, inst) {
          console.log(formattedDate);
        }
      })
      .data('datepicker');
  }


  public selectedValues(items: Select.Value[]) {
    let result = '';
    items.forEach(value => {
      result += value.value + '\n';
    })

    alert(result);
  }

  public selectedValue(item: Select.Value) {
    alert(item.value);
  }
}
