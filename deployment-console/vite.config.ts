import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// 개발 서버에서 /api 와 /healthcheck 는 deployment-service 로 넘긴다.
// 대상 목록도 배포 서비스가 답한다(ADR 0002, 대상 목록을 배포 쪽으로 옮긴다). 전에는 target-agent 로 넘겼다.
// 배포 때는 nginx 가 같은 일을 하므로 브라우저는 항상 같은 출처로 요청한다(CORS 불필요).
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const target = env.DEPLOYMENT_SERVICE_URL ?? 'http://localhost:8080'
  // 커리큘럼 편집(저작)은 다른 서비스다(ADR 0008). 아래 '/api' 보다 먼저 와야 이 경로가 저작 서비스로 간다
  const authoring = env.AUTHORING_SERVICE_URL ?? 'http://localhost:8002'
  return {
    plugins: [react(), tailwindcss()],
    server: {
      port: 5173,
      proxy: {
        '/api/v1/curricula': authoring,
        '/api': target,
        '/healthcheck': target,
      },
    },
    test: {
      environment: 'node',
    },
  }
})
