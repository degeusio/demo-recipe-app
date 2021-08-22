import {NgModule} from '@angular/core';
import {PreloadAllModules, RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './home/home.component';
import {TechnologyApiComponent} from "./technology-api/technology-api.component";
import {RecipesComponent} from './recipes/recipes.component';

export const routes: Routes = [
  {
    path:  "",
    pathMatch:  "full",
    redirectTo:  "recipes"
  },
  {path: "home", component: HomeComponent},
  {path: "api", component: TechnologyApiComponent},
  {path: "recipes", component: RecipesComponent},

  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
    anchorScrolling: 'enabled',
    preloadingStrategy: PreloadAllModules,
    enableTracing: false,
    relativeLinkResolution: 'legacy'
})
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
