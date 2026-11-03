// 요청자. 로그인이 들어오기 전까지 화면 상단에서 고르고 브라우저에 남긴다. 요청할 때 X-Requested-By 헤더로 보낸다.

import { useSyncExternalStore } from 'react'

export const OPERATORS = ['김운영', '이배포', '박콘텐츠']

const KEY = 'transfer-console.user'
const listeners = new Set<() => void>()

function read(): string {
  if (typeof localStorage === 'undefined') return OPERATORS[0]
  return localStorage.getItem(KEY) ?? OPERATORS[0]
}

export function setCurrentUser(name: string): void {
  localStorage.setItem(KEY, name)
  listeners.forEach((l) => l())
}

export function useCurrentUser(): string {
  return useSyncExternalStore(
    (listener) => {
      listeners.add(listener)
      return () => listeners.delete(listener)
    },
    read,
    read,
  )
}
