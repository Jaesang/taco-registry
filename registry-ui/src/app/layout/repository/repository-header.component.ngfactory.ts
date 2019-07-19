/**
 * @fileoverview This file is generated by the Angular template compiler.
 * Do not edit.
 * @suppress {suspiciousCode,uselessCode,missingProperties,missingOverride}
 */
 /* tslint:disable */


import * as i0 from '@angular/core';
import * as i1 from './popup/copy-as-popup.component.ngfactory';
import * as i2 from './popup/copy-as-popup.component';
import * as i3 from './build-history/build-history.service';
import * as i4 from '../user/user.service';
import * as i5 from '../../common/service/file.service';
import * as i6 from '../../common/service/docker.service';
import * as i7 from './repository.service';
import * as i8 from './repository-header.component';
import * as i9 from '@angular/common';
import * as i10 from './popup/build-popup.component.ngfactory';
import * as i11 from './popup/build-popup.component';
import * as i12 from '../organization/organization.service';
const styles_RepositoryHeaderComponent:any[] = ([] as any[]);
export const RenderType_RepositoryHeaderComponent:i0.RendererType2 = i0.ɵcrt({encapsulation:2,
    styles:styles_RepositoryHeaderComponent,data:{}});
function View_RepositoryHeaderComponent_1(_l:any):i0.ɵViewDefinition {
  return i0.ɵvid(0,[(_l()(),i0.ɵeld(0,0,(null as any),(null as any),0,'span',[['class',
      'type-private']],(null as any),(null as any),(null as any),(null as any),(null as any)))],
      (null as any),(null as any));
}
function View_RepositoryHeaderComponent_2(_l:any):i0.ɵViewDefinition {
  return i0.ɵvid(0,[(_l()(),i0.ɵeld(0,0,(null as any),(null as any),1,'button',[['class',
      'btn-sub-action type-box'],['type','button']],(null as any),[[(null as any),
      'click']],(_v,en,$event) => {
    var ad:boolean = true;
    var _co:any = _v.component;
    if (('click' === en)) {
      const pd_0:any = ((<any>_co.startNewBuildClick()) !== false);
      ad = (pd_0 && ad);
    }
    return ad;
  },(null as any),(null as any))),(_l()(),i0.ɵted(-1,(null as any),['Start New Build']))],
      (null as any),(null as any));
}
function View_RepositoryHeaderComponent_3(_l:any):i0.ɵViewDefinition {
  return i0.ɵvid(0,[(_l()(),i0.ɵeld(0,0,(null as any),(null as any),1,'div',[['copy-as-popup',
      '']],(null as any),[[(null as any),'onClose']],(_v,en,$event) => {
    var ad:boolean = true;
    var _co:any = _v.component;
    if (('onClose' === en)) {
      const pd_0:any = ((<any>_co.copyAsPopupClose()) !== false);
      ad = (pd_0 && ad);
    }
    return ad;
  },i1.View_CopyAsPopupComponent_0,i1.RenderType_CopyAsPopupComponent)),i0.ɵdid(1,
      245760,(null as any),0,i2.CopyAsPopupComponent,[i0.ElementRef,i0.Injector,i3.BuildHistoryService,
          i4.UserService,i5.FileService,i3.BuildHistoryService,i6.DockerService,i7.RepositoryService],
      (null as any),{onClose:'onClose'})],(_ck,_v) => {
    _ck(_v,1,0);
  },(null as any));
}
export function View_RepositoryHeaderComponent_0(_l:any):i0.ɵViewDefinition {
  return i0.ɵvid(0,[i0.ɵqud(402653184,1,{fileUploader:0}),(_l()(),i0.ɵeld(1,0,(null as any),
      (null as any),37,'h2',[['class','section-sub-title']],(null as any),(null as any),
      (null as any),(null as any),(null as any))),(_l()(),i0.ɵted(-1,(null as any),
      ['\n  '])),(_l()(),i0.ɵeld(3,0,(null as any),(null as any),34,'div',[['class',
      'title-area']],(null as any),(null as any),(null as any),(null as any),(null as any))),
      (_l()(),i0.ɵted(-1,(null as any),['\n    '])),(_l()(),i0.ɵeld(5,0,(null as any),
          (null as any),31,'div',[['class','title-block']],(null as any),(null as any),
          (null as any),(null as any),(null as any))),(_l()(),i0.ɵted(-1,(null as any),
          ['\n      '])),(_l()(),i0.ɵeld(7,0,(null as any),(null as any),0,'a',[['aria-label',
          'Back'],['class','btn-back'],['href','javascript:;']],(null as any),[[(null as any),
          'click']],(_v,en,$event) => {
        var ad:boolean = true;
        var _co:i8.RepositoryHeaderComponent = _v.component;
        if (('click' === en)) {
          const pd_0:any = ((<any>_co.backClick()) !== false);
          ad = (pd_0 && ad);
        }
        return ad;
      },(null as any),(null as any))),(_l()(),i0.ɵted(-1,(null as any),['\n      '])),
      (_l()(),i0.ɵeld(9,0,(null as any),(null as any),4,'span',[['class','txt-title']],
          (null as any),(null as any),(null as any),(null as any),(null as any))),
      (_l()(),i0.ɵted(-1,(null as any),['\n        '])),(_l()(),i0.ɵeld(11,0,(null as any),
          (null as any),1,'em',([] as any[]),(null as any),(null as any),(null as any),
          (null as any),(null as any))),(_l()(),i0.ɵted(12,(null as any),['','/',''])),
      (_l()(),i0.ɵted(-1,(null as any),['\n      '])),(_l()(),i0.ɵted(-1,(null as any),
          ['\n      '])),(_l()(),i0.ɵeld(15,0,(null as any),(null as any),11,'div',
          [['class','favorite-private']],(null as any),(null as any),(null as any),
          (null as any),(null as any))),(_l()(),i0.ɵted(-1,(null as any),['\n        '])),
      (_l()(),i0.ɵeld(17,0,(null as any),(null as any),5,'div',[['class','component-starred']],
          (null as any),(null as any),(null as any),(null as any),(null as any))),
      (_l()(),i0.ɵted(-1,(null as any),['\n          '])),(_l()(),i0.ɵeld(19,0,(null as any),
          (null as any),0,'input',[['class','input-checkbox'],['id','starred01'],['type',
              'checkbox']],[[8,'checked',0]],[[(null as any),'change']],(_v,en,$event) => {
            var ad:boolean = true;
            var _co:i8.RepositoryHeaderComponent = _v.component;
            if (('change' === en)) {
              const pd_0:any = ((<any>_co.starChange(_co.repo)) !== false);
              ad = (pd_0 && ad);
            }
            return ad;
          },(null as any),(null as any))),(_l()(),i0.ɵted(-1,(null as any),['\n          '])),
      (_l()(),i0.ɵeld(21,0,(null as any),(null as any),0,'label',[['class','txt-label'],
          ['for','starred01']],(null as any),(null as any),(null as any),(null as any),
          (null as any))),(_l()(),i0.ɵted(-1,(null as any),['\n        '])),(_l()(),
          i0.ɵted(-1,(null as any),['\n        '])),(_l()(),i0.ɵand(16777216,(null as any),
          (null as any),1,(null as any),View_RepositoryHeaderComponent_1)),i0.ɵdid(25,
          16384,(null as any),0,i9.NgIf,[i0.ViewContainerRef,i0.TemplateRef],{ngIf:[0,
              'ngIf']},(null as any)),(_l()(),i0.ɵted(-1,(null as any),['\n      '])),
      (_l()(),i0.ɵted(-1,(null as any),['\n\n      '])),(_l()(),i0.ɵeld(28,0,(null as any),
          (null as any),7,'div',[['class','buttons']],(null as any),(null as any),
          (null as any),(null as any),(null as any))),(_l()(),i0.ɵted(-1,(null as any),
          ['\n        '])),(_l()(),i0.ɵand(16777216,(null as any),(null as any),1,
          (null as any),View_RepositoryHeaderComponent_2)),i0.ɵdid(31,16384,(null as any),
          0,i9.NgIf,[i0.ViewContainerRef,i0.TemplateRef],{ngIf:[0,'ngIf']},(null as any)),
      (_l()(),i0.ɵted(-1,(null as any),['\n        '])),(_l()(),i0.ɵeld(33,0,(null as any),
          (null as any),1,'button',[['class','btn-action type-box'],['type','button']],
          (null as any),[[(null as any),'click']],(_v,en,$event) => {
            var ad:boolean = true;
            var _co:i8.RepositoryHeaderComponent = _v.component;
            if (('click' === en)) {
              const pd_0:any = ((<any>_co.copyAsClick()) !== false);
              ad = (pd_0 && ad);
            }
            return ad;
          },(null as any),(null as any))),(_l()(),i0.ɵted(-1,(null as any),['Copy As'])),
      (_l()(),i0.ɵted(-1,(null as any),['\n      '])),(_l()(),i0.ɵted(-1,(null as any),
          ['\n    '])),(_l()(),i0.ɵted(-1,(null as any),['\n  '])),(_l()(),i0.ɵted(-1,
          (null as any),['\n'])),(_l()(),i0.ɵted(-1,(null as any),['\n\n'])),(_l()(),
          i0.ɵeld(40,0,(null as any),(null as any),1,'div',[['build-popup','']],(null as any),
              [[(null as any),'onClose']],(_v,en,$event) => {
                var ad:boolean = true;
                var _co:i8.RepositoryHeaderComponent = _v.component;
                if (('onClose' === en)) {
                  const pd_0:any = ((<any>_co.buildPopupClose()) !== false);
                  ad = (pd_0 && ad);
                }
                return ad;
              },i10.View_BuildPopupComponent_0,i10.RenderType_BuildPopupComponent)),
      i0.ɵdid(41,770048,(null as any),0,i11.BuildPopupComponent,[i0.ElementRef,i0.Injector,
          i7.RepositoryService,i12.OrganizationService,i6.DockerService,i5.FileService,
          i3.BuildHistoryService],{show:[0,'show']},{onClose:'onClose'}),(_l()(),i0.ɵted(-1,
          (null as any),['\n\n'])),(_l()(),i0.ɵand(16777216,(null as any),(null as any),
          1,(null as any),View_RepositoryHeaderComponent_3)),i0.ɵdid(44,16384,(null as any),
          0,i9.NgIf,[i0.ViewContainerRef,i0.TemplateRef],{ngIf:[0,'ngIf']},(null as any)),
      (_l()(),i0.ɵted(-1,(null as any),['\n']))],(_ck,_v) => {
    var _co:i8.RepositoryHeaderComponent = _v.component;
    const currVal_3:boolean = !_co.repo.is_public;
    _ck(_v,25,0,currVal_3);
    const currVal_4:any = _co.mine;
    _ck(_v,31,0,currVal_4);
    const currVal_5:any = _co.showBuildPopup;
    _ck(_v,41,0,currVal_5);
    const currVal_6:any = _co.showCopyAsPopup;
    _ck(_v,44,0,currVal_6);
  },(_ck,_v) => {
    var _co:i8.RepositoryHeaderComponent = _v.component;
    const currVal_0:any = _co.orgName;
    const currVal_1:any = _co.repoName;
    _ck(_v,12,0,currVal_0,currVal_1);
    const currVal_2:any = _co.repo.is_starred;
    _ck(_v,19,0,currVal_2);
  });
}
export function View_RepositoryHeaderComponent_Host_0(_l:any):i0.ɵViewDefinition {
  return i0.ɵvid(0,[(_l()(),i0.ɵeld(0,0,(null as any),(null as any),1,'div',[['repository-header',
      '']],(null as any),(null as any),(null as any),View_RepositoryHeaderComponent_0,
      RenderType_RepositoryHeaderComponent)),i0.ɵdid(1,245760,(null as any),0,i8.RepositoryHeaderComponent,
      [i0.ElementRef,i0.Injector,i7.RepositoryService,i5.FileService,i6.DockerService,
          i3.BuildHistoryService],(null as any),(null as any))],(_ck,_v) => {
    _ck(_v,1,0);
  },(null as any));
}
export const RepositoryHeaderComponentNgFactory:i0.ComponentFactory<i8.RepositoryHeaderComponent> = i0.ɵccf('[repository-header]',
    i8.RepositoryHeaderComponent,View_RepositoryHeaderComponent_Host_0,{},{},([] as any[]));
//# sourceMappingURL=data:application/json;base64,eyJmaWxlIjoiL1VzZXJzL2V4bnR1L0lkZWFQcm9qZWN0cy9yZWdpc3RyeS9yZWdpc3RyeS11aS9zcmMvYXBwL2xheW91dC9yZXBvc2l0b3J5L3JlcG9zaXRvcnktaGVhZGVyLmNvbXBvbmVudC5uZ2ZhY3RvcnkudHMiLCJ2ZXJzaW9uIjozLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyJuZzovLy9Vc2Vycy9leG50dS9JZGVhUHJvamVjdHMvcmVnaXN0cnkvcmVnaXN0cnktdWkvc3JjL2FwcC9sYXlvdXQvcmVwb3NpdG9yeS9yZXBvc2l0b3J5LWhlYWRlci5jb21wb25lbnQudHMiLCJuZzovLy9Vc2Vycy9leG50dS9JZGVhUHJvamVjdHMvcmVnaXN0cnkvcmVnaXN0cnktdWkvc3JjL2FwcC9sYXlvdXQvcmVwb3NpdG9yeS9yZXBvc2l0b3J5LWhlYWRlci5jb21wb25lbnQuaHRtbCIsIm5nOi8vL1VzZXJzL2V4bnR1L0lkZWFQcm9qZWN0cy9yZWdpc3RyeS9yZWdpc3RyeS11aS9zcmMvYXBwL2xheW91dC9yZXBvc2l0b3J5L3JlcG9zaXRvcnktaGVhZGVyLmNvbXBvbmVudC50cy5SZXBvc2l0b3J5SGVhZGVyQ29tcG9uZW50X0hvc3QuaHRtbCJdLCJzb3VyY2VzQ29udGVudCI6WyIgIiwiPGgyIGNsYXNzPVwic2VjdGlvbi1zdWItdGl0bGVcIj5cbiAgPGRpdiBjbGFzcz1cInRpdGxlLWFyZWFcIj5cbiAgICA8ZGl2IGNsYXNzPVwidGl0bGUtYmxvY2tcIj5cbiAgICAgIDxhIGhyZWY9XCJqYXZhc2NyaXB0OjtcIiBjbGFzcz1cImJ0bi1iYWNrXCIgYXJpYS1sYWJlbD1cIkJhY2tcIiAoY2xpY2spPVwiYmFja0NsaWNrKClcIj48L2E+XG4gICAgICA8c3BhbiBjbGFzcz1cInR4dC10aXRsZVwiPlxuICAgICAgICA8ZW0+e3sgb3JnTmFtZSB9fS97eyByZXBvTmFtZSB9fTwvZW0+XG4gICAgICA8L3NwYW4+XG4gICAgICA8ZGl2IGNsYXNzPVwiZmF2b3JpdGUtcHJpdmF0ZVwiPlxuICAgICAgICA8ZGl2IGNsYXNzPVwiY29tcG9uZW50LXN0YXJyZWRcIj5cbiAgICAgICAgICA8aW5wdXQgdHlwZT1cImNoZWNrYm94XCIgY2xhc3M9XCJpbnB1dC1jaGVja2JveFwiIGlkPVwic3RhcnJlZDAxXCIgW2NoZWNrZWRdPVwicmVwby5pc19zdGFycmVkXCIgKGNoYW5nZSk9XCJzdGFyQ2hhbmdlKHJlcG8pXCI+XG4gICAgICAgICAgPGxhYmVsIGZvcj1cInN0YXJyZWQwMVwiIGNsYXNzPVwidHh0LWxhYmVsXCI+PC9sYWJlbD5cbiAgICAgICAgPC9kaXY+XG4gICAgICAgIDxzcGFuIGNsYXNzPVwidHlwZS1wcml2YXRlXCIgKm5nSWY9XCIhcmVwby5pc19wdWJsaWNcIj48L3NwYW4+XG4gICAgICA8L2Rpdj5cblxuICAgICAgPGRpdiBjbGFzcz1cImJ1dHRvbnNcIj5cbiAgICAgICAgPGJ1dHRvbiB0eXBlPVwiYnV0dG9uXCIgY2xhc3M9XCJidG4tc3ViLWFjdGlvbiB0eXBlLWJveFwiIChjbGljayk9XCJzdGFydE5ld0J1aWxkQ2xpY2soKVwiICpuZ0lmPVwibWluZVwiPlN0YXJ0IE5ldyBCdWlsZDwvYnV0dG9uPlxuICAgICAgICA8YnV0dG9uIHR5cGU9XCJidXR0b25cIiBjbGFzcz1cImJ0bi1hY3Rpb24gdHlwZS1ib3hcIiAoY2xpY2spPVwiY29weUFzQ2xpY2soKVwiPkNvcHkgQXM8L2J1dHRvbj5cbiAgICAgIDwvZGl2PlxuICAgIDwvZGl2PlxuICA8L2Rpdj5cbjwvaDI+XG5cbjxkaXYgYnVpbGQtcG9wdXAgW3Nob3ddPVwic2hvd0J1aWxkUG9wdXBcIiAob25DbG9zZSk9XCJidWlsZFBvcHVwQ2xvc2UoKVwiPjwvZGl2PlxuXG48ZGl2IGNvcHktYXMtcG9wdXAgKm5nSWY9XCJzaG93Q29weUFzUG9wdXBcIiAob25DbG9zZSk9XCJjb3B5QXNQb3B1cENsb3NlKClcIj48L2Rpdj5cbiIsIjxkaXYgcmVwb3NpdG9yeS1oZWFkZXI+PC9kaXY+Il0sIm1hcHBpbmdzIjoiQUFBQTs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztvQkNZUTtNQUFBOzs7O29CQUlBO01BQUE7TUFBQTtJQUFBO0lBQUE7SUFBc0Q7TUFBQTtNQUFBO0lBQUE7SUFBdEQ7RUFBQSxnQ0FBa0c7Ozs7b0JBUzFHO01BQUE7SUFBQTtJQUFBO0lBQTJDO01BQUE7TUFBQTtJQUFBO0lBQTNDO0VBQUEsNkVBQUE7TUFBQTtvR0FBQTtNQUFBO0lBQUE7Ozs7MERBekJBO01BQUE7TUFBQSw0Q0FBOEI7TUFBQSxXQUM1QjtNQUFBO01BQXdCLDhDQUN0QjtVQUFBO1VBQUEsNENBQXlCO1VBQUEsZUFDdkI7VUFBQTtVQUFBO1FBQUE7UUFBQTtRQUEwRDtVQUFBO1VBQUE7UUFBQTtRQUExRDtNQUFBLGdDQUFvRjtNQUNwRjtVQUFBO01BQXdCLGtEQUN0QjtVQUFBO1VBQUEsOEJBQUk7TUFBaUMsZ0RBQ2hDO1VBQUEsZUFDUDtVQUFBO1VBQUEsOEJBQThCO01BQzVCO1VBQUE7TUFBK0Isb0RBQzdCO1VBQUE7Y0FBQTtZQUFBO1lBQUE7WUFBeUY7Y0FBQTtjQUFBO1lBQUE7WUFBekY7VUFBQSxnQ0FBcUg7TUFDckg7VUFBQTtVQUFBLGdCQUFpRCxrREFDN0M7aUJBQUEsbUNBQ047VUFBQSx3RUFBQTtVQUFBO2NBQUEsd0JBQTBEO01BQ3RELGtEQUVOO1VBQUE7VUFBQSw0Q0FBcUI7VUFBQSxpQkFDbkI7VUFBQSx3REFBQTtVQUFBO01BQTBILGtEQUMxSDtVQUFBO1VBQUE7WUFBQTtZQUFBO1lBQWtEO2NBQUE7Y0FBQTtZQUFBO1lBQWxEO1VBQUEsZ0NBQTBFO01BQWdCLGdEQUN0RjtVQUFBLGFBQ0YsNENBQ0Y7VUFBQSx1QkFDSCw0Q0FFTDtpQkFBQTtjQUFBO2dCQUFBO2dCQUFBO2dCQUF5QztrQkFBQTtrQkFBQTtnQkFBQTtnQkFBekM7Y0FBQTthQUFBOztnQ0FBQSx5Q0FBNkU7VUFBQSx5QkFFN0U7VUFBQSwwREFBQTtVQUFBO01BQWdGOztJQWI3QztJQUEzQixZQUEyQixTQUEzQjtJQUlxRjtJQUFyRixZQUFxRixTQUFyRjtJQU9TO0lBQWpCLFlBQWlCLFNBQWpCO0lBRW1CO0lBQW5CLFlBQW1CLFNBQW5COzs7SUFwQlk7SUFBQTtJQUFBO0lBSTJEO0lBQTdELFlBQTZELFNBQTdEOzs7O29CQ1RWO01BQUE7MENBQUEsVUFBQTtNQUFBO2dDQUFBO0lBQUE7Ozs7In0=
