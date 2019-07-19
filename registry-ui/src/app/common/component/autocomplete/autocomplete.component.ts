import {
  Component, ElementRef, Injector, Input, OnDestroy, OnInit, Output, EventEmitter,
  ViewChild, HostListener
} from "@angular/core";
import {AbstractComponent} from "../abstract.component";
import {Autocomplete} from "./autocomplete.value";
import {Subject, Observable} from "rxjs";

@Component({
  selector: '[autocomplete]',
  templateUrl: 'autocomplete.component.html',
})
export class AutocompleteComponent extends AbstractComponent implements OnInit, OnDestroy {
  @ViewChild('searchInput')
  private searchInput: ElementRef;

  @Input()
  public type: Autocomplete.Type;

  public AutocompleteType: typeof Autocomplete.Type = Autocomplete.Type;

  public AutocompleteItemType: typeof Autocomplete.ItemType = Autocomplete.ItemType;

  // 검색 object
  public search$: Subject<string> = new Subject<string>();

  private currentRowIndex: number;

  private _searchKey: string = '';

  @Input()
  public set searchKey(text: string) {
    this._searchKey = text ? text : '';

    this.searchInput.nativeElement.value = this._searchKey;
  }

  public get searchKey() {
    return this._searchKey;
  }

  private _matchList: Autocomplete.Entity[] = [];

  @Input()
  public set matchList(list: Autocomplete.Entity[]) {
    this.currentRowIndex = -1;

    list.forEach(value => {
      if (this.searchKey && this.searchKey != '') {
        var s = String(value.label);
        var regex = new RegExp(this.searchKey, 'gi');

        value.matchText = s.replace(regex, `<span>${this.searchKey}</span>`);
      }
    });

    this._matchList = list;
  }

  @Output()
  private onSearch: EventEmitter<string> = new EventEmitter();

  @Output()
  private onEnter: EventEmitter<string> = new EventEmitter();

  @Output()
  private onAutocompleteSelect: EventEmitter<Object> = new EventEmitter();

  public get matchList() {
    return this._matchList;
  }

  /**
   * 아이템 목록이 펼쳐져 있는 상태에서 외부 ( 도큐먼트 ) 를 클릭하면
   * 펼쳐져 있는 목록을 닫아준다.
   *
   * @param event
   */
  @HostListener('document:click', [ '$event' ])
  documentClick(event) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.matchList = [];
    }
  }

  constructor(protected elementRef: ElementRef,
              protected injector: Injector) {
    super(elementRef, injector);
  }

  ngOnInit() {
    // 검색어 입력
    this.subscriptions.push(
      this.search$
        .switchMap((value) => Observable.of<string>(value))
        .subscribe((text) => {
          let minLength = 2;
          if (this.type == Autocomplete.Type.MEMBER) {
            minLength = 0;
          }

          if (text.length > minLength) {
            this.searchKey = text;

            this.onSearch.emit(text);
          } else {
            this.matchList = [];
          }
        })
    );
  }

  ngOnDestroy(): void {

  }

  public keydown(event) {
    let keyCode = event.keyCode;
    if (keyCode == 38) {
      event.preventDefault();
    }
  }

  public keyup(event, text: string) {
    let keyCode = event.keyCode;
    if (keyCode == 13) {
      // enter

      if (this.currentRowIndex > -1) {
        this.autocompleteItemClick(this.matchList[this.currentRowIndex]);
      } else {
        this.onEnter.emit(text);
        this.matchList = [];
      }

    } else {

      if (keyCode == 38) {
        if (!this.matchList.length) {
          return;
        }

        // up
        this.currentRowIndex--;
        if (this.currentRowIndex < 0) {
          this.currentRowIndex = -1;
          event.target.value = this.searchKey;
        } else {
          event.target.value = this.matchList[this.currentRowIndex].label;
        }

      } else if (keyCode == 40) {
        // down
        this.currentRowIndex++;
        if (this.currentRowIndex > this.matchList.length - 1) {
          this.currentRowIndex = this.matchList.length - 1;
        }

        event.target.value = this.matchList[this.currentRowIndex].label;
      } else {
        this.search$.next(text);
      }
    }
  }

  public autocompleteItemClick(item: Autocomplete.Entity) {
    this.onAutocompleteSelect.emit(item.value);
    this.init();
  }

  public init() {
    this.searchInput.nativeElement.value = '';
    this.matchList = [];
  }

}
