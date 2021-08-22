// used in home.component.ts via declare directive
// changing this requires a full stop & start of ng serve -o
function loadSwaggerUi(apiUrl) {
    const ui = SwaggerUIBundle({
      url: apiUrl,
      dom_id: '#swagger-ui',
      presets: [
        SwaggerUIBundle.presets.apis,
        SwaggerUIStandalonePreset
      ],
      layout: "StandaloneLayout"
    })
    window.ui = ui
}
