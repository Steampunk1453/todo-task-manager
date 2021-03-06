## About

Application to remind a user different kinds of tasks like watch series, films and read books.

Web developed with KHipster, Kotlin, Spring Boot, Security with JWT, Angular, TypeScript and Heroku. Following Clean Code and SOLID principles.

This app was generated using JHipster 6.8.0, you can find documentation and help at: [https://www.jhipster.tech](https://www.jhipster.tech).

## Screenshots

<table>
  <tr>
    <td valign="top"><img src="screenshots/login.png"></td>
    <td valign="top"><img src="screenshots/calendar.png"></td>
    <td valign="top"><img src="screenshots/series.png"></td>
    <td valign="top"><img src="screenshots/books.png"></td>
  </tr>
 </table>

## To test application

Deployed in Heroku, navigate to:

https://pers-task-manager.herokuapp.com

## Local environment

Run command `./gradlew`

Access to: http://localhost:8080

You can try the default accounts:

Administrator: login="admin" and password="admin"

User: login="user" and password="user"

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:

1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools.
You will only need to run this command when dependencies change in [package.json](package.json).

    npm install

We use npm scripts and [Webpack][] as our build system.

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

    ./gradlew -x webpack
    npm start

Npm is also used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in [package.json](package.json). You can also run `npm update` and `npm install` to manage dependencies.
Add the `help` flag on any command to see how you can use it. For example, `npm help update`.

The `npm run` command will list all of the scripts available to run for this project.

## Building for production

### Packaging as jar

To build the final jar and optimize the ToDoTaskManager application for production, run:

    ./gradlew -Pprod clean bootJar

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

    java -jar build/libs/*.jar

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

### Packaging as war

To package your application as a war in order to deploy it to an application server, run:

    ./gradlew -Pprod -Pwar clean bootWar

## Testing

To launch your application's tests, run:

    ./gradlew test integrationTest jacocoTestReport

## Using Docker to simplify development (optional)

You can use Docker . A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.

For example, to start a postgresql database in a docker container, run:

    docker-compose -f src/main/docker/postgresql.yml up -d

To stop it and remove the container, run:

    docker-compose -f src/main/docker/postgresql.yml down

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

    ./gradlew bootJar -Pprod jibDockerBuild

Then run:

    docker-compose -f src/main/docker/app.yml up -d
