import { Component } from '@angular/core';
import { SettingsService } from './settings/settings.service';
import { Settings } from './settings/settings';
import { Router, NavigationEnd } from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {

  settings: Settings;

  constructor(
    settingsService: SettingsService,
    public router: Router) {
    this.settings = settingsService.get();
  }

  title = 'app';

}
