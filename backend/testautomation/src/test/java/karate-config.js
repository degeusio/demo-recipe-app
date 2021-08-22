function fn() {
  // get environment from the system property 'karate.env' (otherwise default to 'feature')
  var env = karate.env || 'feature';
  karate.log('karate environment is:', env);

  // Read settings from yaml
  var config = read('classpath:env-settings-'+env+'.yml');

  //hack to override when triggered from command line
  var is_testautomation = karate.properties['testautomation']; //pass in -Dtestautomation=true
  if (is_testautomation) {
    karate.log('Overriding default values with passed-in values');
    karate.log('Using url: ' + karate.properties['env_route_url']);
    config.env.urls['app_base'] = karate.properties['env_route_url'];
  }

  karate.log("Starting Karate tests using configuration: " + config);

  // Generic configuration
  karate.configure('logPrettyRequest', true);
  karate.configure('logPrettyResponse', true);
  karate.configure('connectTimeout', 10000);
  karate.configure('readTimeout', 10000);
  karate.configure('ssl', true);

  return config;
}

