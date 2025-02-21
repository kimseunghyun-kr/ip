# Spring - A JavaFX Chatbot Todo Tracker
<img src="./UI.png" alt="UI">

## Overview
Spring is a JavaFX-based chatbot application designed for efficient task and event tracking. It includes advanced features such as:

- **Autosave** to ensure your data is always preserved.
- **Dependency Injection** for modularity and testability.
- **Aspect-Oriented Programming (AOP)** for logging and cross-cutting concerns.

## Getting Started

### Prerequisites
Ensure you have:
- Java 17 or later installed.
- JavaFX dependencies included in your runtime environment.

### Installation
1. Download the latest JAR artifact from the release repository.
2. Run the application using:
   ```sh
   java -jar <path-to-spring.jar>
   ```
3. Once started, you can interact with the chatbot using the following commands.

## Commands Guide

### Searching for Tasks

| Command                                                              | Description                                           |
|----------------------------------------------------------------------|-------------------------------------------------------|
| `find homework`                                                      | Searches for tasks containing the keyword 'homework'. |
| `find date <deadline/event> <to :: YYYY-MM-DD> <from :: YYYY-MM-DD>` | Searches tasks/events within a specified date range.  |
| `find UUID <UUID>`                                                   | Searches a task by its UUID and returns its order.    |

### Adding Tasks

| Command                                                        | Description                                     |
|----------------------------------------------------------------|-------------------------------------------------|
| `todo <name>`                                                  | Adds a new to-do task with the given name.      |
| `deadline <name> <dueby :: YYYY-MM-DD>`                        | Adds a deadline task with a specified due date. |
| `event <name> <startfrom :: YYYY-MM-DD> <endBy :: YYYY-MM-DD>` | Adds an event with start and end dates.         |

### Deleting Tasks

| Command          | Description                                  |
|------------------|----------------------------------------------|
| `clear`          | Deletes all tasks.                           |
| `delete <order>` | Deletes a specific task by its order number. |

### Marking Task Completion

| Command          | Description                |
|------------------|----------------------------|
| `mark <order>`   | Marks a task as completed. |
| `unmark <order>` | Unmarks a completed task.  |

### Exiting the Application

| Command | Description                                  |
|---------|----------------------------------------------|
| `exit`  | Closes the application.                      |
| `quit`  | Alternative command to exit the application. |
| `bye`   | Another way to close the application.        |

## Features & Design
### 1. **Autosave**
Spring automatically saves tasks after every modification, ensuring data persistence without requiring manual saves.

### 2. **Dependency Injection**
- The application follows DI principles for modular, scalable code.
- Easily testable components with reduced coupling.

### 3. **Aspect-Oriented Programming (AOP)**
- AOP is used for logging and tracking method execution times.
- Reduces boilerplate logging code throughout the application.

## Example Usage
### Adding Tasks
```sh
> todo Buy groceries
Added: [T] Buy groceries

> deadline Project submission 2025-02-25
Added: [D] Project submission (by: 2025-02-25)

> event Seminar 2025-03-01 2025-03-02
Added: [E] Seminar (from: 2025-03-01 to: 2025-03-02)
```

### Searching
```sh
> find homework
Found: [D] Submit homework (by: 2025-02-20)
```

### Marking Completion
```sh
> mark 1
Marked as done: [T] Buy groceries
```

### Exiting
```sh
> exit
Goodbye! Have a productive day.
```

## Conclusion
Spring provides a seamless task management experience with powerful automation and modern Java principles. Enjoy tracking your tasks efficiently!

