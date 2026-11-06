# 화면을 빌드해 nginx 로 내보낸다. nginx 가 /api 를 deployment-service(대상 API 는 target-agent)로 넘기므로
# 브라우저는 같은 출처로만 요청한다(CORS 설정 불필요).
FROM node:22-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
# mock 이면 백엔드 없이 도는 시연용 이미지를 만든다
ARG API_MODE=live
RUN if [ "$API_MODE" = "mock" ]; then npm run build:mock; else npm run build; fi

FROM nginx:1.27-alpine
# templates/ 아래 파일은 컨테이너가 뜰 때 환경 변수가 채워져 conf.d 로 복사된다
COPY nginx/default.conf.template /etc/nginx/templates/default.conf.template
COPY --from=build /app/dist /usr/share/nginx/html
# 리졸버를 /etc/resolv.conf 에서 읽어 NGINX_LOCAL_RESOLVERS 로 넘긴다(nginx 공식 이미지 기능)
ENV DEPLOYMENT_SERVICE_URL=http://deployment-service:8080 \
    TARGET_AGENT_URL=http://target-agent:8001 \
    NGINX_ENTRYPOINT_LOCAL_RESOLVERS=1
EXPOSE 80
