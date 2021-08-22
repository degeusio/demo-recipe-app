import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {SettingsService} from "../../settings/settings.service";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";
import {Recipe} from 'src/app/_models/recipes/recipe';

/** service to interact with the Recipes API */
@Injectable()
export class RecipesService {

  private CONTEXT_PATH : string = "/recipes";

  constructor(private http: HttpClient, private settingsService: SettingsService) {
  }

  public getRecipes(): Observable<Recipe[]> {
    let uri = this.settingsService.get().apiUrl + this.CONTEXT_PATH;
    return this.http.get<any>(uri).pipe(
      map((response: any) => <Recipe[]>response) //map response to expected type, allow clients to handle success and error response
    )
  }
  //
  // public getDigipoortFiling(id: string): Observable<DigipoortFilingResponse> {
  //   let uri = this.settingsService.get().apiUrl + this.CONTEXT_PATH + `/filings/${id}`;
  //   return this.http.get<any>(uri, {observe: 'response'}).pipe(
  //     map((resp: any) => <DigipoortFilingResponse>resp.body) //body present given we subscribed to full HTTP resposne object with '{observe: 'response'}'.
  //   );
  // }
  //
  // public postDigipoortFiling(model: DigipoortFilingRequestPart, file: File): Observable<DigipoortFilingResponse> {
  //
  //   let apiUrl = this.settingsService.get().apiUrl + this.CONTEXT_PATH + "/filings";
  //
  //   const formData = new FormData();
  //
  //   //append part 1: the file metadata
  //   formData.append("digipoort_filing_request_part",
  //     new Blob(
  //       [JSON.stringify(model)], {type: "application/json"}
  //     )
  //   );
  //   //append part 2: the file itself
  //   formData.append("file", file);
  //
  //   return this.http.post(apiUrl, formData, {observe: 'response'}).pipe(
  //     map((response: any) => <DigipoortFilingResponse>response.body) //map response.body to expected type, allow clients to handle success and error response
  //   );
  //
  // }
}
