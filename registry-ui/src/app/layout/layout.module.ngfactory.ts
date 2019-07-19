/**
 * @fileoverview This file is generated by the Angular template compiler.
 * Do not edit.
 * @suppress {suspiciousCode,uselessCode,missingProperties,missingOverride}
 */
 /* tslint:disable */


import * as i0 from '@angular/core';
import * as i1 from './layout.module';
import * as i2 from './layout.component.ngfactory';
import * as i3 from '@angular/common';
import * as i4 from '@angular/forms';
import * as i5 from '@angular/http';
import * as i6 from './main/main.service';
import * as i7 from './organization/organization.service';
import * as i8 from './organization/organization.guard';
import * as i9 from '../common/service/common.service';
import * as i10 from './user/user.service';
import * as i11 from '@angular/router';
import * as i12 from './user/user.guard';
import * as i13 from './repository/repository.service';
import * as i14 from './repository/repository.guard';
import * as i15 from './admin/admin.guard';
import * as i16 from './admin/user-list/super-user.service';
import * as i17 from './repository/build-history/build-history.service';
import * as i18 from '../common/component/selectbox/select-box.module';
import * as i19 from '../common/component/pagination/pagination.module';
import * as i20 from '../common/component/file-upload/file-upload.module';
import * as i21 from '../common/component/autocomplete/autocomplete.module';
import * as i22 from '../common/shared.module';
import * as i23 from './gnb/organization-area/organization-area.module';
import * as i24 from './gnb/profile-area/profile-area.module';
import * as i25 from './gnb/gnb.module';
import * as i26 from './lnb/lnb.module';
import * as i27 from './layout.component';
export const LayoutModuleNgFactory:i0.NgModuleFactory<i1.LayoutModule> = i0.ɵcmf(i1.LayoutModule,
    ([] as any[]),(_l:any) => {
      return i0.ɵmod([i0.ɵmpd(512,i0.ComponentFactoryResolver,i0.ɵCodegenComponentFactoryResolver,
          [[8,[i2.LayoutComponentNgFactory]],[3,i0.ComponentFactoryResolver],i0.NgModuleRef]),
          i0.ɵmpd(4608,i3.NgLocalization,i3.NgLocaleLocalization,[i0.LOCALE_ID]),i0.ɵmpd(4608,
              i4.ɵi,i4.ɵi,([] as any[])),i0.ɵmpd(4608,i5.BrowserXhr,i5.BrowserXhr,
              ([] as any[])),i0.ɵmpd(4608,i5.ResponseOptions,i5.BaseResponseOptions,
              ([] as any[])),i0.ɵmpd(5120,i5.XSRFStrategy,i5.ɵb,([] as any[])),i0.ɵmpd(4608,
              i5.XHRBackend,i5.XHRBackend,[i5.BrowserXhr,i5.ResponseOptions,i5.XSRFStrategy]),
          i0.ɵmpd(4608,i5.RequestOptions,i5.BaseRequestOptions,([] as any[])),i0.ɵmpd(5120,
              i5.Http,i5.ɵc,[i5.XHRBackend,i5.RequestOptions]),i0.ɵmpd(4608,i6.MainService,
              i6.MainService,[i0.Injector]),i0.ɵmpd(4608,i7.OrganizationService,i7.OrganizationService,
              [i0.Injector]),i0.ɵmpd(4608,i8.OrganizationGuard,i8.OrganizationGuard,
              [i7.OrganizationService,i9.CommonService,i10.UserService,i0.Injector,
                  i11.Router]),i0.ɵmpd(4608,i12.UserGuard,i12.UserGuard,[i10.UserService,
              i9.CommonService,i0.Injector,i11.Router]),i0.ɵmpd(4608,i13.RepositoryService,
              i13.RepositoryService,[i0.Injector]),i0.ɵmpd(4608,i14.RepositoryGuard,
              i14.RepositoryGuard,[i13.RepositoryService,i9.CommonService,i10.UserService,
                  i0.Injector,i11.Router]),i0.ɵmpd(4608,i15.AdminGuard,i15.AdminGuard,
              [i16.SuperUserService,i9.CommonService,i0.Injector,i11.Router]),i0.ɵmpd(4608,
              i17.BuildHistoryService,i17.BuildHistoryService,[i0.Injector]),i0.ɵmpd(512,
              i3.CommonModule,i3.CommonModule,([] as any[])),i0.ɵmpd(512,i4.ɵba,i4.ɵba,
              ([] as any[])),i0.ɵmpd(512,i4.FormsModule,i4.FormsModule,([] as any[])),
          i0.ɵmpd(512,i5.HttpModule,i5.HttpModule,([] as any[])),i0.ɵmpd(512,i11.RouterModule,
              i11.RouterModule,[[2,i11.ɵa],[2,i11.Router]]),i0.ɵmpd(512,i18.SelectBoxModule,
              i18.SelectBoxModule,([] as any[])),i0.ɵmpd(512,i19.PaginationModule,
              i19.PaginationModule,([] as any[])),i0.ɵmpd(512,i20.FileUploadModule,
              i20.FileUploadModule,([] as any[])),i0.ɵmpd(512,i21.AutocompleteModule,
              i21.AutocompleteModule,([] as any[])),i0.ɵmpd(512,i22.SharedModule,i22.SharedModule,
              ([] as any[])),i0.ɵmpd(512,i23.OrganizationAreaModule,i23.OrganizationAreaModule,
              ([] as any[])),i0.ɵmpd(512,i24.ProfileAreaModule,i24.ProfileAreaModule,
              ([] as any[])),i0.ɵmpd(512,i25.GnbModule,i25.GnbModule,([] as any[])),
          i0.ɵmpd(512,i26.LnbModule,i26.LnbModule,([] as any[])),i0.ɵmpd(512,i1.LayoutModule,
              i1.LayoutModule,([] as any[])),i0.ɵmpd(1024,i11.ROUTES,() => {
            return [[{path:'',component:i27.LayoutComponent,children:[{path:'',redirectTo:'main',
                pathMatch:'full'},{path:'main',loadChildren:'app/layout/main/main.module#MainModule',
                canActivate:[i8.OrganizationGuard]},{path:'organization/:org',loadChildren:'app/layout/organization/organization.module#OrganizationModule',
                canActivate:[i8.OrganizationGuard]},{path:'user/:user',loadChildren:'app/layout/user/user.module#UserModule',
                canActivate:[i12.UserGuard]},{path:'image/:org/:repo',loadChildren:'app/layout/repository/repository.module#RepositoryModule',
                canActivate:[i14.RepositoryGuard]},{path:'admin',loadChildren:'app/layout/admin/admin.module#AdminModule',
                canActivate:[i15.AdminGuard]},{path:'error',loadChildren:'app/layout/error/error.module#ErrorModule'}]}]];
          },([] as any[]))]);
    });
//# sourceMappingURL=data:application/json;base64,eyJmaWxlIjoiL1VzZXJzL2V4bnR1L0lkZWFQcm9qZWN0cy9yZWdpc3RyeS9yZWdpc3RyeS11aS9zcmMvYXBwL2xheW91dC9sYXlvdXQubW9kdWxlLm5nZmFjdG9yeS50cyIsInZlcnNpb24iOjMsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIm5nOi8vL1VzZXJzL2V4bnR1L0lkZWFQcm9qZWN0cy9yZWdpc3RyeS9yZWdpc3RyeS11aS9zcmMvYXBwL2xheW91dC9sYXlvdXQubW9kdWxlLnRzIl0sInNvdXJjZXNDb250ZW50IjpbIiAiXSwibWFwcGluZ3MiOiJBQUFBOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7In0=
