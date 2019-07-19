import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'filter'
})
export class FilterPipe implements PipeTransform {
  transform(items: any, filter: { [ key: string ]: any }, isAnd: boolean): any {

    if (!(filter && Array.isArray(items))) {
      return items;
    }

    let filterKeys = Object.keys(filter);
    if (isAnd) {
      return items.filter(item => {
        filterKeys.reduce((memo, keyName) => (memo && new RegExp(filter[ keyName ], 'gi').test(item[ keyName ])) || filter[ keyName ] === '', true);
      });
    }

    return items.filter(item => {
      return filterKeys.some((keyName) => {
        if (Array.isArray(filter[ keyName ])) {
          return filter[ keyName ].indexOf(item[ keyName ]) > -1 || filter[ keyName ].length == 0;
        } else {
          return new RegExp(filter[ keyName ], 'gi').test(item[ keyName ]) || filter[ keyName ] === '';
        }
      });
    });

  }
}
