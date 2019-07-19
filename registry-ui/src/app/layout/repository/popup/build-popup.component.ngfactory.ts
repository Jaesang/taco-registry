/**
 * @fileoverview This file is generated by the Angular template compiler.
 * Do not edit.
 * @suppress {suspiciousCode,uselessCode,missingProperties,missingOverride}
 */
 /* tslint:disable */


import * as i0 from '@angular/core';
import * as i1 from './build-popup.component';
import * as i2 from '../../../common/component/file-upload/file-upload.component.ngfactory';
import * as i3 from '../../../common/component/file-upload/file-upload.component';
import * as i4 from '../repository.service';
import * as i5 from '../../organization/organization.service';
import * as i6 from '../../../common/service/docker.service';
import * as i7 from '../../../common/service/file.service';
import * as i8 from '../build-history/build-history.service';
const styles_BuildPopupComponent:any[] = ([] as any[]);
export const RenderType_BuildPopupComponent:i0.RendererType2 = i0.ɵcrt({encapsulation:2,
    styles:styles_BuildPopupComponent,data:{}});
export function View_BuildPopupComponent_0(_l:any):i0.ɵViewDefinition {
  return i0.ɵvid(0,[i0.ɵqud(402653184,1,{fileUploader:0}),(_l()(),i0.ɵted(-1,(null as any),
      ['\n'])),(_l()(),i0.ɵeld(2,0,(null as any),(null as any),34,'div',[['class',
      'layout-popup']],[[8,'hidden',0]],(null as any),(null as any),(null as any),
      (null as any))),(_l()(),i0.ɵted(-1,(null as any),['\n  '])),(_l()(),i0.ɵeld(4,
      0,(null as any),(null as any),31,'div',[['class','layer-popup']],(null as any),
      (null as any),(null as any),(null as any),(null as any))),(_l()(),i0.ɵted(-1,
      (null as any),['\n    '])),(_l()(),i0.ɵeld(6,0,(null as any),(null as any),28,
      'div',[['class','section-popup']],(null as any),(null as any),(null as any),
      (null as any),(null as any))),(_l()(),i0.ɵted(-1,(null as any),['\n\n      '])),
      (_l()(),i0.ɵeld(8,0,(null as any),(null as any),25,'div',[['class','popup-area']],
          (null as any),(null as any),(null as any),(null as any),(null as any))),
      (_l()(),i0.ɵted(-1,(null as any),['\n        '])),(_l()(),i0.ɵeld(10,0,(null as any),
          (null as any),0,'a',[['aria-label','Popup Close'],['class','btn-close'],
              ['href','javascript:;']],(null as any),[[(null as any),'click']],(_v,
              en,$event) => {
            var ad:boolean = true;
            var _co:i1.BuildPopupComponent = _v.component;
            if (('click' === en)) {
              const pd_0:any = ((<any>_co.close()) !== false);
              ad = (pd_0 && ad);
            }
            return ad;
          },(null as any),(null as any))),(_l()(),i0.ɵted(-1,(null as any),['\n        '])),
      (_l()(),i0.ɵeld(12,0,(null as any),(null as any),1,'h1',[['class','txt-title']],
          (null as any),(null as any),(null as any),(null as any),(null as any))),
      (_l()(),i0.ɵted(-1,(null as any),['Start Image Build'])),(_l()(),i0.ɵted(-1,
          (null as any),['\n\n        '])),(_l()(),i0.ɵted(-1,(null as any),['\n        '])),
      (_l()(),i0.ɵeld(16,0,(null as any),(null as any),4,'div',[['class','popup-repository-build']],
          (null as any),(null as any),(null as any),(null as any),(null as any))),
      (_l()(),i0.ɵted(-1,(null as any),['\n          '])),(_l()(),i0.ɵeld(18,0,(null as any),
          (null as any),1,'div',[['file-upload','']],(null as any),[[(null as any),
              'onChange']],(_v,en,$event) => {
            var ad:boolean = true;
            var _co:i1.BuildPopupComponent = _v.component;
            if (('onChange' === en)) {
              const pd_0:any = ((<any>_co.fileChange($event)) !== false);
              ad = (pd_0 && ad);
            }
            return ad;
          },i2.View_FileUploadComponent_0,i2.RenderType_FileUploadComponent)),i0.ɵdid(19,
          245760,[[1,4]],0,i3.FileUploadComponent,[i0.ElementRef,i0.Injector],{status:[0,
              'status']},{onChange:'onChange'}),(_l()(),i0.ɵted(-1,(null as any),['\n        '])),
      (_l()(),i0.ɵted(-1,(null as any),['\n        '])),(_l()(),i0.ɵted(-1,(null as any),
          ['\n\n        '])),(_l()(),i0.ɵted(-1,(null as any),['\n        '])),(_l()(),
          i0.ɵeld(24,0,(null as any),(null as any),8,'div',[['class','button-block']],
              (null as any),(null as any),(null as any),(null as any),(null as any))),
      (_l()(),i0.ɵted(-1,(null as any),['\n          '])),(_l()(),i0.ɵeld(26,0,(null as any),
          (null as any),1,'button',[['class','btn-basic type-small'],['type','button']],
          (null as any),[[(null as any),'click']],(_v,en,$event) => {
            var ad:boolean = true;
            var _co:i1.BuildPopupComponent = _v.component;
            if (('click' === en)) {
              const pd_0:any = ((<any>_co.close()) !== false);
              ad = (pd_0 && ad);
            }
            return ad;
          },(null as any),(null as any))),(_l()(),i0.ɵted(-1,(null as any),['Cancel'])),
      (_l()(),i0.ɵted(-1,(null as any),['\n          '])),(_l()(),i0.ɵted(-1,(null as any),
          ['\n          '])),(_l()(),i0.ɵeld(30,0,(null as any),(null as any),1,'button',
          [['class','btn-action type-small'],['type','button']],[[8,'disabled',0]],
          [[(null as any),'click']],(_v,en,$event) => {
            var ad:boolean = true;
            var _co:i1.BuildPopupComponent = _v.component;
            if (('click' === en)) {
              const pd_0:any = ((<any>_co.startBuildClick()) !== false);
              ad = (pd_0 && ad);
            }
            return ad;
          },(null as any),(null as any))),(_l()(),i0.ɵted(-1,(null as any),['Start Build'])),
      (_l()(),i0.ɵted(-1,(null as any),['\n        '])),(_l()(),i0.ɵted(-1,(null as any),
          ['\n      '])),(_l()(),i0.ɵted(-1,(null as any),['\n\n    '])),(_l()(),i0.ɵted(-1,
          (null as any),['\n  '])),(_l()(),i0.ɵted(-1,(null as any),['\n'])),(_l()(),
          i0.ɵted(-1,(null as any),['\n'])),(_l()(),i0.ɵted(-1,(null as any),['\n']))],
      (_ck,_v) => {
        var _co:i1.BuildPopupComponent = _v.component;
        const currVal_1:any = _co.fileStatus;
        _ck(_v,19,0,currVal_1);
      },(_ck,_v) => {
        var _co:i1.BuildPopupComponent = _v.component;
        const currVal_0:boolean = !_co.show;
        _ck(_v,2,0,currVal_0);
        const currVal_2:any = (_co.fileStatus != 'success');
        _ck(_v,30,0,currVal_2);
      });
}
export function View_BuildPopupComponent_Host_0(_l:any):i0.ɵViewDefinition {
  return i0.ɵvid(0,[(_l()(),i0.ɵeld(0,0,(null as any),(null as any),1,'div',[['build-popup',
      '']],(null as any),(null as any),(null as any),View_BuildPopupComponent_0,RenderType_BuildPopupComponent)),
      i0.ɵdid(1,770048,(null as any),0,i1.BuildPopupComponent,[i0.ElementRef,i0.Injector,
          i4.RepositoryService,i5.OrganizationService,i6.DockerService,i7.FileService,
          i8.BuildHistoryService],(null as any),(null as any))],(_ck,_v) => {
    _ck(_v,1,0);
  },(null as any));
}
export const BuildPopupComponentNgFactory:i0.ComponentFactory<i1.BuildPopupComponent> = i0.ɵccf('[build-popup]',
    i1.BuildPopupComponent,View_BuildPopupComponent_Host_0,{show:'show'},{onClose:'onClose'},
    ([] as any[]));
//# sourceMappingURL=data:application/json;base64,eyJmaWxlIjoiL1VzZXJzL2V4bnR1L0lkZWFQcm9qZWN0cy9yZWdpc3RyeS9yZWdpc3RyeS11aS9zcmMvYXBwL2xheW91dC9yZXBvc2l0b3J5L3BvcHVwL2J1aWxkLXBvcHVwLmNvbXBvbmVudC5uZ2ZhY3RvcnkudHMiLCJ2ZXJzaW9uIjozLCJzb3VyY2VSb290IjoiIiwic291cmNlcyI6WyJuZzovLy9Vc2Vycy9leG50dS9JZGVhUHJvamVjdHMvcmVnaXN0cnkvcmVnaXN0cnktdWkvc3JjL2FwcC9sYXlvdXQvcmVwb3NpdG9yeS9wb3B1cC9idWlsZC1wb3B1cC5jb21wb25lbnQudHMiLCJuZzovLy9Vc2Vycy9leG50dS9JZGVhUHJvamVjdHMvcmVnaXN0cnkvcmVnaXN0cnktdWkvc3JjL2FwcC9sYXlvdXQvcmVwb3NpdG9yeS9wb3B1cC9idWlsZC1wb3B1cC5jb21wb25lbnQuaHRtbCIsIm5nOi8vL1VzZXJzL2V4bnR1L0lkZWFQcm9qZWN0cy9yZWdpc3RyeS9yZWdpc3RyeS11aS9zcmMvYXBwL2xheW91dC9yZXBvc2l0b3J5L3BvcHVwL2J1aWxkLXBvcHVwLmNvbXBvbmVudC50cy5CdWlsZFBvcHVwQ29tcG9uZW50X0hvc3QuaHRtbCJdLCJzb3VyY2VzQ29udGVudCI6WyIgIiwiPCEtLSBMYXllciBQb3B1cCAtLT5cbjxkaXYgY2xhc3M9XCJsYXlvdXQtcG9wdXBcIiBbaGlkZGVuXT1cIiFzaG93XCI+XG4gIDxkaXYgY2xhc3M9XCJsYXllci1wb3B1cFwiPlxuICAgIDxkaXYgY2xhc3M9XCJzZWN0aW9uLXBvcHVwXCI+XG5cbiAgICAgIDxkaXYgY2xhc3M9XCJwb3B1cC1hcmVhXCI+XG4gICAgICAgIDxhIGhyZWY9XCJqYXZhc2NyaXB0OjtcIiBjbGFzcz1cImJ0bi1jbG9zZVwiIGFyaWEtbGFiZWw9XCJQb3B1cCBDbG9zZVwiIChjbGljayk9XCJjbG9zZSgpXCI+PC9hPlxuICAgICAgICA8aDEgY2xhc3M9XCJ0eHQtdGl0bGVcIj5TdGFydCBJbWFnZSBCdWlsZDwvaDE+XG5cbiAgICAgICAgPCEtLSBQb3B1cCBTdGFydCBSZXBvc2l0b3J5IEJ1aWxkIC0tPlxuICAgICAgICA8ZGl2IGNsYXNzPVwicG9wdXAtcmVwb3NpdG9yeS1idWlsZFwiPlxuICAgICAgICAgIDxkaXYgZmlsZS11cGxvYWQgW3N0YXR1c109XCJmaWxlU3RhdHVzXCIgKG9uQ2hhbmdlKT1cImZpbGVDaGFuZ2UoJGV2ZW50KVwiPjwvZGl2PlxuICAgICAgICA8L2Rpdj5cbiAgICAgICAgPCEtLSAvL1BvcHVwIFN0YXJ0IFJlcG9zaXRvcnkgQnVpbGQgLS0+XG5cbiAgICAgICAgPCEtLSBCdXR0b24gLS0+XG4gICAgICAgIDxkaXYgY2xhc3M9XCJidXR0b24tYmxvY2tcIj5cbiAgICAgICAgICA8YnV0dG9uIHR5cGU9XCJidXR0b25cIiBjbGFzcz1cImJ0bi1iYXNpYyB0eXBlLXNtYWxsXCIgKGNsaWNrKT1cImNsb3NlKClcIj5DYW5jZWw8L2J1dHRvbj5cbiAgICAgICAgICA8IS0tIFtEXSBTdWNjZXNzIOyduCDqsr3smrAgZGlzYWJsZWQg7IaN7ISxIOygnOqxsCAtLT5cbiAgICAgICAgICA8YnV0dG9uIHR5cGU9XCJidXR0b25cIiBjbGFzcz1cImJ0bi1hY3Rpb24gdHlwZS1zbWFsbFwiIFtkaXNhYmxlZF09XCJmaWxlU3RhdHVzICE9ICdzdWNjZXNzJ1wiIChjbGljayk9XCJzdGFydEJ1aWxkQ2xpY2soKVwiPlN0YXJ0IEJ1aWxkPC9idXR0b24+XG4gICAgICAgIDwvZGl2PlxuICAgICAgPC9kaXY+XG5cbiAgICA8L2Rpdj5cbiAgPC9kaXY+XG48L2Rpdj5cbjwhLS0gLy9MYXllciBQb3B1cCAtLT5cbiIsIjxkaXYgYnVpbGQtcG9wdXA+PC9kaXY+Il0sIm1hcHBpbmdzIjoiQUFBQTs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OzBEQ0FvQjtNQUFBLFNBQ3BCO01BQUE7TUFBQSxnQkFBMkMsNENBQ3pDO01BQUE7TUFBQSwwREFBeUI7TUFBQSwyQkFDdkI7TUFBQTtNQUFBLDhCQUEyQjtNQUV6QjtVQUFBO01BQXdCLGtEQUN0QjtVQUFBO2NBQUE7dUJBQUE7WUFBQTtZQUFBO1lBQWtFO2NBQUE7Y0FBQTtZQUFBO1lBQWxFO1VBQUEsZ0NBQXdGO01BQ3hGO1VBQUE7TUFBc0IseURBQXNCO1VBQUEsaUNBRVA7TUFDckM7VUFBQTtNQUFvQyxvREFDbEM7VUFBQTtjQUFBO1lBQUE7WUFBQTtZQUF1QztjQUFBO2NBQUE7WUFBQTtZQUF2QztVQUFBLDJFQUFBO1VBQUE7Y0FBQSxrQ0FBNkU7TUFDekUsa0RBQ2lDO1VBQUEsbUJBRXhCLGtEQUNmO2lCQUFBO2NBQUE7TUFBMEIsb0RBQ3hCO1VBQUE7VUFBQTtZQUFBO1lBQUE7WUFBbUQ7Y0FBQTtjQUFBO1lBQUE7WUFBbkQ7VUFBQSxnQ0FBcUU7TUFBZSxvREFDNUM7VUFBQSxtQkFDeEM7VUFBQTtVQUFBO1lBQUE7WUFBQTtZQUF5RjtjQUFBO2NBQUE7WUFBQTtZQUF6RjtVQUFBLGdDQUFxSDtNQUFvQixrREFDckk7VUFBQSxlQUNGLGdEQUVGO1VBQUEseUJBQ0YsMENBQ0Y7aUJBQUEsMkJBQ2dCOzs7UUFmSztRQUFqQixZQUFpQixTQUFqQjs7O1FBVmdCO1FBQTFCLFdBQTBCLFNBQTFCO1FBa0I4RDtRQUFwRCxZQUFvRCxTQUFwRDs7OztvQkNuQlY7TUFBQTthQUFBOztnQ0FBQTtJQUFBOzs7OzsifQ==
