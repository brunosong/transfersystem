import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// 개발 서버에서 /api/v1/targets 는 data-migration-service 로, 나머지 /api 와 /healthcheck 는 transfer-service 로 넘긴다.
// 배포 때는 nginx 가 같은 일을 하므로 브라우저는 항상 같은 출처로 요청한다(CORS 불필요).
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const target = env.TRANSFER_SERVICE_URL ?? 'http://localhost:8080'
  const dataMigration = env.DATA_MIGRATION_SERVICE_URL ?? 'http://localhost:8001'
  return {
    plugins: [react(), tailwindcss()],
    server: {
      port: 5173,
      // 먼저 적은 규칙이 먼저 맞는다
      proxy: {
        '/api/v1/targets': dataMigration,
        '/api': target,
        '/healthcheck': target,
      },
    },
    test: {
      environment: 'node',
    },
  }
})
