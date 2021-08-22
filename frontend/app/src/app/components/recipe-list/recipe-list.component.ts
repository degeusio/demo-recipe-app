import {Component, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {RecipesService} from 'src/app/_services/recipes/recipes.service';
import {Recipe} from 'src/app/_models/recipes/recipe';

@Component({
  selector: 'app-recipe-list',
  templateUrl: './recipe-list.component.html'
})
export class RecipeListComponent implements OnInit {
  constructor(
    private recipesService: RecipesService
  ) {
  }

  recipes$: Observable<Array<Recipe>>;

  ngOnInit(): void {
    this.recipes$ = this.recipesService.getRecipes();
  }

  refresh() {
    this.recipes$ = this.recipesService.getRecipes();
  }
}
