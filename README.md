# Vistora DB Crawler - Extended (Deployable)

See earlier conversation for full details. Quick start:
1. Start demo DB: `docker compose up -d`
2. Build: `mvn clean package`
3. Run: `java -jar target/db-crawler-1.0.0.jar`
4. Generate & persist: POST /api/generate?persist=true

CI: workflows in .github/workflows provide Docker Hub and Cloud Run examples.
