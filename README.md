# Job Tracker

A web app for tracking job applications. Each user can sign up, log in and keep their own list of jobs: which companies they want to apply to, where they have applied, and what the status is.

I made this project for the Back End Programming course at Haaga-Helia.

## Features

- Sign up with username and password, log in and log out
- Each user sees only their own jobs
- Add, edit and delete jobs
- Job status: WISHLIST, APPLIED, INTERVIEW, OFFER, REJECTED
- Form validation (company and position are required)
- A user cannot edit or delete someone else's job, even by typing the URL
- Passwords are saved only as BCrypt hash

## Technologies

- Java 17, Spring Boot 4
- Spring Data JPA, MySQL (H2 for tests)
- Spring Security (form login, BCrypt, CSRF)
- Thymeleaf, Bootstrap
- JUnit 5, MockMvc, Spring Security Test
- Deployed to the Haaga-Helia softala server

## Database

Two tables with a one-to-many relationship:

- AppUser: id, username, password hash, role
- JobApplication: id, company, position, location, job link, applied date, status, user

One user has many job applications. Each job application belongs to one user.

## Project structure

```
src/main/java/fi/haagahelia/jobtracker
├── config    Spring Security settings and login
├── domain    Entities, repositories, enum, sign up form
└── web       Controllers
```

## Things I learned by myself

- My own login page with messages (wrong password, signed out, account created)
- Checking on the server that a job belongs to the logged in user before edit or delete
- Reading the database password from an environment variable, so it is not in the code
- A separate test configuration, so tests use H2 and do not need MySQL
- Running the app in the background on the server with `screen`

## How to run

### Run the tests

The tests use H2 in memory, so no database is needed:

```
./mvnw test
```

### Run the app

The app uses MySQL. Give the database password as an environment variable:

```
MYSQL_PASSWORD='your_password' ./mvnw spring-boot:run
```

Then open http://softala.haaga-helia.fi:8091

### Demo users

| Username | Password |
|---|---|
| admin | admin |
| demo | demo |

You can also create your own user on the sign up page.

## Author

Minh Doan, Haaga-Helia University of Applied Sciences