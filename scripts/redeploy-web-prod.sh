#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WEB_CONTAINER="${WEB_CONTAINER:-cnbjti-web}"
WEB_PORT="${WEB_PORT:-3000}"
NODE_IMAGE="${NODE_IMAGE:-node:22-bookworm}"
SITE_URL="${NUXT_PUBLIC_SITE_URL:-https://cnbjti.com}"
API_BASE="${NUXT_API_BASE:-http://host.docker.internal:8080/api}"
PUBLIC_API_BASE="${NUXT_PUBLIC_API_BASE:-/api}"

docker run --rm \
  -v "$ROOT:/app" \
  -w /app \
  -e NODE_OPTIONS=--max-old-space-size=4096 \
  "$NODE_IMAGE" \
  sh -lc 'corepack enable && corepack prepare pnpm@10.23.0 --activate && pnpm --filter web build'

docker rm -f "$WEB_CONTAINER" >/dev/null 2>&1 || true

docker run -d \
  --name "$WEB_CONTAINER" \
  --restart unless-stopped \
  --add-host=host.docker.internal:host-gateway \
  -p "$WEB_PORT:3000" \
  -v "$ROOT:/app" \
  -w /app \
  -e NUXT_API_BASE="$API_BASE" \
  -e NUXT_PUBLIC_API_BASE="$PUBLIC_API_BASE" \
  -e NUXT_PUBLIC_SITE_URL="$SITE_URL" \
  -e HOST=0.0.0.0 \
  -e PORT=3000 \
  -e NODE_OPTIONS=--max-old-space-size=1024 \
  "$NODE_IMAGE" \
  sh -lc 'node apps/web/.output/server/index.mjs'

curl -fsSI "http://127.0.0.1:${WEB_PORT}/" >/dev/null
docker stats --no-stream "$WEB_CONTAINER"
