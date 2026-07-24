# RecipeHub

A "GitHub for recipes" - a social platform for sharing meal recipes with GitHub inspired forking mechanic

### Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
3. [Getting Started](#getting-started)
   - [Prerequisites](#prerequisites)
   - [Installation](#installation)
4. [Architecture](#architecture)
5. [Roadmap](#roadmap)

## Introduction

Inspired by social networks and GitHub's forking workflow. Recipe Hub lets users take any public recipe and create their own version of it.
Or make your own recipe and store it for your meal rotation.

App is centered around loading quickly, browsing by tags, collecting your favorites, and copying and modifying others' recipes.

The goal is to make sharing recipes feel collaborative instead of static. Every recipe can evolve while still giving credit to the original creator.

### Motivation
Idea of RecipeHub was building a production-style Spring Boot web application and backend project around an idea that I would personally use. 

## Features

- User registration and authentication.
- Users can manage their profile information.
- Users can create, update, delete and list their personal recipes.
- User can fork any public recipe.
- Forked recipes maintains relationship with original recipe.
- Users can browse and search for public recipes
- Users can create, update, delete and assign a tag for recipes.
- Users can browse recipes by tag.

## Getting Started

### Prerequisites

- Java 21
- Docker and Docker compose
- Maven

### Installation
1. Clone repository

```bash
git clone https://github.com/rohergun/RecipeHub
```

2. Start the database

```bash
docker compose up -d
```

3. Run the application

```bash   
./mvnw clean spring-boot:run
```

4. Accessing API docs

```
 http://localhost:8080/swagger-ui/index.html
```

5. (Optional) Accessing pgAdmin

```
http://localhost:5050
```


## Architecture

## Roadmap

* UI
* Import and Export recipes
* API Rate limiting
* Recipe image updating
* Caching on API and user preferences
* Monitoring and logging
