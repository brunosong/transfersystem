import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// 개발 서버에서 /api 와 /healthcheck 는 deployment-service 로 넘긴다.
// 대상 목록도 배포 서비스가 답한다(ADR 0002, 대상 목록을 배포 쪽으로 옮긴다). 전에는 target-agent 로 넘겼다.
// 배포 때는 nginx 가 같은 일을 하므로 브라우저는 항상 같은 출처로 요청한다(CORS 불필요).
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const target = env.DEPLOYMENT_SERVICE_URL ?? 'http://localhost:8080'
  return {
    plugins: [react(), tailwindcss()],
    server: {
      port: 5173,
      proxy: {
        '/api': target,
        '/healthcheck': target,
      },
    },
    test: {
      environment: 'node',
    },
  }
})
