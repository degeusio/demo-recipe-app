import {Component, OnInit} from '@angular/core';
import {SettingsService} from "../settings/settings.service";
import {Settings} from "../settings/settings";

declare function loadSwaggerUi(apiUrl: string);

@Component({
  selector: 'app-technology-api',
  templateUrl: './technology-api.component.html'
})
export class TechnologyApiComponent implements OnInit {
  private settings: Settings;

  constructor(private settingsService: SettingsService) {
    this.settings = settingsService.get();
  }

  ngOnInit(): void {
    let apiUrl = this.settings.apiUrl + "/v2/api-docs";
    loadSwaggerUi(apiUrl);
  }

  doLoadSwaggerUi(path: String) {
    let apiUrl = this.settings.apiUrl + path;
    loadSwaggerUi(apiUrl)
  }

}
