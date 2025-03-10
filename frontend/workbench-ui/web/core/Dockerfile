FROM egovio/alpine-node-builder-14:yarn AS build
#FROM ghcr.io/egovernments/alpine-node-builder-14:yarn AS build
RUN apk update && apk upgrade
RUN apk add --no-cache git>2.30.0
ARG WORK_DIR
WORKDIR /app
ENV NODE_OPTIONS "--max-old-space-size=4792"

# Accept the build argument
ARG REACT_APP_PUBLIC_PATH
ENV REACT_APP_PUBLIC_PATH=${REACT_APP_PUBLIC_PATH}

COPY ${WORK_DIR} .
RUN ls -lah

#RUN node web/envs.js
RUN cd web/ \
    && node -e 'console.log(v8.getHeapStatistics().heap_size_limit/(1024*1024))' \
    && node -e 'console.log("core only")' \
    && cd core/ \
    &&  ./install-deps.sh \
    && cd ../ \
    && yarn install \ 
    && REACT_APP_PUBLIC_PATH=${REACT_APP_PUBLIC_PATH} yarn build:webpack 

FROM nginx:mainline-alpine
#FROM ghcr.io/egovernments/nginx:mainline-alpine
ENV WORK_DIR=/var/web/core-ui

RUN mkdir -p ${WORK_DIR}

COPY --from=build /app/web/build ${WORK_DIR}/
COPY --from=build /app/web/core/nginx.conf /etc/nginx/conf.d/default.conf
