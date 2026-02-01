ShortLink is a URL shortening platform that shortens long URLs in favor of URLs with 7 letter strings.

## Tech Stack:
React, Spring Boot, PostgreSQL, Nginx, Docker

## Running:
Update application.properties.template to include information for personal PostgreSQL database for fall-backs

Create .env with the following PostgreSQL information:

SPRING_DATASOURCE_URL=

SPRING_DATASOURCE_USERNAME=

SPRING_DATASOURCE_PASSWORD=


`docker-compose up --build`

Launches the dockerized project (including the backend, frontend, and database).

## To-Do

Connect the project to AWS to allow launch the project on the cloud and storing URLs in the cloud.