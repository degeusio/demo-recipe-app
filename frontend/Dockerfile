# downgraded from 13.x version to 12.8.1 for issue encountered when upgrading to angular10 --> https://stackoverflow.com/questions/64051509/unknown-error-syntaxerror-unexpected-token-export-on-ng-command
FROM node:12.8.1-alpine As builder
WORKDIR /usr/src/app
COPY ./app/package.json package-lock.json* .
# fixing ERR! during, built. See note 'workaround adding git' in readme.md
RUN apk update && apk add --no-cache bash git openssh
RUN git config --global url."https://github.com".insteadOf git://github.com
RUN npm install -g @angular/cli@11.2.10

RUN npm install

COPY ./app/. .
# we cannot run karma tests here, for it requires e.g. chrome browser be installed..
RUN ng build --prod
# finish of builder

FROM nginx:1.15.8-alpine
COPY --from=builder /usr/src/app/dist/app/ /usr/share/nginx/html
COPY docker/default.conf /etc/nginx/conf.d
# E.g. run with docker run --rm -d -eAPI_URL="http://localhost:8080" -p4200:80 to inject API_URL variable for replacement in next command
CMD ["/bin/sh",  "-c",  "envsubst < /usr/share/nginx/html/assets/settings-template.json > /usr/share/nginx/html/assets/settings.json \
 && exec nginx -g 'daemon off;'"]
