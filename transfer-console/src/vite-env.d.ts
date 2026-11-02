/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** "mock" 이면 브라우저 안의 모의 API 를 쓴다 */
  readonly VITE_API_MODE?: 'mock' | 'live'
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
