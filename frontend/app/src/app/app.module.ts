import {registerLocaleData} from '@angular/common';
import localeNl from '@angular/common/locales/nl';
import {BrowserModule} from '@angular/platform-browser';
import {APP_INITIALIZER, NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {SettingsHttpService} from './settings/settings.http.service';

import {HomeComponent} from './home/home.component';
import {HeaderComponent} from './header/header.component';
import {FooterComponent} from './footer/footer.component';
import {TechnologyApiComponent} from './technology-api/technology-api.component';

import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {IConfig, NgxMaskModule} from 'ngx-mask';

import {RecipesService} from './_services/recipes/recipes.service';
import {RecipeListComponent} from './components/recipe-list/recipe-list.component';
import { RecipesComponent } from './recipes/recipes.component';

export function app_Init(settingsHttpService: SettingsHttpService) {
  return () => settingsHttpService.initializeApp();
}

export const options: Partial<IConfig> | (() => Partial<IConfig>) = null; //required for NgxMaskModule, as per https://www.npmjs.com/package/ngx-mask

@NgModule({
  declarations: [
    /* custom components */
    AppComponent,
    HomeComponent,
    HeaderComponent,
    FooterComponent,
    TechnologyApiComponent,
    RecipesComponent,

    RecipeListComponent,

  ],
  imports: [
    AppRoutingModule,

    //3rd party
    NgxMaskModule.forRoot(),
    NgbModule,

    //angular's
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    RecipesService,
    { provide: APP_INITIALIZER, useFactory: app_Init, deps: [SettingsHttpService], multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
