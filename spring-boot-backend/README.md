# Getting started
You need Java 14 installed.

    ./gradlew bootRun

To test that it works, open a browser tab at http://localhost:8080/tags .  
Alternatively, you can run

    curl http://localhost:8080/tags

# Try it out with [Docker](https://www.docker.com/)

You need Docker installed.
	
	docker-compose up -d

# Try it out with a  frontend

The entry point address of the backend API is at http://localhost:8080, **not** http://localhost:8080/api as some of the frontend documentation suggests.

